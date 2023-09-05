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

package fixtures.audit

import fixtures.{BaseFixtures, SubmitShortageExcessFixtures}
import models.ChooseShortageExcessItem.{Excess, Shortage}
import models.UnexpectedDownstreamResponseError
import models.audit.SubmitShortageExcessAuditModel
import models.common.SubmitterType.Consignee
import play.api.libs.json.{JsValue, Json}

trait SubmitShortageExcessAuditModelFixtures extends BaseFixtures with SubmitShortageExcessFixtures {

  val submitShortageOrExcessAuditSuccessful: SubmitShortageExcessAuditModel = SubmitShortageExcessAuditModel(
    credentialId = testCredId,
    internalId = testInternalId,
    ern = testErn,
    submissionRequest = submitExplainShortageExcessWholeMovementModel,
    submissionResponse = Right(submitShortageOrExcessResponse)
  )

  val submitShortageOrExcessAuditSuccessfulJSON: JsValue = Json.obj(
    "credentialId" -> testCredId,
    "internalId" -> testInternalId,
    "ern" -> testErn,
    "arc" -> testArc,
    "sequenceNumber" -> 1,
    "submitterType" -> Consignee.toString,
    "consigneeTrader" -> traderModelJson,
    "consignorTrader" -> traderModelJson,
    "wholeMovement" -> Json.obj(
      "dateOfAnalysis" -> testDateOfWhenReceiveShortageOrExcess.toString,
      "globalExplanation" -> "reason"
    ),
    "status" -> "success",
    "receipt" -> testConfirmationReference,
    "receiptDate" -> testReceiptDateTime
  )

  val submitShortageOrExcessAuditFailed: SubmitShortageExcessAuditModel = SubmitShortageExcessAuditModel(
    credentialId = testCredId,
    internalId = testInternalId,
    ern = testErn,
    submissionRequest = submitExplainShortageExcessIndividualItemsModel,
    submissionResponse = Left(UnexpectedDownstreamResponseError)
  )

  val submitShortageOrExcessAuditFailedJSON: JsValue = Json.obj(
    "credentialId" -> testCredId,
    "internalId" -> testInternalId,
    "ern" -> testErn,
    "arc" -> testArc,
    "sequenceNumber" -> 1,
    "submitterType" -> Consignee.toString,
    "consigneeTrader" -> traderModelJson,
    "consignorTrader" -> traderModelJson,
    "individualItems" -> Json.arr(
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
    ),
    "status" -> "failed",
    "failedMessage" -> "Unexpected downstream response status"
  )
}
