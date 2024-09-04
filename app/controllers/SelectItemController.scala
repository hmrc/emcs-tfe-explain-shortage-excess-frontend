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
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import navigation.Navigator
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{ReferenceDataService, UserAnswersService}
import views.html.SelectItemView

import javax.inject.Inject
import scala.concurrent.Future

class SelectItemController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: Navigator,
                                      override val auth: AuthAction,
                                      override val withMovement: MovementAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      val controllerComponents: MessagesControllerComponents,
                                      referenceDataService: ReferenceDataService,
                                      view: SelectItemView
                                    ) extends BaseNavigationController with AuthActionHelper {


  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withAtLeastOneIncompleteItem {
        referenceDataService.getMovementItemsWithReferenceData(_).map { serviceResult =>
          Ok(view(serviceResult))
        }
      }
    }

  private[controllers] def withAtLeastOneIncompleteItem(f: Seq[MovementItem] => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    incompleteItems() match {
      case incompleteItems if incompleteItems.nonEmpty =>
        f(incompleteItems)
      case _ =>
        Future.successful(Redirect(routes.AddToListController.onPageLoad(request.ern, request.arc)))
    }

  private[controllers] def incompleteItems()(implicit request: DataRequest[_]): Seq[MovementItem] =
    request.movementDetails.items.filterNot { movementDetailsItem =>
      request.userAnswers.completedItems.exists(_.itemUniqueReference == movementDetailsItem.itemUniqueReference)
    }
}
