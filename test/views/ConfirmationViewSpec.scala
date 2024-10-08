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

package views

import base.ViewSpecBase
import fixtures.messages.ConfirmationMessages
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.DateUtils
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewSpecBase with ViewBehaviours with DateUtils {

  object Selectors extends BaseSelectors

  "ConfirmationView" - {

    Seq(ConfirmationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "for duty suspended" - {

          val userAnswers = emptyUserAnswers

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          val view = app.injector.instanceOf[ConfirmationView]

          implicit val doc: Document = Jsoup.parse(view(testConfirmationDetails).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.movementInfoH2,
            Selectors.summaryRowKey(1) -> messagesForLanguage.movementInfoArc,
            Selectors.summaryRowValue(1) -> testArc,
            Selectors.summaryRowKey(2) -> messagesForLanguage.movementInfoDate,
            Selectors.summaryRowValue(2) -> testConfirmationDetails.dateOfSubmission.formatDateForUIOutput(),
            Selectors.p(1) -> messagesForLanguage.print,
            Selectors.h2(2) -> messagesForLanguage.whatNextH2,
            Selectors.p(2) -> messagesForLanguage.whatNextP1,
            Selectors.h2(3) -> messagesForLanguage.whatMayNeedDoH2,
            Selectors.p(3) -> messagesForLanguage.whatMayNeedDoP1,
            Selectors.h3(1) -> messagesForLanguage.shortageH3,
            Selectors.p(4) -> messagesForLanguage.shortageP1,
            Selectors.h3(2) -> messagesForLanguage.excessH3,
            Selectors.p(5) -> messagesForLanguage.excessP1,
            Selectors.bullet(1) -> messagesForLanguage.excessSameGoodsBullet1,
            Selectors.bullet(2) -> messagesForLanguage.excessSameGoodsBullet2,
            Selectors.p(6) -> messagesForLanguage.excessP2,
            Selectors.bullet(1, 2) -> messagesForLanguage.excessDifferentGoodsBullet1,
            Selectors.bullet(2, 2) -> messagesForLanguage.excessDifferentGoodsBullet2,
            Selectors.p(7) -> messagesForLanguage.returnToMovementLink,
            Selectors.p(8) -> messagesForLanguage.returnToAccountHomeLink,
            Selectors.p(9) -> messagesForLanguage.feedback
          ))
        }

        "for duty paid" - {
          val userAnswers = emptyUserAnswers

          implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, ern = testDutyPaidErn)

          val view = app.injector.instanceOf[ConfirmationView]

          implicit val doc: Document = Jsoup.parse(view(testConfirmationDetails).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.movementInfoH2,
            Selectors.summaryRowKey(1) -> messagesForLanguage.movementInfoArc,
            Selectors.summaryRowValue(1) -> testArc,
            Selectors.summaryRowKey(2) -> messagesForLanguage.movementInfoDate,
            Selectors.summaryRowValue(2) -> testConfirmationDetails.dateOfSubmission.formatDateForUIOutput(),
            Selectors.p(1) -> messagesForLanguage.print,
            Selectors.h2(2) -> messagesForLanguage.whatNextH2,
            Selectors.p(2) -> messagesForLanguage.whatNextP1,
            Selectors.h2(3) -> messagesForLanguage.whatMayNeedDoH2,
            Selectors.p(3) -> messagesForLanguage.whatMayNeedDoP1DutyPaid,
            Selectors.h3(1) -> messagesForLanguage.shortageH3,
            Selectors.p(4) -> messagesForLanguage.shortageP1DutyPaid,
            Selectors.p(5) -> messagesForLanguage.shortageP2DutyPaid,
            Selectors.h3(2) -> messagesForLanguage.excessH3,
            Selectors.p(6) -> messagesForLanguage.excessP1DutyPaid,
            Selectors.p(7) -> messagesForLanguage.excessP2DutyPaid,
            Selectors.p(8) -> messagesForLanguage.returnToMovementLink,
            Selectors.p(9) -> messagesForLanguage.returnToAccountHomeLink,
            Selectors.p(10) -> messagesForLanguage.feedback
          ))
        }
      }
    }
  }
}
