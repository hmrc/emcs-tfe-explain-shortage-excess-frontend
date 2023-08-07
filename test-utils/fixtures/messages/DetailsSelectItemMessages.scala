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

object DetailsSelectItemMessages {


  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val h1: Int => String
    val tableProductCategoryKey: String
    val tableCNCodeKey: String
    val tableBrandNameKey: String
    val tableCommercialDescriptionKey: String
    val tableQuantityKey: String
    val quantityValue: BigDecimal => String
    val tableAlcoholStrengthKey: String
    val alcoholicStrengthValue: BigDecimal => String
    val tableDensityKey: String
    val densityValue: BigDecimal => String
    val alcoholStrength: Option[BigDecimal] => String
    val density: Option[BigDecimal] => String
    val tablePackaging: String
    val viewAllDetails: Int => String

  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "Do you want to give information about this item?"
    override val title: String = titleHelper(heading)
    override val h1: Int => String = i => s"Item $i"
    override val tableProductCategoryKey = "Product category"
    override val tableCNCodeKey = "CN code"
    override val tableBrandNameKey = "Brand name"
    override val tableCommercialDescriptionKey = "Commercial description"
    override val tableQuantityKey = "Quantity"
    override val quantityValue: BigDecimal => String = value => s"$value litres (temperature of 15°C)"
    override val tableAlcoholStrengthKey = "Alcohol strength (ABV)"
    override val alcoholicStrengthValue: BigDecimal => String = value => s"$value%"
    override val tableDensityKey = "Density"
    override val densityValue: BigDecimal => String = value => s"${value}kg/m<sup>3</sup> (temperature of 15&deg;C)"
    override val alcoholStrength: Option[BigDecimal] => String = {
      case Some(strength) => strength + "%"
      case None => "N/A"
    }
    override val density: Option[BigDecimal] => String = {
      case Some(density) => density + "kg/m3 kilograms"
      case None => "N/A"
    }
    override val tablePackaging: String = "Packaging"
    override val viewAllDetails: Int => String = i => s"View all details for item $i"
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val heading = "Do you want to give information about this item?"
    override val title: String = titleHelper(heading)
    override val h1: Int => String = i => s"Item $i"
    override val tableProductCategoryKey = "Product category"
    override val tableCNCodeKey = "CN code"
    override val tableBrandNameKey = "Brand name"
    override val tableCommercialDescriptionKey = "Commercial description"
    override val tableQuantityKey = "Quantity"
    override val quantityValue: BigDecimal => String = value => s"$value litres (temperature of 15°C)"
    override val tableAlcoholStrengthKey = "Alcohol strength (ABV)"
    override val alcoholicStrengthValue: BigDecimal => String = value => s"$value%"
    override val tableDensityKey = "Density"
    override val densityValue: BigDecimal => String = value => s"${value}kg/m<sup>3</sup> (temperature of 15&deg;C)"
    override val alcoholStrength: Option[BigDecimal] => String = {
      case Some(strength) => strength + "%"
      case None => "N/A"
    }
    override val density: Option[BigDecimal] => String = {
      case Some(density) => density + "kg/m3 kilograms"
      case None => "N/A"
    }
    override val tablePackaging: String = "Packaging"
    override val viewAllDetails: Int => String = i => s"View all details for item $i"
  }
}
