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
import pages.ChooseShortageExcessItemPage
import pages.individualItems.GiveInformationItemPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{ReferenceDataService, UserAnswersService}
import views.html.GiveInformationItemView

import javax.inject.Inject
import scala.concurrent.Future

class GiveInformationItemController @Inject()(override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: Navigator,
                                              override val auth: AuthAction,
                                              override val withMovement: MovementAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              override val userAllowList: UserAllowListAction,
                                              formProvider: CharacterCounterFormProvider,
                                              referenceDataService: ReferenceDataService,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: GiveInformationItemView) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      renderView(Ok, fillForm(GiveInformationItemPage(idx), formProvider(GiveInformationItemPage(idx))), idx, mode)
    }

  def onSubmit(ern: String, arc: String, idx: Int, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request: DataRequest[_] =>
      submitAndTrimWhitespaceFromTextarea[String](GiveInformationItemPage(idx), formProvider)(
        renderView(BadRequest, _, idx, mode),
        saveAndRedirect(GiveInformationItemPage(idx), _, mode)
      )
    }

  private def renderView(status: Status, form: Form[_], idx: Int, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    withMovementItemAsync(idx) { item =>
      referenceDataService.itemWithReferenceData(item) { (itemWithRefData, cnCode) =>
        withAnswer(ChooseShortageExcessItemPage(idx)) { shortageOrExcess =>
          Future.successful(status(view(
            form,
            routes.GiveInformationItemController.onSubmit(request.ern, request.arc, idx, mode),
            itemWithRefData,
            cnCode,
            shortageOrExcess
          )))
        }
      }
    }
}
