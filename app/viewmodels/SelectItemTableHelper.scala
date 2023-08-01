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

import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import views.html.components.{link, list}

import javax.inject.Inject

class SelectItemTableHelper @Inject()(link: link, list: list) {

  private[viewmodels] def headerRow(implicit messages: Messages): Option[Seq[HeadCell]] = Some(Seq(
    HeadCell(Text(messages("selectItem.table.heading.item"))),
    HeadCell(Text(messages("selectItem.table.heading.description"))),
    HeadCell(Text(messages("selectItem.table.heading.quantity"))),
    HeadCell(Text(messages("selectItem.table.heading.packaging")))
  ))

  private[viewmodels] def dataRows(ern: String, arc: String, items: Seq[(MovementItem, CnCodeInformation)])(implicit messages: Messages): Seq[Seq[TableRow]] =
    items.sortBy(_._1.itemUniqueReference).map {
      case (item, cnCodeInformation) =>
        Seq(
          TableRow(
            content = HtmlContent(link(
              // TODO: change when /details-select-item is created
              link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
              messageKey = messages("selectItem.table.row.item", item.itemUniqueReference)
            )),
            classes = "white-space-nowrap"
          ),
          TableRow(
            content = Text(messages(cnCodeInformation.cnCodeDescription)),
            classes = "govuk-!-width-one-half"
          ),
          TableRow(
            content = Text(messages(
              "selectItem.table.row.quantity",
              item.quantity.toString(),
              messages(s"unitOfMeasure.${cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure}.short")
            ))
          ),
          TableRow(
            content = HtmlContent(list(item.packaging.map(pckg =>
              Html(messages("selectItem.table.row.packaging", pckg.quantity.getOrElse(0), pckg.typeOfPackage))
            )))
          )
        )
    }

  def constructTable(movements: Seq[(MovementItem, CnCodeInformation)])(implicit messages: Messages, request: DataRequest[_]): Table =
    Table(
      firstCellIsHeader = true,
      rows = dataRows(request.ern, request.arc, movements),
      head = headerRow
    )
}
