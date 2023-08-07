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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import pages._
import pages.individualItems.ItemAmountPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryItemAmountUserAnswersEntry: Arbitrary[(ItemAmountPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ItemAmountPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryGiveInformationMovementUserAnswersEntry: Arbitrary[(GiveInformationMovementPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GiveInformationMovementPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDetailsSelectItemUserAnswersEntry: Arbitrary[(DetailsSelectItemPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DetailsSelectItemPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhenReceiveShortageExcessUserAnswersEntry: Arbitrary[(WhenReceiveShortageExcessPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenReceiveShortageExcessPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowGiveInformationUserAnswersEntry: Arbitrary[(HowGiveInformationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowGiveInformationPage.type]
        value <- arbitrary[HowGiveInformation].map(Json.toJson(_))
      } yield (page, value)
    }

}
