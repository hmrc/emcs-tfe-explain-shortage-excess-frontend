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

package viewmodels

import base.SpecBase
import controllers.routes
import fixtures.messages.GiveInformationMovementMessages
import models.CheckMode
import pages.GiveInformationMovementPage
import play.api.test.FakeRequest
import utils.DateUtils
import viewmodels.checkAnswers.GiveInformationMovementSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class GiveInformationMovementSummarySpec extends SpecBase with DateUtils {


  "GiveInformationMovementSummary" - {

    Seq(GiveInformationMovementMessages.English).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val moreGiveInformationMovementSummary = new GiveInformationMovementSummary

        "when an answer is set" - {

          "must render the expected SummaryListRow" in {

            val info = "Some info"

            val answers = emptyUserAnswers.set(GiveInformationMovementPage, info)
            implicit val request = dataRequest(FakeRequest(), answers)

            moreGiveInformationMovementSummary.row() mustBe
              Some(SummaryListRowViewModel(
                key = langMessages.checkAnswersLabel,
                value = ValueViewModel(info),
                actions = Seq(
                  ActionItemViewModel(
                    langMessages.change,
                    routes.GiveInformationMovementController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
                    id = GiveInformationMovementPage
                  ).withVisuallyHiddenText(langMessages.checkAnswersHiddenChangeLinkText)
                )
              ))
          }
        }

        "when no answer is set" - {

          "must render None" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
            moreGiveInformationMovementSummary.row() mustBe None
          }
        }
      }
    }
  }
}
