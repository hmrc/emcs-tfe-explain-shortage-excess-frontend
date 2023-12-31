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

import forms.mappings.Mappings
import models.ChooseShortageExcessItem.{Excess, Shortage}
import models.{ChooseShortageExcessItem, UnitOfMeasure}
import play.api.data.Form
import play.api.data.Forms.{optional, text => playText}
import play.api.i18n.Messages

import javax.inject.Inject

class ItemAmountFormProvider @Inject() extends Mappings {

  def apply(maxAmount: Option[BigDecimal],
            unit: UnitOfMeasure,
            shortageOrExcess: ChooseShortageExcessItem
           )(implicit messages: Messages): Form[Option[BigDecimal]] =
    Form(
      "value" -> optional(playText
        .verifying(
          firstError(
            decimalMaxLength(MAX_LENGTH_15, "itemAmount.error.maxLength"),
            isNumeric("itemAmount.error.isNotNumeric")
          )
        )
        .transform[BigDecimal](BigDecimal(_), _.toString())
        .verifying(
          firstError(
            Seq(
              Some(greaterThanValue(BigDecimal(MIN_VALUE_0), "itemAmount.error.notGreaterThanZero", MIN_VALUE_0)),
              Some(maxScale(3, "itemAmount.error.threeDecimalPlaces")),
              maxAmount.map(
                max =>
                  shortageOrExcess match {
                    case Shortage =>
                      lessThanEqualValue(max, "itemAmount.error.exceedsMaxAmount", max, messages(s"unitOfMeasure.$unit.short"))
                    case Excess =>
                      greaterThanValue(max, "itemAmount.error.doesNotExceedValue", max)
                  }
              )
            ).flatten: _*
          )
        )
      )
    )
}