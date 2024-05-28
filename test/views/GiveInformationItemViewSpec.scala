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
import fixtures.messages.GiveInformationItemMessages
import forms.CharacterCounterFormProvider
import models.ChooseShortageExcessItem.{Excess, Shortage}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.individualItems.GiveInformationItemPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.GiveInformationItemView

class GiveInformationItemViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val day = "label[for='value.day']"
    val month = "label[for='value.month']"
    val year = "label[for='value.year']"
    val legend = "legend h2"
  }

  "GiveInformationItem view" - {

    Seq(GiveInformationItemMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val form = app.injector.instanceOf[CharacterCounterFormProvider].apply(GiveInformationItemPage(item1.itemUniqueReference))
        val view = app.injector.instanceOf[GiveInformationItemView]

        "when rendered for Shortage variation" - {

          implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, item1WithReferenceData, cnCodeInfo, Shortage).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleShortage(item1.itemUniqueReference),
            Selectors.h1 -> messagesForLanguage.headingShortage(item1.itemUniqueReference),
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.detailsSummary(1) -> messagesForLanguage.viewItem(item1.itemUniqueReference),
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.savePreviousAnswersAndExit
          ))
        }

        "when rendered for Excess variation" - {

          implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, item1WithReferenceData, cnCodeInfo, Excess).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleExcess(item1.itemUniqueReference),
            Selectors.h1 -> messagesForLanguage.headingExcess(item1.itemUniqueReference)
          ))
        }
      }
    }
  }
}
