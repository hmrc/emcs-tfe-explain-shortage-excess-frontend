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

package forms

import fixtures.messages.{ItemAmountMessages, UnitOfMeasureMessages}
import forms.behaviours.IntFieldBehaviours
import models.UnitOfMeasure.Kilograms
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class ItemAmountFormProviderSpec extends IntFieldBehaviours with GuiceOneAppPerSuite {

  val fieldName = "value"

  Seq(
    ItemAmountMessages.English -> UnitOfMeasureMessages.English,
    ItemAmountMessages.Welsh -> UnitOfMeasureMessages.Welsh
  ).foreach { case (messagesForLanguage, unitOfMeasureMessages) =>

    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

    val form = new ItemAmountFormProvider().apply(None, Kilograms)

    "ItemAmountForm" - {

      s"when rendered with lang code ${messagesForLanguage.lang}" - {

        "must return expected error" - {

          "when no value entered" in {

            val result = form.bind(Map(fieldName -> ""))
            result.errors.headOption mustBe Some(FormError(fieldName, "itemAmount.error.required"))
            messages("itemAmount.error.required") mustBe messagesForLanguage.requiredError
          }

          "when more than 15 characters are entered" in {

            val result = form.bind(Map(fieldName -> "1234567890123456"))
            result.errors.headOption mustBe Some(FormError(fieldName, "itemAmount.error.maxLength", Seq(MAX_LENGTH_15)))
            messages("itemAmount.error.maxLength", MAX_LENGTH_15) mustBe messagesForLanguage.maxLengthError(MAX_LENGTH_15)
          }

          "when not numeric" in {

            val result = form.bind(Map(fieldName -> "abc"))
            result.errors.headOption mustBe Some(FormError(fieldName, "itemAmount.error.isNotNumeric"))
            messages("itemAmount.error.isNotNumeric") mustBe messagesForLanguage.isNotNumericError
          }

          "when more than 3dp" in {

            val result = form.bind(Map(fieldName -> "123.1234"))
            result.errors.headOption mustBe Some(FormError(fieldName, "itemAmount.error.threeDecimalPlaces"))
            messages("itemAmount.error.threeDecimalPlaces") mustBe messagesForLanguage.threeDecimalPlacesError
          }

          "when no greater than 0" in {

            val result = form.bind(Map(fieldName -> "0"))
            result.errors.headOption mustBe Some(FormError(fieldName, "itemAmount.error.notGreaterThanZero", Seq(0)))
            messages("itemAmount.error.notGreaterThanZero", 0) mustBe messagesForLanguage.notGreaterThanZeroError
          }

          "when value exceeds the max amount passed to the form" in {

            val form = new ItemAmountFormProvider().apply(Some(12.56), Kilograms)
            val result = form.bind(Map(fieldName -> "12.57"))
            result.errors.headOption mustBe Some(FormError(fieldName, "itemAmount.error.exceedsMaxAmount", Seq(12.56, unitOfMeasureMessages.kilogramsShort)))
            messages("itemAmount.error.exceedsMaxAmount", 12.56, unitOfMeasureMessages.kilogramsShort) mustBe
              messagesForLanguage.exceedsMaxAmountError(12.56, unitOfMeasureMessages.kilogramsShort)
          }
        }

        "must return the bound form" - {

          "when 0dp" in {

            val result = form.bind(Map(fieldName -> "1"))
            result.value mustBe Some(1)
          }

          "when 1dp" in {

            val result = form.bind(Map(fieldName -> "1.1"))
            result.value mustBe Some(1.1)
          }

          "when 2dp" in {

            val result = form.bind(Map(fieldName -> "1.12"))
            result.value mustBe Some(1.12)
          }

          "when 3dp" in {

            val result = form.bind(Map(fieldName -> "1.123"))
            result.value mustBe Some(1.123)
          }
        }
      }
    }
  }
}
