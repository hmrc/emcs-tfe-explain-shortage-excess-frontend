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

package connectors.emcsTfe

import config.AppConfig
import models.ErrorResponse
import models.submitShortageExcess.SubmitShortageExcessModel
import models.response.emcsTfe.SubmitShortageExcessResponse
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitShortageExcessConnector @Inject()(val http: HttpClient,
                                              config: AppConfig) extends EmcsTfeHttpParser[SubmitShortageExcessResponse] {

  override implicit val reads: Reads[SubmitShortageExcessResponse] = SubmitShortageExcessResponse.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl
  def submit(exciseRegistrationNumber: String, submitShortageOrExcessModel: SubmitShortageExcessModel)
            (implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, SubmitShortageExcessResponse]] =
    post(s"$baseUrl/explain-shortage-excess/$exciseRegistrationNumber/${submitShortageOrExcessModel.arc}", submitShortageOrExcessModel)

}
