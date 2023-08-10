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
import fixtures.messages.ChooseShortageExcessItemMessages
import forms.ChooseShortageExcessItemFormProvider
import models.ReferenceDataUnitOfMeasure.`1`
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ChooseShortageExcessItemView

class ChooseShortageExcessItemViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ChooseShortageExcessItem view" - {

    Seq(ChooseShortageExcessItemMessages.English, ChooseShortageExcessItemMessages.Welsh).foreach {
      messagesForLanguage =>

        s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

          val form = app.injector.instanceOf[ChooseShortageExcessItemFormProvider].apply()
          val view = app.injector.instanceOf[ChooseShortageExcessItemView]

          implicit val doc: Document = Jsoup.parse(view(testIdx, item1, CnCodeInformation("testdata", "product code desc", `1`), form, testOnwardRoute).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(1),
            Selectors.h1 -> messagesForLanguage.heading(1),
            Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
            Selectors.hint -> messagesForLanguage.hint(1),
            Selectors.radioButton(1) -> messagesForLanguage.shortage,
            Selectors.radioButton(2) -> messagesForLanguage.excess,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.savePreviousAnswersAndExit
          ))
        }
    }
  }
}
