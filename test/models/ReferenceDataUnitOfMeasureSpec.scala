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

import base.SpecBase

class ReferenceDataUnitOfMeasureSpec extends SpecBase {
  s"toUnitOfMeasure" - {
    "must convert 1 to kilograms" in {
      ReferenceDataUnitOfMeasure.`1`.toUnitOfMeasure mustBe UnitOfMeasure.Kilograms
    }
    "must convert 2 to litres (15)" in {
      ReferenceDataUnitOfMeasure.`2`.toUnitOfMeasure mustBe UnitOfMeasure.Litres15
    }
    "must convert 3 to litres (20)" in {
      ReferenceDataUnitOfMeasure.`3`.toUnitOfMeasure mustBe UnitOfMeasure.Litres20
    }
    "must convert 4 to thousands" in {
      ReferenceDataUnitOfMeasure.`4`.toUnitOfMeasure mustBe UnitOfMeasure.Thousands
    }
  }
}
