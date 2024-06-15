{-# LANGUAGE DerivingStrategies #-}
{-# LANGUAGE StandaloneDeriving #-}
{-# LANGUAGE TemplateHaskell #-}
{-# OPTIONS_GHC -Wno-unused-imports #-}

module Storage.Beam.TransporterConfig where

import qualified Data.Aeson
import qualified Database.Beam as B
import qualified Domain.Types.UtilsTH
import qualified Domain.Types.Vehicle
import qualified Email.Types
import Kernel.External.Encryption
import qualified Kernel.External.Types
import Kernel.Prelude
import qualified Kernel.Prelude
import qualified Kernel.Types.Beckn.City
import qualified Kernel.Types.Common
import Tools.Beam.UtilsTH

data TransporterConfigT f = TransporterConfigT
  { aadhaarImageResizeConfig :: B.C f (Kernel.Prelude.Maybe Data.Aeson.Value),
    aadhaarVerificationRequired :: B.C f Kernel.Prelude.Bool,
    acStatusCheckGap :: B.C f Kernel.Prelude.Int,
    actualRideDistanceDiffThreshold :: B.C f Kernel.Types.Common.HighPrecMeters,
    actualRideDistanceDiffThresholdIfWithinPickupDrop :: B.C f Kernel.Types.Common.HighPrecMeters,
    allowDefaultPlanAllocation :: B.C f Kernel.Prelude.Bool,
    approxRideDistanceDiffThreshold :: B.C f Kernel.Types.Common.HighPrecMeters,
    arrivedPickupThreshold :: B.C f (Kernel.Prelude.Maybe Kernel.Types.Common.HighPrecMeters),
    arrivedStopThreshold :: B.C f (Kernel.Prelude.Maybe Kernel.Types.Common.HighPrecMeters),
    arrivingPickupThreshold :: B.C f Kernel.Types.Common.HighPrecMeters,
    automaticRCActivationCutOff :: B.C f Kernel.Types.Common.Seconds,
    avgSpeedOfVehicle :: B.C f (Kernel.Prelude.Maybe Data.Aeson.Value),
    badDebtBatchSize :: B.C f Kernel.Prelude.Int,
    badDebtRescheduleTime :: B.C f Kernel.Types.Common.Seconds,
    badDebtSchedulerTime :: B.C f Kernel.Types.Common.Seconds,
    badDebtTimeThreshold :: B.C f Kernel.Prelude.Int,
    bankErrorExpiry :: B.C f Kernel.Types.Common.Seconds,
    bookAnyVehicleDowngradeLevel :: B.C f Kernel.Prelude.Int,
    cacheOfferListByDriverId :: B.C f Kernel.Prelude.Bool,
    canAddCancellationFee :: B.C f Kernel.Prelude.Bool,
    canDowngradeToHatchback :: B.C f Kernel.Prelude.Bool,
    canDowngradeToSedan :: B.C f Kernel.Prelude.Bool,
    canDowngradeToTaxi :: B.C f Kernel.Prelude.Bool,
    canSuvDowngradeToHatchback :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Bool),
    canSuvDowngradeToTaxi :: B.C f Kernel.Prelude.Bool,
    canSwitchToInterCity :: B.C f Kernel.Prelude.Bool,
    canSwitchToRental :: B.C f Kernel.Prelude.Bool,
    cancellationDistDiff :: B.C f Kernel.Prelude.Int,
    cancellationFee :: B.C f Kernel.Types.Common.HighPrecMoney,
    cancellationFeeDisputeLimit :: B.C f Kernel.Prelude.Int,
    cancellationTimeDiff :: B.C f Kernel.Types.Common.Seconds,
    checkImageExtractionForDashboard :: B.C f Kernel.Prelude.Bool,
    coinConversionRate :: B.C f Kernel.Types.Common.HighPrecMoney,
    coinExpireTime :: B.C f Kernel.Types.Common.Seconds,
    coinFeature :: B.C f Kernel.Prelude.Bool,
    considerDriversForSearch :: B.C f Kernel.Prelude.Bool,
    considerSpecialZoneRideChargesInFreeTrial :: B.C f Kernel.Prelude.Bool,
    considerSpecialZoneRidesForPlanCharges :: B.C f Kernel.Prelude.Bool,
    createdAt :: B.C f Kernel.Prelude.UTCTime,
    crossTravelCities :: B.C f [Kernel.Types.Beckn.City.City],
    currency :: B.C f (Kernel.Prelude.Maybe Kernel.Types.Common.Currency),
    defaultPopupDelay :: B.C f Kernel.Types.Common.Seconds,
    distanceUnit :: B.C f (Kernel.Prelude.Maybe Kernel.Types.Common.DistanceUnit),
    dlNumberVerification :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Bool),
    driverAutoPayExecutionTime :: B.C f Kernel.Types.Common.Seconds,
    driverAutoPayExecutionTimeFallBack :: B.C f Kernel.Types.Common.Seconds,
    driverAutoPayNotificationTime :: B.C f Kernel.Types.Common.Seconds,
    driverDistanceToPickupThresholdOnCancel :: B.C f Kernel.Types.Common.Meters,
    driverDistanceTravelledOnPickupThresholdOnCancel :: B.C f Kernel.Types.Common.Meters,
    driverFeeCalculationTime :: B.C f (Kernel.Prelude.Maybe Kernel.Types.Common.Seconds),
    driverFeeCalculatorBatchGap :: B.C f (Kernel.Prelude.Maybe Kernel.Types.Common.Seconds),
    driverFeeCalculatorBatchSize :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Int),
    driverFeeMandateExecutionBatchSize :: B.C f Kernel.Prelude.Int,
    driverFeeMandateNotificationBatchSize :: B.C f Kernel.Prelude.Int,
    driverFeeOverlaySendingTimeLimitInDays :: B.C f Kernel.Prelude.Int,
    driverFeeRetryThresholdConfig :: B.C f Kernel.Prelude.Int,
    driverLocationAccuracyBuffer :: B.C f Kernel.Types.Common.Meters,
    driverPaymentCycleBuffer :: B.C f Kernel.Types.Common.Seconds,
    driverPaymentCycleDuration :: B.C f Kernel.Types.Common.Seconds,
    driverPaymentCycleStartTime :: B.C f Kernel.Types.Common.Seconds,
    driverPaymentReminderInterval :: B.C f Kernel.Types.Common.Seconds,
    driverSmsReceivingLimit :: B.C f (Kernel.Prelude.Maybe Data.Aeson.Value),
    driverTimeSpentOnPickupThresholdOnCancel :: B.C f Kernel.Types.Common.Seconds,
    dropLocThreshold :: B.C f Kernel.Types.Common.Meters,
    dummyFromLocation :: B.C f (Kernel.Prelude.Maybe Data.Aeson.Value),
    dummyToLocation :: B.C f (Kernel.Prelude.Maybe Data.Aeson.Value),
    editLocDriverPermissionNeeded :: B.C f Kernel.Prelude.Bool,
    editLocTimeThreshold :: B.C f Kernel.Types.Common.Seconds,
    emailOtpConfig :: B.C f (Kernel.Prelude.Maybe Email.Types.EmailOTPConfig),
    enableDashboardSms :: B.C f Kernel.Prelude.Bool,
    enableFaceVerification :: B.C f Kernel.Prelude.Bool,
    enableTollCrossedNotifications :: B.C f Kernel.Prelude.Bool,
    enableUdfForOffers :: B.C f Kernel.Prelude.Bool,
    fakeOtpEmails :: B.C f [Kernel.Prelude.Text],
    fakeOtpMobileNumbers :: B.C f [Kernel.Prelude.Text],
    fareRecomputeDailyExtraKmsThreshold :: B.C f Kernel.Types.Common.HighPrecMeters,
    fareRecomputeWeeklyExtraKmsThreshold :: B.C f Kernel.Types.Common.HighPrecMeters,
    fcmServiceAccount :: B.C f Kernel.Prelude.Text,
    fcmTokenKeyPrefix :: B.C f Kernel.Prelude.Text,
    fcmUrl :: B.C f Kernel.Prelude.Text,
    freeTrialDays :: B.C f Kernel.Prelude.Int,
    includeDriverCurrentlyOnRide :: B.C f Kernel.Prelude.Bool,
    isAvoidToll :: B.C f Kernel.Prelude.Bool,
    isPlanMandatory :: B.C f Kernel.Prelude.Bool,
    kaptureDisposition :: B.C f Kernel.Prelude.Text,
    kaptureQueue :: B.C f Kernel.Prelude.Text,
    languagesToBeTranslated :: B.C f [Kernel.External.Types.Language],
    lastNdaysToCheckForPayoutOrderStatus :: B.C f Kernel.Prelude.Int,
    mandateExecutionRescheduleInterval :: B.C f Kernel.Types.Common.Seconds,
    mandateNotificationRescheduleInterval :: B.C f Kernel.Types.Common.Seconds,
    mandateValidity :: B.C f Kernel.Prelude.Int,
    mediaFileSizeUpperLimit :: B.C f Kernel.Prelude.Int,
    mediaFileUrlPattern :: B.C f Kernel.Prelude.Text,
    merchantId :: B.C f Kernel.Prelude.Text,
    merchantOperatingCityId :: B.C f Kernel.Prelude.Text,
    minLocationAccuracy :: B.C f Kernel.Prelude.Double,
    minRidesForCancellationScore :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Int),
    minRidesToUnlist :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Int),
    nightSafetyEndTime :: B.C f Kernel.Types.Common.Seconds,
    nightSafetyRouteDeviationThreshold :: B.C f Kernel.Types.Common.Meters,
    nightSafetyStartTime :: B.C f Kernel.Types.Common.Seconds,
    notificationRetryCountThreshold :: B.C f Kernel.Prelude.Int,
    notificationRetryEligibleErrorCodes :: B.C f [Kernel.Prelude.Text],
    notificationRetryTimeGap :: B.C f Kernel.Types.Common.Seconds,
    numOfCancellationsAllowed :: B.C f Kernel.Prelude.Int,
    onboardingRetryTimeInHours :: B.C f Kernel.Prelude.Int,
    onboardingTryLimit :: B.C f Kernel.Prelude.Int,
    openMarketUnBlocked :: B.C f Kernel.Prelude.Bool,
    orderAndNotificationStatusCheckFallBackTime :: B.C f Kernel.Types.Common.Seconds,
    orderAndNotificationStatusCheckTime :: B.C f Kernel.Types.Common.Seconds,
    orderAndNotificationStatusCheckTimeLimit :: B.C f Kernel.Types.Common.Seconds,
    overlayBatchSize :: B.C f Kernel.Prelude.Int,
    pastDaysRideCounter :: B.C f Kernel.Prelude.Int,
    payoutBatchLimit :: B.C f Kernel.Prelude.Int,
    pickupLocThreshold :: B.C f Kernel.Types.Common.Meters,
    placeNameCacheExpiryDays :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Int),
    popupDelayToAddAsPenalty :: B.C f (Kernel.Prelude.Maybe Kernel.Types.Common.Seconds),
    ratingAsDecimal :: B.C f Kernel.Prelude.Bool,
    rcLimit :: B.C f Kernel.Prelude.Int,
    recomputeIfPickupDropNotOutsideOfThreshold :: B.C f Kernel.Prelude.Bool,
    referralLinkPassword :: B.C f Kernel.Prelude.Text,
    refillVehicleModel :: B.C f Kernel.Prelude.Bool,
    rideTimeEstimatedThreshold :: B.C f Kernel.Types.Common.Seconds,
    routeDeviationThreshold :: B.C f Kernel.Types.Common.Meters,
    scheduleRideBufferTime :: B.C f Kernel.Types.Common.Seconds,
    searchRepeatLimit :: B.C f Kernel.Prelude.Int,
    snapToRoadConfidenceThreshold :: B.C f Kernel.Prelude.Double,
    specialDrivers :: B.C f [Kernel.Prelude.Text],
    specialLocationTags :: B.C f [Kernel.Prelude.Text],
    specialZoneBookingOtpExpiry :: B.C f Kernel.Prelude.Int,
    stepFunctionToConvertCoins :: B.C f Kernel.Prelude.Int,
    subscription :: B.C f Kernel.Prelude.Bool,
    subscriptionStartTime :: B.C f Kernel.Prelude.UTCTime,
    thresholdCancellationPercentageToUnlist :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Int),
    thresholdCancellationScore :: B.C f (Kernel.Prelude.Maybe Kernel.Prelude.Int),
    timeDiffFromUtc :: B.C f Kernel.Types.Common.Seconds,
    toNotifyDriverForExtraKmsLimitExceed :: B.C f Kernel.Prelude.Bool,
    updateNotificationStatusBatchSize :: B.C f Kernel.Prelude.Int,
    updateOrderStatusBatchSize :: B.C f Kernel.Prelude.Int,
    updatePayoutStatusBatchSize :: B.C f Kernel.Prelude.Int,
    updatedAt :: B.C f Kernel.Prelude.UTCTime,
    upwardsRecomputeBuffer :: B.C f Kernel.Types.Common.HighPrecMeters,
    useOfferListCache :: B.C f Kernel.Prelude.Bool,
    useSilentFCMForForwardBatch :: B.C f Kernel.Prelude.Bool,
    useWithSnapToRoadFallback :: B.C f Kernel.Prelude.Bool,
    variantsToEnableForSubscription :: B.C f [Domain.Types.Vehicle.Variant],
    volunteerSmsSendingLimit :: B.C f (Kernel.Prelude.Maybe Data.Aeson.Value)
  }
  deriving (Generic, B.Beamable)

instance B.Table TransporterConfigT where
  data PrimaryKey TransporterConfigT f = TransporterConfigId (B.C f Kernel.Prelude.Text) deriving (Generic, B.Beamable)
  primaryKey = TransporterConfigId . merchantOperatingCityId

type TransporterConfig = TransporterConfigT Identity

$(enableKVPG ''TransporterConfigT ['merchantOperatingCityId] [])

$(mkTableInstancesWithTModifier ''TransporterConfigT "transporter_config" [("automaticRCActivationCutOff", "automatic_r_c_activation_cut_off")])

$(Domain.Types.UtilsTH.mkCacParseInstance ''TransporterConfigT)
