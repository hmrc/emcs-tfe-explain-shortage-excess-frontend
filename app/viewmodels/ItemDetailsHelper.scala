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

import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import views.html.components.list

import javax.inject.Inject

class ItemDetailsHelper @Inject()(list: list) {
  //noinspection ScalaStyle
  def constructItemSummaryRows(item: MovementItem, cnCodeInformation: CnCodeInformation)(implicit messages: Messages): Seq[SummaryListRow] = {

    def createSummaryListRow(key: String, value: String): SummaryListRow =
      SummaryListRow(
        Key(Text(key)),
        Value(HtmlContent(value))
      )

    val productCategory = Some(
      createSummaryListRow(
        messages("detailsSelectItem.key.productCategory"),
        cnCodeInformation.exciseProductCodeDescription
      )
    )

    val cnCode = Some(
      createSummaryListRow(
        messages("detailsSelectItem.key.commodityCode"),
        item.cnCode
      )
    )

    val brandName = item.brandNameOfProduct map {
      brandNameOfProduct =>
        createSummaryListRow(
          messages("detailsSelectItem.key.brandNameOfProduct"),
          brandNameOfProduct
        )
    }

    val commercialDescription = item.commercialDescription map {
      commercialDescription =>
        createSummaryListRow(
          messages("detailsSelectItem.key.commercialDescription"),
          commercialDescription
        )
    }

    val quantity = Some(
      createSummaryListRow(
        messages("detailsSelectItem.key.quantity"),
        messages("detailsSelectItem.value.quantity", item.quantity.toString(), messages(s"unitOfMeasure.${cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure}.long"))
      )
    )

    val alcoholStrength = item.alcoholicStrength match {
      case Some(value) =>
        Some(
          createSummaryListRow(
            messages("detailsSelectItem.key.alcoholicStrength"),
            messages("detailsSelectItem.value.alcoholicStrength", value.toString())
          )
        )
      case None => None
    }

    val density = item.density match {
      case Some(value) =>
        Some(
          createSummaryListRow(
            messages("detailsSelectItem.key.density"),
            messages("detailsSelectItem.value.density", value.toString(), messages(s"detailsSelectItem.value.density.${cnCodeInformation.unitOfMeasureCode.toUnitOfMeasure}"))
          )
        )
      case None => None
    }

    val packagingTypes = HtmlContent(list(item.packaging.map(pckg => {
      Html(messages("detailsSelectItem.value.packaging", pckg.quantity.getOrElse(""), pckg.typeOfPackage))
    })))

    val packaging =
      Some(
        SummaryListRow(
          Key(Text(messages("detailsSelectItem.key.packaging"))),
          Value(packagingTypes)
        )
      )

    Seq(
      productCategory,
      cnCode,
      brandName,
      commercialDescription,
      quantity,
      alcoholStrength,
      density,
      packaging
    ).flatten

  }

}
