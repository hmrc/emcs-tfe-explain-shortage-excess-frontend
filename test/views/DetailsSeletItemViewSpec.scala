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
import fixtures.messages.DetailsSelectItemMessages
import forms.DetailsSelectItemFormProvider
import models.ReferenceDataUnitOfMeasure.`1`
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.DetailsSelectItemView

class DetailsSeletItemViewSpec extends ViewSpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "DetailsSelectItemView" - {

    Seq(DetailsSelectItemMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val form = app.injector.instanceOf[DetailsSelectItemFormProvider].apply()
        val view = app.injector.instanceOf[DetailsSelectItemView]

        val cnCodeInformation = CnCodeInformation("testdata", "product code desc", `1`)

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, item1, cnCodeInformation).toString())

        // scalastyle:off magic.number
        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.h1(item1.itemUniqueReference),
          Selectors.summaryRowKey(1) -> messagesForLanguage.tableProductCategoryKey,
          Selectors.summaryRowValue(1) -> cnCodeInformation.exciseProductCodeDescription,
          Selectors.summaryRowKey(2) -> messagesForLanguage.tableCNCodeKey,
          Selectors.summaryRowValue(2) -> item1.cnCode,
          Selectors.summaryRowKey(3) -> messagesForLanguage.tableBrandNameKey,
          Selectors.summaryRowValue(3) -> item1.brandNameOfProduct.getOrElse(""),
          Selectors.summaryRowKey(4) -> messagesForLanguage.tableCommercialDescriptionKey,
          Selectors.summaryRowValue(4) -> item1.commercialDescription.getOrElse(""),
          Selectors.summaryRowKey(5) -> messagesForLanguage.tableQuantityKey,
          Selectors.summaryRowValue(5) -> (item1.quantity.toString() + " kilograms"),
          Selectors.summaryRowKey(6) -> messagesForLanguage.tableAlcoholStrengthKey,
          Selectors.summaryRowValue(6) -> messagesForLanguage.alcoholStrength(item1.alcoholicStrength),
          Selectors.summaryRowKey(7) -> messagesForLanguage.tableDensityKey,
          Selectors.summaryRowValue(7) -> messagesForLanguage.density(item1.density),
          Selectors.link(1) -> messagesForLanguage.viewAllDetails(1),
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.id("save-and-exit") -> messagesForLanguage.savePreviousAnswersAndExit
        ))
        // scalastyle:on magic.number
      }

    }

  }
}
