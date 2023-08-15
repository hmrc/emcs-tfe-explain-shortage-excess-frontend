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

package models.audit

import base.SpecBase
import fixtures.audit.SubmitShortageExcessAuditModelFixtures

class SubmitShortageExcessAuditModelSpec extends SpecBase with SubmitShortageExcessAuditModelFixtures {

  "SubmitShortageExcessAuditModel" - {

    "should write a correct audit json" - {

      "when a successful submission has occurred" in {
        submitShortageOrExcessAuditSuccessful.detail mustBe submitShortageOrExcessAuditSuccessfulJSON
      }

      "when a failed to submit has occurred" in {
        submitShortageOrExcessAuditFailed.detail mustBe submitShortageOrExcessAuditFailedJSON
      }
    }
  }
}
