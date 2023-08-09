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
import forms.ChooseShortageExcessItemFormProvider
import models.Mode
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import navigation.Navigator
import pages.individualItems.ChooseShortageExcessItemPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{ReferenceDataService, UserAnswersService}
import views.html.ChooseShortageExcessItemView

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ChooseShortageExcessItemController @Inject()(override val messagesApi: MessagesApi,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val navigator: Navigator,
                                                   override val auth: AuthAction,
                                                   override val withMovement: MovementAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val requireData: DataRequiredAction,
                                                   override val userAllowList: UserAllowListAction,
                                                   formProvider: ChooseShortageExcessItemFormProvider,
                                                   referenceDataService: ReferenceDataService,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ChooseShortageExcessItemView
                                                  ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) {
      implicit request =>
        withMovementItemAsync(idx) {
          renderView(Ok, _, fillForm(ChooseShortageExcessItemPage(idx), formProvider()), idx, mode)
        }
    }


  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) {
      implicit request =>
        withMovementItemAsync(idx) {
          movementItem =>
            formProvider().bindFromRequest().fold(
              renderView(BadRequest, movementItem, _, idx, mode),
              saveAndRedirect(ChooseShortageExcessItemPage(idx), _, mode)
            )
        }

    }

  private def renderView(status: Status, movementItem: MovementItem, form: Form[_], idx: Int, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    referenceDataService.itemWithReferenceData(movementItem) {
      (item, cnCode) =>
        Future.successful(status(view(
          idx = idx,
          movementItem = item,
          cnCodeInformation = cnCode,
          form = form,
          call = routes.ChooseShortageExcessItemController.onSubmit(request.ern, request.arc, idx, mode)
        )))
    }

}
