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

  private[navigation] val normalRoutes: Page => UserAnswers => Call = {
    case WhenReceiveShortageExcessPage => (userAnswers: UserAnswers) =>
      controllers.routes.HowGiveInformationController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case HowGiveInformationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(HowGiveInformationPage) match {
        case Some(Whole) =>
          routes.GiveInformationMovementController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          routes.SelectItemController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }
    case GiveInformationMovementPage => (userAnswers: UserAnswers) =>
      routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
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
    case AddToListPage => (userAnswers: UserAnswers) =>
      routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case CheckAnswersPage => (userAnswers: UserAnswers) =>
      routes.ConfirmationController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case _ => (userAnswers: UserAnswers) =>
      routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val checkRoutes: Page => UserAnswers => Call = {
    case page@ChooseShortageExcessItemPage(_) => normalRoutes(page)
    case ItemAmountPage(_) => (userAnswers: UserAnswers) =>
      routes.AddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case GiveInformationItemPage(_) => (userAnswers: UserAnswers) =>
      routes.AddToListController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case _ => (userAnswers: UserAnswers) =>
      routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRoutesMap: Page => UserAnswers => Call = {
    case page@ChooseShortageExcessItemPage(_) =>
      normalRoutes(page)
    case _ => (userAnswers: UserAnswers) =>
      routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode => normalRoutes(page)(userAnswers)
    case CheckMode => checkRoutes(page)(userAnswers)
    case ReviewMode => reviewRoutesMap(page)(userAnswers)
  }
}
