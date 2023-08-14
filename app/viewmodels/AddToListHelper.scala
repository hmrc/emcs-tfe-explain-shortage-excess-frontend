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

package viewmodels

import controllers.routes
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.{CheckMode, ChooseShortageExcessItem, ReviewMode, UnitOfMeasure}
import pages.individualItems.{ChooseShortageExcessItemPage, GiveInformationItemPage, ItemAmountPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import utils.JsonOptionFormatter
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

import javax.inject.Inject

class AddToListHelper @Inject()(link: link) extends JsonOptionFormatter {
  def summaryList(item: MovementItem, unitOfMeasure: UnitOfMeasure, onFinalCheckAnswers: Boolean = false)
                 (implicit request: DataRequest[_], messages: Messages): SummaryList = {
    val additionalLinkIdSignifier = if (onFinalCheckAnswers) s"-item-${item.itemUniqueReference}" else ""

    val rows: Seq[SummaryListRow] = request.userAnswers.get(ChooseShortageExcessItemPage(item.itemUniqueReference)).map {
      answer =>
        Seq(
          whatWasWrongRow(answer, item.itemUniqueReference, additionalLinkIdSignifier),
          amountReceivedRow(item.itemUniqueReference, unitOfMeasure, additionalLinkIdSignifier),
          moreInformationRow(answer, item.itemUniqueReference, additionalLinkIdSignifier)
        ).flatten
    }.getOrElse(Seq.empty)

    SummaryListViewModel(rows)
  }


  private def whatWasWrongRow(answer: ChooseShortageExcessItem, idx: Int, additionalLinkIdSignifier: String)
                             (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val mode = if (additionalLinkIdSignifier != "") ReviewMode else CheckMode
    Some(SummaryListRowViewModel(
      key = s"${ChooseShortageExcessItemPage(idx)}.checkYourAnswers.label",
      value = ValueViewModel(messages(s"${ChooseShortageExcessItemPage(idx)}.$answer")),
      actions = Seq(
        ActionItemViewModel(
          "site.change",
          routes.ChooseShortageExcessItemController.onPageLoad(request.ern, request.arc, idx, mode).url,
          id = ChooseShortageExcessItemPage(idx) + additionalLinkIdSignifier
        ).withVisuallyHiddenText(messages(s"${ChooseShortageExcessItemPage(idx)}.checkYourAnswers.change.hidden"))
      )
    ))
  }

  private def amountReceivedRow(idx: Int, unitOfMeasure: UnitOfMeasure, additionalLinkIdSignifier: String)
                               (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val mode = if (additionalLinkIdSignifier != "") ReviewMode else CheckMode
    request.userAnswers.get(ItemAmountPage(idx)) match {
      case Some(answer) if answer.nonEmpty =>
        Some(SummaryListRowViewModel(
          key = s"${ItemAmountPage(idx)}.checkYourAnswers.label",
          value = ValueViewModel(
            messages(
              s"${ItemAmountPage(idx)}.checkYourAnswers.amount.value",
              answer.get,
              messages(s"unitOfMeasure.$unitOfMeasure.long")
            )
          ),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.ItemAmountController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
              id = ItemAmountPage(idx) + additionalLinkIdSignifier
            ).withVisuallyHiddenText(messages(s"${ItemAmountPage(idx)}.checkYourAnswers.change.hidden"))
          )
        ))
      case _ =>
        Some(SummaryListRowViewModel(
          key = s"${ItemAmountPage(idx)}.checkYourAnswers.label",
          value = ValueViewModel(HtmlContent(link(
            link = routes.ItemAmountController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, idx, mode).url,
            messageKey = s"${ItemAmountPage(idx)}.checkYourAnswers.addMoreInformation"
          ))),
          actions = Seq()
        ))
    }
  }

  private def moreInformationRow(shortageOrExcess: ChooseShortageExcessItem, idx: Int, additionalLinkIdSignifier: String)
                                (implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val mode = if (additionalLinkIdSignifier != "") ReviewMode else CheckMode
    Some(request.userAnswers.get(GiveInformationItemPage(idx)) match {
      case Some(answer) if answer.length > 0 =>
        SummaryListRowViewModel(
          key = s"${GiveInformationItemPage(idx)}.checkYourAnswers.$shortageOrExcess.label",
          value = ValueViewModel(Text(answer)),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.GiveInformationItemController.onPageLoad(request.ern, request.arc, idx, mode).url,
              id = GiveInformationItemPage(idx) + additionalLinkIdSignifier
            ).withVisuallyHiddenText(messages(s"${GiveInformationItemPage(idx)}.checkYourAnswers.$shortageOrExcess.change.hidden"))
          )
        )
      case _ =>
        SummaryListRowViewModel(
          key = s"${GiveInformationItemPage(idx)}.checkYourAnswers.$shortageOrExcess.label",
          value = ValueViewModel(HtmlContent(link(
            link = routes.GiveInformationItemController.onPageLoad(request.ern, request.arc, idx, mode).url,
            messageKey = s"${GiveInformationItemPage(idx)}.checkYourAnswers.$shortageOrExcess.addMoreInformation"
          ))),
          actions = Seq()
        )
    })
  }

}
