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

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

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

      "from GiveInformationMovementPage" - {

        //TODO: Update as part of future story when page exists
        "must go to UnderConstructionPage" in {
          navigator.nextPage(GiveInformationMovementPage, NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
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

        //TODO: Update as part of future story when page exists
        "must go to UnderConstructionPage" in {
          navigator.nextPage(GiveInformationItemPage(1), NormalMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }

    "in Check mode" - {

      "from WhenReceiveShortageExcessPage" - {

        //TODO: Update as part of future story when page exists to go to Check Answers
        "must go to UnderConstructionPage" in {
          navigator.nextPage(WhenReceiveShortageExcessPage, CheckMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }
  }
}
