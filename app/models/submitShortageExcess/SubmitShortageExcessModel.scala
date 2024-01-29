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

import models.UserAnswers
import models.common.{SubmitterType, TraderModel}
import models.response.emcsTfe.GetMovementResponse
import models.submitShortageExcess.AnalysisModel.mandatoryPage
import pages.WhenReceiveShortageExcessPage
import play.api.libs.json.{Json, Writes}
import utils.JsonOptionFormatter

import java.time.LocalDate

case class SubmitShortageExcessModel(submitterType: SubmitterType,
                                     arc: String,
                                     sequenceNumber: Int,
                                     consigneeTrader: Option[TraderModel],
                                     consignorTrader: Option[TraderModel],
                                     wholeMovement: Option[AnalysisModel],
                                     individualItems: Option[Seq[BodyAnalysisModel]],
                                     dateOfAnalysis: LocalDate)

object SubmitShortageExcessModel extends JsonOptionFormatter {

  implicit val writes: Writes[SubmitShortageExcessModel] = Writes { model =>
    jsonObjNoNulls(
      "attributes" -> Json.obj(
        "submitterType" -> Json.toJson(model.submitterType)
      ),
      "exciseMovement" -> Json.obj(
        "arc" -> model.arc,
        "sequenceNumber" -> model.sequenceNumber
      ),
      "consigneeTrader" -> Json.toJson(model.consigneeTrader),
      "consignorTrader" -> Json.toJson(model.consignorTrader),
      "analysis" -> Json.toJson(model.wholeMovement),
      "bodyAnalysis" -> Json.toJson(model.individualItems)
    )
  }

  def apply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): SubmitShortageExcessModel =
    SubmitShortageExcessModel(
      submitterType = SubmitterType(movementDetails),
      arc = movementDetails.arc,
      sequenceNumber = movementDetails.sequenceNumber,
      consigneeTrader = movementDetails.consigneeTrader,
      consignorTrader = Some(movementDetails.consignorTrader),
      wholeMovement = AnalysisModel(),
      individualItems = BodyAnalysisModel(movementDetails),
      dateOfAnalysis = mandatoryPage(WhenReceiveShortageExcessPage)
    )
}
