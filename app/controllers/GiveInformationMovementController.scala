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
import forms.CharacterCounterFormProvider
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.GiveInformationMovementPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.GiveInformationMovementView

import javax.inject.Inject
import scala.concurrent.Future

class GiveInformationMovementController @Inject()(override val messagesApi: MessagesApi,
                                                  override val userAnswersService: UserAnswersService,
                                                  override val navigator: Navigator,
                                                  override val auth: AuthAction,
                                                  override val withMovement: MovementAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  formProvider: CharacterCounterFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: GiveInformationMovementView) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(GiveInformationMovementPage, formProvider(GiveInformationMovementPage)), mode)
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request: DataRequest[_] =>
      submitAndTrimWhitespaceFromTextarea[String](GiveInformationMovementPage, formProvider)(
        renderView(BadRequest, _, mode),
        saveAndRedirect(GiveInformationMovementPage, _, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(form, routes.GiveInformationMovementController.onSubmit(request.ern, request.arc, mode))))
}
