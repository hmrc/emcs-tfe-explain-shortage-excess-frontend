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

object ChooseShortageExcessItemMessages {

  sealed trait ViewMessages { _: i18n =>
    def title(idx: Int): String
    def heading(idx:Int): String
    def hint(idx: Int): String
    val shortage: String
    val excess: String
    val errorRequired: String
  }

  object English extends ViewMessages with BaseEnglish {
    override def title(idx: Int): String = titleHelper(s"What's wrong with item $idx?")
    override def heading(idx: Int) = s"What's wrong with item $idx?"
    override def hint(idx: Int) = s"View item $idx details"
    override val shortage = "Shortage"
    override val excess = "Excess"
    override val errorRequired = "Select if you received a shortage or excess of this item"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override def title(idx: Int): String = titleHelper(s"What's wrong with item $idx?")

    override def heading(idx: Int) = s"What's wrong with item $idx?"

    override def hint(idx: Int) = s"View item $idx details"

    override val shortage = "Shortage"
    override val excess = "Excess"
    override val errorRequired = "Select if you received a shortage or excess of this items"
  }
}
