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
import fixtures.messages.WhenReceiveShortageExcessMessages
import forms.WhenReceiveShortageExcessFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.WhenReceiveShortageExcessView

import java.time.LocalDate

class WhenReceiveShortageExcessViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val day = "label[for='value.day']"
    val month = "label[for='value.month']"
    val year = "label[for='value.year']"
    val legend = "legend h2"
  }

  "WhenReceiveShortageExcess view" - {

    Seq(WhenReceiveShortageExcessMessages.English, WhenReceiveShortageExcessMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val form = app.injector.instanceOf[WhenReceiveShortageExcessFormProvider].apply(LocalDate.now())
        val view = app.injector.instanceOf[WhenReceiveShortageExcessView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.p(2) -> messagesForLanguage.p2,
          Selectors.legend -> messagesForLanguage.h2,
          Selectors.day -> messagesForLanguage.day,
          Selectors.month -> messagesForLanguage.month,
          Selectors.year -> messagesForLanguage.year,
          Selectors.button -> messagesForLanguage.saveAndContinue
        ))
      }
    }
  }
}
