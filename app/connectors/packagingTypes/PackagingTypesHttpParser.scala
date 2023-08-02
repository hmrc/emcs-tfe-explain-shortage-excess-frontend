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

package connectors.packagingTypes

import connectors.BaseConnectorUtils
import models.requests.PackagingTypesRequest
import models.response.referenceData.PackagingTypesResponse
import models.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.OK
import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait PackagingTypesHttpParser extends BaseConnectorUtils[PackagingTypesResponse] {


  implicit val reads: Reads[PackagingTypesResponse] = PackagingTypesResponse.reads

  def http: HttpClient

  implicit object PackagingTypesReads extends HttpReads[Either[ErrorResponse, PackagingTypesResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, PackagingTypesResponse] = {
      response.status match {
        case OK =>
          response.validateJson match {
            case Some(valid) => Right(valid)
            case None =>
              logger.warn(s"[read] Bad JSON response from emcs-tfe-reference-data")
              Left(JsonValidationError)
          }
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe-reference-data: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def post(url: String, body: PackagingTypesRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, writes: Writes[PackagingTypesRequest]): Future[Either[ErrorResponse, PackagingTypesResponse]] =
    http.POST[PackagingTypesRequest, Either[ErrorResponse, PackagingTypesResponse]](url, body)(writes, PackagingTypesReads, hc, ec)
}
