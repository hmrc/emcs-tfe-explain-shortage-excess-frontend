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
import models.requests.{DataRequest, MovementRequest, OptionalDataRequest, UserRequest}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.data.Forms._
import play.api.test.FakeRequest
import viewmodels.traderInfo.TraderInfo

class ViewUtilsSpec extends ViewSpecBase with ViewBehaviours {

  def createMovementRequest(hasMultipleErns: Boolean): MovementRequest[_] = {
    MovementRequest(
      UserRequest(FakeRequest("GET", "/"), testErn, testInternalId, testCredId, hasMultipleErns),
      testArc,
      getMovementResponseModel
    )
  }

  def createDataRequest(hasMultipleErns: Boolean): DataRequest[_] =
    DataRequest(createMovementRequest(hasMultipleErns), emptyUserAnswers, testMinTraderKnownFacts)

  def createOptionalDataRequest(hasMultipleErns: Boolean): OptionalDataRequest[_] =
    OptionalDataRequest(createMovementRequest(hasMultipleErns), None, Some(testMinTraderKnownFacts))


  ".title" in {
    case class UserData(name: String, age: Int)

    val userForm = Form(
      mapping(
        "name" -> text,
        "age" -> number
      )(UserData.apply)(UserData.unapply)
    )

    implicit val msgs: Messages = messages(app)

    ViewUtils.title(userForm, "TITLE") mustBe " TITLE - Excise Movement and Control System - GOV.UK"
  }

  ".titleNoForm" in {

    implicit val msgs: Messages = messages(app)
    ViewUtils.titleNoForm("TITLE") mustBe "TITLE - Excise Movement and Control System - GOV.UK"
  }

  ".maybeShowActiveTrader" - {

    "given an optional data request, when the user has multiple ERNs" in {
      ViewUtils.maybeShowActiveTrader(createOptionalDataRequest(true)) mustBe Some(TraderInfo("testTraderName", "ern"))
    }

    "given an optional data request, when the user has a single ERN" in {
      ViewUtils.maybeShowActiveTrader(createOptionalDataRequest(false)) mustBe None
    }

    "given a data request, when the user has multiple ERNs" in {
      ViewUtils.maybeShowActiveTrader(createDataRequest(true)) mustBe Some(TraderInfo("testTraderName", "ern"))
    }

    "given a data request, when the user has a single ERN" in {
      ViewUtils.maybeShowActiveTrader(createDataRequest(false)) mustBe None
    }
  }

}
