{-
 Copyright 2022-23, Juspay India Pvt Ltd

 This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License

 as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program

 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY

 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. You should have received a copy of

 the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
-}

module Domain.Types.Person.API where

import qualified Domain.Types.Merchant as DMerchant
import Domain.Types.Person.Type
import qualified Domain.Types.Role as DRole
import Kernel.Prelude
import Kernel.Types.Beckn.City as City
import Kernel.Types.Id

data PersonAPIEntity = PersonAPIEntity
  { id :: Id Person,
    firstName :: Text,
    lastName :: Text,
    role :: DRole.RoleAPIEntity,
    email :: Maybe Text,
    mobileNumber :: Text,
    mobileCountryCode :: Text,
    availableMerchants :: [ShortId DMerchant.Merchant],
    availableCitiesForMerchant :: Maybe [AvailableCitiesForMerchant],
    registeredAt :: UTCTime,
    verified :: Maybe Bool,
    receiveNotification :: Maybe Bool
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

data AvailableCitiesForMerchant = AvailableCitiesForMerchant
  { merchantShortId :: ShortId DMerchant.Merchant,
    operatingCity :: [City.City]
  }
  deriving (Show, Generic, FromJSON, ToJSON, ToSchema)

makePersonAPIEntity :: DecryptedPerson -> DRole.Role -> [ShortId DMerchant.Merchant] -> Maybe [AvailableCitiesForMerchant] -> PersonAPIEntity
makePersonAPIEntity Person {..} personRole availableMerchants availableCitiesForMerchant =
  PersonAPIEntity
    { registeredAt = createdAt,
      role = DRole.mkRoleAPIEntity personRole,
      ..
    }
