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
import forms.RemoveItemFormProvider
import models.requests.DataRequest

import javax.inject.Inject
import models.{Mode, ReviewMode}
import navigation.Navigator
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.RemoveItemView

import scala.concurrent.Future

class RemoveItemController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: RemoveItemFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: RemoveItemView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAddedItemAsync(idx)(_ => renderView(Ok, formProvider(idx), idx, mode))
    }


  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAddedItemAsync(idx) {
        _ =>
          formProvider(idx).bindFromRequest().fold(
            formWithErrors =>
              renderView(BadRequest, formWithErrors, idx, mode),
            {
              case true =>
                val updatedAnswers = request.userAnswers.removeItem(idx)
                userAnswersService.set(updatedAnswers).map { _ =>
                  Redirect(routes.AddToListController.onPageLoad(request.ern, request.arc))
                }
              case false if mode == ReviewMode =>
                Future(Redirect(routes.CheckYourAnswersController.onPageLoad(request.ern, request.arc)))
              case false =>
                Future(Redirect(routes.AddToListController.onPageLoad(request.ern, request.arc)))
            }
          )
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Int, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(form, idx, routes.RemoveItemController.onSubmit(request.ern, request.arc, idx, mode))))
}
