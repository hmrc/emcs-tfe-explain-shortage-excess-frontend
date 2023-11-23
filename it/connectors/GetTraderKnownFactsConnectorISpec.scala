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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetTraderKnownFactsConnector
import models.UnexpectedDownstreamResponseError
import models.response.referenceData.TraderKnownFacts
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.{INTERNAL_SERVER_ERROR, NO_CONTENT, OK}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global


class GetTraderKnownFactsConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def url(ern: String) = s"/emcs-tfe-reference-data/oracle/trader-known-facts?exciseRegistrationId=$ern"

  val testErn = "testErn"
  val testMinTraderKnownFacts: TraderKnownFacts = TraderKnownFacts(
    traderName = "testTraderName",
    addressLine1 = None,
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    addressLine5 = None,
    postcode = None
  )

  ".getTraderKnownFacts" - {
    "when the feature switch is disabled" - {

      def app: Application =
        new GuiceApplicationBuilder()
          .configure("microservice.services.emcs-tfe-reference-data.port" -> server.port)
          .configure("features.stub-get-trader-known-facts" -> "false")
          .build()

      lazy val connector: GetTraderKnownFactsConnector = app.injector.instanceOf[GetTraderKnownFactsConnector]

      "must return true when the server responds OK" in {

        server.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.stringify(Json.obj("traderName" -> "testTraderName"))))
        )

        connector.getTraderKnownFacts(testErn).futureValue mustBe Right(Some(testMinTraderKnownFacts))
      }

      "must return false when the server responds NO_CONTENT" in {

        server.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        connector.getTraderKnownFacts(testErn).futureValue mustBe Right(None)
      }

      "must fail when the server responds with any other status" in {

        server.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }

      "must fail when the connection fails" in {

        server.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
        )

        connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  "when the feature switch is enabled" - {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data-stub.port" -> server.port)
        .configure("features.stub-get-trader-known-facts" -> "true")
        .build()

    lazy val connector: GetTraderKnownFactsConnector = app.injector.instanceOf[GetTraderKnownFactsConnector]

    "must return true when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.obj("traderName" -> "testTraderName"))))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Right(Some(testMinTraderKnownFacts))
    }

    "must return false when the server responds NO_CONTENT" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Right(None)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
