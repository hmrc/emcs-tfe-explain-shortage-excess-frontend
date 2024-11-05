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
import fixtures.SubmitShortageExcessFixtures
import handlers.ErrorHandler
import mocks.services.{MockGetCnCodeInformationService, MockSubmitShortageExcessService, MockUserAnswersService}
import mocks.viewmodels.MockCheckAnswersHelper
import models.ChooseShortageExcessItem.Excess
import models.HowGiveInformation.{Choose, Whole}
import models.{ConfirmationDetails, MissingMandatoryPage, SubmitShortageExcessException, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.individualItems._
import pages.{ConfirmationPage, GiveInformationMovementPage, HowGiveInformationPage, WhenReceiveShortageExcessPage}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{GetCnCodeInformationService, SubmitShortageExcessService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import utils.JsonOptionFormatter._
import viewmodels.checkAnswers.CheckAnswersHelper
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency
  with MockCheckAnswersHelper
  with MockGetCnCodeInformationService
  with MockUserAnswersService
  with MockSubmitShortageExcessService
  with SubmitShortageExcessFixtures {

  class Fixture(userAnswers: Option[UserAnswers]) {
    val application =
      applicationBuilder(userAnswers)
        .overrides(
          inject.bind[CheckAnswersHelper].toInstance(mockCheckAnswersHelper),
          inject.bind[Navigator].toInstance(new FakeNavigator(testOnwardRoute)),
          inject.bind[GetCnCodeInformationService].toInstance(mockGetCnCodeInformationService),
          inject.bind[UserAnswersService].toInstance(mockUserAnswersService),
          inject.bind[SubmitShortageExcessService].toInstance(mockSubmitShortageExcessService)
        )
        .build()

    lazy val errorHandler = application.injector.instanceOf[ErrorHandler]
    val view = application.injector.instanceOf[CheckYourAnswersView]
  }

  "Check Your Answers Controller" - {

    ".onPageLoad" - {

      def request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(testErn, testArc).url)

      def selectItemsRoute = routes.SelectItemController.onPageLoad(testErn, testArc).url

      "must return OK and the correct view for a GET (when Whole Movement)" in new Fixture(Some(
        emptyUserAnswers
          .set(WhenReceiveShortageExcessPage, testDateOfWhenReceiveShortageOrExcess)
          .set(HowGiveInformationPage, Whole)
          .set(GiveInformationMovementPage, "Foobar")
      )) {

        running(application) {

          val list = SummaryListViewModel(Seq.empty)
          val itemList = Seq.empty[(Int, SummaryList)]

          MockCheckAnswersHelper.summaryList().returns(list)

          val result = route(application, request).value

          status(result) mustBe OK
          contentAsString(result) mustBe view(routes.CheckYourAnswersController.onSubmit(testErn, testArc), selectItemsRoute, list, itemList, false)(dataRequest(request), messages(application)).toString
        }
      }

      "must return OK and the correct view for a GET (when Individual Items)" in new Fixture(Some(
        emptyUserAnswers
          .set(WhenReceiveShortageExcessPage, testDateOfWhenReceiveShortageOrExcess)
          .set(HowGiveInformationPage, Choose)
          .set(SelectItemPage(item1.itemUniqueReference), item1.itemUniqueReference)
          .set(ChooseShortageExcessItemPage(item1.itemUniqueReference), Excess)
          .set(ItemAmountPage(item1.itemUniqueReference), Some(BigDecimal(12.1)))
          .set(GiveInformationItemPage(item1.itemUniqueReference), "Foobar")
          .set(CheckAnswersItemPage(item1.itemUniqueReference), true)
      )) {

        running(application) {

          val list = SummaryListViewModel(Seq.empty)
          val itemList = Seq.empty[(Int, SummaryList)]

          MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1))
            .returns(Future.successful(Seq()))
          MockCheckAnswersHelper.summaryList().returns(list)

          val result = route(application, request).value

          status(result) mustBe OK
          contentAsString(result) mustBe view(routes.CheckYourAnswersController.onSubmit(testErn, testArc), selectItemsRoute, list, itemList, true)(dataRequest(request), messages(application)).toString
        }
      }

      "must return SEE_OTHER and redirect to Select Items (when Individual Items AND NO added items)" in new Fixture(Some(
        emptyUserAnswers
          .set(HowGiveInformationPage, Choose)
      )) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(selectItemsRoute)
        }
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

        running(application) {

          val result = route(application, request).value

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url
        }
      }
    }

    ".onSubmit" - {

      def request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(testErn, testArc).url)

      "when valid data exists so the submission can be generated" - {

        val userAnswers = emptyUserAnswers
          .set(WhenReceiveShortageExcessPage, testDateOfWhenReceiveShortageOrExcess)
          .set(HowGiveInformationPage, Whole)
          .set(GiveInformationMovementPage, "Foobar")

        "when the submission is successful" - {

          "must redirect to the onward route" in new Fixture(Some(userAnswers)) {

            running(application) {

              val updatedUserAnswers = emptyUserAnswers.set(
                ConfirmationPage,
                ConfirmationDetails(
                  submitShortageOrExcessChRISResponseModel.receipt,
                  testReceiptDateTime
                )
              )

              MockUserAnswersService.set().returns(Future.successful(updatedUserAnswers))
              MockSubmitShortageExcessService.submit(testErn, testArc, getMovementResponseModel, userAnswers)
                .returns(Future.successful(submitShortageOrExcessChRISResponseModel))

              val result = route(application, request).value

              status(result) mustBe SEE_OTHER
              redirectLocation(result).value mustBe testOnwardRoute.url
            }
          }
        }

        "when the submission fails" - {

          "must render an ISE" in new Fixture(Some(userAnswers)) {

            running(application) {

              MockSubmitShortageExcessService.submit(testErn, testArc, getMovementResponseModel, userAnswers)
                .returns(Future.failed(SubmitShortageExcessException("bang")))

              val result = route(application, request).value

              status(result) mustBe INTERNAL_SERVER_ERROR
              contentAsString(result) mustBe await(errorHandler.internalServerErrorTemplate(request)).toString()
            }
          }
        }
      }

      "when invalid data exists so the submission can NOT be generated" - {

        "must return BadRequest" in new Fixture(Some(emptyUserAnswers)) {

          running(application) {

            MockSubmitShortageExcessService.submit(testErn, testArc, getMovementResponseModel, emptyUserAnswers)
              .returns(Future.failed(MissingMandatoryPage("bang")))

            val result = route(application, request).value

            status(result) mustBe BAD_REQUEST
            contentAsString(result) mustBe await(errorHandler.badRequestTemplate(request)).toString()
          }
        }
      }
    }
  }
}
