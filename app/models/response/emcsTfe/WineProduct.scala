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

import models.ReferenceDataCategoryOfWine
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class WineProduct(wineProductCategory: String,
                       wineGrowingZoneCode: Option[String],
                       thirdCountryOfOrigin: Option[String],
                       otherInformation: Option[String],
                       wineOperations: Option[Seq[String]]
                      )

object WineProduct {
  implicit val reads: Reads[WineProduct] = (
    (JsPath \ "wineProductCategory").read[ReferenceDataCategoryOfWine].map(_.toCategoryOfWine.toString) and
      (JsPath \ "wineGrowingZoneCode").readNullable[String] and
      (JsPath \ "thirdCountryOfOrigin").readNullable[String] and
      (JsPath \ "otherInformation").readNullable[String] and
      (JsPath \ "wineOperations").readNullable[Seq[String]]
    )(WineProduct.apply _)

  implicit val writes: OWrites[WineProduct] = Json.writes
}
