{-# OPTIONS_GHC -Wno-orphans #-}
{-# OPTIONS_GHC -Wno-unused-imports #-}

module Domain.Action.UI.SocialLogin where

import qualified API.Types.UI.SocialLogin
import qualified API.Types.UI.SocialLogin as SL
import Control.Monad (mzero)
import Data.Aeson
import Data.ByteString.Lazy (ByteString)
import Data.OpenApi (ToSchema)
import qualified Data.Text as T
import qualified Domain.Action.UI.Registration as DR
import qualified Domain.Types.Merchant
import qualified Domain.Types.Merchant.MerchantOperatingCity
import qualified Domain.Types.Person
import qualified Domain.Types.Person as SP
import qualified Domain.Types.RegistrationToken as SR
import qualified Environment
import qualified EulerHS.Language as L
import EulerHS.Prelude hiding (id)
import Kernel.External.Encryption (decrypt, encrypt, getDbHash)
import qualified Kernel.Prelude
import qualified Kernel.Types.APISuccess
import Kernel.Types.Id
import Kernel.Utils.Common
import Network.HTTP.Client
import Network.HTTP.Client.TLS (tlsManagerSettings)
import Network.HTTP.Types.Status (statusCode)
import Servant hiding (throwError)
import qualified Storage.CachedQueries.Merchant.MerchantOperatingCity as CQMOC
import qualified Storage.Queries.Person as PQ
import qualified Storage.Queries.RegistrationToken as QR
import Tools.Auth
import Tools.Error

googleTokenInfoUrl :: Text -> String -- TODO: change this to local validation as mentioned in this doc: https://developers.google.com/identity/sign-in/web/backend-auth#verify-the-integrity-of-the-id-token
googleTokenInfoUrl token = "https://oauth2.googleapis.com/tokeninfo?id_token=" <> T.unpack token

data TokenInfo = TokenInfo
  { email :: Text,
    email_verified :: Maybe String,
    name :: Maybe Text,
    picture :: Maybe Text
  }
  deriving (Show)

instance FromJSON TokenInfo where
  parseJSON = withObject "TokenInfo" $ \v ->
    TokenInfo
      <$> v .: "email"
      <*> v .:? "email_verified"
      <*> v .:? "name"
      <*> v .:? "picture"

fetchTokenInfo :: Text -> SL.OAuthProvider -> Text -> IO (Either String TokenInfo)
fetchTokenInfo iosValidateEnpoint oauthProvider token = do
  manager <- newManager tlsManagerSettings
  request <- parseRequest $ case oauthProvider of
    SL.Google -> googleTokenInfoUrl token
    SL.IOS -> T.unpack $ iosValidateEnpoint <> token
  response <- httpLbs request manager
  let statusCode' = statusCode $ responseStatus response
  if statusCode' == 200
    then return $ eitherDecode $ responseBody response
    else return $ Left $ "Failed to fetch token info, status code: " ++ show statusCode'

postSocialLogin :: SL.SocialLoginReq -> Environment.Flow SL.SocialLoginRes
postSocialLogin req = do
  iosValidateEnpoint <- asks (.iosValidateEnpoint)
  result <- L.runIO $ fetchTokenInfo iosValidateEnpoint req.oauthProvider req.tokenId
  case result of
    Right info -> do
      oldPerson <- PQ.findByEmailAndMerchant req.merchantId info.email
      moc <- CQMOC.findByMerchantIdAndCity req.merchantId req.merchantOperatingCity >>= fromMaybeM (MerchantOperatingCityNotFound $ show req.merchantOperatingCity)
      person <-
        case oldPerson of
          Just person' -> pure person'
          Nothing -> do
            deploymentVersion <- asks (.version)
            let createPersonInput = buildCreatePersonInput moc.city req.name info.email
            DR.createDriverWithDetails createPersonInput Nothing Nothing Nothing Nothing (Just deploymentVersion.getDeploymentVersion) req.merchantId moc.id False
      QR.deleteByPersonId person.id
      token <- makeSession person.id.getId req.merchantId.getId moc.id.getId
      _ <- QR.create token
      pure $ SL.SocialLoginRes token.token
    Left _ -> throwError . FailedToVerifyIdToken $ show req.oauthProvider <> ", idToken: " <> req.tokenId <> " error: "
  where
    buildCreatePersonInput city name email =
      DR.AuthReq
        { mobileNumber = Nothing,
          mobileCountryCode = Nothing,
          name = name,
          merchantId = req.merchantId.getId,
          merchantOperatingCity = Just city,
          email = Just email,
          identifierType = Just SP.EMAIL,
          registrationLat = req.registrationLat,
          registrationLon = req.registrationLon
        }

makeSession ::
  Text ->
  Text ->
  Text ->
  Environment.Flow SR.RegistrationToken
makeSession entityId merchantId merchantOpCityId = do
  otp <- generateOTPCode
  rtid <- generateGUID
  token <- generateGUID
  now <- getCurrentTime
  return $
    SR.RegistrationToken
      { id = Id rtid,
        token = token,
        attempts = 3, -- TODO: maybe change later
        authMedium = SR.EMAIL,
        authType = SR.OAUTH,
        authValueHash = otp,
        verified = True,
        authExpiry = 3,
        tokenExpiry = 365,
        entityId = entityId,
        merchantId = merchantId,
        merchantOperatingCityId = merchantOpCityId,
        entityType = SR.USER,
        createdAt = now,
        updatedAt = now,
        info = Nothing,
        alternateNumberAttempts = 3 -- TODO: change later
      }

postSocialUpdateProfile ::
  ( ( Kernel.Prelude.Maybe (Kernel.Types.Id.Id Domain.Types.Person.Person),
      Kernel.Types.Id.Id Domain.Types.Merchant.Merchant,
      Kernel.Types.Id.Id Domain.Types.Merchant.MerchantOperatingCity.MerchantOperatingCity
    ) ->
    API.Types.UI.SocialLogin.SocialUpdateProfileReq ->
    Environment.Flow Kernel.Types.APISuccess.APISuccess
  )
postSocialUpdateProfile (mbPersonId, _, _) req = do
  case mbPersonId of
    Nothing -> throwError $ InternalError "Not Implemented for dashboard"
    Just personId -> do
      encNewPhoneNumber <- encrypt `mapM` req.mobileNumber
      person <-
        PQ.findById personId
          >>= fromMaybeM (PersonDoesNotExist personId.getId)
      let updPerson =
            person
              { SP.mobileCountryCode = req.mobileCountryCode,
                SP.mobileNumber = encNewPhoneNumber,
                SP.unencryptedMobileNumber = req.mobileNumber,
                SP.firstName = fromMaybe person.firstName req.firstName,
                SP.lastName = req.lastName <|> person.lastName
              }
      PQ.updatePersonDetails updPerson
      pure Kernel.Types.APISuccess.Success
