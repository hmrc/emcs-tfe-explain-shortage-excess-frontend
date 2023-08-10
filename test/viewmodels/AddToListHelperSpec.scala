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
import mocks.services.MockUserAnswersService
import models.UnitOfMeasure.Kilograms
import models.requests.DataRequest
import models.{CheckMode, ChooseShortageExcessItem, ReviewMode}
import pages.individualItems._
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.govukfrontend.views.Aliases.{SummaryList, SummaryListRow}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

import scala.concurrent.{ExecutionContext, Future}

class AddToListHelperSpec extends SpecBase with MockUserAnswersService {
  private lazy val app: Application = applicationBuilder().build()
  private lazy val link: link = app.injector.instanceOf[link]

  implicit lazy val msgs: Messages = messages(app)

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  trait Test {

    lazy val addToListHelper = new AddToListHelper(link, mockUserAnswersService)

    val wrongWithItemPageValue: ChooseShortageExcessItem
    val amountReceivedValue: Option[BigDecimal] = Some(3.2)
    val moreInformationValue: Option[String] = Some("more information")

    private lazy val userAnswers = emptyUserAnswers
      .set(SelectItemPage(1), 1)
      .set(ChooseShortageExcessItemPage(1), wrongWithItemPageValue)
      .set(ItemAmountPage(1), amountReceivedValue)

    implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
      FakeRequest(),
      moreInformationValue
        .map(value => userAnswers.set(GiveInformationItemPage(1), value))
        .getOrElse(userAnswers)
    )
  }


  "AddToListHelperHelper" - {

    "being rendered" - {

      Seq(ChooseShortageExcessItem.Shortage, ChooseShortageExcessItem.Excess).foreach(
        shortageOrExcess =>
          s"for shortage or excess value [$shortageOrExcess]" - {
            "when all values are provided" - {
              "must return a filled-in SummaryList" in new Test {
                override val wrongWithItemPageValue: ChooseShortageExcessItem = shortageOrExcess
                val result: SummaryList = SummaryList(
                  rows = Seq(
                    SummaryListRow(
                      key = "chooseShortageExcessItem.checkYourAnswers.label",
                      value = ValueViewModel(msgs(s"chooseShortageExcessItem.$shortageOrExcess")),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, testIdx, CheckMode).url,
                        visuallyHiddenText = Some(msgs("chooseShortageExcessItem.checkYourAnswers.change.hidden")),
                        attributes = Map("id" -> "chooseShortageExcessItem")
                      ))))
                    ),
                    SummaryListRow(
                      key = "itemAmount.checkYourAnswers.label",
                      value = ValueViewModel(msgs(
                        s"itemAmount.checkYourAnswers.amount.value",
                        amountReceivedValue.get,
                        msgs(s"unitOfMeasure.kilograms.long")
                      )),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.ItemAmountController.onPageLoad(testErn, testArc, item1.itemUniqueReference, CheckMode).url,
                        visuallyHiddenText = Some(msgs("itemAmount.checkYourAnswers.change.hidden")),
                        attributes = Map("id" -> "itemAmount")
                      ))))
                    ),
                    SummaryListRow(
                      key = s"giveInformationItem.checkYourAnswers.$shortageOrExcess.label",
                      value = ValueViewModel(Text(moreInformationValue.get)),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.GiveInformationItemController.onPageLoad(testErn, testArc, testIdx, CheckMode).url,
                        visuallyHiddenText = Some(msgs(s"giveInformationItem.checkYourAnswers.$shortageOrExcess.change.hidden")),
                        attributes = Map("id" -> "giveInformationItem")
                      ))))
                    )
                  )
                )

                await(addToListHelper.summaryList(item1, Kilograms)) mustBe result
              }
            }

            "when optional values are missing" - {
              "must return default text on optional rows" in new Test {
                override val wrongWithItemPageValue: ChooseShortageExcessItem = shortageOrExcess
                override val amountReceivedValue: Option[BigDecimal] = None
                override val moreInformationValue: Option[String] = None
                val result: SummaryList = SummaryList(
                  rows = Seq(
                    SummaryListRow(
                      key = "chooseShortageExcessItem.checkYourAnswers.label",
                      value = ValueViewModel(msgs(s"chooseShortageExcessItem.$shortageOrExcess")),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, testIdx, CheckMode).url,
                        visuallyHiddenText = Some(msgs("chooseShortageExcessItem.checkYourAnswers.change.hidden")),
                        attributes = Map("id" -> "chooseShortageExcessItem")
                      ))))
                    ),
                    SummaryListRow(
                      key = "itemAmount.checkYourAnswers.label",
                      value = ValueViewModel(HtmlContent(link(
                        link = routes.ItemAmountController.onPageLoad(testErn, testArc, item1.itemUniqueReference, CheckMode).url,
                        messageKey = "itemAmount.checkYourAnswers.addMoreInformation"
                      ))),
                      actions = Some(Actions(items = Seq()))
                    ),
                    SummaryListRow(
                      key = s"giveInformationItem.checkYourAnswers.$shortageOrExcess.label",
                      value = ValueViewModel(
                        HtmlContent(link(
                          link = routes.GiveInformationItemController.onPageLoad(testErn, testArc, testIdx, CheckMode).url,
                          messageKey = s"giveInformationItem.checkYourAnswers.$shortageOrExcess.addMoreInformation"
                        ))
                      ),
                      actions = Some(Actions(items = Seq()))
                    )
                  )
                )

                await(addToListHelper.summaryList(item1, Kilograms)) mustBe result
              }
            }

            "when optional values are empty" - {
              "must return default text on optional rows" in new Test {
                override val wrongWithItemPageValue: ChooseShortageExcessItem = shortageOrExcess
                override val amountReceivedValue: Option[BigDecimal] = None
                override val moreInformationValue: Option[String] = Some("")
                val result: SummaryList = SummaryList(
                  rows = Seq(
                    SummaryListRow(
                      key = "chooseShortageExcessItem.checkYourAnswers.label",
                      value = ValueViewModel(s"chooseShortageExcessItem.$shortageOrExcess"),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, testIdx, CheckMode).url,
                        visuallyHiddenText = Some(msgs("chooseShortageExcessItem.checkYourAnswers.change.hidden")),
                        attributes = Map("id" -> "chooseShortageExcessItem")
                      ))))
                    ),
                    SummaryListRow(
                      key = "itemAmount.checkYourAnswers.label",
                      value = ValueViewModel(HtmlContent(link(
                        link = routes.ItemAmountController.onPageLoad(testErn, testArc, item1.itemUniqueReference, CheckMode).url,
                        messageKey = "itemAmount.checkYourAnswers.addMoreInformation"
                      ))),
                      actions = Some(Actions(items = Seq()))
                    ),
                    SummaryListRow(
                      key = s"giveInformationItem.checkYourAnswers.$shortageOrExcess.label",
                      value = ValueViewModel(
                        HtmlContent(link(
                          link = routes.GiveInformationItemController.onPageLoad(testErn, testArc, testIdx, CheckMode).url,
                          messageKey = s"giveInformationItem.checkYourAnswers.$shortageOrExcess.addMoreInformation"
                        ))
                      ),
                      actions = Some(Actions(items = Seq()))
                    )
                  )
                )

                await(addToListHelper.summaryList(item1, Kilograms)) mustBe result
              }
            }

            "when on the final CYA page" - {
              "must render the correct additionalLinkIdSignifier" in new Test {
                override val wrongWithItemPageValue: ChooseShortageExcessItem = shortageOrExcess
                val result: SummaryList = SummaryList(
                  rows = Seq(
                    SummaryListRow(
                      key = "chooseShortageExcessItem.checkYourAnswers.label",
                      value = ValueViewModel(msgs(s"chooseShortageExcessItem.$shortageOrExcess")),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.ChooseShortageExcessItemController.onPageLoad(testErn, testArc, testIdx, ReviewMode).url,
                        visuallyHiddenText = Some(msgs("chooseShortageExcessItem.checkYourAnswers.change.hidden")),
                        attributes = Map("id" -> "chooseShortageExcessItem-item-1")
                      ))))
                    ),
                    SummaryListRow(
                      key = "itemAmount.checkYourAnswers.label",
                      value = ValueViewModel(msgs(
                        s"itemAmount.checkYourAnswers.amount.value",
                        amountReceivedValue.get,
                        msgs(s"unitOfMeasure.kilograms.long")
                      )),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.ItemAmountController.onPageLoad(testErn, testArc, item1.itemUniqueReference, ReviewMode).url,
                        visuallyHiddenText = Some(msgs("itemAmount.checkYourAnswers.change.hidden")),
                        attributes = Map("id" -> "itemAmount-item-1")
                      ))))
                    ),
                    SummaryListRow(
                      key = s"giveInformationItem.checkYourAnswers.$shortageOrExcess.label",
                      value = ValueViewModel(Text(moreInformationValue.get)),
                      actions = Some(Actions(items = Seq(ActionItem(
                        content = msgs("site.change"),
                        href = routes.GiveInformationItemController.onPageLoad(testErn, testArc, testIdx, ReviewMode).url,
                        visuallyHiddenText = Some(msgs(s"giveInformationItem.checkYourAnswers.$shortageOrExcess.change.hidden")),
                        attributes = Map("id" -> "giveInformationItem-item-1")
                      ))))
                    )
                  )
                )

                await(addToListHelper.summaryList(item1, Kilograms, onFinalCheckAnswers = true)) mustBe result
              }
            }
          }
      )

      "must render an empty list" - {
        "when SelectItemPage is empty" in {
          lazy val addToListHelper = new AddToListHelper(link, mockUserAnswersService)

          implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(ChooseShortageExcessItemPage(1), ChooseShortageExcessItem.Excess)
          )

          MockUserAnswersService.set()returns(Future.successful(emptyUserAnswers))

          await(addToListHelper.summaryList(item1, Kilograms)) mustBe SummaryList(rows = Seq())
        }
        "when ChooseShortageExcessItemPage is empty" in {
          lazy val addToListHelper = new AddToListHelper(link, mockUserAnswersService)

          implicit def request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            emptyUserAnswers.set(SelectItemPage(1), 1)
          )

          MockUserAnswersService.set() returns (Future.successful(emptyUserAnswers))

          await(addToListHelper.summaryList(item1, Kilograms)) mustBe SummaryList(rows = Seq())
        }
      }
    }
  }
}
