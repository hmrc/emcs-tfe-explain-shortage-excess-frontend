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

package models.response.referenceData

import base.SpecBase
import models.ReferenceDataUnitOfMeasure
import play.api.libs.json.{JsArray, JsResultException, JsString, Json}

class CnCodeInformationResponseSpec extends SpecBase {
  "reads" - {
    "must return a JsSuccess" - {
      "when the input JSON is valid" in {
        Json.obj(
          "24029000" -> Json.obj(
            "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            "exciseProductCodeDescription" -> "Fine-cut tobacco for the rolling of cigarettes",
            "unitOfMeasureCode" -> 1
          )
        ).as[CnCodeInformationResponse] mustBe CnCodeInformationResponse(data = Map("24029000" -> CnCodeInformation(
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasureCode = ReferenceDataUnitOfMeasure.`1`
        )))
      }
    }

    "must return a JsError" - {
      "when input JSON is not a JsObject" in {
        val result = intercept[JsResultException](JsString("").as[CnCodeInformationResponse])
        result.errors.toString must include("Unable to read CnCodeInformationResponse as a JSON object")
      }

      "when input JSON is invalid" in {
        val result = intercept[JsResultException](Json.obj(
          "24029000" -> JsArray(Seq(Json.obj(
            "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            "unitOfMeasureCode" -> 1
          )))
        ).as[CnCodeInformationResponse])
        result.errors.toString must include("/unitOfMeasureCode")
        result.errors.toString must include("/cnCodeDescription")
      }
    }
  }
}
