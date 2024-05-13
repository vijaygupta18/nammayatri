{-
 Copyright 2022-23, Juspay India Pvt Ltd

 This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License

 as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program

 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY

 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of

 the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
-}

module Domain.Action.Dashboard.Driver.Registration
  ( documentsList,
    getDocument,
    uploadDocument,
    registerDL,
    registerRC,
    generateAadhaarOtp,
    verifyAadhaarOtp,
    auth,
    verify,
    underReviewDriversList,
    driverDocumentInfo,
    updateDocument,
  )
where

import qualified "dashboard-helper-api" Dashboard.ProviderPlatform.Driver.Registration as Common
import qualified Domain.Action.UI.DriverOnboarding.AadhaarVerification as AV
import Domain.Action.UI.DriverOnboarding.DriverLicense
import Domain.Action.UI.DriverOnboarding.Image
import Domain.Action.UI.DriverOnboarding.VehicleRegistrationCertificate
import qualified Domain.Action.UI.Registration as DReg
import qualified Domain.Types.DocumentVerificationConfig as Domain
import qualified Domain.Types.FleetDriverAssociation as FDV
import qualified Domain.Types.Merchant as DM
import qualified Domain.Types.Person as SP
import qualified Domain.Types.RegistrationToken as SR
import Environment
import Kernel.Beam.Functions
import Kernel.External.AadhaarVerification.Interface.Types
import Kernel.Prelude
import Kernel.Types.APISuccess (APISuccess (Success))
import Kernel.Types.Beckn.Context as Context
import Kernel.Types.Error
import Kernel.Types.Id
import Kernel.Utils.Common
import SharedLogic.Merchant (findMerchantByShortId)
import qualified Storage.CachedQueries.Merchant.MerchantOperatingCity as CQMOC
import qualified Storage.Queries.FleetDriverAssociation as QFDV
import Storage.Queries.Image as QImage
import qualified Tools.AadhaarVerification as AadhaarVerification

documentsList :: ShortId DM.Merchant -> Context.City -> Id Common.Driver -> Flow Common.DocumentsListResponse
documentsList merchantShortId _ driverId = do
  merchant <- findMerchantByShortId merchantShortId
  licImgs <- map (.id.getId) <$> runInReplica (findImagesByPersonAndType merchant.id (cast driverId) Domain.DriverLicense)
  vehRegImgs <- map (.id.getId) <$> runInReplica (findImagesByPersonAndType merchant.id (cast driverId) Domain.VehicleRegistrationCertificate)
  pure
    Common.DocumentsListResponse
      { driverLicense = licImgs,
        vehicleRegistrationCertificate = vehRegImgs
      }

getDocument :: ShortId DM.Merchant -> Context.City -> Id Common.Image -> Flow Common.GetDocumentResponse
getDocument merchantShortId _ imageId = do
  merchant <- findMerchantByShortId merchantShortId
  img <- getImage merchant.id (cast imageId)
  pure Common.GetDocumentResponse {imageBase64 = img}

mapDocumentType :: Common.DocumentType -> Domain.DocumentType
mapDocumentType Common.DriverLicense = Domain.DriverLicense
mapDocumentType Common.VehicleRegistrationCertificate = Domain.VehicleRegistrationCertificate

uploadDocument :: ShortId DM.Merchant -> Context.City -> Id Common.Driver -> Common.UploadDocumentReq -> Flow Common.UploadDocumentResp
uploadDocument merchantShortId opCity driverId_ req = do
  merchant <- findMerchantByShortId merchantShortId
  merchantOpCityId <- CQMOC.getMerchantOpCityId Nothing merchant (Just opCity)
  res <-
    validateImage
      True
      (cast driverId_, cast merchant.id, merchantOpCityId)
      ImageValidateRequest
        { image = req.imageBase64,
          imageType = mapDocumentType req.imageType,
          rcNumber = req.rcNumber,
          vehicleCategory = Nothing
        }
  pure $ Common.UploadDocumentResp {imageId = cast res.imageId}

registerDL :: ShortId DM.Merchant -> Context.City -> Id Common.Driver -> Common.RegisterDLReq -> Flow APISuccess
registerDL merchantShortId opCity driverId_ Common.RegisterDLReq {..} = do
  merchant <- findMerchantByShortId merchantShortId
  merchantOpCityId <- CQMOC.getMerchantOpCityId Nothing merchant (Just opCity)
  verifyDL
    True
    (Just merchant)
    (cast driverId_, cast merchant.id, merchantOpCityId)
    DriverDLReq
      { imageId1 = cast imageId1,
        imageId2 = fmap cast imageId2,
        vehicleCategory = Nothing,
        ..
      }

