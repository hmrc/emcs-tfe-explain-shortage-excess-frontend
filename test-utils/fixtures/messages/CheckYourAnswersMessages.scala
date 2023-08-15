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

object CheckYourAnswersMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val movementDetailsH2: String
    val itemsH2: String
    val item: Int => String
    val itemDetails: Int => String
    val itemRemove: Int => String
    val addAnotherItem: String
    val submitH2: String
    val declaration: String
    val submitButton: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "Check your answers"
    override val title: String = titleHelper(heading)
    override val movementDetailsH2 = "Movement"
    override val itemsH2 = "Items"
    override val item = (itemReference: Int) => s"Item $itemReference"
    override val itemDetails = (itemReference: Int) => s"Item details for item $itemReference"
    override val itemRemove = (itemReference: Int) => s"Remove item $itemReference"
    override val addAnotherItem = "Add another item"
    override val submitH2 = "Now submit your explanation for a shortage or excess"
    override val declaration = "By submitting this explanation for a shortage or excess, you are confirming that to the best of your knowledge, the details you are providing are correct."
    override val submitButton = "Submit explanation for shortage or excess"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val heading = "Check your answers"
    override val title: String = titleHelper(heading)
    override val movementDetailsH2 = "Movement"
    override val itemsH2 = "Items"
    override val item = (itemReference: Int) => s"Item $itemReference"
    override val itemDetails = (itemReference: Int) => s"Item details for item $itemReference"
    override val itemRemove = (itemReference: Int) => s"Remove item $itemReference"
    override val addAnotherItem = "Add another item"
    override val submitH2 = "Now submit your explanation for a shortage or excess"
    override val declaration = "By submitting this explanation for a shortage or excess, you are confirming that to the best of your knowledge, the details you are providing are correct."
    override val submitButton = "Submit explanation for shortage or excess"
  }
}
