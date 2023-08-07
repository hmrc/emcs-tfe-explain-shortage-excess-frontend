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
import forms.ChooseShortageExcessItemFormProvider
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.{ChooseShortageExcessItem, NormalMode}
import navigation.{FakeNavigator, Navigator}
import pages.individualItems.ChooseShortageExcessItemPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.ChooseShortageExcessItemView

import scala.concurrent.Future

class ChooseShortageExcessItemControllerSpec extends SpecBase with MockUserAnswersService with MockReferenceDataService {

  def onwardRoute = Call("GET", "/foo")

  lazy val chooseShortageExcessItemControllerRoute = routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, testIdx, NormalMode).url

  val formProvider = new ChooseShortageExcessItemFormProvider()
  val form = formProvider()

  val submitCall = routes.HowGiveInformationController.onSubmit(testErn, testArc, NormalMode)

  "ChooseShortageExcessItemController" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, chooseShortageExcessItemControllerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ChooseShortageExcessItemView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(testIdx, item1, cnCodeInfo, form, submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(ChooseShortageExcessItemPage(1), ChooseShortageExcessItem.values.head)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, chooseShortageExcessItemControllerRoute)

        val view = application.injector.instanceOf[ChooseShortageExcessItemView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(testIdx, item1, cnCodeInfo, form.fill(ChooseShortageExcessItem.values.head), submitCall)(dataRequest(request), messages(application)).toString
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
        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request =
          FakeRequest(POST, chooseShortageExcessItemControllerRoute)
            .withFormUrlEncodedBody(("value", ChooseShortageExcessItem.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request =
          FakeRequest(POST, chooseShortageExcessItemControllerRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[ChooseShortageExcessItemView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(testIdx, item1, cnCodeInfo, boundForm, submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, chooseShortageExcessItemControllerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, chooseShortageExcessItemControllerRoute)
            .withFormUrlEncodedBody(("value", ChooseShortageExcessItem.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
