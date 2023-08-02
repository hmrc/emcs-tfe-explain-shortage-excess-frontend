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
import fixtures.messages.{ItemAmountMessages, UnitOfMeasureMessages}
import forms.ItemAmountFormProvider
import models.UnitOfMeasure.Kilograms
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ItemAmountView

class ItemAmountViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val day = "label[for='value.day']"
    val month = "label[for='value.month']"
    val year = "label[for='value.year']"
    val legend = "legend h2"
  }

  "ItemAmount view" - {

    Seq(
      ItemAmountMessages.English -> UnitOfMeasureMessages.English,
      ItemAmountMessages.Welsh -> UnitOfMeasureMessages.Welsh
    ).foreach { case (messagesForLanguage, unitOfMeasureMessages) =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val form = app.injector.instanceOf[ItemAmountFormProvider].apply(None, Kilograms)
        val view = app.injector.instanceOf[ItemAmountView]

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, item1, cnCodeInfo).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(unitOfMeasureMessages.kilogramsLong, item1.itemUniqueReference),
          Selectors.h1 -> messagesForLanguage.heading(unitOfMeasureMessages.kilogramsLong, item1.itemUniqueReference),
          Selectors.h2(1) -> messagesForLanguage.arcSubheading(testArc),
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.savePreviousAnswersAndExit
        ))
      }
    }
  }
}
