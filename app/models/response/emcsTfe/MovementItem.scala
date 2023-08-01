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

package models.response.emcsTfe

import play.api.libs.json.{Format, Json, Reads, __}

case class MovementItem(itemUniqueReference: Int,
                        productCode: String,
                        cnCode: String,
                        quantity: BigDecimal,
                        grossMass: BigDecimal,
                        netMass: BigDecimal,
                        alcoholicStrength: Option[BigDecimal],
                        degreePlato: Option[BigDecimal],
                        fiscalMark: Option[String],
                        designationOfOrigin: Option[String],
                        sizeOfProducer: Option[String],
                        density: Option[BigDecimal],
                        commercialDescription: Option[String],
                        brandNameOfProduct: Option[String],
                        maturationAge: Option[String],
                        packaging: Seq[Packaging],
                        wineProduct: Option[WineProduct]
                       )
object MovementItem {

  implicit val format: Format[MovementItem] = Json.format

  val readItemUniqueReference: Reads[Int] = Reads.at[Int](__ \ "itemUniqueReference")
}
