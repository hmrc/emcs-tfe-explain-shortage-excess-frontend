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

import base.SpecBase
import forms.ContinueDraftFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import pages.{ConfirmationPage, WhenReceiveShortageExcessPage}
import play.api.http.Status.SEE_OTHER
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.ContinueDraftView

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers).overrides(
      bind[UserAnswersService].toInstance(mockUserAnswersService)
    ).build()

    lazy val view = application.injector.instanceOf[ContinueDraftView]

    lazy val form = application.injector.instanceOf[ContinueDraftFormProvider].apply()

    implicit val msgs: Messages = messages(application)
  }

  "IndexController" - {

    ".onPageLoad()" - {

      "when existing UserAnswers do not exist" - {

        "must initialise answers and redirect to when receive shortage or excess page" in new Fixture() {
          running(application) {

            MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.WhenReceiveShortageExcessController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }
      }

      "when ConfirmationPage UserAnswers exist" - {

        "must Initialise the UserAnswers and redirect to DelayType" in {

          val userAnswers = emptyUserAnswers.set(ConfirmationPage, testConfirmationDetails)

          val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          ).build()

          MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

          running(application) {
            val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.WhenReceiveShortageExcessController.onPageLoad(testErn, testArc, NormalMode).url)
          }
        }
      }

      "when existing UserAnswers exist" - {

        "must redirect to the DraftController" in
          new Fixture(Some(emptyUserAnswers.set(WhenReceiveShortageExcessPage, testDateOfWhenReceiveShortageOrExcess))) {

            running(application) {

              val request = FakeRequest(GET, routes.IndexController.onPageLoad(testErn, testArc).url)
              val result = route(application, request).value

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(routes.DraftController.onPageLoad(testErn, testArc).url)
            }
          }
      }

    }

  }
}
