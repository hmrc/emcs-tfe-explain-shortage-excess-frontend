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
import models.{ErrorResponse, UserAnswers}
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersConnector @Inject()(val http: HttpClientV2,
                                     config: AppConfig) extends UserAnswersHttpParsers {

  override implicit val reads: Reads[UserAnswers] = UserAnswers.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def get(ern: String, arc: String)
         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Option[UserAnswers]]] = {
    http
      .get(url"$baseUrl/user-answers/explain-shortage-or-excess/$ern/$arc")
      .execute[Either[ErrorResponse, Option[UserAnswers]]](GetUserAnswersReads, ec)
  }

  def put(userAnswers: UserAnswers)
         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, UserAnswers]] = {
    http
      .put(url"$baseUrl/user-answers/explain-shortage-or-excess/${userAnswers.ern}/${userAnswers.arc}")
      .withBody(Json.toJson(userAnswers))
      .execute[Either[ErrorResponse, UserAnswers]](PutUserAnswersReads, ec)
  }

  def delete(ern: String, arc: String)
            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] = {
    http
      .delete(url"$baseUrl/user-answers/explain-shortage-or-excess/$ern/$arc")
      .execute[Either[ErrorResponse, Boolean]](DeleteUserAnswersReads, ec)
  }

}
