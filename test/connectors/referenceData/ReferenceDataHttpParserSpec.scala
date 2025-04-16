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

package connectors.referenceData

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import mocks.MockHttpClient
import models.ReferenceDataUnitOfMeasure.`1`
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import models.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2

class ReferenceDataHttpParserSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetMovementResponseFixtures {

  lazy val httpParser: ReferenceDataHttpParser = new ReferenceDataHttpParser {
    override def http: HttpClientV2 = mockHttpClient
  }

  "ReferenceDataReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when valid JSON is returned that can be parsed to the model" in {

        val cnCodeInformation = Map("24029000" -> CnCodeInformation(
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasureCode = `1`
        ))

        val cnCodeInformationJson = Json.obj("24029000" -> Json.obj(
          "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          "exciseProductCodeDescription" -> "Fine-cut tobacco for the rolling of cigarettes",
          "unitOfMeasureCode" -> 1
        ))

        val httpResponse = HttpResponse(Status.OK, cnCodeInformationJson, Map())

        httpParser.ReferenceDataReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Right(
          CnCodeInformationResponse(cnCodeInformation)
        )
      }
    }

    "should return UnexpectedDownstreamError" - {

      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.ReferenceDataReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return JsonValidationError" - {

      s"when response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.ReferenceDataReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Left(JsonValidationError)
      }

      s"when response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.arr(), Map())

        httpParser.ReferenceDataReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }
}
