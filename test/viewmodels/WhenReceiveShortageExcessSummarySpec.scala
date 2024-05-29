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
import fixtures.messages.WhenReceiveShortageExcessMessages
import models.CheckMode
import pages.WhenReceiveShortageExcessPage
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import utils.DateUtils
import viewmodels.checkAnswers.WhenReceiveShortageExcessSummary
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate

class WhenReceiveShortageExcessSummarySpec extends SpecBase with DateUtils {


  "WhenReceiveShortageExcessSummary" - {

    Seq(WhenReceiveShortageExcessMessages.English).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val msgs = messagesApi(app).preferred(Seq(langMessages.lang))
        lazy val moreWhenReceiveShortageExcessSummary = new WhenReceiveShortageExcessSummary

        "when an answer is set" - {

          "must render the expected SummaryListRow" in {

            val dateOfArrival = LocalDate.of(2020,1,1)
            val answers = emptyUserAnswers.set(WhenReceiveShortageExcessPage, dateOfArrival)
            implicit val request = dataRequest(FakeRequest(), answers)

            moreWhenReceiveShortageExcessSummary.row() mustBe
              Some(SummaryListRowViewModel(
                key = langMessages.checkAnswersLabel,
                value = ValueViewModel(Text(dateOfArrival.formatDateForUIOutput())),
                actions = Seq(
                  ActionItemViewModel(
                    langMessages.change,
                    routes.WhenReceiveShortageExcessController.onPageLoad(request.userAnswers.ern, request.userAnswers.arc, CheckMode).url,
                    id = WhenReceiveShortageExcessPage
                  ).withVisuallyHiddenText(langMessages.checkAnswersHiddenChangeLinkText)
                )
              ))
          }
        }

        "when no answer is set" - {

          "must render None" in {

            implicit val request = dataRequest(FakeRequest(), emptyUserAnswers)
            moreWhenReceiveShortageExcessSummary.row() mustBe None
          }
        }
      }
    }
  }
}
