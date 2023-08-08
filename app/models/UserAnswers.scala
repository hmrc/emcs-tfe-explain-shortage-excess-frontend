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

package models

import models.response.emcsTfe.MovementItem
import play.api.libs.json._
import queries.{Gettable, Settable}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import pages.individualItems.CheckAnswersItemPage

import java.time.Instant

final case class UserAnswers(internalId: String,
                             ern: String,
                             arc: String,
                             data: JsObject = Json.obj(),
                             lastUpdated: Instant = Instant.now) {


  /**
   * @return all item unique references which have answers against them
   */
  private[models] def itemKeys: Seq[String] = (data \\ "items").flatMap {
    case JsObject(underlying) => underlying.keys.toSeq
    case _ => Seq()
  }.toSeq

  /**
   * @param key an item's unique reference
   * @param reads
   * @tparam A
   * @return a Seq[A] when an A is present in data, and a Seq.empty when it isn't
   */
  private[models] def getItemWithReads[A](key: String)(reads: Reads[A]): Seq[A] = {
    data \ "items" \ key match {
      case JsDefined(value) =>
        value.validate(reads) match {
          case JsSuccess(value, _) => Seq(value)
          case JsError(_) => Seq.empty
        }
      case _: JsUndefined => Seq.empty
    }
  }

  def itemReferences: Seq[Int] = {
    itemKeys.flatMap {
      getItemWithReads(_)(MovementItem.readItemUniqueReference)
    }.sorted
  }

  def allItemsIncludingIncomplete: Seq[ItemModel] =
    itemKeys.flatMap {
      getItemWithReads(_)(ItemModel.reads)
    }.sortBy(_.itemUniqueReference)

  def completedItems: Seq[ItemModel] =
    allItemsIncludingIncomplete.filter(item => get(CheckAnswersItemPage(item.itemUniqueReference)).contains(true))

  private[models] def handleResult: JsResult[JsObject] => UserAnswers = {
    case JsSuccess(updatedAnswers, _) =>
      copy(data = updatedAnswers)
    case JsError(errors) =>
      throw JsResultException(errors)
  }

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).asOpt.flatten

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): UserAnswers =
    handleResult {
      data.setObject(page.path, Json.toJson(value))
    }

  def remove[A](page: Settable[A]): UserAnswers =
    handleResult {
      data.removeObject(page.path)
    }

  def removeItem(idx: Int): UserAnswers =
    handleResult {
      data.removeObject(__ \ "items" \ s"item-$idx")
    }
}

object UserAnswers {

  val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "internalId").read[String] and
        (__ \ "ern").read[String] and
        (__ \ "arc").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
      )(UserAnswers.apply _)
  }

  val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "internalId").write[String] and
        (__ \ "ern").write[String] and
        (__ \ "arc").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
      )(unlift(UserAnswers.unapply))
  }

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)
}
