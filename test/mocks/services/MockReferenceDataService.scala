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

package mocks.services

import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import org.scalamock.handlers.{CallHandler3, CallHandler5}
import org.scalamock.scalatest.MockFactory
import play.api.mvc.Result
import services.ReferenceDataService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockReferenceDataService extends MockFactory {

  lazy val mockReferenceDataService: ReferenceDataService = mock[ReferenceDataService]

  object MockReferenceDataService {

    def getCnCodeInformationWithMovementItems(items: Seq[MovementItem])
    : CallHandler3[Seq[MovementItem], HeaderCarrier, ExecutionContext, Future[Seq[(MovementItem, CnCodeInformation)]]] =
      (mockReferenceDataService.getMovementItemsWithReferenceData(_: Seq[MovementItem])(_: HeaderCarrier, _: ExecutionContext))
        .expects(where {
          (_items: Seq[MovementItem], _, _) =>
            (_items.map(_.cnCode) == items.map(_.cnCode)) && (_items.map(_.productCode) == items.map(_.productCode))
        })

    def itemWithReferenceData(item: MovementItem): CallHandler5[MovementItem, (MovementItem, CnCodeInformation) => Future[Result], HeaderCarrier, ExecutionContext, DataRequest[_], Future[Result]] =
      (mockReferenceDataService.itemWithReferenceData(_: MovementItem)(_: (MovementItem, CnCodeInformation) => Future[Result])(_: HeaderCarrier, _: ExecutionContext, _: DataRequest[_]))
        .expects(where {
          (_items: MovementItem, _, _, _, _) => _items.cnCode == item.cnCode
        })

    def itemWithReferenceDataSuccessHandler(movementItem: MovementItem, cnCodeInformation: CnCodeInformation): Product => Future[Result] = {
      handler =>
        val f = handler.productElement(1).asInstanceOf[(MovementItem, CnCodeInformation) => Future[Result]]
        f(movementItem, cnCodeInformation)
    }
  }
}
