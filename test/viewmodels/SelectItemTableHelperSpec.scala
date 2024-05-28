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

import base.SpecBase
import fixtures.messages.SelectItemMessages
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.CnCodeInformation
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, TableRow}
import views.html.components.{link, list}

class SelectItemTableHelperSpec extends SpecBase {

  "SelectItemTableHelper" - {

    Seq(SelectItemMessages.English).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))

        lazy val link = app.injector.instanceOf[link]
        lazy val list = app.injector.instanceOf[list]

        lazy val SelectItemTableHelper = new SelectItemTableHelper(link, list)

        "should render the correct header rows" in {

          SelectItemTableHelper.headerRow mustBe Some(Seq(
            HeadCell(Text(langMessages.tableHeadItem)),
            HeadCell(Text(langMessages.tableHeadDescription)),
            HeadCell(Text(langMessages.tableHeadQuantity)),
            HeadCell(Text(langMessages.tableHeadPackaging))
          ))
        }

        "should render the correct data rows" in {

          SelectItemTableHelper.dataRows(
            ern = testErn,
            arc = testArc,
            items = Seq(item1, item2).zipWithIndex.map { case (l, i) => (l, CnCodeInformation(s"testdata${i + 1}", "", `1`)) }
          ) mustBe Seq(
            Seq(
              TableRow(
                content = HtmlContent(link(
                  link = controllers.routes.DetailsSelectItemController.onPageLoad(testErn, testArc, item1.itemUniqueReference).url,
                  messageKey = langMessages.tableRowItem(item1.itemUniqueReference)
                )),
                classes = "white-space-nowrap",
              ),
              TableRow(
                content = Text("description 1"),
                classes = "govuk-!-width-one-half"
              ),
              TableRow(
                content = Text(item1.quantity.toString() + " " + langMessages.kilogramsShort)
              ),
              TableRow(
                content = HtmlContent(list(Seq(
                  Html(boxPackage.quantity.get.toString() + " " + boxPackage.typeOfPackage)
                )))
              )
            ),
            Seq(
              TableRow(
                content = HtmlContent(link(
                  link = controllers.routes.DetailsSelectItemController.onPageLoad(testErn, testArc, item2.itemUniqueReference).url,
                  messageKey = langMessages.tableRowItem(item2.itemUniqueReference)
                )),
                classes = "white-space-nowrap",
              ),
              TableRow(
                content = Text("description 2"),
                classes = "govuk-!-width-one-half"
              ),
              TableRow(
                content = Text(item2.quantity.toString() + " " + langMessages.kilogramsShort)
              ),
              TableRow(
                content = HtmlContent(list(Seq(
                  Html(boxPackage.quantity.get.toString() + " " + boxPackage.typeOfPackage),
                  Html(cratePackage.quantity.get.toString() + " " + cratePackage.typeOfPackage)
                )))
              )
            )
          )
        }

        "should render the CN code description when the item description isn't present" in {
          SelectItemTableHelper.dataRows(
            ern = testErn,
            arc = testArc,
            items = Seq((item3, CnCodeInformation(s"Some CN Code Description", "", `1`)))
          ) mustBe Seq(
            Seq(
              TableRow(
                content = HtmlContent(link(
                  link = controllers.routes.DetailsSelectItemController.onPageLoad(testErn, testArc, item3.itemUniqueReference).url,
                  messageKey = langMessages.tableRowItem(item3.itemUniqueReference)
                )),
                classes = "white-space-nowrap",
              ),
              TableRow(
                content = Text("Some CN Code Description"),
                classes = "govuk-!-width-one-half"
              ),
              TableRow(
                content = Text(item3.quantity.toString() + " " + langMessages.kilogramsShort)
              ),
              TableRow(
                content = HtmlContent(list(Seq(
                  Html(boxPackage.quantity.get.toString() + " " + boxPackage.typeOfPackage),
                  Html(cratePackage.quantity.get.toString() + " " + cratePackage.typeOfPackage)
                )))
              )
            )
          )
        }
      }
    }
  }
}
