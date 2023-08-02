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

import forms.BaseTextAreaFormProvider
import models.requests.DataRequest
import models.{Mode, UserAnswers}
import navigation.BaseNavigator
import pages.QuestionPage
import play.api.data.{Form, FormError}
import play.api.libs.json.{Format, Reads}
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import services.UserAnswersService
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait BaseNavigationController extends BaseController with Logging {

  val userAnswersService: UserAnswersService
  val navigator: BaseNavigator

  def withAnswer[A](page: QuestionPage[A])(f: A => Future[Result])(implicit request: DataRequest[_], reads: Reads[A]): Future[Result] =
    request.userAnswers.get(page) match {
      case Some(value) => f(value)
      case None =>
        logger.warn(s"Failed to retrieve expected answer for page: $page on uri: ${request.uri}")
        Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad(request.ern, request.arc)))
    }

  def saveAndRedirect[A](page: QuestionPage[A], answer: A, currentAnswers: UserAnswers, mode: Mode)
                        (implicit hc: HeaderCarrier, format: Format[A]): Future[Result] =
    save(page, answer, currentAnswers).map { updatedAnswers =>
      Redirect(navigator.nextPage(page, mode, updatedAnswers))
    }

  def saveAndRedirect[A](page: QuestionPage[A], answer: A, mode: Mode)
                        (implicit request: DataRequest[_], format: Format[A]): Future[Result] =
    save(page, answer).map { updatedAnswers =>
      Redirect(navigator.nextPage(page, mode, updatedAnswers))
    }

  private def save[A](page: QuestionPage[A], answer: A, currentAnswers: UserAnswers)(implicit hc: HeaderCarrier, format: Format[A]): Future[UserAnswers] =
    if (currentAnswers.get[A](page).contains(answer)) {
      Future.successful(currentAnswers)
    } else {
      for {
        updatedAnswers <- Future.successful(currentAnswers.set(page, answer))
        _ <- userAnswersService.set(updatedAnswers)
      } yield updatedAnswers
    }

  private def save[A](page: QuestionPage[A], answer: A)
                     (implicit request: DataRequest[_], format: Format[A]): Future[UserAnswers] =
    save(page, answer, request.userAnswers)

  def submitAndTrimWhitespaceFromTextarea[PageType](
                                                     page: QuestionPage[PageType],
                                                     formProvider: BaseTextAreaFormProvider[PageType]
                                                   )(
                                                     formWithErrorsView: Form[PageType] => Future[Result],
                                                     successFunction: PageType => Future[Result]
                                                   )(implicit request: DataRequest[_]): Future[Result] = {
    Try {
      val trimmedFormValues: Map[String, Seq[String]] = request.body.asInstanceOf[AnyContentAsFormUrlEncoded].data.map {
        case (k, v) => (k, v.map(_.trim))
      }

      formProvider(page).bindFromRequest(trimmedFormValues).fold(
        formWithErrors =>
          formWithErrorsView(formWithErrors),
        value =>
          successFunction(value)
      )
    } match {
      case Failure(exception) =>
        logger.warn(exception.getMessage)
        formWithErrorsView(formProvider(page).withError(FormError(formProvider.textAreaName, s"$page.error.required")))
      case Success(value) => value
    }
  }
}
