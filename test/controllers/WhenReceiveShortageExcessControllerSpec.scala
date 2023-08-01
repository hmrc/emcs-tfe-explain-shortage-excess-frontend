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

import java.time.{LocalDate, LocalDateTime, ZoneOffset}
import base.SpecBase
import forms.WhenReceiveShortageExcessFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.WhenReceiveShortageExcessPage
import play.api.data.Form
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import utils.TimeMachine
import views.html.WhenReceiveShortageExcessView

import scala.concurrent.Future

class WhenReceiveShortageExcessControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[UserAnswersService].toInstance(mockUserAnswersService)
        )
        .build()

    lazy val view = application.injector.instanceOf[WhenReceiveShortageExcessView]
    implicit lazy val msgs = messages(application)
  }

  val fixedNow = LocalDateTime.now()
  val dateOfDispatch = fixedNow.minusDays(3)

  val timeMachine = new TimeMachine {
    override def now(): LocalDateTime = fixedNow
  }

  val formProvider = new WhenReceiveShortageExcessFormProvider(timeMachine)
  private def form(implicit messages: Messages): Form[LocalDate] = formProvider(dateOfDispatch.toLocalDate)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val whenReceiveShortageExcessRoute = routes.WhenReceiveShortageExcessController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val submitAction = routes.WhenReceiveShortageExcessController.onSubmit(testErn, testArc, NormalMode)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, whenReceiveShortageExcessRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, whenReceiveShortageExcessRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "WhenReceiveShortageExcess Controller" - {

    "must return OK and the correct view for a GET" in new Fixture() {
      running(application) {
        val result = route(application, getRequest()).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, submitAction)(dataRequest(getRequest()), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(WhenReceiveShortageExcessPage, validAnswer))
    ) {
      running(application) {

        val result = route(application, getRequest()).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), submitAction)(dataRequest(getRequest()), messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      running(application) {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val result = route(application, postRequest()).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      running(application) {

        val request =
          FakeRequest(POST, whenReceiveShortageExcessRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, submitAction)(dataRequest(request), messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      running(application) {

        val result = route(application, getRequest()).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      running(application) {

        val result = route(application, postRequest()).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
