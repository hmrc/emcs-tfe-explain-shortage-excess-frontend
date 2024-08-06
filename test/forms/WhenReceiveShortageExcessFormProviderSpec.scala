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

import fixtures.messages.WhenReceiveShortageExcessMessages
import forms.behaviours.DateBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Lang, Messages, MessagesApi}
import utils.{DateUtils, TimeMachine}

import java.time.{Instant, LocalDate, LocalDateTime}

class WhenReceiveShortageExcessFormProviderSpec extends DateBehaviours with DateUtils with GuiceOneAppPerSuite {

  val fixedNow = LocalDate.of(2023, 2, 9)
  val dateOfDispatch = fixedNow.minusDays(3)

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(Lang("en")))

  val timeMachine = new TimeMachine {
    override def now(): LocalDateTime = fixedNow.atStartOfDay()
    override def instant(): Instant = Instant.now
  }

  val form = new WhenReceiveShortageExcessFormProvider(timeMachine)(dateOfDispatch)

  ".value" - {

    val validData = datesBetween(
      min = dateOfDispatch,
      max = fixedNow
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "whenReceiveShortageExcess.error.required.all")

    "accept a date that is equal to the dateOfDispatch" in {

      val date = dateOfDispatch

      val data = Map(
        s"value.day" -> date.getDayOfMonth.toString,
        s"value.month" -> date.getMonthValue.toString,
        s"value.year" -> date.getYear.toString
      )

      val result = form.bind(data)

      result.errors mustBe Seq()
      result.value mustBe Some(date)
    }

    "return an error when date is before dateOfDispatch" in {

      val date = dateOfDispatch.minusDays(1)

      val data = Map(
        s"value.day" -> date.getDayOfMonth.toString,
        s"value.month" -> date.getMonthValue.toString,
        s"value.year" -> date.getYear.toString
      )

      val result = form.bind(data)
      result.errors must contain only FormError("value", "whenReceiveShortageExcess.error.notBeforeDateOfDispatch", Seq(dateOfDispatch.formatDateForUIOutput()))
    }

    "return an error when date is after todays date" in {

      val date = fixedNow.plusDays(1)

      val data = Map(
        s"value.day" -> date.getDayOfMonth.toString,
        s"value.month" -> date.getMonthValue.toString,
        s"value.year" -> date.getYear.toString
      )

      val result = form.bind(data)
      result.errors must contain only FormError("value", "whenReceiveShortageExcess.error.notInFuture")
    }

    "accept a date that is equal to todays date" in {

      val date = fixedNow

      val data = Map(
        s"value.day" -> date.getDayOfMonth.toString,
        s"value.month" -> date.getMonthValue.toString,
        s"value.year" -> date.getYear.toString
      )

      val result = form.bind(data)

      result.errors mustBe Seq()
      result.value mustBe Some(date)
    }
  }

  "Error Messages" - {

    Seq(WhenReceiveShortageExcessMessages.English) foreach { messagesForLanguage =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for no date" in {
          messages("whenReceiveShortageExcess.error.required.all") mustBe
            messagesForLanguage.requiredError
        }

        "have the correct error message for when one of the fields is missing" in {
          messages("whenReceiveShortageExcess.error.required.two", "day", "month") mustBe
            messagesForLanguage.twoRequiredError("day", "month")
        }

        "have the correct error message for when two of the fields are missing" in {
          messages("whenReceiveShortageExcess.error.required", "day") mustBe
            messagesForLanguage.oneRequiredError("day")
        }

        "have the correct error message for when the date isn't a real date" in {
          messages("whenReceiveShortageExcess.error.invalid") mustBe
            messagesForLanguage.invalidDate
        }

        "have the correct error message for when the date is before the date of dispatch" in {
          messages("whenReceiveShortageExcess.error.notBeforeDateOfDispatch", dateOfDispatch.formatDateForUIOutput()) mustBe
            messagesForLanguage.notBeforeDateOfDispatch("6 February 2023")
        }

        "have the correct error message for when the date is in the future" in {
          messages("whenReceiveShortageExcess.error.notInFuture") mustBe
            messagesForLanguage.notInFuture
        }
      }
    }
  }
}