registerRC :: ShortId DM.Merchant -> Context.City -> Id Common.Driver -> Common.RegisterRCReq -> Flow APISuccess
registerRC merchantShortId opCity driverId_ Common.RegisterRCReq {..} = do
  merchant <- findMerchantByShortId merchantShortId
  merchantOpCityId <- CQMOC.getMerchantOpCityId Nothing merchant (Just opCity)
  verifyRC
    True
    (Just merchant)
    (cast driverId_, cast merchant.id, merchantOpCityId)
    ( DriverRCReq
        { imageId = cast imageId,
          vehicleCategory = Nothing,
          vehicleDetails = Nothing,
          ..
        }
    )

generateAadhaarOtp :: ShortId DM.Merchant -> Context.City -> Id Common.Driver -> Common.GenerateAadhaarOtpReq -> Flow Common.GenerateAadhaarOtpRes
generateAadhaarOtp merchantShortId opCity driverId_ req = do
  merchant <- findMerchantByShortId merchantShortId
  merchantOpCityId <- CQMOC.getMerchantOpCityId Nothing merchant (Just opCity)
  res <-
    AV.generateAadhaarOtp
      True
      (Just merchant)
      (cast driverId_)
      merchantOpCityId
      AadhaarVerification.AadhaarOtpReq
        { aadhaarNumber = req.aadhaarNumber,
          consent = req.consent
        }
  pure (convertVerifyOtp res)

verifyAadhaarOtp :: ShortId DM.Merchant -> Context.City -> Id Common.Driver -> Common.VerifyAadhaarOtpReq -> Flow Common.VerifyAadhaarOtpRes
verifyAadhaarOtp merchantShortId opCity driverId_ req = do
  merchant <- findMerchantByShortId merchantShortId
  merchantOpCityId <- CQMOC.getMerchantOpCityId Nothing merchant (Just opCity)
  res <-
    AV.verifyAadhaarOtp
      (Just merchant)
      (cast driverId_)
      merchantOpCityId
      AV.VerifyAadhaarOtpReq
        { otp = req.otp,
          shareCode = req.shareCode
        }
  pure (convertSubmitOtp res)

auth :: ShortId DM.Merchant -> Context.City -> Common.AuthReq -> Flow Common.AuthRes
auth merchantShortId opCity req = do
  merchant <- findMerchantByShortId merchantShortId
  res <-
    DReg.auth
      True
      DReg.AuthReq
        { mobileNumber = Just req.mobileNumber,
          mobileCountryCode = Just req.mobileCountryCode,
          merchantId = merchant.id.getId,
          merchantOperatingCity = Just opCity,
          registrationLat = Nothing,
          registrationLon = Nothing,
          name = Nothing,
          email = Nothing,
          identifierType = Just SP.MOBILENUMBER
        }
      Nothing
      Nothing
      Nothing
      Nothing
  pure $ Common.AuthRes {authId = res.authId.getId, attempts = res.attempts}

verify :: Text -> Bool -> Text -> Common.AuthVerifyReq -> Flow APISuccess
verify authId mbFleet fleetOwnerId req = do
  let regId = Id authId :: Id SR.RegistrationToken
  res <-
    DReg.verify
      regId
      DReg.AuthVerifyReq
        { otp = req.otp,
          deviceToken = req.deviceToken,
          whatsappNotificationEnroll = Nothing
        }
  when mbFleet $ do
    assoc <- FDV.makeFleetVehicleDriverAssociation res.person.id fleetOwnerId
    QFDV.upsert assoc
  pure Success

underReviewDriversList :: ShortId DM.Merchant -> Context.City -> Maybe Int -> Maybe Int -> Flow Common.UnderReviewDriversListResponse
underReviewDriversList _merchantShortId _opCity _limit _offset = throwError (InternalError "Not Implemented")

driverDocumentInfo :: ShortId DM.Merchant -> Context.City -> Id Common.Driver -> Flow [Common.DriverDocument]
driverDocumentInfo _merchantShortId _opCity _driverId = throwError (InternalError "Not Implemented")

updateDocument :: ShortId DM.Merchant -> Context.City -> Id Common.Image -> Common.UpdateDocumentRequest -> Flow APISuccess
updateDocument _merchantShortId _opCity _imageId _req = throwError (InternalError "Not Implemented")

convertVerifyOtp :: AadhaarVerificationResp -> Common.GenerateAadhaarOtpRes
convertVerifyOtp AadhaarVerificationResp {..} = Common.GenerateAadhaarOtpRes {..}

convertSubmitOtp :: AadhaarOtpVerifyRes -> Common.VerifyAadhaarOtpRes
convertSubmitOtp AadhaarOtpVerifyRes {..} = Common.VerifyAadhaarOtpRes {..}
