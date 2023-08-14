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

package navigation

import base.SpecBase
import controllers.routes
import models.HowGiveInformation.{Choose, Whole}
import models._
import pages._
import pages.individualItems._
import play.api.libs.json.JsPath

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  case object TestPage extends QuestionPage[String] {
    override def path: JsPath = JsPath
  }

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page

        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn, testArc)
      }

      "from WhenReceiveShortageExcessPage" - {

        "must go to UnderConstructionPage" in {
          navigator.nextPage(WhenReceiveShortageExcessPage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.HowGiveInformationController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "from HowGiveInformationPage" - {

        "when selected Whole" - {

          "must go to GiveInformationMovement" in {
            navigator.nextPage(HowGiveInformationPage, NormalMode, emptyUserAnswers.set(HowGiveInformationPage, Whole)) mustBe
              routes.GiveInformationMovementController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "when selected Choose" - {

          "must go to SelectItem" in {
            navigator.nextPage(HowGiveInformationPage, NormalMode, emptyUserAnswers.set(HowGiveInformationPage, Choose)) mustBe
              routes.SelectItemController.onPageLoad(testErn, testArc)
          }
        }
      }

      "from CheckAnswersItemPage" - {
        "must go to AddToList" in {
          navigator.nextPage(CheckAnswersItemPage(1), NormalMode, emptyUserAnswers) mustBe
            routes.AddToListController.onPageLoad(testErn, testArc)
        }
      }

      "from AddToListPage" - {
        "must go to Check Answers" in {
          navigator.nextPage(AddToListPage, NormalMode, emptyUserAnswers) mustBe
            routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "from GiveInformationMovementPage" - {

        "must go to Check Answers" in {
          navigator.nextPage(GiveInformationMovementPage, NormalMode, emptyUserAnswers) mustBe
            routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "from SelectItemPage" - {

        "must go to ChooseShortageExcessItemPage" in {
          navigator.nextPage(SelectItemPage(1), NormalMode, emptyUserAnswers) mustBe
            routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, testIdx, NormalMode)
        }
      }

      "from ChooseShortageExcessItemPage" - {

        "must go to ItemAmountPage" in {
          navigator.nextPage(ChooseShortageExcessItemPage(1), NormalMode, emptyUserAnswers) mustBe
            routes.ItemAmountController.onPageLoad(testErn, testArc, testIdx, NormalMode)
        }
      }

      "from ItemAmountPage" - {

        "must go to GiveInformationItemPage" in {
          navigator.nextPage(ItemAmountPage(1), NormalMode, emptyUserAnswers) mustBe
            routes.GiveInformationItemController.onPageLoad(testErn, testArc, 1, NormalMode)
        }
      }

      "from GiveInformationItemPage" - {

        "must go to CheckAnswersItemPage" in {
          navigator.nextPage(GiveInformationItemPage(1), NormalMode, emptyUserAnswers) mustBe
            routes.ItemCheckAnswersController.onPageLoad(testErn, testArc, testIdx)
        }
      }

      "from CheckAnswersPage" - {

        "must go to ConfirmationPage" in {
          navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.ConfirmationController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {

      "from ChooseShortageExcessItemPage" - {

        "must go through the normal routes to the next page" in {
          navigator.nextPage(ChooseShortageExcessItemPage(testIdx), CheckMode, emptyUserAnswers) mustBe
            navigator.normalRoutes(ChooseShortageExcessItemPage(testIdx))(emptyUserAnswers)
        }
      }

      "from ItemAmountPage" - {

        "must go to AddToListPage" in {
          navigator.nextPage(ItemAmountPage(testIdx), CheckMode, emptyUserAnswers) mustBe
            routes.AddToListController.onPageLoad(testErn, testArc)
        }
      }

      "from GiveInformationItemPage" - {

        "must go to AddToListPage" in {
          navigator.nextPage(GiveInformationItemPage(testIdx), CheckMode, emptyUserAnswers) mustBe
            routes.AddToListController.onPageLoad(testErn, testArc)
        }
      }

      "default scenario" - {

        "must go to Check Answers" in {
          navigator.nextPage(TestPage, CheckMode, emptyUserAnswers) mustBe
            routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Review mode" - {

      "for ChooseShortageExcessItemPage" - {

        "must go through the normal routes to the next page" in {
          navigator.nextPage(ChooseShortageExcessItemPage(testIdx), ReviewMode, emptyUserAnswers) mustBe
            navigator.normalRoutes(ChooseShortageExcessItemPage(testIdx))(emptyUserAnswers)
        }
      }

      "default scenario" - {

        "must go to Check Answers" in {
          navigator.nextPage(TestPage, ReviewMode, emptyUserAnswers) mustBe
            routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }
    }
  }
}
