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

package models.submitShortageExcess

import base.SpecBase
import fixtures.{SubmitShortageExcessFixtures, TraderModelFixtures}
import models.ChooseShortageExcessItem.{Excess, Shortage}
import models.HowGiveInformation.{Choose, Whole}
import pages.individualItems._
import pages.{GiveInformationMovementPage, HowGiveInformationPage, WhenReceiveShortageExcessPage}
import play.api.libs.json.Json

class SubmitShortageExcessModelSpec extends SpecBase
  with SubmitShortageExcessFixtures
  with TraderModelFixtures {

  val movementResponse = getMovementResponseModel.copy(consigneeTrader = Some(traderModel))

  "SubmitShortageExcessModel" - {

    "for the Whole Movement flow" - {

      val wholeMovementJourney = emptyUserAnswers
        .set(WhenReceiveShortageExcessPage, testDateOfWhenReceiveShortageOrExcess)
        .set(HowGiveInformationPage, Whole)
        .set(GiveInformationMovementPage, "reason")

      val actualModel = SubmitShortageExcessModel.apply(movementResponse)(wholeMovementJourney)

      "can be constructed from UserAnswers" in {
        actualModel mustBe submitExplainShortageExcessWholeMovementModel
      }

      "can write to JSON as expected" in {
        Json.toJson(actualModel) mustBe submitExplainShortageExcessWholeMovementJson
      }
    }

    "can be constructed from UserAnswers for a Individual Items flow" in {

      val individualItemsJourney = emptyUserAnswers
        .set(WhenReceiveShortageExcessPage, testDateOfWhenReceiveShortageOrExcess)
        .set(HowGiveInformationPage, Choose)
        .set(SelectItemPage(item1.itemUniqueReference), item1.itemUniqueReference)
        .set(ChooseShortageExcessItemPage(item1.itemUniqueReference), Shortage)
        .set(ItemAmountPage(item1.itemUniqueReference), Some(BigDecimal(10.1)))
        .set(GiveInformationItemPage(item1.itemUniqueReference), "reason")
        .set(CheckAnswersItemPage(item1.itemUniqueReference), true)
        .set(SelectItemPage(item2.itemUniqueReference), item2.itemUniqueReference)
        .set(ChooseShortageExcessItemPage(item2.itemUniqueReference), Excess)
        .set(ItemAmountPage(item2.itemUniqueReference), None)
        .set(GiveInformationItemPage(item2.itemUniqueReference), "reason")
        .set(CheckAnswersItemPage(item2.itemUniqueReference), true)

      val actualModel = SubmitShortageExcessModel.apply(movementResponse)(individualItemsJourney)

      actualModel mustBe submitExplainShortageExcessIndividualItemsModel
    }
  }
}
