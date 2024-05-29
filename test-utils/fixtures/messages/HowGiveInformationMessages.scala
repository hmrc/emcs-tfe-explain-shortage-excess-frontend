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

object HowGiveInformationMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val whole: String
    val choose: String
    val errorRequired: String
    val checkAnswersLabel: String
    val checkAnswersHiddenChangeLinkText: String
    val checkAnswersValueWholeMovement: String
    val checkAnswersValueIndividualItems: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = titleHelper("How do you want to give information about the shortage or excess?")
    override val heading = "How do you want to give information about the shortage or excess?"
    override val whole = "I want to give information about the whole movement"
    override val choose = "I want to choose which item(s) to give information about"
    override val errorRequired = "Select how you want to give information about the shortage or excess"
    override val checkAnswersLabel = "Information given"
    override val checkAnswersHiddenChangeLinkText = "how information is given"
    override val checkAnswersValueWholeMovement = "Whole movement"
    override val checkAnswersValueIndividualItems = "Specific items"
  }

}
