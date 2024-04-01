{-
 Copyright 2022-23, Juspay India Pvt Ltd

 This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License

 as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program

 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY

 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of

 the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
-}

module Domain.Action.Dashboard.Registration where

import Data.List (groupBy, sortOn)
import qualified Data.Text as T
import qualified Domain.Action.Dashboard.Person as DP
import qualified Domain.Types.Merchant as DMerchant
import qualified Domain.Types.MerchantAccess as DAccess
import qualified Domain.Types.MerchantAccess as DMerchantAccess
import Domain.Types.Person as DP
import qualified Domain.Types.Person.Type as PT
import qualified Domain.Types.RegistrationToken as DR
import Domain.Types.Role as DRole
import qualified Domain.Types.ServerName as DTServer
import qualified EulerHS.Language as L
import Kernel.Beam.Functions as B
import Kernel.External.Encryption (encrypt)
import Kernel.Prelude
import qualified Kernel.Storage.Hedis as Redis
import Kernel.Types.APISuccess (APISuccess (Success))
import qualified Kernel.Types.Beckn.City as City
import Kernel.Types.Common hiding (id)
import Kernel.Types.Error
import Kernel.Types.Id
import Kernel.Types.Predicate
import Kernel.Utils.Common
import qualified Kernel.Utils.Predicates as P
import Kernel.Utils.Validation
import Storage.Beam.BeamFlow
import qualified Storage.Queries.Merchant as QMerchant
import qualified Storage.Queries.MerchantAccess as MA
import qualified Storage.Queries.MerchantAccess as QAccess
import qualified Storage.Queries.Person as QP
import qualified Storage.Queries.RegistrationToken as QR
import qualified Storage.Queries.Role as QRole
import Tools.Auth
import qualified Tools.Auth.Common as Auth
import Tools.Auth.Merchant
import Tools.Error
import qualified Tools.Utils as Utils

data LoginReq = LoginReq
  { email :: Maybe Text,
    password :: Text,
    otp :: Maybe Text
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

data Enable2FAReq = Enable2FAReq
  { email :: Text,
    password :: Text,
    merchantId :: ShortId DMerchant.Merchant,
    city :: Maybe City.City
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

newtype Enable2FARes = Enable2FARes
  { qrcode :: Text
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

data LoginRes = LoginRes
  { authToken :: Text,
    is2faMandatory :: Bool,
    is2faEnabled :: Bool,
    message :: Text,
    city :: City.City,
    merchantId :: ShortId DMerchant.Merchant
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

data SwitchMerchantAndCityReq = SwitchMerchantAndCityReq
  { merchantId :: ShortId DMerchant.Merchant,
    city :: City.City,
    otp :: Maybe Text
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

data SwitchMerchantReq = SwitchMerchantReq
  { merchantId :: ShortId DMerchant.Merchant,
    otp :: Maybe Text
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

newtype LogoutRes = LogoutRes {message :: Text}
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

data FleetRegisterReq = FleetRegisterReq
  { firstName :: Text,
    lastName :: Text,
    mobileNumber :: Text,
    mobileCountryCode :: Text,
    merchantId :: ShortId DMerchant.Merchant,
    fleetType :: Maybe FleetType,
    city :: Maybe City.City
  }
  deriving (Generic, ToJSON, FromJSON, ToSchema)

data FleetType = RENTAL_FLEET | NORMAL_FLEET
  deriving (Generic, ToJSON, FromJSON, ToSchema)

login ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text],
    HasFlowEnv m r '["dataServers" ::: [DTServer.DataServer]],
    EncFlow m r
  ) =>
  LoginReq ->
  m LoginRes
login LoginReq {..} = do
  email_ <- email & fromMaybeM (InvalidRequest "Email cannot be empty when login type is email")
  person <- QP.findByEmailAndPassword email_ password >>= fromMaybeM (PersonDoesNotExist email_)
  merchantAccessList <- B.runInReplica $ QAccess.findAllMerchantAccessByPersonId person.id
  (merchant', city') <- case merchantAccessList of
    [] -> throwError (InvalidRequest "No access to any merchant")
    merchantAccessList' -> do
      let sortedMerchantAccessList = sortOn DAccess.merchantShortId merchantAccessList'
      let groupedByMerchant = head $ groupBy ((==) `on` DAccess.merchantShortId) sortedMerchantAccessList
      let merchantWithCityList = DP.AvailableCitiesForMerchant ((.merchantShortId) (head groupedByMerchant)) (map (.operatingCity) groupedByMerchant)
      merchant <- QMerchant.findByShortId merchantWithCityList.merchantShortId >>= fromMaybeM (MerchantDoesNotExist merchantWithCityList.merchantShortId.getShortId)
      let defaultCityPresent = elem merchant.defaultOperatingCity merchantWithCityList.operatingCity
      let city' = if defaultCityPresent then merchant.defaultOperatingCity else head merchantWithCityList.operatingCity
      pure (merchant, city')
  generateLoginRes person merchant' otp city'

switchMerchant ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text],
    HasFlowEnv m r '["dataServers" ::: [DTServer.DataServer]],
    EncFlow m r
  ) =>
  TokenInfo ->
  SwitchMerchantReq ->
  m LoginRes
switchMerchant authToken SwitchMerchantReq {..} = do
  merchant <- QMerchant.findByShortId merchantId >>= fromMaybeM (MerchantDoesNotExist merchantId.getShortId)
  merchantServerAccessCheck merchant
  person <- QP.findById authToken.personId >>= fromMaybeM (PersonDoesNotExist authToken.personId.getId)
  generateLoginRes person merchant otp merchant.defaultOperatingCity

switchMerchantAndCity ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text],
    HasFlowEnv m r '["dataServers" ::: [DTServer.DataServer]],
    EncFlow m r
  ) =>
  TokenInfo ->
  SwitchMerchantAndCityReq ->
  m LoginRes
switchMerchantAndCity authToken SwitchMerchantAndCityReq {..} = do
  merchant <- QMerchant.findByShortId merchantId >>= fromMaybeM (MerchantDoesNotExist merchantId.getShortId)
  merchantServerAccessCheck merchant
  person <- QP.findById authToken.personId >>= fromMaybeM (PersonDoesNotExist authToken.personId.getId)
  generateLoginRes person merchant otp city

generateLoginRes ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text],
    EncFlow m r
  ) =>
  DP.Person ->
  DMerchant.Merchant ->
  Maybe Text ->
  City.City ->
  m LoginRes
