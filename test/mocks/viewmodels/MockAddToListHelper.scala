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

package mocks.viewmodels

import models.UnitOfMeasure
import models.requests.DataRequest
import models.response.emcsTfe.MovementItem
import org.scalamock.handlers.CallHandler7
import org.scalamock.scalatest.MockFactory
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.AddToListHelper

import scala.concurrent.{ExecutionContext, Future}

trait MockAddToListHelper extends MockFactory {

  lazy val mockAddToListHelper: AddToListHelper = mock[AddToListHelper]

  object MockAddToListHelper {

    def summaryList(): CallHandler7[MovementItem, UnitOfMeasure, Boolean, DataRequest[_], HeaderCarrier, ExecutionContext, Messages, Future[SummaryList]] =
      (mockAddToListHelper.summaryList(_: MovementItem, _: UnitOfMeasure, _: Boolean)(_: DataRequest[_], _: HeaderCarrier, _: ExecutionContext, _: Messages))
        .expects(*, *, *, *, *, *, *)
  }
}
