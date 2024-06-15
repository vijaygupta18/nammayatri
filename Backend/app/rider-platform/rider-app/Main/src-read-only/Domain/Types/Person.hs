{-# LANGUAGE ApplicativeDo #-}
{-# LANGUAGE TemplateHaskell #-}
{-# OPTIONS_GHC -Wno-unused-imports #-}

module Domain.Types.Person where

import Data.Aeson
import qualified Domain.Types.Merchant
import qualified Domain.Types.MerchantConfig
import qualified Domain.Types.MerchantOperatingCity
import qualified Domain.Types.PartnerOrganization
import qualified Kernel.Beam.Lib.UtilsTH
import Kernel.External.Encryption
import qualified Kernel.External.Maps
import qualified Kernel.External.Payment.Interface.Types
import qualified Kernel.External.Whatsapp.Interface.Types
import Kernel.Prelude
import qualified Kernel.Types.Beckn.Context
import qualified Kernel.Types.Common
import qualified Kernel.Types.Id
import qualified Kernel.Types.Version
import qualified Kernel.Utils.TH
import qualified Tools.Beam.UtilsTH

data PersonE e = Person
  { aadhaarVerified :: Kernel.Prelude.Bool,
    backendAppVersion :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    blocked :: Kernel.Prelude.Bool,
    blockedAt :: Kernel.Prelude.Maybe Kernel.Prelude.UTCTime,
    blockedByRuleId :: Kernel.Prelude.Maybe (Kernel.Types.Id.Id Domain.Types.MerchantConfig.MerchantConfig),
    blockedCount :: Kernel.Prelude.Maybe Kernel.Prelude.Int,
    clientBundleVersion :: Kernel.Prelude.Maybe Kernel.Types.Version.Version,
    clientConfigVersion :: Kernel.Prelude.Maybe Kernel.Types.Version.Version,
    clientDevice :: Kernel.Prelude.Maybe Kernel.Types.Version.Device,
    clientSdkVersion :: Kernel.Prelude.Maybe Kernel.Types.Version.Version,
    createdAt :: Kernel.Prelude.UTCTime,
    currentCity :: Kernel.Types.Beckn.Context.City,
    customerPaymentId :: Kernel.Prelude.Maybe Kernel.External.Payment.Interface.Types.CustomerId,
    customerReferralCode :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    defaultPaymentMethodId :: Kernel.Prelude.Maybe Kernel.External.Payment.Interface.Types.PaymentMethodId,
    description :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    deviceId :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    deviceToken :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    email :: Kernel.Prelude.Maybe (Kernel.External.Encryption.EncryptedHashedField e Kernel.Prelude.Text),
    enabled :: Kernel.Prelude.Bool,
    falseSafetyAlarmCount :: Kernel.Prelude.Int,
    firstName :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    followsRide :: Kernel.Prelude.Bool,
    gender :: Domain.Types.Person.Gender,
    hasCompletedMockSafetyDrill :: Kernel.Prelude.Maybe Kernel.Prelude.Bool,
    hasCompletedSafetySetup :: Kernel.Prelude.Bool,
    hasDisability :: Kernel.Prelude.Maybe Kernel.Prelude.Bool,
    hasTakenValidRide :: Kernel.Prelude.Bool,
    id :: Kernel.Types.Id.Id Domain.Types.Person.Person,
    identifier :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    identifierType :: Domain.Types.Person.IdentifierType,
    isNew :: Kernel.Prelude.Bool,
    isValidRating :: Kernel.Prelude.Bool,
    language :: Kernel.Prelude.Maybe Kernel.External.Maps.Language,
    lastName :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    merchantId :: Kernel.Types.Id.Id Domain.Types.Merchant.Merchant,
    merchantOperatingCityId :: Kernel.Types.Id.Id Domain.Types.MerchantOperatingCity.MerchantOperatingCity,
    middleName :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    mobileCountryCode :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    mobileNumber :: Kernel.Prelude.Maybe (Kernel.External.Encryption.EncryptedHashedField e Kernel.Prelude.Text),
    nightSafetyChecks :: Kernel.Prelude.Bool,
    notificationToken :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    passwordHash :: Kernel.Prelude.Maybe Kernel.External.Encryption.DbHash,
    rating :: Kernel.Prelude.Maybe Kernel.Types.Common.Centesimal,
    referralCode :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    referredAt :: Kernel.Prelude.Maybe Kernel.Prelude.UTCTime,
    referredByCustomer :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    registeredViaPartnerOrgId :: Kernel.Prelude.Maybe (Kernel.Types.Id.Id Domain.Types.PartnerOrganization.PartnerOrganization),
    registrationLat :: Kernel.Prelude.Maybe Kernel.Prelude.Double,
    registrationLon :: Kernel.Prelude.Maybe Kernel.Prelude.Double,
    role :: Domain.Types.Person.Role,
    safetyCenterDisabledOnDate :: Kernel.Prelude.Maybe Kernel.Prelude.UTCTime,
    shareEmergencyContacts :: Kernel.Prelude.Bool,
    shareTripWithEmergencyContactOption :: Kernel.Prelude.Maybe Domain.Types.Person.RideShareOptions,
    totalRatingScore :: Kernel.Prelude.Int,
    totalRatings :: Kernel.Prelude.Int,
    totalRidesCount :: Kernel.Prelude.Maybe Kernel.Prelude.Int,
    unencryptedMobileNumber :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    updatedAt :: Kernel.Prelude.UTCTime,
    useFakeOtp :: Kernel.Prelude.Maybe Kernel.Prelude.Text,
    whatsappNotificationEnrollStatus :: Kernel.Prelude.Maybe Kernel.External.Whatsapp.Interface.Types.OptApiMethods
  }
  deriving (Generic)

type Person = PersonE 'AsEncrypted

type DecryptedPerson = PersonE 'AsUnencrypted

instance EncryptedItem Person where
  type Unencrypted Person = (DecryptedPerson, HashSalt)
  encryptItem (entity, salt) = do
    email_ <- encryptItem $ (,salt) <$> email entity
    mobileNumber_ <- encryptItem $ (,salt) <$> mobileNumber entity
    pure
      Person
        { aadhaarVerified = aadhaarVerified entity,
          backendAppVersion = backendAppVersion entity,
          blocked = blocked entity,
          blockedAt = blockedAt entity,
          blockedByRuleId = blockedByRuleId entity,
          blockedCount = blockedCount entity,
          clientBundleVersion = clientBundleVersion entity,
          clientConfigVersion = clientConfigVersion entity,
          clientDevice = clientDevice entity,
          clientSdkVersion = clientSdkVersion entity,
          createdAt = createdAt entity,
          currentCity = currentCity entity,
          customerPaymentId = customerPaymentId entity,
          customerReferralCode = customerReferralCode entity,
          defaultPaymentMethodId = defaultPaymentMethodId entity,
          description = description entity,
          deviceId = deviceId entity,
          deviceToken = deviceToken entity,
          email = email_,
          enabled = enabled entity,
          falseSafetyAlarmCount = falseSafetyAlarmCount entity,
          firstName = firstName entity,
          followsRide = followsRide entity,
          gender = gender entity,
          hasCompletedMockSafetyDrill = hasCompletedMockSafetyDrill entity,
          hasCompletedSafetySetup = hasCompletedSafetySetup entity,
          hasDisability = hasDisability entity,
          hasTakenValidRide = hasTakenValidRide entity,
          id = id entity,
          identifier = identifier entity,
          identifierType = identifierType entity,
          isNew = isNew entity,
          isValidRating = isValidRating entity,
          language = language entity,
          lastName = lastName entity,
          merchantId = merchantId entity,
          merchantOperatingCityId = merchantOperatingCityId entity,
          middleName = middleName entity,
          mobileCountryCode = mobileCountryCode entity,
          mobileNumber = mobileNumber_,
          nightSafetyChecks = nightSafetyChecks entity,
          notificationToken = notificationToken entity,
          passwordHash = passwordHash entity,
          rating = rating entity,
          referralCode = referralCode entity,
          referredAt = referredAt entity,
          referredByCustomer = referredByCustomer entity,
          registeredViaPartnerOrgId = registeredViaPartnerOrgId entity,
          registrationLat = registrationLat entity,
          registrationLon = registrationLon entity,
          role = role entity,
          safetyCenterDisabledOnDate = safetyCenterDisabledOnDate entity,
          shareEmergencyContacts = shareEmergencyContacts entity,
          shareTripWithEmergencyContactOption = shareTripWithEmergencyContactOption entity,
          totalRatingScore = totalRatingScore entity,
          totalRatings = totalRatings entity,
          totalRidesCount = totalRidesCount entity,
          unencryptedMobileNumber = unencryptedMobileNumber entity,
          updatedAt = updatedAt entity,
          useFakeOtp = useFakeOtp entity,
          whatsappNotificationEnrollStatus = whatsappNotificationEnrollStatus entity
        }
  decryptItem entity = do
    email_ <- fmap fst <$> decryptItem (email entity)
    mobileNumber_ <- fmap fst <$> decryptItem (mobileNumber entity)
    pure
      ( Person
          { aadhaarVerified = aadhaarVerified entity,
            backendAppVersion = backendAppVersion entity,
            blocked = blocked entity,
            blockedAt = blockedAt entity,
            blockedByRuleId = blockedByRuleId entity,
            blockedCount = blockedCount entity,
            clientBundleVersion = clientBundleVersion entity,
            clientConfigVersion = clientConfigVersion entity,
            clientDevice = clientDevice entity,
            clientSdkVersion = clientSdkVersion entity,
            createdAt = createdAt entity,
            currentCity = currentCity entity,
            customerPaymentId = customerPaymentId entity,
            customerReferralCode = customerReferralCode entity,
            defaultPaymentMethodId = defaultPaymentMethodId entity,
            description = description entity,
            deviceId = deviceId entity,
            deviceToken = deviceToken entity,
            email = email_,
            enabled = enabled entity,
            falseSafetyAlarmCount = falseSafetyAlarmCount entity,
            firstName = firstName entity,
            followsRide = followsRide entity,
            gender = gender entity,
            hasCompletedMockSafetyDrill = hasCompletedMockSafetyDrill entity,
            hasCompletedSafetySetup = hasCompletedSafetySetup entity,
            hasDisability = hasDisability entity,
            hasTakenValidRide = hasTakenValidRide entity,
            id = id entity,
            identifier = identifier entity,
            identifierType = identifierType entity,
            isNew = isNew entity,
            isValidRating = isValidRating entity,
            language = language entity,
            lastName = lastName entity,
            merchantId = merchantId entity,
            merchantOperatingCityId = merchantOperatingCityId entity,
            middleName = middleName entity,
            mobileCountryCode = mobileCountryCode entity,
            mobileNumber = mobileNumber_,
            nightSafetyChecks = nightSafetyChecks entity,
            notificationToken = notificationToken entity,
            passwordHash = passwordHash entity,
            rating = rating entity,
            referralCode = referralCode entity,
            referredAt = referredAt entity,
            referredByCustomer = referredByCustomer entity,
            registeredViaPartnerOrgId = registeredViaPartnerOrgId entity,
            registrationLat = registrationLat entity,
            registrationLon = registrationLon entity,
            role = role entity,
            safetyCenterDisabledOnDate = safetyCenterDisabledOnDate entity,
            shareEmergencyContacts = shareEmergencyContacts entity,
            shareTripWithEmergencyContactOption = shareTripWithEmergencyContactOption entity,
            totalRatingScore = totalRatingScore entity,
            totalRatings = totalRatings entity,
            totalRidesCount = totalRidesCount entity,
            unencryptedMobileNumber = unencryptedMobileNumber entity,
            updatedAt = updatedAt entity,
            useFakeOtp = useFakeOtp entity,
            whatsappNotificationEnrollStatus = whatsappNotificationEnrollStatus entity
          },
        ""
      )

instance EncryptedItem' Person where
  type UnencryptedItem Person = DecryptedPerson
  toUnencrypted a salt = (a, salt)
  fromUnencrypted = fst

data Gender = MALE | FEMALE | OTHER | UNKNOWN | PREFER_NOT_TO_SAY deriving (Show, Eq, Ord, Read, Generic, ToJSON, FromJSON, ToSchema, ToParamSchema)

data IdentifierType = MOBILENUMBER | AADHAAR | EMAIL deriving (Show, Eq, Read, Ord, Generic, ToJSON, FromJSON, ToSchema, ToParamSchema)

data RideShareOptions = ALWAYS_SHARE | SHARE_WITH_TIME_CONSTRAINTS | NEVER_SHARE deriving (Show, Eq, Ord, Read, Generic, ToJSON, FromJSON, ToSchema, ToParamSchema)

data Role = USER | CUSTOMER_SUPPORT deriving (Show, Eq, Ord, Read, Generic, ToJSON, FromJSON, ToSchema, ToParamSchema)

$(Kernel.Utils.TH.mkFromHttpInstanceForEnum ''Role)

$(Kernel.Beam.Lib.UtilsTH.mkBeamInstancesForEnumAndList ''Role)

$(Kernel.Utils.TH.mkFromHttpInstanceForEnum ''IdentifierType)

$(Kernel.Beam.Lib.UtilsTH.mkBeamInstancesForEnumAndList ''IdentifierType)

$(Kernel.Beam.Lib.UtilsTH.mkBeamInstancesForEnumAndList ''Gender)

$(Kernel.Utils.TH.mkFromHttpInstanceForEnum ''Gender)

$(Kernel.Beam.Lib.UtilsTH.mkBeamInstancesForEnumAndList ''RideShareOptions)

$(Kernel.Utils.TH.mkFromHttpInstanceForEnum ''RideShareOptions)
