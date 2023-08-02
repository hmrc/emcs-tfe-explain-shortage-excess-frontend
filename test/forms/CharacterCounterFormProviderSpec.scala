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

import fixtures.messages.GiveInformationMovementMessages
import forms.behaviours.StringFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.GiveInformationMovementPage
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}

class CharacterCounterFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite {

  val maxLength = 350
  val aboveMaxLength = 351

  val form = new CharacterCounterFormProvider()(GiveInformationMovementPage)

  ".value" - {

    "form returns no errors when" - {
      "input is valid" in {
        val data = Map("value" -> "Test 123.")
        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some("Test 123.")
      }

      "input includes a Carriage Return" in {
        val data = Map("value" -> "Test\n123.")
        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some("Test 123.")
      }


      "input is a value of 350 alpha characters that are valid" in {
        val data = Map("value" -> "a" * maxLength)
        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some("a" * maxLength)
      }

      "input begins with a valid special character" in {
        val data = Map("value" -> ".A")
        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some(".A")
      }

      "input is just numbers" in {
        val data = Map("value" -> "123")
        val result = form.bind(data)

        result.errors mustBe Seq()
        result.value mustBe Some("123")
      }
    }

    "form returns an error when" - {
      "alpha numeric data isn't used" in {
        val data = Map("value" -> "..")
        val result = form.bind(data)

        result.errors must contain only FormError("value", "giveInformationMovement.error.character", Seq(ALPHANUMERIC_REGEX))
      }

      "more than 350 characters are used" in {
        val data = Map("value" -> "a" * aboveMaxLength)
        val result = form.bind(data)

        result.errors must contain only FormError("value", "giveInformationMovement.error.length", Seq(maxLength))
      }

      "invalid characters are used" in {
        val data = Map("value" -> "<>")
        val result = form.bind(data)

        result.errors must contain only (Seq(
          FormError("value", "giveInformationMovement.error.character", Seq(ALPHANUMERIC_REGEX)),
          FormError("value", "giveInformationMovement.error.invalidCharacter", Seq(XSS_REGEX))
        ): _*)
      }

      "the required field is empty" in {
        val data = Map("value" -> "")
        val result = form.bind(data)

        result.errors must contain only FormError("value", "giveInformationMovement.error.required")
      }

      "the required field contains only whitespace" in {
        val data = Map("value" ->
          """
            |
            |
            |
            |
            |
            |
            |""".stripMargin)
        val result = form.bind(data)

        result.errors must contain only FormError("value", "giveInformationMovement.error.required")
      }

      "the required field is not present" in {
        val data = Map("something" -> "")
        val result = form.bind(data)

        result.errors must contain only FormError("value", "giveInformationMovement.error.required")
      }
    }
  }

  "have the correct error messages for the GiveInformationMovementPage" - {

    Seq(GiveInformationMovementMessages.English, GiveInformationMovementMessages.Welsh) foreach { messagesForLanguage =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for no value" in {
          messages(s"$GiveInformationMovementPage.error.required") mustBe
            messagesForLanguage.requiredError
        }

        "have the correct error message for when value exceeds max length" in {
          messages(s"$GiveInformationMovementPage.error.length") mustBe
            messagesForLanguage.lengthError
        }

        "have the correct error message for when needs alphanumerics" in {
          messages(s"$GiveInformationMovementPage.error.character") mustBe
            messagesForLanguage.characterError
        }

        "have the correct error message for when invalid characters are included" in {
          messages(s"$GiveInformationMovementPage.error.invalidCharacter") mustBe
            messagesForLanguage.invalidCharacterError
        }
      }
    }
  }
}
