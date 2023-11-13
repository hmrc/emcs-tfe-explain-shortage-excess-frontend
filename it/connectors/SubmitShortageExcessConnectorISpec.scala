package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.SubmitShortageExcessConnector
import fixtures.{BaseFixtures, SubmitShortageExcessFixtures}
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
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class SubmitShortageExcessConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with MockitoSugar
  with ModelGenerators with BaseFixtures with SubmitShortageExcessFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit private lazy val ec: ExecutionContext = ExecutionContext.global

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port
      )
      .build()

  private lazy val connector: SubmitShortageExcessConnector = app.injector.instanceOf[SubmitShortageExcessConnector]

  ".submit" - {

    val url = s"/emcs-tfe/explain-shortage-excess/$testErn/$testArc"

    "must return true when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(submitExplainShortageExcessWholeMovementJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(submitShortageOrExcessChRISResponseJson)))
      )

      connector.submit(testErn, submitExplainShortageExcessWholeMovementModel).futureValue mustBe Right(submitShortageOrExcessChRISResponseModel)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(submitExplainShortageExcessWholeMovementJson)))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.submit(testErn, submitExplainShortageExcessWholeMovementModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(submitExplainShortageExcessWholeMovementJson)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.submit(testErn, submitExplainShortageExcessWholeMovementModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(submitExplainShortageExcessWholeMovementJson)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.submit(testErn, submitExplainShortageExcessWholeMovementModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }


}
