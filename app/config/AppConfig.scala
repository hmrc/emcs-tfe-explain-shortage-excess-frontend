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

package config

import featureswitch.core.config.{FeatureSwitching, StubGetTraderKnownFacts}
import play.api.Configuration
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  override lazy val config: AppConfig = this

  private lazy val host: String = configuration.get[String]("host")

  lazy val appName: String = configuration.get[String]("appName")

  lazy val selfUrl: String = servicesConfig.baseUrl("emcs-tfe-explain-shortage-excess-frontend")

  lazy val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")

  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

  lazy val signOutUrl: String = configuration.get[String]("urls.signOut")

  lazy val deskproName: String = configuration.get[String]("deskproName")

  lazy val loginGuidance: String = configuration.get[String]("urls.loginGuidance")

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")

  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName/beta"

  private lazy val contactHost = configuration.get[String]("contact-frontend.host")

  lazy val loginUrl: String = configuration.get[String]("urls.login")

  lazy val registerGuidance: String = configuration.get[String]("urls.registerGuidance")

  lazy val signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  lazy val fallbackProceduresUrl: String = configuration.get[String]("urls.fallbackProcedures")

  lazy val contactEMCSHelpdeskUrl: String = configuration.get[String]("urls.contactEmcsHelpdesk")

  lazy val emcsGeneralEnquiriesUrl: String = configuration.get[String]("urls.emcsGeneralEnquiries")

  private lazy val tradeTariffCommoditiesUrl: String = configuration.get[String]("urls.tradeTariffCommodities")

  def getUrlForCommodityCode(code: String): String = s"$tradeTariffCommoditiesUrl/${code}00"

  def loginContinueUrl(ern: String, arc: String): String = configuration.get[String]("urls.loginContinue") + s"/trader/$ern/movement/$arc"

  def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def emcsTfeFrontendBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-frontend")

  private def nrsBrokerService: String = servicesConfig.baseUrl("nrs-broker")

  def nrsBrokerBaseUrl(): String = s"$nrsBrokerService/emcs-tfe-nrs-message-broker"

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  def emcsTfeHomeUrl: String = configuration.get[String]("urls.emcsTfeHome")

  def emcsMovementDetailsUrl(ern: String, arc: String): String =
    configuration.get[String]("urls.emcsTfeMovementDetails").replace(":ern", ern).replace(":arc", arc)

  def emcsMovementsUrl(ern: String): String = configuration.get[String]("urls.emcsTfeMovements").replace(":ern", ern)

  private def traderKnownFactsReferenceDataService: String =
    if (isEnabled(StubGetTraderKnownFacts)) {
      servicesConfig.baseUrl("emcs-tfe-reference-data-stub")
    } else {
      servicesConfig.baseUrl("emcs-tfe-reference-data")
    }

  def betaBannerFeedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$deskproName&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  def internalAuthToken: String = configuration.get[String]("internal-auth.token")

  private def userAllowListService: String = servicesConfig.baseUrl("user-allow-list")

  def userAllowListBaseUrl: String = s"$userAllowListService/user-allow-list"

  private def referenceDataService: String = servicesConfig.baseUrl("emcs-tfe-reference-data")

  def referenceDataBaseUrl: String = s"$referenceDataService/emcs-tfe-reference-data"

  def traderKnownFactsReferenceDataBaseUrl: String = s"$traderKnownFactsReferenceDataService/emcs-tfe-reference-data"
}


