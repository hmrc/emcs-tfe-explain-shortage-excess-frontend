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

import controllers.routes
import models.HowGiveInformation.Whole
import models.{CheckMode, Mode, NormalMode, ReviewMode, UserAnswers}
import pages._
import pages.individualItems._
import play.api.mvc.Call

import javax.inject.Inject

class Navigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case WhenReceiveShortageExcessPage => (userAnswers: UserAnswers) =>
      controllers.routes.HowGiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case HowGiveInformationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(HowGiveInformationPage) match {
        case Some(Whole) =>
          routes.GiveInformationMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          routes.SelectItemController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }

    case GiveInformationMovementPage => (_: UserAnswers) =>
      //TODO: Update as part of future story when Next page exists
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    case SelectItemPage(idx) => (userAnswers: UserAnswers) =>
      routes.ChooseShortageExcessItemController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
    case ChooseShortageExcessItemPage(idx) => (userAnswers: UserAnswers) =>
      routes.ItemAmountController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
    case ItemAmountPage(idx) => (userAnswers: UserAnswers) =>
      routes.GiveInformationItemController.onPageLoad(userAnswers.ern, userAnswers.arc, idx, NormalMode)
    case GiveInformationItemPage(idx) => (userAnswers: UserAnswers) =>
      routes.ItemCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc, idx)
    case CheckAnswersItemPage(_) => (userAnswers: UserAnswers) =>
      routes.AddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case AddToListPage => (_: UserAnswers) =>
      //TODO: Update as part of future story when Next page exists
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    case _ => (userAnswers: UserAnswers) =>
      routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private val checkRoutes: Page => UserAnswers => Call = {
    case ChooseShortageExcessItemPage(_) => (userAnswers: UserAnswers) =>
      routes.AddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case ItemAmountPage(_) => (userAnswers: UserAnswers) =>
      routes.AddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case GiveInformationItemPage(_) => (userAnswers: UserAnswers) =>
      routes.AddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case _ => (_: UserAnswers) =>
      //TODO: Update as part of future story when Check Answers page exists
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val reviewRoutesMap: Page => UserAnswers => Call = {
    case _ => (_: UserAnswers) =>
      //TODO: Update as part of future story when Check Answers page exists
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode => normalRoutes(page)(userAnswers)
    case CheckMode => checkRoutes(page)(userAnswers)
    case ReviewMode => reviewRoutesMap(page)(userAnswers)
  }
}
