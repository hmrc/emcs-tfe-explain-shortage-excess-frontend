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

package services

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import mocks.connectors.MockGetPackagingTypesConnector
import models.requests.PackagingTypesRequest
import models.response.emcsTfe.{MovementItem, Packaging}
import models.response.referenceData.PackagingTypesResponse
import models.{PackagingTypesException, UnexpectedDownstreamResponseError}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetPackagingTypesServiceSpec extends SpecBase with MockGetPackagingTypesConnector with GetMovementResponseFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetPackagingTypesService(mockGetPackagingTypesConnector)

  val request: PackagingTypesRequest = PackagingTypesRequest(packagingTypes = Seq("BX", "CR"))
  val movementItems: Seq[MovementItem] = Seq(item1, item2)

  val updatedBoxPackage: Packaging = boxPackage.copy(typeOfPackage = "Box")

  val updatedCratePackage: Packaging = cratePackage.copy(typeOfPackage = "Crate")

  ".getPackagingTypes" - {

    "should return Success response" - {

      "when Connector returns success from downstream" in {
        MockGetPackagingTypesConnector.getPackagingTypes(request).returns(Future.successful(Right(PackagingTypesResponse(data = Map(
          "BX" -> "Box",
          "CR" -> "Crate"
        )))))

        testService.getPackagingTypes(movementItems)(hc).futureValue mustBe Seq(
          item1.copy(packaging = Seq(updatedBoxPackage)),
          item2.copy(packaging = Seq(updatedBoxPackage, updatedCratePackage))
        )
      }
    }

    "should return Failure response" - {

      "when Connector returns failure from downstream" in {

        MockGetPackagingTypesConnector.getPackagingTypes(request).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[PackagingTypesException](await(testService.getPackagingTypes(movementItems)(hc)))

        result.getMessage must include("Failed to retrieve packaging types from emcs-tfe-reference-data")
      }

      "when not all items match something from the Connector" in {
        MockGetPackagingTypesConnector.getPackagingTypes(request).returns(Future.successful(Right(PackagingTypesResponse(data = Map(
          "BX" -> "Box"
        )))))

        val result = intercept[PackagingTypesException](await(testService.getPackagingTypes(movementItems)(hc)))

        result.getMessage must include(s"Failed to match item with packaging type from emcs-tfe-reference-data")
      }
    }
  }

}
