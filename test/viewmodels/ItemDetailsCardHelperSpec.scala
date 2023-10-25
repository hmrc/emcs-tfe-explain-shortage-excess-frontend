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
import config.AppConfig
import fixtures.messages.ItemDetailsMessages
import models.ReferenceDataUnitOfMeasure
import models.response.emcsTfe.WineProduct
import models.response.referenceData.CnCodeInformation
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import views.html.components.{link, list}

class ItemDetailsCardHelperSpec extends SpecBase {

  private def summaryListRowBuilder(key: Content, value: Content) = SummaryListRow(
    Key(key),
    Value(value),
    classes = "govuk-summary-list__row--no-border"
  )

  "ItemDetailsCardHelper" - {

    Seq(ItemDetailsMessages.English, ItemDetailsMessages.Welsh).foreach { langMessages =>

      s"when rendered for language code '${langMessages.lang.code}'" - {

        implicit lazy val app = applicationBuilder().build()
        implicit lazy val messages = messagesApi(app).preferred(Seq(langMessages.lang))

        lazy val link = app.injector.instanceOf[link]
        lazy val list = app.injector.instanceOf[list]
        lazy val appConfig = app.injector.instanceOf[AppConfig]

        lazy val helper = new ItemDetailsCardHelper(link, list, appConfig)

        "should show the link for CN Code" - {
          "if the CN Code is not S500" in {
            helper.commodityCodeRow()(item1, messages) mustBe Some(summaryListRowBuilder(
              Text(langMessages.commodityCodeKey),
              HtmlContent(link(link = appConfig.getUrlForCommodityCode(item1.cnCode), messageKey = item1.cnCode, opensInNewTab = true, id = Some("commodity-code")))
            ))
          }
        }

        "should NOT show the link for CN Code" - {
          "if the CN Code is S500" in {
            helper.commodityCodeRow()(item1.copy(productCode = "S500"), messages) mustBe Some(summaryListRowBuilder(
              Text(langMessages.commodityCodeKey),
              Text(item1.cnCode)
            ))
          }
        }

        "should render the ItemDetails card" - {

          val cnCodeInformation = CnCodeInformation("cn code description", "excise product code description", ReferenceDataUnitOfMeasure.`2`)

          //noinspection ScalaStyle
          def card(wineProduct: Option[WineProduct]): Seq[SummaryListRow] = {
            Seq(
              Seq(summaryListRowBuilder(
                Text(langMessages.commodityCodeKey),
                HtmlContent(link(link = appConfig.getUrlForCommodityCode(item1.cnCode), messageKey = item1.cnCode, opensInNewTab = true, id = Some("commodity-code")))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.descriptionKey),
                HtmlContent(cnCodeInformation.exciseProductCodeDescription)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.quantityKey),
                Text(langMessages.quantityValue(item1.quantity))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.grossWeightKey),
                Text(langMessages.grossWeightValue(item1.grossMass))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.netWeightKey),
                Text(langMessages.netWeightValue(item1.netMass))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.densityKey),
                HtmlContent(langMessages.densityValue(item1.density.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.alcoholicStrengthKey),
                Text(langMessages.alcoholicStrengthValue(item1.alcoholicStrength.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.maturationAgeKey),
                Text(item1.maturationAge.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.degreePlatoKey),
                HtmlContent(langMessages.degreePlatoValue(item1.degreePlato.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.fiscalMarkKey),
                Text(item1.fiscalMark.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.designationOfOriginKey),
                Text(item1.designationOfOrigin.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.sizeOfProducerKey),
                Text(langMessages.sizeOfProducerValue(item1.sizeOfProducer.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.brandNameOfProductKey),
                Text(item1.brandNameOfProduct.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.commercialDescriptionKey),
                Text(item1.commercialDescription.get)
              )),
              wineProduct match {
                case Some(_) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineProductCategoryKey),
                  Text(langMessages.wineWithoutPDOPGI)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineOperationsKey),
                  value.wineOperations match {
                    case Some(values) if values.nonEmpty => HtmlContent(list(values.map(v => Html(v))))
                    case _ => Text(langMessages.wineOperationsValueNone)
                  }
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineGrowingZoneCodeKey),
                  Text(value.wineGrowingZoneCode.get)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.thirdCountryOfOriginKey),
                  Text(value.thirdCountryOfOrigin.get)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineOtherInformationKey),
                  Text(value.otherInformation.get)
                ))
                case None => Seq()
              }
            ).flatten
          }

          "when wineOperations is not empty" in {
            helper.constructItemDetailsCard(item1, cnCodeInformation) mustBe card(Some(item1.wineProduct.get))
          }

          "when wineOperations is empty" in {
            val item1WithEmptyWineOperations = item1.copy(wineProduct = Some(wineProduct.copy(wineOperations = Some(Seq()))))
            helper.constructItemDetailsCard(item1WithEmptyWineOperations, cnCodeInformation) mustBe card(item1WithEmptyWineOperations.wineProduct)
          }

          "when wineProduct is empty" in {
            val item1WithEmptyWineOperations = item1.copy(wineProduct = None)
            helper.constructItemDetailsCard(item1WithEmptyWineOperations, cnCodeInformation) mustBe card(item1WithEmptyWineOperations.wineProduct)
          }
        }

        "should render the PackagingType card" in {

          helper.constructPackagingTypeCard(boxPackage) mustBe Seq(
            summaryListRowBuilder(
              Text(langMessages.packagingTypeKey),
              Text(boxPackage.typeOfPackage)
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingQuantityKey),
              Text(boxPackage.quantity.get.toString())
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingIdentityOfCommercialSealKey),
              Text(boxPackage.identityOfCommercialSeal.get)
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingSealInformationKey),
              Text(boxPackage.sealInformation.get)
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingShippingMarksKey),
              Text(boxPackage.shippingMarks.get)
            )
          )
        }
      }
    }
  }
}
