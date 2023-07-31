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

package views.errors

import base.ViewSpecBase
import fixtures.messages.UnauthorisedMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.{BaseSelectors, ViewBehaviours}
import views.html.auth.errors.UnauthorisedView

class UnauthorisedViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "UnauthorisedView" - {

    Seq(UnauthorisedMessages.English, UnauthorisedMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

        val view = app.injector.instanceOf[UnauthorisedView]

        implicit val doc: Document = Jsoup.parse(view().toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.p(2) -> messagesForLanguage.p2,
          Selectors.bullet(1) -> messagesForLanguage.bullet1,
          Selectors.bullet(2) -> messagesForLanguage.bullet2,
          Selectors.p(3) -> messagesForLanguage.p3
        ))
      }
    }
  }
}
