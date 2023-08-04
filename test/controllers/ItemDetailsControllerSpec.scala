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
import mocks.services.{MockReferenceDataService, MockUserAnswersService}
import models.UserAnswers
import play.api.inject.bind
import play.api.mvc.Results.Redirect
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ReferenceDataService
import utils.JsonOptionFormatter
import views.html.ItemDetailsView

import scala.concurrent.Future

class ItemDetailsControllerSpec extends SpecBase
  with JsonOptionFormatter
  with MockUserAnswersService
  with MockReferenceDataService {

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )
      .build()

    lazy val view = application.injector.instanceOf[ItemDetailsView]
  }

  def onPageLoadUrl(idx: Int): String = routes.ItemDetailsController.onPageLoad(testErn, testArc, idx).url

  "ItemDetails Controller" - {

    "when calling .onPageLoad()" - {

      "must return OK and the correct view for a GET" in new Fixture() {
        running(application) {

          MockReferenceDataService.itemWithReferenceData(item1).onCall(
            MockReferenceDataService.itemWithReferenceDataSuccessHandler(item1WithReferenceData, cnCodeInfo)
          )

          val request = FakeRequest(GET, onPageLoadUrl(idx = 1))
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(item1WithReferenceData, cnCodeInfo)(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to /select-items-give-information" - {

        "when the item doesn't exist in UserAnswers" in new Fixture() {
          running(application) {

            val request = FakeRequest(GET, onPageLoadUrl(idx = 3))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemController.onPageLoad(testErn, testArc).url
          }
        }

        "when one of the service calls returns no data" in new Fixture() {
          running(application) {

            MockReferenceDataService.itemWithReferenceData(item1).returns(
              Future.successful(Redirect(routes.SelectItemController.onPageLoad(testErn, testArc)))
            )

            val request = FakeRequest(GET, onPageLoadUrl(idx = 1))
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.SelectItemController.onPageLoad(testErn, testArc).url
          }
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
        running(application) {

          val request = FakeRequest(GET, onPageLoadUrl(3))
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
    }
  }
}
