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

object ContinueDraftMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val requiredError: String
    val p1: String
    val inset: String
    val continueRadio: String
    val startAgainRadio: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "Do you want to continue this draft explanation for a shortage or excess?"
    override val title = titleHelper(heading)
    override val requiredError: String = "Select if you want to continue this draft explanation for a shortage or excess"
    override val p1: String = "An explanation for a shortage or excess is already in progress for this movement. You can continue with this draft, or start a new one."
    override val inset: String = "If you choose to start a new explanation for a shortage or excess this draft will be deleted."
    override val continueRadio: String = "Continue with this draft"
    override val startAgainRadio: String = "Start a new explanation for a shortage or excess"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val heading = "Do you want to continue this draft explanation for a shortage or excess?"
    override val title = titleHelper(heading)
    override val requiredError: String = "Select if you want to continue this draft explanation for a shortage or excess"
    override val p1: String = "An explanation for a shortage or excess is already in progress for this movement. You can continue with this draft, or start a new one."
    override val inset: String = "If you choose to start a new explanation for a shortage or excess this draft will be deleted."
    override val continueRadio: String = "Continue with this draft"
    override val startAgainRadio: String = "Start a new explanation for a shortage or excess"
  }
}
