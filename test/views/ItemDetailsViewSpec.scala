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
import fixtures.messages.ItemDetailsMessages
import models.ReferenceDataUnitOfMeasure.`1`
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ItemDetailsView

class ItemDetailsViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ItemDetailsView" - {

    Seq(ItemDetailsMessages.English, ItemDetailsMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[ItemDetailsView]

        implicit val doc: Document = Jsoup.parse(view(item2, CnCodeInformation("testdata", "product code desc", `1`)).toString)

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(item2.itemUniqueReference),
          Selectors.h1 -> messagesForLanguage.h1(item2.itemUniqueReference),
          Selectors.h2(1) -> messagesForLanguage.h2(request.arc),
          Selectors.id("commodity-code") -> (item2.cnCode + " " + msgs("site.opensInNewTab")),
          Selectors.cardHeader(1) -> messagesForLanguage.itemDetailsCardHeading,
          Selectors.cardHeader(2) -> messagesForLanguage.packagingCardHeading(1),
          Selectors.cardHeader(3) -> messagesForLanguage.packagingCardHeading(2)
        ))
      }
    }
  }
}
