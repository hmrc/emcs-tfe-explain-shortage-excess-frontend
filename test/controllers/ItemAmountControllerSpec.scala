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
import forms.ItemAmountFormProvider
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.ChooseShortageExcessItem.{Excess, Shortage}
import models.UnitOfMeasure.Kilograms
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.individualItems.{ChooseShortageExcessItemPage, ItemAmountPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ReferenceDataService, UserAnswersService}
import views.html.ItemAmountView

import scala.concurrent.Future

class ItemAmountControllerSpec extends SpecBase with MockUserAnswersService with MockReferenceDataService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
        bind[UserAnswersService].toInstance(mockUserAnswersService),
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      ).build()

    lazy val view = application.injector.instanceOf[ItemAmountView]
    implicit val msgs = messages(application)

    val formProvider = new ItemAmountFormProvider()
    val form =
      if(userAnswers.flatMap(_.get(ChooseShortageExcessItemPage(1))).contains(Shortage)) {
        formProvider(Some(item1.quantity), Kilograms)
      } else {
        formProvider(None, Kilograms)
      }
  }

  val validAnswer = BigDecimal(5.145)
  val defaultUserAnswers = emptyUserAnswers.set(ChooseShortageExcessItemPage(1), Shortage)

  lazy val itemAmountRoute = routes.ItemAmountController.onPageLoad(testErn, testArc, 1, NormalMode).url
  lazy val itemAmountSubmitAction = routes.ItemAmountController.onSubmit(testErn, testArc, 1, NormalMode)

  "ItemAmount Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, itemAmountRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, itemAmountSubmitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET (when ShortageOrExcess is set to Excess)" in new Fixture(
      Some(emptyUserAnswers.set(ChooseShortageExcessItemPage(1), Excess))
    ) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, itemAmountRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, itemAmountSubmitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ItemAmountPage(1), validAnswer))
    ) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, itemAmountRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), itemAmountSubmitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request = FakeRequest(POST, itemAmountRoute).withFormUrlEncodedBody(("value", validAnswer.toString))
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

        val request = FakeRequest(POST, itemAmountRoute).withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, itemAmountSubmitAction, item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, itemAmountRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, itemAmountRoute).withFormUrlEncodedBody(("value", validAnswer.toString))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Select Item page no movement data is found" in new Fixture() {
      running(application) {

        val request = FakeRequest(GET, routes.ItemAmountController.onPageLoad(testErn, testArc, 25, NormalMode).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SelectItemController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
