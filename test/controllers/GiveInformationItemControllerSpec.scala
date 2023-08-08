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
import forms.CharacterCounterFormProvider
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.ChooseShortageExcessItem.{Excess, Shortage}
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.individualItems._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ReferenceDataService, UserAnswersService}
import views.html.GiveInformationItemView

import scala.concurrent.Future

class GiveInformationItemControllerSpec extends SpecBase with MockUserAnswersService with MockReferenceDataService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService),
          bind[ReferenceDataService].toInstance(mockReferenceDataService)
        )
        .build()

    lazy val view = application.injector.instanceOf[GiveInformationItemView]
    implicit lazy val msgs = messages(application)
  }

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new CharacterCounterFormProvider()
  val form = formProvider(GiveInformationItemPage(1))

  val defaultUserAnswers = emptyUserAnswers.set(ChooseShortageExcessItemPage(1), Shortage)

  lazy val giveInformationItemRoute = routes.GiveInformationItemController.onPageLoad(testErn, testArc, 1, NormalMode).url
  lazy val giveInformationItemSubmitAction = routes.GiveInformationItemController.onSubmit(testErn, testArc, 1, NormalMode)

  "GiveInformationItem Controller" - {

    "must return OK and the correct view for a GET (shortage)" in new Fixture() {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, giveInformationItemRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          action = giveInformationItemSubmitAction,
          item = item1WithReferenceData,
          cnCodeInfo = cnCodeInfo,
          shortageOrExcess = Shortage
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET (excess)" in new Fixture(
      Some(emptyUserAnswers.set(ChooseShortageExcessItemPage(1), Excess))
    ) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, giveInformationItemRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          action = giveInformationItemSubmitAction,
          item = item1WithReferenceData,
          cnCodeInfo = cnCodeInfo,
          shortageOrExcess = Excess
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      defaultUserAnswers.set(GiveInformationItemPage(1), "answer")
    )) {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(GET, giveInformationItemRoute)
        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill("answer"),
          action = giveInformationItemSubmitAction,
          item = item1WithReferenceData,
          cnCodeInfo = cnCodeInfo,
          shortageOrExcess = Shortage
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(defaultUserAnswers))

        val request = FakeRequest(POST, giveInformationItemRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        MockReferenceDataService.itemWithReferenceData(item1).onCall(
          MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
        )

        val request = FakeRequest(POST, giveInformationItemRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          action = giveInformationItemSubmitAction,
          item = item1WithReferenceData,
          cnCodeInfo = cnCodeInfo,
          shortageOrExcess = Shortage
        )(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(GET, giveInformationItemRoute)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val request = FakeRequest(POST, giveInformationItemRoute).withFormUrlEncodedBody(("value", "answer"))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
