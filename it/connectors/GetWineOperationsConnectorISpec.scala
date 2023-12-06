package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.wineOperations.GetWineOperationsConnector
import generators.ModelGenerators
import models.requests.WineOperationsRequest
import models.response.referenceData.WineOperationsResponse
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

class GetWineOperationsConnectorISpec
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

  private lazy val connector: GetWineOperationsConnector = app.injector.instanceOf[GetWineOperationsConnector]

  ".check" - {

    val url = "/emcs-tfe-reference-data/oracle/wine-operations"
    val request = WineOperationsRequest(Seq("4", "11", "9"))
    val requestJson = JsArray(Seq(JsString("4"), JsString("11"), JsString("9")))

    "must return a response model when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "4" -> "The product has been sweetened",
            "11" -> "The product has been partially dealcoholised",
            "9" -> "The product has been made using oak chips"
          ))))
      )

      connector.getWineOperations(request).futureValue mustBe Right(WineOperationsResponse(data = Map(
        "4" -> "The product has been sweetened",
        "11" -> "The product has been partially dealcoholised",
        "9" -> "The product has been made using oak chips"
      )))
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getWineOperations(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
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

      connector.getWineOperations(request).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getWineOperations(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
