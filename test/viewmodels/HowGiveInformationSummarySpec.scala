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
import fixtures.messages.HowGiveInformationMessages
import models.HowGiveInformation.Whole
import models.NormalMode
import pages.HowGiveInformationPage
import play.api.test.FakeRequest
import utils.DateUtils
import viewmodels.checkAnswers.HowGiveInformationSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class HowGiveInformationSummarySpec extends SpecBase with DateUtils {


  "HowGiveInformationSummary" - {

    Seq(HowGiveInformationMessages.English).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val moreHowGiveInformationSummary = new HowGiveInformationSummary

        "when an answer is set" - {

          "must render the expected SummaryListRow" in {

            val answers = emptyUserAnswers.set(HowGiveInformationPage, Whole)
            implicit val request = dataRequest(FakeRequest(), answers)

            moreHowGiveInformationSummary.row() mustBe
              Some(SummaryListRowViewModel(
                key = langMessages.checkAnswersLabel,
                value = ValueViewModel(langMessages.checkAnswersValueWholeMovement),
                actions = Seq(
                  ActionItemViewModel(
                    langMessages.change,
                    routes.HowGiveInformationController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, NormalMode).url,
                    id = HowGiveInformationPage
                  ).withVisuallyHiddenText(langMessages.checkAnswersHiddenChangeLinkText)
                )
              ))
          }
        }

        "when no answer is set" - {

          "must render None" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
            moreHowGiveInformationSummary.row() mustBe None
          }
        }
      }
    }
  }
}
