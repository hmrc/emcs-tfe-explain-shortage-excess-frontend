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

package fixtures

import models.ChooseShortageExcessItem.{Excess, Shortage}
import models.common.SubmitterType.Consignee
import models.response.emcsTfe._
import models.submitShortageExcess.{AnalysisModel, BodyAnalysisModel, SubmitShortageExcessModel}
import play.api.libs.json.Json

trait SubmitShortageExcessFixtures extends TraderModelFixtures with GetMovementResponseFixtures {
  _: BaseFixtures =>

  val submitExplainShortageExcessWholeMovementModel: SubmitShortageExcessModel = SubmitShortageExcessModel(
    submitterType = Consignee,
    arc = testArc,
    sequenceNumber = 1,
    consigneeTrader = Some(traderModel),
    consignorTrader = Some(traderModel),
    wholeMovement = Some(AnalysisModel(
      dateOfAnalysis = testDateOfWhenReceiveShortageOrExcess,
      globalExplanation = "reason"
    )),
    individualItems = None
  )

  val submitExplainShortageExcessWholeMovementJson = Json.obj(fields =
    "attributes" -> Json.obj(fields =
      "submitterType" -> Consignee.toString
    ),
    "exciseMovement" -> Json.obj(fields =
      "arc" -> testArc,
      "sequenceNumber" -> 1
    ),
    "consigneeTrader" -> traderModelJson,
    "consignorTrader" -> traderModelJson,
    "analysis" -> Json.obj(
      "dateOfAnalysis" -> testDateOfWhenReceiveShortageOrExcess.toString,
      "globalExplanation" -> "reason"
    )
  )

  val submitExplainShortageExcessIndividualItemsModel: SubmitShortageExcessModel = SubmitShortageExcessModel(
    submitterType = Consignee,
    arc = testArc,
    sequenceNumber = 1,
    consigneeTrader = Some(traderModel),
    consignorTrader = Some(traderModel),
    wholeMovement = None,
    individualItems = Some(Seq(
      BodyAnalysisModel(
        exciseProductCode = item1.productCode,
        bodyRecordUniqueReference = item1.itemUniqueReference,
        explanation = "reason",
        actualQuantity = Some(10.1),
        whatWasWrong = Shortage
      ),
      BodyAnalysisModel(
        exciseProductCode = item2.productCode,
        bodyRecordUniqueReference = item2.itemUniqueReference,
        explanation = "reason",
        actualQuantity = None,
        whatWasWrong = Excess
      )
    ))
  )

  val submitExplainShortageExcessIndividualItemsJson = Json.obj(fields =
    "attributes" -> Json.obj(fields =
      "submitterType" -> Consignee.toString
    ),
    "exciseMovement" -> Json.obj(fields =
      "arc" -> testArc,
      "sequenceNumber" -> 1
    ),
    "consigneeTrader" -> traderModelJson,
    "consignorTrader" -> traderModelJson,
    "bodyAnalysis" -> Json.arr(
      Json.obj(
        "exciseProductCode" -> item1.productCode,
        "bodyRecordUniqueReference" -> item1.itemUniqueReference,
        "explanation" -> "reason",
        "actualQuantity" -> 10.1,
        "whatWasWrong" -> Shortage.toString
      ),
      Json.obj(
        "exciseProductCode" -> item2.productCode,
        "bodyRecordUniqueReference" -> item2.itemUniqueReference,
        "explanation" -> "reason",
        "whatWasWrong" -> Excess.toString
      )
    )
  )

  val submitShortageOrExcessChRISResponseJson = Json.obj(
    "receipt" -> testConfirmationReference,
    "receiptDate" -> testReceiptDateTime.toString
  )

  val submitShortageOrExcessChRISResponseModel: SubmitShortageExcessResponse = SubmitShortageExcessResponse(
    receipt = testConfirmationReference,
    downstreamService = "ChRIS"
  )

  val submitShortageOrExcessEISResponseJson = Json.obj(
    "message" -> testConfirmationReference
  )

  val submitShortageOrExcessEISResponseModel: SubmitShortageExcessResponse = SubmitShortageExcessResponse(
    receipt = testConfirmationReference,
    downstreamService = "EIS"
  )
}
