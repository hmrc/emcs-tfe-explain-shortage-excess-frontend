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
import forms.WhenReceiveShortageExcessFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.WhenReceiveShortageExcessPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.WhenReceiveShortageExcessView

import javax.inject.Inject
import scala.concurrent.Future

class WhenReceiveShortageExcessController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val withMovement: MovementAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: WhenReceiveShortageExcessFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhenReceiveShortageExcessView
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(WhenReceiveShortageExcessPage, formProvider(request.movementDetails.dateOfDispatch)), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider(request.movementDetails.dateOfDispatch).bindFromRequest().fold(
        renderView(BadRequest, _, mode),
        value =>
          saveAndRedirect(WhenReceiveShortageExcessPage, value, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(form, routes.WhenReceiveShortageExcessController.onSubmit(request.ern, request.arc, mode))))
}
