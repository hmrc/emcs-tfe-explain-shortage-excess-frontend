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

package fixtures.messages

import fixtures.i18n

object WhenReceiveShortageExcessMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val p1: String
    val p2: String
    val h2: String
    val requiredError: String
    val twoRequiredError: (String, String) => String
    val oneRequiredError: String => String
    val invalidDate: String
    val notBeforeDateOfDispatch: String => String
    val notInFuture: String
    val checkAnswersLabel: String
    val checkAnswersHiddenChangeLinkText: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = titleHelper("Tell HMRC about a shortage or excess")
    override val heading = "Tell HMRC about a shortage or excess"
    override val p1 = "Unlike with a report of receipt, the information you give here will only be visible to you and to HMRC."
    override val p2 = "If you are a Northern Ireland warehousekeeper receiving goods from an EU member state, this information will also be visible to the state of dispatch."
    override val h2 = "When did you receive the shortage or excess?"
    override val requiredError = "Enter the date you received the shortage or excess"
    override val twoRequiredError = (x: String, y: String) => s"The date you received the shortage or excess must include a $x and $y"
    override val oneRequiredError = (x: String) => s"The date you received the shortage or excess must include a $x"
    override val invalidDate = "The date you received the shortage or excess must be a real date"
    override val notBeforeDateOfDispatch = (dispatchDate: String) => s"The date you received the shortage or excess must be the same as or after $dispatchDate when the movement started"
    override val notInFuture = "The date you received the shortage or excess must be today or in the past"
    override val checkAnswersLabel = "Date shortage or excess was received"
    override val checkAnswersHiddenChangeLinkText = "date movement received"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title: String = titleHelper("Tell HMRC about a shortage or excess")
    override val heading = "Tell HMRC about a shortage or excess"
    override val p1 = "Unlike with a report of receipt, the information you give here will only be visible to you and to HMRC."
    override val p2 = "If you are a Northern Ireland warehousekeeper receiving goods from an EU member state, this information will also be visible to the state of dispatch."
    override val h2 = "When did you receive the shortage or excess?"
    override val requiredError = "Enter the date you received the shortage or excess"
    override val twoRequiredError = (x: String, y: String) => s"The date you received the shortage or excess must include a $x and $y"
    override val oneRequiredError = (x: String) => s"The date you received the shortage or excess must include a $x"
    override val invalidDate = "The date you received the shortage or excess must be a real date"
    override val notBeforeDateOfDispatch = (dispatchDate: String) => s"The date you received the shortage or excess must be the same as or after $dispatchDate when the movement started"
    override val notInFuture = "The date you received the shortage or excess must be today or in the past"
    override val checkAnswersLabel = "Date shortage or excess was received"
    override val checkAnswersHiddenChangeLinkText = "date movement received"
  }
}
