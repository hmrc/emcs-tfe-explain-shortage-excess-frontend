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

package connectors.packagingTypes

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import mocks.MockHttpClient
import models.response.referenceData.PackagingTypesResponse
import models.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2

class PackagingTypesHttpParserSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetMovementResponseFixtures {

  lazy val httpParser: PackagingTypesHttpParser = new PackagingTypesHttpParser {
    override def http: HttpClientV2 = mockHttpClient
  }

  "PackagingTypesReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return a successful response" - {

      "when valid JSON is returned that can be parsed to the model" in {

        val packagingTypes = Map("TN" -> "Tin")

        val packagingTypesJson = Json.toJson(packagingTypes)

        val httpResponse = HttpResponse(Status.OK, packagingTypesJson, Map())

        httpParser.PackagingTypesReads.read("POST", "/oracle/packaging-types", httpResponse) mustBe Right(
          PackagingTypesResponse(packagingTypes)
        )
      }
    }

    "should return UnexpectedDownstreamError" - {

      s"when status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.PackagingTypesReads.read("POST", "/oracle/packaging-types", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return JsonValidationError" - {

      s"when response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.PackagingTypesReads.read("POST", "/oracle/packaging-types", httpResponse) mustBe Left(JsonValidationError)
      }

      s"when response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.arr(), Map())

        httpParser.PackagingTypesReads.read("POST", "/oracle/packaging-types", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }
}
