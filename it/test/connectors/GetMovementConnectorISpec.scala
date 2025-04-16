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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.GetMovementConnector
import fixtures.{BaseFixtures, GetMovementResponseFixtures}
import generators.ModelGenerators
import models.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetMovementConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with MockitoSugar
  with ModelGenerators
  with BaseFixtures
  with GetMovementResponseFixtures {

  val exciseRegistrationNumber = "ern"
  val arc = "arc"

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  private lazy val connector: GetMovementConnector = app.injector.instanceOf[GetMovementConnector]

  ".getMovement" - {

    val body = Json.toJson(getMovementResponseInputJson)

    def url(forceFetchNew: Boolean): String = s"/emcs-tfe/movement/ern/arc?forceFetchNew=$forceFetchNew"

    "must return true when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url(forceFetchNew = true)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(body)))
      )

      connector.getMovement(exciseRegistrationNumber, arc, forceFetchNew = true).futureValue mustBe Right(getMovementResponseModel)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        get(urlEqualTo(url(forceFetchNew = false)))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.getMovement(exciseRegistrationNumber, arc, forceFetchNew = false).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url(forceFetchNew = true)))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getMovement(exciseRegistrationNumber, arc, forceFetchNew = true).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url(forceFetchNew = true)))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getMovement(exciseRegistrationNumber, arc, forceFetchNew = true).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

}
