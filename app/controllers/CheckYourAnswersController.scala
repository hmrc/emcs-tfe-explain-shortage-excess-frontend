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
import handlers.ErrorHandler
import models.HowGiveInformation.Choose
import models.requests.DataRequest
import models.response.emcsTfe.{MovementItem, SubmitShortageExcessResponse}
import models.{ConfirmationDetails, MissingMandatoryPage, NormalMode, UserAnswers}
import navigation.Navigator
import pages.{CheckAnswersPage, ConfirmationPage, HowGiveInformationPage}
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, SubmitShortageExcessService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.AddToListHelper
import viewmodels.checkAnswers.CheckAnswersHelper
import views.html.CheckYourAnswersView

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.Future

class CheckYourAnswersController @Inject()(override val messagesApi: MessagesApi,
                                           override val userAnswersService: UserAnswersService,
                                           override val auth: AuthAction,
                                           override val withMovement: MovementAction,
                                           override val getData: DataRetrievalAction,
                                           override val requireData: DataRequiredAction,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: Navigator,
                                           view: CheckYourAnswersView,
                                           checkAnswersHelper: CheckAnswersHelper,
                                           checkAnswersItemHelper: AddToListHelper,
                                           getCnCodeInformationService: GetCnCodeInformationService,
                                           submitShortageOrExcessService: SubmitShortageExcessService,
                                           errorHandler: ErrorHandler
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withAllCompletedItemsAsync() { items =>
        guardPageAccess(items) {
          val formattedAnswersFuture: Future[Seq[(Int, SummaryList)]] = {
            if (items.nonEmpty) {
              getCnCodeInformationService.getCnCodeInformationWithMovementItems(items).map {
                serviceResult =>
                  serviceResult.map {
                    case (item, cnCodeInformation) =>
                      item.itemUniqueReference -> checkAnswersItemHelper.summaryList(
                        item,
                        unitOfMeasure = cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure,
                        onFinalCheckAnswers = true
                      )
                  }
              }
            } else {
              Future.successful(Seq())
            }
          }


          formattedAnswersFuture.map { formattedAnswers =>
            val moreItemsToAdd: Boolean =
              (request.movementDetails.items.size != items.size) && items.nonEmpty

            Ok(view(routes.CheckYourAnswersController.onSubmit(ern, arc),
              routes.SelectItemController.onPageLoad(ern, arc).url,
              checkAnswersHelper.summaryList(),
              formattedAnswers,
              moreItemsToAdd
            ))
          }
        }
      }
    }

  private[controllers] def guardPageAccess(items: Seq[MovementItem])(block: => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.get(HowGiveInformationPage) match {
      case Some(Choose) if items.isEmpty => Future.successful(Redirect(routes.SelectItemController.onPageLoad(request.ern, request.arc)))
      case _ => block
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      submitShortageOrExcessService.submit(ern, arc).flatMap {
        response =>
          logger.debug(s"[onSubmit] response received from downstream service ${response.downstreamService}: ${response.receipt}")

          deleteDraftAndSetConfirmationFlow(response).map { userAnswers =>
            Redirect(navigator.nextPage(CheckAnswersPage, NormalMode, userAnswers))
          }
      } recover {
        case _: MissingMandatoryPage =>
          BadRequest(errorHandler.badRequestTemplate)
        case _ =>
          InternalServerError(errorHandler.internalServerErrorTemplate)
      }
    }


  private def deleteDraftAndSetConfirmationFlow(response: SubmitShortageExcessResponse)
                                               (implicit hc: HeaderCarrier, request: DataRequest[_]): Future[UserAnswers] = {
    userAnswersService.set(
      UserAnswers(
        request.ern,
        request.arc,
        data = Json.obj(ConfirmationPage.toString ->
          Json.toJson(ConfirmationDetails(
            receipt = response.receipt,
            dateOfSubmission = LocalDateTime.now()
          )))
      ))
  }

}
