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

import controllers.actions._
import forms.ItemAmountFormProvider
import models.ChooseShortageExcessItem.Shortage
import models.Mode
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import navigation.Navigator
import pages.individualItems.{ChooseShortageExcessItemPage, ItemAmountPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{ReferenceDataService, UserAnswersService}
import utils.JsonOptionFormatter
import views.html.ItemAmountView

import javax.inject.Inject
import scala.concurrent.Future

class ItemAmountController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: Navigator,
                                      override val auth: AuthAction,
                                      override val withMovement: MovementAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      override val userAllowList: UserAllowListAction,
                                      formProvider: ItemAmountFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      referenceDataService: ReferenceDataService,
                                      view: ItemAmountView
                                    ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {


  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withItemCnCodeAndFormProvider(idx) { (item, cnCode, form) =>
        renderView(Ok, fillForm(ItemAmountPage(idx), form), item, cnCode, mode)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withItemCnCodeAndFormProvider(idx) { (item, cnCode, form) =>
        form.bindFromRequest().fold(
          renderView(BadRequest, _, item, cnCode, mode),
          saveAndRedirect(ItemAmountPage(idx), _, mode)
        )
      }
    }

  private def withItemCnCodeAndFormProvider(idx: Int)
                                           (f: (MovementItem, CnCodeInformation, Form[Option[BigDecimal]]) => Future[Result])
                                           (implicit request: DataRequest[_]): Future[Result] =
    withMovementItemAsync(idx) { item =>
      referenceDataService.itemWithReferenceData(item) { (itemWithRefData, cnCode) =>
        withAnswer(ChooseShortageExcessItemPage(idx)) {
          case Shortage => f(itemWithRefData, cnCode, formProvider(Some(item.quantity), cnCode.unitOfMeasureCode.toUnitOfMeasure))
          case _ => f(itemWithRefData, cnCode, formProvider(None, cnCode.unitOfMeasureCode.toUnitOfMeasure))
        }
      }
    }

  private def renderView(status: Status,
                         form: Form[Option[BigDecimal]],
                         item: MovementItem,
                         cnCode: CnCodeInformation,
                         mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form, routes.ItemAmountController.onSubmit(request.ern, request.arc, item.itemUniqueReference, mode), item, cnCode
    )))
}