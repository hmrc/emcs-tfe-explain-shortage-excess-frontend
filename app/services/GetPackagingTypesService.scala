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

import connectors.packagingTypes.GetPackagingTypesConnector
import models.PackagingTypesException
import models.requests.PackagingTypesRequest
import models.response.emcsTfe.MovementItem
import models.response.referenceData.PackagingTypesResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetPackagingTypesService @Inject()(connector: GetPackagingTypesConnector)
                                        (implicit ec: ExecutionContext) {

  /**
   * @param items movement items
   * @param hc implicit header carrier (passed through the implicit request)
   * @return a Seq of MovementItem, where each packaging.typeOfPackage has been updated to its packaging type equivalent
   * @throws PackagingTypesException if the connector call fails or a MovementItem doesn't contain a value from emcs-tfe-reference-data
   */
  def getPackagingTypes(items: Seq[MovementItem])(implicit hc: HeaderCarrier): Future[Seq[MovementItem]] = {
    connector.getPackagingTypes(generateRequestModel(items)).map {
      case Right(response) => matchItemsWithReferenceDataValues(response, items)
      case Left(errorResponse) => throw PackagingTypesException(s"Failed to retrieve packaging types from emcs-tfe-reference-data: $errorResponse")
    }
  }

  private def generateRequestModel(items: Seq[MovementItem]): PackagingTypesRequest = {
    val packagingTypes: Seq[String] = items.flatMap(_.packaging.map(_.typeOfPackage)).distinct
    PackagingTypesRequest(packagingTypes = packagingTypes)
  }

  private def matchItemsWithReferenceDataValues(
                                                 response: PackagingTypesResponse,
                                                 items: Seq[MovementItem]
                                               ): Seq[MovementItem] = {
    val data = response.data
    items.map {
      item =>
        val newPackaging = item.packaging.map {
          packaging =>
            data.get(packaging.typeOfPackage) match {
              case Some(value) => packaging.copy(typeOfPackage = value)
              case None => throw PackagingTypesException(s"Failed to match item with packaging type from emcs-tfe-reference-data: $item")
            }

        }
        item.copy(packaging = newPackaging)
    }
  }
}
