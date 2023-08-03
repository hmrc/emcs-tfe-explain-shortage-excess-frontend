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

import connectors.wineOperations.GetWineOperationsConnector
import models.WineOperationsException
import models.requests.WineOperationsRequest
import models.response.emcsTfe.{MovementItem, WineProduct}
import models.response.referenceData.WineOperationsResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetWineOperationsService @Inject()(connector: GetWineOperationsConnector)
                                        (implicit ec: ExecutionContext) {

  /**
   * @param items movement items
   * @param hc implicit header carrier (passed through the implicit request)
   * @return a Seq of MovementItem, where each wineProduct.typeOfPackage has been updated to its packaging type equivalent
   * @throws WineOperationsException if the connector call fails or a MovementItem doesn't contain a value from emcs-tfe-reference-data
   */
  def getWineOperations(items: Seq[MovementItem])(implicit hc: HeaderCarrier): Future[Seq[MovementItem]] = {
    val wineOperations = items.flatMap(_.wineProduct.flatMap(_.wineOperations)).flatten

    wineOperations match {
      case Nil => Future.successful(items)
      case operations =>
        connector.getWineOperations(generateRequestModel(operations)).map {
          case Right(response) => matchItemsWithReferenceDataValues(response, items)
          case Left(errorResponse) => throw WineOperationsException(s"Failed to retrieve wine operations from emcs-tfe-reference-data: $errorResponse")
        }
    }
  }

  private def generateRequestModel(operations: Seq[String]): WineOperationsRequest = {
    val wineOperations: Seq[String] = operations.distinct
    WineOperationsRequest(wineOperations = wineOperations)
  }

  private def matchItemsWithReferenceDataValues(
                                                 response: WineOperationsResponse,
                                                 items: Seq[MovementItem]
                                               ): Seq[MovementItem] = {
    val data = response.data
    items.map {
      item =>
        val newWineProduct: Option[WineProduct] = item.wineProduct.map {
          wineProduct =>
            val newOperations: Option[Seq[String]] = wineProduct.wineOperations.map {
              operations =>
                operations.map(data.get(_) match {
                  case Some(value) => value
                  case None => throw WineOperationsException(s"Failed to match item with wine operation from emcs-tfe-reference-data: $item")
                })
            }
            wineProduct.copy(wineOperations = newOperations)
        }
        item.copy(wineProduct = newWineProduct)
    }
  }
}
