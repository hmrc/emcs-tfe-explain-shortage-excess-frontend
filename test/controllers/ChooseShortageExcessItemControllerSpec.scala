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
import models.{ChooseShortageExcessItem, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.individualItems.ChooseShortageExcessItemPage
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ReferenceDataService, UserAnswersService}
import views.html.ChooseShortageExcessItemView

import scala.concurrent.Future


class ChooseShortageExcessItemControllerSpec extends SpecBase with MockUserAnswersService with MockReferenceDataService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application: Application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
        bind[ReferenceDataService].toInstance(mockReferenceDataService),
        bind[UserAnswersService].toInstance(mockUserAnswersService)
      ).build()

    lazy val view: ChooseShortageExcessItemView = application.injector.instanceOf[ChooseShortageExcessItemView]

    lazy val chooseShortageExcessItemControllerRoute: String = routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, testIdx, NormalMode).url
    lazy val submitCall: Call = routes.ChooseShortageExcessItemController.onSubmit(testErn, testArc, testIdx, NormalMode)

    val formProvider: ChooseShortageExcessItemFormProvider = new ChooseShortageExcessItemFormProvider()
    val form: Form[ChooseShortageExcessItem] = formProvider()
  }


  "ChooseShortageExcessItemController" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, chooseShortageExcessItemControllerRoute)

        val result = route(application, request).value


        status(result) mustEqual OK

        contentAsString(result) mustEqual view(
          idx = testIdx,
          movementItem = item1WithReferenceData,
          cnCodeInformation = cnCodeInfo,
          form = form,
          call = submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers.set(ChooseShortageExcessItemPage(1), ChooseShortageExcessItem.values.head)
    )) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, chooseShortageExcessItemControllerRoute)

        lazy val view = application.injector.instanceOf[ChooseShortageExcessItemView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          idx = testIdx,
          movementItem = item1WithReferenceData,
          cnCodeInformation = cnCodeInfo,
          form = form.fill(ChooseShortageExcessItem.values.head),
          call = submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request =
          FakeRequest(POST, chooseShortageExcessItemControllerRoute)
            .withFormUrlEncodedBody(("value", ChooseShortageExcessItem.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request =
          FakeRequest(POST, chooseShortageExcessItemControllerRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        lazy val view = application.injector.instanceOf[ChooseShortageExcessItemView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          idx = testIdx,
          movementItem = item1WithReferenceData,
          cnCodeInformation = cnCodeInfo,
          form = boundForm,
          call = submitCall)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET" - {
      "if no existing data is found" in new Fixture(None) {
        running(application) {

          val request = FakeRequest(GET, chooseShortageExcessItemControllerRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
      "if no item is found" in new Fixture() {
        running(application) {

          // there isn't an item 3
          val request = FakeRequest(GET, routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, 3, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.SelectItemController.onPageLoad(testErn, testArc).url
        }
      }
    }

    "redirect to Journey Recovery for a POST" - {
      "if no existing data is found" in new Fixture(None) {
        running(application) {

          val request =
            FakeRequest(POST, chooseShortageExcessItemControllerRoute)
              .withFormUrlEncodedBody(("value", ChooseShortageExcessItem.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
      "if no item is found" in new Fixture() {
        running(application) {

          // there isn't an item 3
          val request =
            FakeRequest(POST, routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, 3, NormalMode).url)
              .withFormUrlEncodedBody(("value", ChooseShortageExcessItem.values.head.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.SelectItemController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}
