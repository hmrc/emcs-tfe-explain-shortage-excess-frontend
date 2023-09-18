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

import cats.syntax.traverse._
import models.response.emcsTfe.GetMovementResponse
import models.submitShortageExcess.AnalysisModel.mandatoryPage
import models.{ChooseShortageExcessItem, HowGiveInformation, UserAnswers}
import pages.HowGiveInformationPage
import pages.individualItems.{ChooseShortageExcessItemPage, GiveInformationItemPage, ItemAmountPage}
import play.api.libs.json.{Json, OFormat}
import utils.JsonOptionFormatter._

case class BodyAnalysisModel(exciseProductCode: String,
                             bodyRecordUniqueReference: Int,
                             explanation: String,
                             actualQuantity: Option[BigDecimal],
                             whatWasWrong: ChooseShortageExcessItem)

object BodyAnalysisModel {
  implicit val fmt: OFormat[BodyAnalysisModel] = Json.format

  def apply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): Option[Seq[BodyAnalysisModel]] =
    mandatoryPage(HowGiveInformationPage) match {
      case HowGiveInformation.Whole => None
      case HowGiveInformation.Choose =>
        userAnswers.completedItems.traverse { item =>
          movementDetails.item(item.itemUniqueReference).map { itemDetails =>

            BodyAnalysisModel(
              exciseProductCode = itemDetails.productCode,
              bodyRecordUniqueReference = item.itemUniqueReference,
              explanation = mandatoryPage(GiveInformationItemPage(item.itemUniqueReference)),
              actualQuantity = userAnswers.get(ItemAmountPage(item.itemUniqueReference)).flatten,
              whatWasWrong = userAnswers.get[ChooseShortageExcessItem](ChooseShortageExcessItemPage(item.itemUniqueReference)).get
            )
          }
        }
    }
}