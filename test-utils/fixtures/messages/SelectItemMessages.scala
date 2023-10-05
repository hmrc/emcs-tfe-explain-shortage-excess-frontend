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

object SelectItemMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val givenInformationInfo: Int => String
    val viewAlreadyAddedItems: String
    val tableHeadItem: String
    val tableHeadDescription: String
    val tableHeadQuantity: String
    val tableHeadPackaging: String
    val tableRowItem: Int => String
  }

  object English extends ViewMessages with UnitOfMeasureMessages.English {
    override val heading = "Select an item to give information about"
    override val title = titleHelper(heading)
    override val givenInformationInfo = {
      case 0 => ""
      case n@1 => s"You have given information for $n item."
      case n => s"You have given information for $n items."
    }
    override val viewAlreadyAddedItems: String = "View and manage the items you’ve already given information about."
    override val tableHeadItem = "Item"
    override val tableHeadDescription = "Description"
    override val tableHeadQuantity = "Quantity"
    override val tableHeadPackaging = "Packaging"
    override val tableRowItem: Int => String = "Item " + _
  }

  object Welsh extends ViewMessages with UnitOfMeasureMessages.Welsh {
    override val heading = "Select an item to give information about"
    override val title = titleHelper(heading)
    override val givenInformationInfo = {
      case 0 => ""
      case n@1 => s"You have given information for $n item."
      case n => s"You have given information for $n items."
    }
    override val viewAlreadyAddedItems: String = "View and manage the items you’ve already given information about."
    override val tableHeadItem = "Item"
    override val tableHeadDescription = "Description"
    override val tableHeadQuantity = "Quantity"
    override val tableHeadPackaging = "Packaging"
    override val tableRowItem: Int => String = "Item " + _
  }
}
