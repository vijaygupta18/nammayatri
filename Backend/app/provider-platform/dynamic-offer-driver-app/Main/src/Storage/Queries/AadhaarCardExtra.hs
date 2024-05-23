{-# OPTIONS_GHC -Wno-orphans #-}
{-# OPTIONS_GHC -Wno-unused-imports #-}

module Storage.Queries.AadhaarCardExtra where

import qualified Domain.Types.AadhaarCard as Domain
import Kernel.Beam.Functions
import Kernel.External.Encryption
import Kernel.Prelude
import Kernel.Types.Error
import Kernel.Utils.Common (CacheFlow, EsqDBFlow, MonadFlow, fromMaybeM, getCurrentTime)
import Sequelize as Se
import Storage.Beam.AadhaarCard as Beam
import Storage.Queries.OrphanInstances.AadhaarCard

upsertAadhaarRecord :: (EsqDBFlow m r, MonadFlow m, CacheFlow m r) => Domain.AadhaarCard -> m ()
upsertAadhaarRecord a@Domain.AadhaarCard {..} =
  findOneWithKV [Se.Is Beam.driverId $ Se.Eq driverId.getId] >>= \case
    Just _ ->
      updateOneWithKV
        [ Se.Set Beam.consentTimestamp consentTimestamp,
          Se.Set Beam.dateOfBirth dateOfBirth,
          Se.Set Beam.nameOnCard nameOnCard,
          Se.Set Beam.aadhaarBackImageId $ aadhaarBackImageId <&> (.getId),
          Se.Set Beam.aadhaarFrontImageId $ aadhaarFrontImageId <&> (.getId),
          Se.Set Beam.maskedAadhaarNumber maskedAadhaarNumber,
          Se.Set Beam.address address,
          Se.Set Beam.updatedAt updatedAt,
          Se.Set Beam.verificationStatus verificationStatus,
          Se.Set Beam.aadhaarNumberHash aadhaarNumberHash,
          Se.Set Beam.driverGender driverGender,
          Se.Set Beam.driverImage driverImage,
          Se.Set Beam.driverImagePath driverImagePath
        ]
        [Se.Is Beam.driverId $ Se.Eq driverId.getId]
    Nothing -> createWithKV a
