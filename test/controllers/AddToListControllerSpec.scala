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
import forms.AddAnotherItemFormProvider
import mocks.services.MockGetCnCodeInformationService
import mocks.viewmodels.MockAddToListHelper
import models.ReferenceDataUnitOfMeasure.`1`
import models.UserAnswers
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import pages.individualItems._
import play.api.inject.bind
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.GetCnCodeInformationService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.AddToListHelper
import views.html.AddToListView

import scala.concurrent.Future

class AddToListControllerSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockAddToListHelper {

  lazy val form = new AddAnotherItemFormProvider()()

  lazy val url = "testurl"

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val application = applicationBuilder(userAnswers)
      .overrides(
        bind[AddToListHelper].toInstance(mockAddToListHelper),
        bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService)
      )
      .build()

    lazy val view = application.injector.instanceOf[AddToListView]
  }

  "AddedItems Controller" - {

    ".onPageLoad()" - {

      "must redirect to the SelectItems page" - {

        "when no items have been added" in new Fixture() {

          val request = FakeRequest(GET, routes.AddToListController.onPageLoad(testErn, testArc).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.SelectItemController.onPageLoad(testErn, testArc).url)
        }

        "when items have been added, but not completed" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
        )) {

          val request = FakeRequest(GET, routes.AddToListController.onPageLoad(testErn, testArc).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.SelectItemController.onPageLoad(testErn, testArc).url)
        }
      }

      "when items have been added, but not all" - {

        "must render the view with the Radio option form to add more" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
        )) {

          val serviceResponse: Seq[(MovementItem, CnCodeInformation)] = Seq(item1 -> CnCodeInformation("", "", `1`))

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(serviceResponse))
          MockAddToListHelper.summaryList().returns(SummaryList())

          val request = FakeRequest(GET, routes.AddToListController.onPageLoad(testErn, testArc).url)

          implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form = Some(form),
            itemList = Seq[(Int, SummaryList)](1 -> SummaryList()),
            allItemsAdded = false,
            action = routes.AddToListController.onSubmit(testErn, testArc))(req, messages(application)
          ).toString
        }
      }

      "when all items have been added" - {

        "must render the view without the Radio option form" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
        )) {

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2))
            .returns(Future.successful(Seq(
              item1 -> CnCodeInformation("", "", `1`),
              item2 -> CnCodeInformation("", "", `1`)
            )))
          MockAddToListHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

          val request = FakeRequest(GET, routes.AddToListController.onPageLoad(testErn, testArc).url)
          implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

          val result = route(application, request).value

          status(result) mustEqual OK

          contentAsString(result) mustEqual view(
            form = Some(form),
            itemList = Seq[(Int, SummaryList)](1 -> SummaryList(), 2 -> SummaryList()),
            allItemsAdded = true,
            action = routes.AddToListController.onSubmit(testErn, testArc))(req, messages(application)
          ).toString
        }
      }
    }

    ".onSubmit()" - {

      "when no items have been added" - {

        "must redirect to the SelectItems page" in new Fixture(Some(emptyUserAnswers)) {

          val request = FakeRequest(POST, routes.AddToListController.onSubmit(testErn, testArc).url)
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.SelectItemController.onPageLoad(testErn, testArc).url)
        }
      }

      "when items have been added (but not all items)" - {

        "when a radio button is not selected" - {

          "must render a BAD_REQUEST with the formError" in new Fixture(Some(
            emptyUserAnswers
              .set(SelectItemPage(1), item1.itemUniqueReference)
              .set(CheckAnswersItemPage(1), true)
          )) {

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
              Seq(item1 -> CnCodeInformation("", "", `1`))
            ))
            MockAddToListHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

            val request = FakeRequest(POST, routes.AddToListController.onSubmit(testErn, testArc).url)
            val result = route(application, request).value

            val boundForm = form.bind(Map("value" -> ""))

            implicit val req: DataRequest[_] = dataRequest(request, userAnswers.get)

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual view(
              form = Some(boundForm),
              itemList = Seq(1 -> SummaryList()),
              allItemsAdded = false,
              action = routes.AddToListController.onSubmit(testErn, testArc))(req, messages(application)
            ).toString
          }
        }

        "when true is selected to add another item" - {

          "must redirect to the SelectItem view" in new Fixture(Some(
            emptyUserAnswers
              .set(SelectItemPage(1), item1.itemUniqueReference)
              .set(CheckAnswersItemPage(1), true)
          )) {

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
              Seq(item1 -> CnCodeInformation("", "", `1`))
            ))
            MockAddToListHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

            val request = FakeRequest(POST, routes.AddToListController.onSubmit(testErn, testArc).url)
              .withFormUrlEncodedBody("value" -> "true")

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(routes.SelectItemController.onPageLoad(testErn, testArc).url)
          }
        }

        "when false is selected to NOT add another item" - {

          "must redirect to the next view" in new Fixture(Some(
            emptyUserAnswers
              .set(SelectItemPage(1), item1.itemUniqueReference)
              .set(ItemAmountPage(1), Some(BigDecimal(1)))
              .set(GiveInformationItemPage(1), "info")
              .set(CheckAnswersItemPage(1), true)
          )) {

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1)).returns(Future.successful(
              Seq(item1 -> CnCodeInformation("", "", `1`))
            ))
            MockAddToListHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

            val request = FakeRequest(POST, routes.AddToListController.onSubmit(testErn, testArc).url)
              .withFormUrlEncodedBody("value" -> "false")

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
          }
        }
      }

      "when all items have been added" - {

        "must redirect to the next view" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(ItemAmountPage(1), Some(BigDecimal(1)))
            .set(GiveInformationItemPage(1), "info")
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(GiveInformationItemPage(2), "info")
            .set(CheckAnswersItemPage(2), true)
        )) {

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2)).returns(Future.successful(
            Seq(
              item1 -> CnCodeInformation("", "", `1`),
              item2 -> CnCodeInformation("", "", `1`)
            )
          ))
          MockAddToListHelper.summaryList().returns(SummaryList()).anyNumberOfTimes()

          val request = FakeRequest(POST, routes.AddToListController.onSubmit(testErn, testArc).url).withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(testOnly.controllers.routes.UnderConstructionController.onPageLoad().url)
        }
      }
    }

    ".hasAmountForAtLeastOneItem()" - {
      def controller = baseApplicationBuilder.build().injector.instanceOf[AddToListController]

      "must return true" - {
        "when all amounts are above 0" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(ItemAmountPage(1), Some(BigDecimal(1)))
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(ItemAmountPage(2), Some(BigDecimal(1)))
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasAmountForAtLeastOneItem()(dataRequest(FakeRequest(), userAnswers.get)) mustBe true
        }

        "when at last one amount is above 0" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(ItemAmountPage(1), Some(BigDecimal(1)))
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasAmountForAtLeastOneItem()(dataRequest(FakeRequest(), userAnswers.get)) mustBe true
        }
      }

      "must return false" - {
        "when all amounts add up to <= 0" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(ItemAmountPage(1), Some(BigDecimal(0)))
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(ItemAmountPage(2), Some(BigDecimal(0)))
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasAmountForAtLeastOneItem()(dataRequest(FakeRequest(), userAnswers.get)) mustBe false
        }

        "no amounts are present" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(ItemAmountPage(1), None)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasAmountForAtLeastOneItem()(dataRequest(FakeRequest(), userAnswers.get)) mustBe false
        }
      }
    }

    ".hasMoreInfoForAllItems()" - {
      def controller = baseApplicationBuilder.build().injector.instanceOf[AddToListController]

      "must return true" - {
        "when all values are non-empty strings" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(GiveInformationItemPage(1), "info")
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(GiveInformationItemPage(2), "other info")
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasMoreInfoForAllItems()(dataRequest(FakeRequest(), userAnswers.get)) mustBe true
        }
      }

      "must return false" - {
        "when at last one value is an empty string" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(GiveInformationItemPage(1), "info")
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(GiveInformationItemPage(2), "")
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasMoreInfoForAllItems()(dataRequest(FakeRequest(), userAnswers.get)) mustBe false
        }

        "when at last one value is missing" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(GiveInformationItemPage(1), "info")
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasMoreInfoForAllItems()(dataRequest(FakeRequest(), userAnswers.get)) mustBe false
        }

        "when all values are empty strings" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(GiveInformationItemPage(1), "")
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(GiveInformationItemPage(2), "")
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasMoreInfoForAllItems()(dataRequest(FakeRequest(), userAnswers.get)) mustBe false
        }

        "when all values are missing" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)
        )) {
          controller.hasMoreInfoForAllItems()(dataRequest(FakeRequest(), userAnswers.get)) mustBe false
        }
      }
    }

    ".onwardRedirect()" - {
      def controller = baseApplicationBuilder.build().injector.instanceOf[AddToListController]

      "must redirect" - {
        "hasAmountForAtLeastOneItem and hasMoreInfoForAllItems are both true" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(ItemAmountPage(1), Some(BigDecimal(1)))
            .set(GiveInformationItemPage(1), "info")
            .set(CheckAnswersItemPage(1), true)
        )) {
          val result: Result = controller.onwardRedirect(Seq(), allItemsAdded = true)(dataRequest(FakeRequest(), userAnswers.get))

          result.header.status mustBe SEE_OTHER
          result.header.headers("Location") mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
        }
      }

      "must return a 400 (BadRequest)" - {
        "hasAmountForAtLeastOneItem is false" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(GiveInformationItemPage(1), "info")
            .set(CheckAnswersItemPage(1), true)
        )) {
          val result: Result = controller.onwardRedirect(Seq(), allItemsAdded = true)(dataRequest(FakeRequest(), userAnswers.get))

          result.header.status mustBe BAD_REQUEST
        }

        "hasMoreInfoForAllItems is false" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(ItemAmountPage(1), Some(BigDecimal(1)))
            .set(CheckAnswersItemPage(1), true)
        )) {
          val result: Result = controller.onwardRedirect(Seq(), allItemsAdded = true)(dataRequest(FakeRequest(), userAnswers.get))

          result.header.status mustBe BAD_REQUEST
        }

        "hasAmountForAtLeastOneItem and hasMoreInfoForAllItems are both false" in new Fixture(Some(
          emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
        )) {
          val result: Result = controller.onwardRedirect(Seq(), allItemsAdded = true)(dataRequest(FakeRequest(), userAnswers.get))

          result.header.status mustBe BAD_REQUEST
        }
      }
    }
  }
}
