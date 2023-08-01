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

class ReferenceDataCategoryOfWineSpec extends SpecBase {
  s"toCategoryOfWine" - {
    s"must convert 1 to ${CategoryOfWine.WineWithoutPDOPGI.toString}" in {
      ReferenceDataCategoryOfWine.`1`.toCategoryOfWine mustBe CategoryOfWine.WineWithoutPDOPGI
    }
    s"must convert 2 to ${CategoryOfWine.VarietalWineWithoutPDOPGI.toString}" in {
      ReferenceDataCategoryOfWine.`2`.toCategoryOfWine mustBe CategoryOfWine.VarietalWineWithoutPDOPGI
    }
    s"must convert 3 to ${CategoryOfWine.WineWithPDOOrPGI.toString}" in {
      ReferenceDataCategoryOfWine.`3`.toCategoryOfWine mustBe CategoryOfWine.WineWithPDOOrPGI
    }
    s"must convert 4 to ${CategoryOfWine.ImportedWine.toString}" in {
      ReferenceDataCategoryOfWine.`4`.toCategoryOfWine mustBe CategoryOfWine.ImportedWine
    }
    s"must convert 5 to ${CategoryOfWine.Other.toString}" in {
      ReferenceDataCategoryOfWine.`5`.toCategoryOfWine mustBe CategoryOfWine.Other
    }
  }
}
