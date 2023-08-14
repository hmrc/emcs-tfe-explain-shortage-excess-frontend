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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import base.SpecBase
import models.response.emcsTfe.MovementItem
import pages.QuestionPage
import pages.individualItems._
import play.api.libs.json._


class UserAnswersSpec extends SpecBase {

  case class TestPage(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override def path: JsPath = jsPath \ toString

    override def toString: String = "TestPage"
  }

  case class TestPage2(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override def path: JsPath = jsPath \ toString

    override def toString: String = "TestPage2"
  }

  case class TestModel(TestPage: String,
                       TestPage2: Option[String] = None)

  object TestModel {
    implicit val fmt: Format[TestModel] = Json.format[TestModel]
  }

  "UserAnswers" - {

    "when calling .set(page)" - {

      "when no data exists for that page" - {

        "must set the answer for the first time" in {
          emptyUserAnswers.set(TestPage(), "foo") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
        }
      }

      "when data exists for that page" - {

        "must change the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.set(TestPage(), "bar") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "bar"
          ))
        }
      }

      "when setting at a subPath with indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 2), "wizz")


          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          )
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 2), "wizz")

          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          )
        }
      }
    }

    "when calling .get(page)" - {

      "when no data exists for that page" - {

        "must return None" in {
          emptyUserAnswers.get(TestPage()) mustBe None
        }
      }

      "when data exists for that page" - {

        "must Some(data)" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.get(TestPage()) mustBe Some("foo")
        }
      }

      "when getting data at a subPath with indexes" - {

        "must return the answer at the subPath" in {

          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0)) mustBe Some("foo")
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0 \ "subItems" \ 0)) mustBe Some("foo")
        }
      }
    }

    "when calling .remove(page)" - {

      "when no data exists for that page" - {

        "must return the emptyUserAnswers unchanged" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "AnotherPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe withData
        }
      }

      "when data exists for that page" - {

        "must remove the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe emptyUserAnswers
        }
      }

      "when removing data at a subPath with indexes" - {

        "when the page is the last page in the subObject" - {

          "must remove the entire object from the array at the subPath" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "bar"),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }

        "when the page is NOT the last page in the subObject" - {

          "must remove just that page object key from the object in the array" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj(
                  "TestPage" -> "bar",
                  "TestPage2" -> "bar2"
                ),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage2" -> "bar2"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }
      }

      "when removing at a subPath which contains nested indexes" - {

        "when the page is that last item in the arrays object" - {

          "must remove the object containing the answer from the array at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "bar"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }

        "when the page is NOT the last item in the arrays object" - {

          "must remove just that key from the object at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage" -> "bar",
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }
      }
    }

    "when calling .handleResult" - {

      "when failed to update the UserAnswers" - {

        "must throw the exception" in {
          intercept[JsResultException](emptyUserAnswers.handleResult(JsError("OhNo")))
        }
      }

      "when updated UserAnswers successfully" - {

        "must return the user answers" in {
          emptyUserAnswers.handleResult(JsSuccess(emptyUserAnswers.data)) mustBe emptyUserAnswers
        }
      }
    }

    "when calling .itemReferences" - {

      "must return all the item references" - {

        "when item references are in user answers, and it must be sorted by reference" in {

          val withData = emptyUserAnswers
            .set(SelectItemPage(2), 2)
            .set(ChooseShortageExcessItemPage(2), ChooseShortageExcessItem.Excess)
            .set(SelectItemPage(1), 1)
            .set(ChooseShortageExcessItemPage(1), ChooseShortageExcessItem.Shortage)

          withData.itemReferences mustBe Seq(1, 2)
        }
      }

      "must return a filtered list" - {

        "when not all item references are in user answers" in {

          val withData = emptyUserAnswers
            .set(ChooseShortageExcessItemPage(1), ChooseShortageExcessItem.Shortage)
            .set(SelectItemPage(2), 2)
            .set(ChooseShortageExcessItemPage(2), ChooseShortageExcessItem.Excess)

          withData.itemReferences mustBe Seq(2)
        }

        "when no item references are in user answers" in {

          val withData = emptyUserAnswers
            .set(ChooseShortageExcessItemPage(1), ChooseShortageExcessItem.Shortage)
            .set(ChooseShortageExcessItemPage(2), ChooseShortageExcessItem.Excess)

          withData.itemReferences mustBe Seq()
        }

        "when user answers is empty" in {

          val withData = emptyUserAnswers

          withData.itemReferences mustBe Seq()
        }
      }
    }

    "when calling .completedItems" - {

      "must return all the completed items" - {

        "when item references are in user answers, and CheckAnswersItemPage(idx) is true" in {

          val withData = emptyUserAnswers
            .set(SelectItemPage(1), 1)
            .set(CheckAnswersItemPage(1), true)
            .set(SelectItemPage(2), 2)
            .set(CheckAnswersItemPage(2), true)

          withData.completedItems mustBe Seq(
            ItemModel(1),
            ItemModel(2)
          )
        }
      }

      "must return a filtered list" - {

        "when not all items are completed" in {

          val withData = emptyUserAnswers
            // item 1 has CheckAnswersItemPage = true
            .set(SelectItemPage(1), 1)
            .set(CheckAnswersItemPage(1), true)
            // item 2 has no CheckAnswersItemPage
            .set(SelectItemPage(2), 2)
            // item 3 has CheckAnswersItemPage = false
            .set(SelectItemPage(3), 3)
            .set(CheckAnswersItemPage(3), false)

          withData.completedItems mustBe Seq(
            // only item 1 is returned
            ItemModel(1)
          )
        }
      }
    }

    "when calling .itemKeys" - {
      "must return all keys" - {
        "when items is an object" in {
          val input = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.obj(
              "key1" -> "value1",
              "key2" -> "value2"
            )
          ))

          input.itemKeys mustBe Seq("key1", "key2")
        }
      }

      "must return an empty Seq" - {
        "when items is not an object" in {
          val input = emptyUserAnswers.copy(data = Json.obj(
            "items" -> JsArray(Seq(Json.obj(
              "key1" -> "value1",
              "key2" -> "value2"
            )))
          ))

          input.itemKeys mustBe Seq()
        }
      }
    }

    "when calling .getItemWithReads(key)" - {
      "must return a value" - {
        "when value found matches the Reads" in {
          val input = emptyUserAnswers
            .set(SelectItemPage(1), 1)
            .set(SelectItemPage(2), 2)

          input.getItemWithReads("item-1")(MovementItem.readItemUniqueReference) mustBe Seq(1)
        }
      }

      "must return an empty Seq" - {
        "when value found doesn't match the Reads" in {
          val input = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.obj(
              "item-1" -> "1"
            )
          ))

          input.getItemWithReads("item-1")(ItemModel.reads) mustBe Seq()
        }

        "when no value is found for the inputted key" in {
          val input = emptyUserAnswers
            .set(SelectItemPage(1), 1)
            .set(SelectItemPage(2), 2)

          input.getItemWithReads("item-3")(MovementItem.readItemUniqueReference) mustBe Seq()
        }
      }
    }

  }
}