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

sealed trait ReferenceDataCategoryOfWine {
  def toCategoryOfWine: CategoryOfWine
}

//noinspection ScalaStyle
object ReferenceDataCategoryOfWine extends Enumerable.Implicits {

  case object `1` extends WithName("1") with ReferenceDataCategoryOfWine {
    override def toCategoryOfWine: CategoryOfWine = CategoryOfWine.WineWithoutPDOPGI
  }
  case object `2` extends WithName("2") with ReferenceDataCategoryOfWine {
    override def toCategoryOfWine: CategoryOfWine = CategoryOfWine.VarietalWineWithoutPDOPGI
  }
  case object `3` extends WithName("3") with ReferenceDataCategoryOfWine {
    override def toCategoryOfWine: CategoryOfWine = CategoryOfWine.WineWithPDOOrPGI
  }
  case object `4` extends WithName("4") with ReferenceDataCategoryOfWine {
    override def toCategoryOfWine: CategoryOfWine = CategoryOfWine.ImportedWine
  }
  case object `5` extends WithName("5") with ReferenceDataCategoryOfWine {
    override def toCategoryOfWine: CategoryOfWine = CategoryOfWine.Other
  }

  val values: Seq[ReferenceDataCategoryOfWine] = Seq(
    `1`,
    `2`,
    `3`,
    `4`,
    `5`
  )

  implicit val enumerable: Enumerable[ReferenceDataCategoryOfWine] =
    Enumerable(values.distinct.map(v => v.toString -> v): _*)
}
