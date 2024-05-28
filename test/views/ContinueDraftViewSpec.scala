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
import fixtures.messages.ContinueDraftMessages
import forms.ContinueDraftFormProvider
import org.jsoup.Jsoup
import play.api.test.FakeRequest
import views.html.ContinueDraftView

class ContinueDraftViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  lazy val view = app.injector.instanceOf[ContinueDraftView]

  "ContinueDraftView" - {

    Seq(ContinueDraftMessages.English).foreach { messagesForLanguage =>

      lazy val form = app.injector.instanceOf[ContinueDraftFormProvider].apply()

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(app, messagesForLanguage.lang)
        implicit val request = optionalDataRequest(FakeRequest())
        implicit val doc = Jsoup.parse(view(form, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.inset(1) -> messagesForLanguage.inset,
          Selectors.radioButton(1) -> messagesForLanguage.continueRadio,
          Selectors.radioButton(2) -> messagesForLanguage.startAgainRadio,
          Selectors.button -> messagesForLanguage.continue
        ))


      }
    }
  }
}