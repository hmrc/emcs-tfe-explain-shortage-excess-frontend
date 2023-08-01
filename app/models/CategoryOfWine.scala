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

package models

sealed trait CategoryOfWine

object CategoryOfWine extends Enumerable.Implicits {

  case object WineWithoutPDOPGI extends WithName("wineWithoutPDOPGI") with CategoryOfWine

  case object VarietalWineWithoutPDOPGI extends WithName("varietalWineWithoutPDOPGI") with CategoryOfWine

  case object WineWithPDOOrPGI extends WithName("wineWithPDOOrPGI") with CategoryOfWine

  case object ImportedWine extends WithName("importedWine") with CategoryOfWine

  case object Other extends WithName("other") with CategoryOfWine

  val values: Seq[CategoryOfWine] = Seq(
    WineWithoutPDOPGI,
    VarietalWineWithoutPDOPGI,
    WineWithPDOOrPGI,
    ImportedWine,
    Other
  )

  implicit val enumerable: Enumerable[CategoryOfWine] =
    Enumerable(values.distinct.map(v => v.toString -> v): _*)
}
