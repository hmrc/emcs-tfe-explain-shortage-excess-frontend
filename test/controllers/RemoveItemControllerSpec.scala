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
import forms.RemoveItemFormProvider
import mocks.services.MockUserAnswersService
import models.HowGiveInformation.Choose
import models.{Mode, NormalMode, ReviewMode, UserAnswers}
import pages.HowGiveInformationPage
import pages.individualItems._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.RemoveItemView

import scala.concurrent.Future

class RemoveItemControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val baseAnswers: UserAnswers = emptyUserAnswers
    .set(HowGiveInformationPage, Choose)
    .set(SelectItemPage(1), item1.itemUniqueReference)
    .set(CheckAnswersItemPage(1), true)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(baseAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val view = application.injector.instanceOf[RemoveItemView]
  }

  val formProvider = new RemoveItemFormProvider()
  val form = formProvider(1)

  def removeItemRoute(mode: Mode = NormalMode) = routes.RemoveItemController.onPageLoad(testErn, testArc, 1, mode).url
  def removeItemSubmit(mode: Mode = NormalMode) = routes.RemoveItemController.onSubmit(testErn, testArc, 1, mode)

  "RemoveItem Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {

      running(application) {

        val request = FakeRequest(GET, removeItemRoute())

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          idx = 1,
          call = removeItemSubmit()
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when true is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      running(application) {
        val request =
          FakeRequest(POST, removeItemRoute())
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.AddToListController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to the next page when false is submitted in normal mode" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, removeItemRoute())
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.AddToListController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to the check your answers page when false is submitted in review mode" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, removeItemRoute(ReviewMode))
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

      running(application) {
        val request =
          FakeRequest(POST, removeItemRoute())
            .withFormUrlEncodedBody(("value", "dhsjkdhas"))

        val boundForm = form.bind(Map("value" -> "hdjskahdas"))

        val view = application.injector.instanceOf[RemoveItemView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          idx = 1,
          call = removeItemSubmit()
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      running(application) {
        val request = FakeRequest(GET, removeItemRoute())

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      running(application) {
        val request =
          FakeRequest(POST, removeItemRoute())
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
