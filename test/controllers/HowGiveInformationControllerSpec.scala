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
import forms.HowGiveInformationFormProvider
import mocks.services.MockUserAnswersService
import models.{HowGiveInformation, NormalMode}
import navigation.{FakeNavigator, Navigator}
import pages.HowGiveInformationPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.HowGiveInformationView

import scala.concurrent.Future

class HowGiveInformationControllerSpec extends SpecBase with MockUserAnswersService {

  def onwardRoute = Call("GET", "/foo")

  lazy val howGiveInformationRoute = routes.HowGiveInformationController.onPageLoad(testErn, testArc, NormalMode).url

  val formProvider = new HowGiveInformationFormProvider()
  val form = formProvider()

  val submitCall = routes.HowGiveInformationController.onSubmit(testErn, testArc, NormalMode)

  "HowGiveInformation Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, howGiveInformationRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowGiveInformationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(HowGiveInformationPage, HowGiveInformation.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, howGiveInformationRoute)

        val view = application.injector.instanceOf[HowGiveInformationView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(HowGiveInformation.values.head), submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[UserAnswersService].toInstance(mockUserAnswersService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, howGiveInformationRoute)
            .withFormUrlEncodedBody(("value", HowGiveInformation.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, howGiveInformationRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[HowGiveInformationView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, howGiveInformationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, howGiveInformationRoute)
            .withFormUrlEncodedBody(("value", HowGiveInformation.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
