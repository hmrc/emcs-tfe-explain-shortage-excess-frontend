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
import pages.individualItems.{CheckAnswersItemPage, SelectItemPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ReferenceDataService
import utils.JsonOptionFormatter
import views.html.SelectItemView

import scala.concurrent.Future

class SelectItemControllerSpec extends SpecBase
  with JsonOptionFormatter
  with MockUserAnswersService
  with MockReferenceDataService {

  lazy val loadListUrl: String = routes.SelectItemController.onPageLoad(testErn, testArc).url

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[ReferenceDataService].toInstance(mockReferenceDataService)
      )
      .build()

    lazy val view = application.injector.instanceOf[SelectItemView]
  }

  "SelectItem Controller" - {

    "when calling .onPageLoad()" - {

      "must return OK and the correct view for a GET" in new Fixture() {
        running(application) {

          MockReferenceDataService.getCnCodeInformationWithMovementItems(Seq(item1, item2)).returns(
            Future.successful(Seq(
              item1WithReferenceData -> cnCodeInfo,
              item2WithReferenceData -> cnCodeInfo
            ))
          )

          val request = FakeRequest(GET, loadListUrl)
          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(Seq(
            item1WithReferenceData -> cnCodeInfo,
            item2WithReferenceData -> cnCodeInfo
          ))(dataRequest(request), messages(application)).toString
        }
      }

      "must redirect to /add-to-list if filteredItems is empty" - {

        "when userAnswers only contains CheckAnswersItemPage = true" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
        )) {
          running(application) {

            val request = FakeRequest(GET, loadListUrl)
            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
          }
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
        running(application) {

          val request = FakeRequest(GET, loadListUrl)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
    }

    "when calling .incompleteItems" - {
      val controller = baseApplicationBuilder.build().injector.instanceOf[SelectItemController]
      "must return the full unfiltered list" - {
        "when no items have checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(SelectItemPage(2), item2.itemUniqueReference)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item1, item2)
        }

        "when nothing has been selected yet" in {
          val userAnswers = emptyUserAnswers
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item1, item2)
        }
      }

      "must return a filtered list" - {
        "when the first item has checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item2)
        }

        "when the second item has checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq(item1)
        }

        "when both items have checkAnswersItem = true" in {
          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
          controller.incompleteItems()(dataRequest(FakeRequest(), userAnswers)) mustBe Seq()
        }
      }
    }
  }
}
