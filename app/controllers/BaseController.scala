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

package controllers

import models.Enumerable
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import pages.QuestionPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.Format
import play.api.mvc.Result
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

trait BaseController extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  implicit lazy val ec: ExecutionContext = controllerComponents.executionContext

  def fillForm[A](page: QuestionPage[A], form: Form[A])
                 (implicit request: DataRequest[_], format: Format[A]): Form[A] =
    request.userAnswers.get(page).fold(form)(form.fill)

  def withMovementItemAsync(idx: Int)(f: MovementItem => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.movementDetails.item(idx) match {
      case Some(item) => f(item)
      case None => Future.successful(Redirect(routes.SelectItemController.onPageLoad(request.ern, request.arc).url))
    }

}
