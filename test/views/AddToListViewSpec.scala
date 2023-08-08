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
import fixtures.messages.AddToListMessages
import forms.AddAnotherItemFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.individualItems._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.AddToListHelper
import views.html.AddToListView

class AddToListViewSpec extends ViewSpecBase with ViewBehaviours {

  lazy val form = new AddAnotherItemFormProvider()()
  lazy val addToListHelper = app.injector.instanceOf[AddToListHelper]

  object Selectors extends BaseSelectors {
    val itemCardTitle = (n: Int) => s"main div.govuk-summary-card:nth-of-type($n) h2"
    val itemCardDetailsLink = (n: Int) => s"main div.govuk-summary-card:nth-of-type($n) ul.govuk-summary-card__actions li:nth-of-type(1)"
    val itemCardRemoveLink = (n: Int) => s"main div.govuk-summary-card:nth-of-type($n) ul.govuk-summary-card__actions li:nth-of-type(2)"
  }

  "AddedItemsView" - {

    Seq(AddToListMessages.English, AddToListMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when being rendered with the YesNo form and single item" - {

          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[AddToListView]

          implicit val doc: Document = Jsoup.parse(view(
            Some(form),
            Seq(1 -> SummaryList()),
            allItemsAdded = false,
            testOnwardRoute
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleSingular,
            Selectors.h1 -> messagesForLanguage.headingSingular,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.itemCardTitle(1) -> messagesForLanguage.item(1),
            Selectors.itemCardDetailsLink(1) -> messagesForLanguage.itemDetails(1),
            Selectors.itemCardRemoveLink(1) -> messagesForLanguage.itemRemove(1),
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
          ))
        }

        "when being rendered without the YesNo form and multiple items" - {

          val userAnswers = emptyUserAnswers
            .set(SelectItemPage(1), item1.itemUniqueReference)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), item2.itemUniqueReference)
            .set(CheckAnswersItemPage(2), true)

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[AddToListView]

          implicit val doc: Document = Jsoup.parse(view(
            None,
            Seq(1 -> SummaryList(), 2 -> SummaryList()),
            allItemsAdded = true,
            testOnwardRoute,
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titlePlural(2),
            Selectors.h1 -> messagesForLanguage.headingPlural(2),
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.itemCardTitle(1) -> messagesForLanguage.item(1),
            Selectors.itemCardDetailsLink(1) -> messagesForLanguage.itemDetails(1),
            Selectors.itemCardRemoveLink(1) -> messagesForLanguage.itemRemove(1),
            Selectors.itemCardTitle(2) -> messagesForLanguage.item(2),
            Selectors.itemCardDetailsLink(2) -> messagesForLanguage.itemDetails(2),
            Selectors.itemCardRemoveLink(2) -> messagesForLanguage.itemRemove(2),
            Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
          ))
        }
      }
    }
  }
}
