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
import forms.AddAnotherItemFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import navigation.Navigator
import pages.individualItems.{AddToListPage, GiveInformationItemPage, ItemAmountPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import utils.JsonOptionFormatter
import viewmodels.AddToListHelper
import views.html.AddToListView

import javax.inject.Inject
import scala.concurrent.Future

class AddToListController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     override val auth: AuthAction,
                                     override val userAllowList: UserAllowListAction,
                                     override val withMovement: MovementAction,
                                     override val getData: DataRetrievalAction,
                                     override val requireData: DataRequiredAction,
                                     override val controllerComponents: MessagesControllerComponents,
                                     view: AddToListView,
                                     formProvider: AddAnotherItemFormProvider,
                                     getCnCodeInformationService: GetCnCodeInformationService,
                                     addToListHelper: AddToListHelper,
                                     override val userAnswersService: UserAnswersService,
                                     override val navigator: Navigator) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withCompletedItems { items =>
        formattedItems(items).map { formattedItems =>
          if(formattedItems.flatMap(_._2.rows).isEmpty) {
            Redirect(routes.SelectItemController.onPageLoad(ern, arc))
          } else {
            val allItemsAdded = formattedItems.size == request.movementDetails.items.size
            Ok(view(Some(formProvider()), formattedItems, allItemsAdded, routes.AddToListController.onSubmit(ern, arc)))
          }
        }
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withCompletedItems { items =>
        formattedItems(items).map { formattedItems =>
          val allItemsAdded = formattedItems.size == request.movementDetails.items.size
          if (allItemsAdded) {
            onwardRedirect(formattedItems, allItemsAdded)
          } else {
            formProvider().bindFromRequest().fold(
              formWithErrors =>
                BadRequest(view(Some(formWithErrors), formattedItems, allItemsAdded, routes.AddToListController.onSubmit(ern, arc)))
              ,
              {
                case true => addAnotherItemRedirect()
                case _ => onwardRedirect(formattedItems, allItemsAdded)
              }
            )
          }
        }
      }
    }

  private def formattedItems(items: Seq[MovementItem])(implicit request: DataRequest[_]): Future[Seq[(Int, SummaryList)]] =
    getCnCodeInformationService.getCnCodeInformationWithMovementItems(items).flatMap { serviceResult =>
      Future.sequence(serviceResult.map {
        case (item, cnCodeInformation) => item.itemUniqueReference -> addToListHelper.summaryList(
          item = item,
          unitOfMeasure = cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure
        )
      }.map {
        case (idx, futureSummaryList) => futureSummaryList.map { summaryList => (idx, summaryList) }
      })
    }

  private def withCompletedItems(f: Seq[MovementItem] => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.getAllCompletedItemDetails match {
      case items if items.isEmpty => Future.successful(addAnotherItemRedirect())
      case items => f(items)
    }

  private def addAnotherItemRedirect()(implicit request: DataRequest[_]): Result =
    Redirect(routes.SelectItemController.onPageLoad(request.ern, request.arc))

  private[controllers] def hasAmountForAtLeastOneItem()(implicit request: DataRequest[_]): Boolean = {
    request.userAnswers.itemReferences.map {
      uniqueReference =>
        request.userAnswers.get(ItemAmountPage(uniqueReference)).flatten
    }.map(_.getOrElse[BigDecimal](0)).sum > 0
  }

  private[controllers] def hasMoreInfoForAllItems()(implicit request: DataRequest[_]): Boolean = {
    val infos: Seq[Option[String]] = request.userAnswers.itemReferences.map {
      uniqueReference =>
        request.userAnswers.get(GiveInformationItemPage(uniqueReference))
    }

    lazy val allValuesAreSome: Boolean = !infos.exists(_.isEmpty)
    lazy val allSomesAreNotEmptyString: Boolean = !infos.flatMap(_.map(_.nonEmpty)).contains(false)

    allValuesAreSome && allSomesAreNotEmptyString
  }

  private[controllers] def onwardRedirect(serviceResult: Seq[(Int, SummaryList)],
                                          allItemsAdded: Boolean)
                                         (implicit request: DataRequest[_]): Result = {
    val hasAmount: Boolean = hasAmountForAtLeastOneItem()
    val hasInfo: Boolean = hasMoreInfoForAllItems()
    if (hasAmount && hasInfo) {
      Redirect(navigator.nextPage(AddToListPage, NormalMode, request.userAnswers))
    } else {
      val formWithError: Form[Boolean] =
        Some(formProvider())
          .map(fp => if (!hasAmount) fp.withGlobalError("addToList.error.atLeastOneItem.amount") else fp)
          .map(fp => if (!hasInfo) fp.withGlobalError("addToList.error.atLeastOneItem.information") else fp)
          .get
          .fill(false)

      BadRequest(
        view(Some(formWithError), serviceResult, allItemsAdded, routes.AddToListController.onSubmit(request.ern, request.arc))
      )
    }
  }

}
