/*
 * Copyright 2025 HM Revenue & Customs
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

package test.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.packagingTypes.GetPackagingTypesConnector
import generators.ModelGenerators
import models.requests.PackagingTypesRequest
import models.response.referenceData.PackagingTypesResponse
import models.{JsonValidationError, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, JsString, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetPackagingTypesConnectorISpec
  extends AnyFreeSpec
    with WireMockHelper
    with ScalaFutures
    with Matchers
    with IntegrationPatience
    with EitherValues
    with OptionValues
    with MockitoSugar
    with ModelGenerators {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe-reference-data.port" -> server.port
      )
      .build()

  private lazy val connector: GetPackagingTypesConnector = app.injector.instanceOf[GetPackagingTypesConnector]

  ".check" - {

    val url = "/emcs-tfe-reference-data/oracle/packaging-types"
    val request = PackagingTypesRequest(Seq("TN"))
    val requestJson = JsArray(Seq(JsString("TN")))

    "must return a response model when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "TN" -> "Tin"
          ))))
      )

      connector.getPackagingTypes(request).futureValue mustBe Right(PackagingTypesResponse(data = Map(
        "TN" -> "Tin"
      )))
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getPackagingTypes(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "24029000" -> JsArray(Seq(Json.obj(
              "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
              "unitOfMeasureCode" -> 1
            )))
          ))))
      )

      connector.getPackagingTypes(request).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getPackagingTypes(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
