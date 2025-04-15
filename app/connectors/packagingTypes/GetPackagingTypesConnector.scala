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

import config.AppConfig
import models.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import models.requests.PackagingTypesRequest
import models.response.referenceData.PackagingTypesResponse
import play.api.libs.json.JsResultException
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPackagingTypesConnector @Inject()(val http: HttpClientV2,
                                           config: AppConfig) extends PackagingTypesHttpParser {

  lazy val baseUrl: String = config.referenceDataBaseUrl

  def getPackagingTypes(request: PackagingTypesRequest)
                          (implicit headerCarrier: HeaderCarrier,
                           executionContext: ExecutionContext): Future[Either[ErrorResponse, PackagingTypesResponse]] = {
    post(url"$baseUrl/oracle/packaging-types", request)
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getPackagingTypes] Bad JSON response from emcs-tfe-reference-data: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getPackagingTypes] Unexpected error from reference-data: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
