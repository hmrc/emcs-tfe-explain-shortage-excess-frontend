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
import mocks.connectors.MockGetCnCodeInformationConnector
import models.requests.{CnCodeInformationItem, CnCodeInformationRequest}
import models.response.emcsTfe.MovementItem
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import models.{ReferenceDataException, ReferenceDataUnitOfMeasure, UnexpectedDownstreamResponseError}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetCnCodeInformationServiceSpec extends SpecBase with MockGetCnCodeInformationConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetCnCodeInformationService(mockGetCnCodeInformationConnector)

  val request = CnCodeInformationRequest(items = Seq(CnCodeInformationItem(productCode = "T400", cnCode = "24029000")))
  val movementItems = Seq(MovementItem(1, "T400", "24029000", 1, 1, 1, None, None, None, None, None, None, None, None, None, Seq(), None))

  ".getCnCodeInformationWithMovementItems" - {

    "should return Success response" - {

      "when Connector returns success from downstream" in {
        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Right(CnCodeInformationResponse(data = Map(
          "24029000" -> CnCodeInformation(
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasureCode = ReferenceDataUnitOfMeasure.`1`
          )
        )))))

        testService.getCnCodeInformationWithMovementItems(movementItems)(hc).futureValue mustBe Seq((movementItems.head, CnCodeInformation(
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasureCode = ReferenceDataUnitOfMeasure.`1`
        )))
      }
    }

    "should return Failure response" - {

      "when Connector returns failure from downstream" in {

        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[ReferenceDataException](await(testService.getCnCodeInformationWithMovementItems(movementItems)(hc)))

        result.getMessage must include(s"Failed to retrieve CN Code information")
      }

      "when not all items match something from the Connector" in {
        val request = CnCodeInformationRequest(
          items = Seq(
            CnCodeInformationItem(productCode = "T400", cnCode = "24029000"),
            CnCodeInformationItem(productCode = "T401", cnCode = "24029001"),
            CnCodeInformationItem(productCode = "T402", cnCode = "24029002")
          )
        )
        val items = Seq(
          MovementItem(1, "T400", "24029000", 1, 1, 1, None, None, None, None, None, None, None, None, None, Seq(), None),
          MovementItem(1, "T401", "24029001", 1, 1, 1, None, None, None, None, None, None, None, None, None, Seq(), None),
          MovementItem(1, "T402", "24029002", 1, 1, 1, None, None, None, None, None, None, None, None, None, Seq(), None)
        )


        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Right(CnCodeInformationResponse(data = Map(
          "24029000" -> CnCodeInformation(
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasureCode = ReferenceDataUnitOfMeasure.`1`
          ),
          "24029001" -> CnCodeInformation(
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasureCode = ReferenceDataUnitOfMeasure.`1`
          )
        )))))

        val result = intercept[ReferenceDataException](await(testService.getCnCodeInformationWithMovementItems(items)(hc)))

        result.getMessage must include(s"Failed to match item with CN Code information")
      }
    }
  }
}