generateLoginRes person merchant otp city = do
  _merchantAccess <- QAccess.findByPersonIdAndMerchantIdAndCity person.id merchant.id city >>= fromMaybeM AccessDenied --FIXME cleanup tokens for this merchantId
  (isToken, msg) <- check2FA _merchantAccess merchant otp
  token <-
    if isToken
      then generateToken person.id merchant.id city
      else pure ""
  pure $ LoginRes token merchant.is2faMandatory _merchantAccess.is2faEnabled msg city merchant.shortId

check2FA :: (EncFlow m r) => DMerchantAccess.MerchantAccess -> DMerchant.Merchant -> Maybe Text -> m (Bool, Text)
check2FA merchantAccess merchant otp =
  case (DMerchant.is2faMandatory merchant, DMerchantAccess.is2faEnabled merchantAccess) of
    (True, True) -> handle2FA merchantAccess.secretKey otp
    (True, False) -> pure (False, "2 Factor authentication is not enabled, it is mandatory for this merchant")
    _ -> pure (True, "Logged in successfully")

handle2FA ::
  ( EncFlow m r
  ) =>
  Maybe Text ->
  Maybe Text ->
  m (Bool, Text)
handle2FA secretKey otp = case (secretKey, otp) of
  (Just key, Just userOtp) -> do
    generatedOtp <- L.runIO (Utils.genTOTP key)
    if generatedOtp == read (T.unpack userOtp)
      then pure (True, "Logged in successfully")
      else pure (False, "Google Authenticator OTP does not match")
  (_, Nothing) -> pure (False, "Google Authenticator OTP is required")
  (Nothing, _) -> pure (False, "Secret key not found for 2FA")

enable2fa ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text],
    HasFlowEnv m r '["dataServers" ::: [DTServer.DataServer]],
    EncFlow m r
  ) =>
  Enable2FAReq ->
  m Enable2FARes
enable2fa Enable2FAReq {..} = do
  person <- QP.findByEmailAndPassword email password >>= fromMaybeM (PersonDoesNotExist email)
  merchant <- QMerchant.findByShortId merchantId >>= fromMaybeM (MerchantDoesNotExist merchantId.getShortId)
  let city' = fromMaybe merchant.defaultOperatingCity city
  _merchantAccess <- QAccess.findByPersonIdAndMerchantIdAndCity person.id merchant.id city' >>= fromMaybeM AccessDenied
  key <- L.runIO Utils.generateSecretKey
  MA.updatePerson2faForMerchant person.id merchant.id key
  let qrCodeUri = Utils.generateAuthenticatorURI key email merchant.shortId
  pure $ Enable2FARes qrCodeUri

generateToken ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text]
  ) =>
  Id DP.Person ->
  Id DMerchant.Merchant ->
  City.City ->
  m Text
