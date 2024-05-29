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

object ItemAmountMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: (String, Int) => String
    val title: (String, Int) => String
    val itemDetails: Int => String
    val hint: String
    val requiredError: String
    val maxLengthError: Int => String
    val isNotNumericError: String
    val notGreaterThanZeroError: String
    val threeDecimalPlacesError: String
    val exceedsMaxAmountError: (BigDecimal, String) => String
    val doesNotExceedValueError: BigDecimal => String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = (unit: String, item: Int) => s"How many $unit of item $item did you receive? (optional)"
    override val title: (String, Int) => String = (unit: String, item: Int) => titleHelper(heading(unit, item))
    override val itemDetails: Int => String = n => s"View item $n details"
    override val hint = "For example, 150 or 12.694."
    override val requiredError = "Enter the amount you received"
    override val maxLengthError = (max: Int) => s"The amount you received must be $max numbers or less"
    override val isNotNumericError = "The amount you received must be a number, like 150 or 12.694"
    override val notGreaterThanZeroError = "The amount you received must be more than 0"
    override val threeDecimalPlacesError = "The amount you received must have 3 decimals or less"
    override val exceedsMaxAmountError = (amount: BigDecimal, unit: String) => s"The amount you received must be $amount $unit or fewer"
    override val doesNotExceedValueError = (amount: BigDecimal) => s"The amount you received must be more than $amount"
  }

}
