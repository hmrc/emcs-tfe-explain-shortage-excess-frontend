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

package mocks.connectors

import connectors.packagingTypes.GetPackagingTypesConnector
import models.ErrorResponse
import models.requests.PackagingTypesRequest
import models.response.referenceData.PackagingTypesResponse
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetPackagingTypesConnector extends MockFactory {

  lazy val mockGetPackagingTypesConnector: GetPackagingTypesConnector = mock[GetPackagingTypesConnector]

  object MockGetPackagingTypesConnector {
    def getPackagingTypes(request: PackagingTypesRequest): CallHandler3[PackagingTypesRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, PackagingTypesResponse]]] =
      (mockGetPackagingTypesConnector.getPackagingTypes(_: PackagingTypesRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(request, *, *)
  }
}