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

package models.audit

import models.ErrorResponse
import models.submitShortageExcess.SubmitShortageExcessModel
import models.response.emcsTfe.SubmitShortageExcessResponse
import play.api.libs.json.{JsValue, Json}
import utils.JsonOptionFormatter

case class SubmitShortageExcessAuditModel(
                                             credentialId: String,
                                             internalId: String,
                                             ern: String,
                                             submissionRequest: SubmitShortageExcessModel,
                                             submissionResponse: Either[ErrorResponse, SubmitShortageExcessResponse]
                                           ) extends AuditModel with JsonOptionFormatter {

  override val auditType: String = "explainShortageOrExcessSubmission"

  override val detail: JsValue = jsonObjNoNulls(fields =
    "credentialId" -> credentialId,
    "internalId" -> internalId,
    "ern" -> ern,
    "arc" -> submissionRequest.arc,
    "sequenceNumber" -> submissionRequest.sequenceNumber,
    "submitterType" -> submissionRequest.submitterType,
    "consigneeTrader" -> Json.toJson(submissionRequest.consigneeTrader),
    "consignorTrader" -> Json.toJson(submissionRequest.consignorTrader),
    "wholeMovement" -> Json.toJson(submissionRequest.wholeMovement),
    "individualItems" -> Json.toJson(submissionRequest.individualItems)
  ) ++ {
    submissionResponse match {
      case Right(success) =>
        Json.obj(fields =
          "status" -> "success",
          "receipt" -> success.receipt,
          "receiptDate" -> success.receiptDate
        )
      case Left(failedMessage) =>
        Json.obj(fields =
          "status" -> "failed",
          "failedMessage" -> failedMessage.message
        )
    }
  }
}