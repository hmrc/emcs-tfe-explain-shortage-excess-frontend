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

object GiveInformationItemMessages {

  sealed trait ViewMessages { _: i18n =>
    val headingShortage: Int => String
    val titleShortage: Int => String
    val headingExcess: Int => String
    val titleExcess: Int => String
    val viewItem: Int => String
    val requiredError: String
    val lengthError: String
    val characterError: String
    val invalidCharacterError: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val headingShortage: Int => String = "Give more information about receiving a shortage of item " + _
    override val titleShortage: Int => String = i => titleHelper(headingShortage(i))
    override val headingExcess: Int => String = "Give more information about receiving an excess of item " + _
    override val titleExcess: Int => String = i => titleHelper(headingExcess(i))
    override val viewItem: Int => String = i => s"View item $i details"
    override val requiredError = "Enter information"
    override val lengthError = "Information must be 350 characters or less"
    override val characterError = "Information must contain letters or numbers"
    override val invalidCharacterError = "Information must not include < and > and : and ;"
  }

}