generateToken personId merchantId city = do
  findPreviousToken <- QR.findByPersonIdAndMerchantIdAndCity personId merchantId city
  case findPreviousToken of
    Just regToken -> pure $ regToken.token
    Nothing -> do
      regToken <- buildRegistrationToken personId merchantId city
      Auth.cleanCachedTokensByMerchantIdAndCity personId merchantId city
      QR.deleteAllByPersonIdAndMerchantIdAndCity personId merchantId city
      QR.create regToken
      pure $ regToken.token

logout ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text]
  ) =>
  TokenInfo ->
  m LogoutRes
logout tokenInfo = do
  let personId = tokenInfo.personId
  person <- QP.findById personId >>= fromMaybeM (PersonNotFound personId.getId)
  -- this function uses tokens from db, so should be called before transaction
  Auth.cleanCachedTokensByMerchantIdAndCity personId tokenInfo.merchantId tokenInfo.city
  QR.deleteAllByPersonIdAndMerchantIdAndCity person.id tokenInfo.merchantId tokenInfo.city
  pure $ LogoutRes "Logged out successfully"

logoutAllMerchants ::
  ( BeamFlow m r,
    Redis.HedisFlow m r,
    HasFlowEnv m r '["authTokenCacheKeyPrefix" ::: Text]
  ) =>
  TokenInfo ->
  m LogoutRes
logoutAllMerchants tokenInfo = do
  let personId = tokenInfo.personId
  person <- QP.findById personId >>= fromMaybeM (PersonNotFound personId.getId)
  -- this function uses tokens from db, so should be called before transaction
  Auth.cleanCachedTokens personId
  QR.deleteAllByPersonId person.id
  pure $ LogoutRes "Logged out successfully from all servers"

buildRegistrationToken :: MonadFlow m => Id DP.Person -> Id DMerchant.Merchant -> City.City -> m DR.RegistrationToken
buildRegistrationToken personId merchantId city = do
  rtid <- generateGUID
  token <- generateGUID
  now <- getCurrentTime
  return $
    DR.RegistrationToken
      { id = Id rtid,
        token = token,
        personId = personId,
        merchantId = merchantId,
        createdAt = now,
        operatingCity = city,
        enabled = True
      }

registerFleetOwner ::
  ( BeamFlow m r,
    EncFlow m r,
    HasFlowEnv m r '["dataServers" ::: [DTServer.DataServer]]
  ) =>
  FleetRegisterReq ->
  m APISuccess
registerFleetOwner req = do
  runRequestValidation validateFleetOwner req
  unlessM (isNothing <$> QP.findByMobileNumber req.mobileNumber req.mobileCountryCode) $ throwError (InvalidRequest "Phone already registered")
  fleetOwnerRole <- QRole.findByDashboardAccessType (getFleetRole req.fleetType) >>= fromMaybeM (RoleDoesNotExist "FLEET_OWNER")
  fleetOwner <- buildFleetOwner req fleetOwnerRole.id
  merchant <-
    QMerchant.findByShortId req.merchantId
      >>= fromMaybeM (MerchantDoesNotExist req.merchantId.getShortId)
  merchantServerAccessCheck merchant
  let city' = fromMaybe merchant.defaultOperatingCity req.city
  merchantAccess <- DP.buildMerchantAccess fleetOwner.id merchant.id merchant.shortId city'
  QP.create fleetOwner
  QAccess.create merchantAccess
  return Success
  where
    getFleetRole mbFleetType = case mbFleetType of
      Just RENTAL_FLEET -> RENTAL_FLEET_OWNER
      Just NORMAL_FLEET -> FLEET_OWNER
      Nothing -> FLEET_OWNER

buildFleetOwner :: (EncFlow m r) => FleetRegisterReq -> Id DRole.Role -> m PT.Person
buildFleetOwner req roleId = do
  pid <- generateGUID
  now <- getCurrentTime
  mobileNumber <- encrypt req.mobileNumber
  return
    PT.Person
      { id = pid,
        firstName = req.firstName,
        lastName = req.lastName,
        roleId = roleId,
        email = Nothing,
        mobileNumber = mobileNumber,
        mobileCountryCode = req.mobileCountryCode,
        passwordHash = Nothing,
        createdAt = now,
        updatedAt = now
      }

validateFleetOwner :: Validate FleetRegisterReq
validateFleetOwner FleetRegisterReq {..} =
  sequenceA_
    [ validateField "firstName" firstName $ MinLength 3 `And` P.name,
      validateField "lastName" lastName $ NotEmpty `And` P.name,
      validateField "mobileNumber" mobileNumber P.mobileNumber,
      validateField "mobileCountryCode" mobileCountryCode P.mobileCountryCode
    ]
