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

package models.requests

import models.UserAnswers
import models.response.emcsTfe.{GetMovementResponse, MovementItem}
import models.response.referenceData.TraderKnownFacts
import pages.individualItems.SelectItemPage
import play.api.mvc.WrappedRequest
import play.twirl.api.Html

case class DataRequest[A](request: MovementRequest[A],
                          userAnswers: UserAnswers,
                          traderKnownFacts: Option[TraderKnownFacts]) extends WrappedRequest[A](request) with NavBarRequest {

  val internalId: String = request.internalId
  val ern: String = request.ern
  val arc: String = request.arc
  val movementDetails: GetMovementResponse = request.movementDetails

  def getItemDetails(idx: Int): Option[MovementItem] =
    userAnswers.get(SelectItemPage(idx)).flatMap {
      reference =>
        request.movementDetails.item(reference)
    }

  def getAllCompletedItemDetails: Seq[MovementItem] =
    userAnswers.completedItems.flatMap {
      itemModel =>
        request.movementDetails.item(itemModel.itemUniqueReference)
    }

  override val navBar: Option[Html] = request.navBar

  val isDutyPaid: Boolean = Seq("XIPA", "XIPB", "XIPC", "XIPD").contains(ern.take(4))
}
