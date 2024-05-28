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

object GiveInformationMovementMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val requiredError: String
    val lengthError: String
    val characterError: String
    val invalidCharacterError: String
    val checkAnswersLabel: String
    val checkAnswersHiddenChangeLinkText: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "Give information about the shortage or excess"
    override val title: String = titleHelper(heading)
    override val requiredError = "Enter information"
    override val lengthError = "Information must be 350 characters or less"
    override val characterError = "Information must contain letters or numbers"
    override val invalidCharacterError = "Information must not include < and > and : and ;"
    override val checkAnswersLabel = "Information about shortage or excess"
    override val checkAnswersHiddenChangeLinkText = "information about shortage or excess"
  }

}
