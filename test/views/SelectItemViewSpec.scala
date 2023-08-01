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

package views

import base.ViewSpecBase
import fixtures.messages.SelectItemMessages
import models.ReferenceDataUnitOfMeasure.`1`
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.individualItems.{CheckAnswersItemPage, SelectItemPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.SelectItemView

class SelectItemViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "SelectItemView" - {

    Seq(SelectItemMessages.English, SelectItemMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when 0 items already added" - {

          val userAnswers = emptyUserAnswers

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[SelectItemView]

          implicit val doc: Document = Jsoup.parse(view(
            Seq(item1)
              .zipWithIndex
              .map { case (l, i) => (l, CnCodeInformation(s"testdata${i + 1}", "", `1`)) }
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.tableHeader(1) -> messagesForLanguage.tableHeadItem,
            Selectors.tableHeader(2) -> messagesForLanguage.tableHeadDescription,
            Selectors.tableHeader(3) -> messagesForLanguage.tableHeadQuantity,
            Selectors.tableHeader(4) -> messagesForLanguage.tableHeadPackaging
          ))

          "must not contain #already-added-items-info" in {
            doc.select(Selectors.id("already-added-items-info")).size mustBe 0
          }
        }

        "when 1 item already added" - {

          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[SelectItemView]

          implicit val doc: Document = Jsoup.parse(view(
            Seq(item1)
              .zipWithIndex
              .map { case (l, i) => (l, CnCodeInformation(s"testdata${i + 1}", "", `1`)) }
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.id("already-added-items-info") -> messagesForLanguage.givenInformationInfo(1),
            Selectors.id("view-already-added-items") -> messagesForLanguage.viewAlreadyAddedItems,
            Selectors.tableHeader(1) -> messagesForLanguage.tableHeadItem,
            Selectors.tableHeader(2) -> messagesForLanguage.tableHeadDescription,
            Selectors.tableHeader(3) -> messagesForLanguage.tableHeadQuantity,
            Selectors.tableHeader(4) -> messagesForLanguage.tableHeadPackaging,
            Selectors.tableRow(1, 1) -> messagesForLanguage.tableRowItem(item1.itemUniqueReference),
            Selectors.tableRow(1, 2) -> "testdata1",
            Selectors.tableRow(1, 3) -> (item1.quantity.toString() + " kg")
          ))

          "must contain #already-added-items-info only once" in {
            doc.select(Selectors.id("already-added-items-info")).size mustBe 1
          }
        }

        "when 2 items already added" - {

          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[SelectItemView]

          implicit val doc: Document = Jsoup.parse(view(
            Seq(item1, item2)
              .zipWithIndex
              .map { case (l, i) => (l, CnCodeInformation(s"testdata${i + 1}", "", `1`)) }
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.id("already-added-items-info") -> messagesForLanguage.givenInformationInfo(2),
            Selectors.id("view-already-added-items") -> messagesForLanguage.viewAlreadyAddedItems,
            Selectors.tableHeader(1) -> messagesForLanguage.tableHeadItem,
            Selectors.tableHeader(2) -> messagesForLanguage.tableHeadDescription,
            Selectors.tableHeader(3) -> messagesForLanguage.tableHeadQuantity,
            Selectors.tableHeader(4) -> messagesForLanguage.tableHeadPackaging,
            Selectors.tableRow(1, 1) -> messagesForLanguage.tableRowItem(item1.itemUniqueReference),
            Selectors.tableRow(1, 2) -> "testdata1",
            Selectors.tableRow(1, 3) -> (item1.quantity.toString() + " kg"),
            Selectors.tableRow(2, 1) -> messagesForLanguage.tableRowItem(item2.itemUniqueReference),
            Selectors.tableRow(2, 2) -> "testdata2",
            Selectors.tableRow(2, 3) -> (item2.quantity.toString() + " kg")
          ))

          "must contain #already-added-items-info only once" in {
            doc.select(Selectors.id("already-added-items-info")).size mustBe 1
          }
        }

      }
    }
  }
}
