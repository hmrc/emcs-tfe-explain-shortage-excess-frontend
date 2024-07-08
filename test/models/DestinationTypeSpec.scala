/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import base.SpecBase
import models.DestinationType._

class DestinationTypeSpec extends SpecBase {

  "DestinationType" - {

    "have the correct codes" in {
      TaxWarehouse.toString mustBe "1"
      RegisteredConsignee.toString mustBe "2"
      TemporaryRegisteredConsignee.toString mustBe "3"
      DirectDelivery.toString mustBe "4"
      ExemptedOrganisations.toString mustBe "5"
      Export.toString mustBe "6"
      UnknownDestination.toString mustBe "8"
      CertifiedConsignee.toString mustBe "9"
      TemporaryCertifiedConsignee.toString mustBe "10"
      ReturnToThePlaceOfDispatchOfTheConsignor.toString mustBe "11"
    }
  }
}
