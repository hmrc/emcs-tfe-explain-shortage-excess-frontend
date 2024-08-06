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
import featureswitch.core.config.EnableNRS
import fixtures.{NRSBrokerFixtures, SubmitShortageExcessFixtures}
import mocks.config.MockAppConfig
import mocks.connectors.MockSubmitShortageExcessConnector
import mocks.services.{MockAuditingService, MockNRSBrokerService}
import models.HowGiveInformation.Whole
import models.submitShortageExcess.SubmitShortageExcessModel
import models.{SubmitShortageExcessException, UnexpectedDownstreamResponseError}
import pages.{GiveInformationMovementPage, HowGiveInformationPage, WhenReceiveShortageExcessPage}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SubmitShortageExcessServiceSpec extends SpecBase
  with MockSubmitShortageExcessConnector
  with SubmitShortageExcessFixtures
  with MockAuditingService
  with MockAppConfig
  with MockNRSBrokerService
  with NRSBrokerFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new SubmitShortageExcessService(mockSubmitShortageExcessConnector, mockNRSBrokerService, mockAuditingService, mockAppConfig)

  val userAnswers = emptyUserAnswers
    .set(WhenReceiveShortageExcessPage, testDateOfWhenReceiveShortageOrExcess)
    .set(HowGiveInformationPage, Whole)
    .set(GiveInformationMovementPage, "reason")

  class Fixture(isNRSEnabled: Boolean) {
    MockAppConfig.getFeatureSwitchValue(EnableNRS).returns(isNRSEnabled)
  }

  ".submit(ern: String, submission: SubmitShortageExcessModel)" - {

    Seq(true, false).foreach { nrsEnabled =>

      s"when NRS enabled is '$nrsEnabled'" - {

        "should submit, audit and return a success response" - {

          "when connector receives a success from downstream" in new Fixture(nrsEnabled) {

            val request = dataRequest(FakeRequest(), userAnswers)

            val submission = SubmitShortageExcessModel(getMovementResponseModel)(userAnswers)

            MockSubmitShortageExcessConnector.submit(testErn, submission).returns(Future.successful(Right(submitShortageOrExcessChRISResponseModel)))

            MockAuditingService.audit().noMoreThanOnce()

            if (nrsEnabled) {
              MockNRSBrokerService.submitPayload(submission, testErn).returns(Future.successful(Right(nrsBrokerResponseModel)))
            } else {
              MockNRSBrokerService.submitPayload(submission, testErn).never()
            }

            testService.submit(testErn, testArc)(hc, request).futureValue mustBe submitShortageOrExcessChRISResponseModel
          }
        }

        "should submit, audit and return a failure response" - {

          "when connector receives a failure from downstream" in new Fixture(nrsEnabled) {

            val request = dataRequest(FakeRequest(), userAnswers)
            val submission = SubmitShortageExcessModel(getMovementResponseModel)(userAnswers)

            MockSubmitShortageExcessConnector.submit(testErn, submission).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            MockAuditingService.audit().noMoreThanOnce()

            if (nrsEnabled) {
              MockNRSBrokerService.submitPayload(submission, testErn).returns(Future.successful(Right(nrsBrokerResponseModel)))
            } else {
              MockNRSBrokerService.submitPayload(submission, testErn).never()
            }

            intercept[SubmitShortageExcessException](await(testService.submit(testErn, testArc)(hc, request))).getMessage mustBe
              s"Failed to submit Explain Shortage or Excess to emcs-tfe for ern: '$testErn' & arc: '$testArc'"
          }
        }
      }
    }
  }
}
