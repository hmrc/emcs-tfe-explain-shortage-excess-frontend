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

sealed trait UnitOfMeasure

object UnitOfMeasure extends Enumerable.Implicits {

  case object Kilograms extends WithName("kilograms") with UnitOfMeasure
  case object Litres15 extends WithName("litres15") with UnitOfMeasure
  case object Litres20 extends WithName("litres20") with UnitOfMeasure
  case object Thousands extends WithName("thousands") with UnitOfMeasure

  val values: Seq[UnitOfMeasure] = Seq(
    Kilograms,
    Litres15,
    Litres20,
    Thousands
  )

  implicit val enumerable: Enumerable[UnitOfMeasure] =
    Enumerable(values.distinct.map(v => v.toString -> v): _*)
}
