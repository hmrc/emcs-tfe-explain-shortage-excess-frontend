import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val playSuffix            = "-play-30"
  val scalatestVersion      = "3.2.17"
  val hmrcBootstrapVersion  = "8.4.0"
  val hmrcMongoVersion      = "1.7.0"
  val catsCoreVersion       = "2.10.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix"     % s"8.5.0",
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix"     %  hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"             %  hmrcMongoVersion,
    "com.google.inject"       %   "guice"                             % "5.1.0",
    "org.typelevel"           %%  "cats-core"                         % catsCoreVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"          % hmrcBootstrapVersion,
    "org.scalatest"           %%  "scalatest"                         % scalatestVersion,
    "org.scalatestplus"       %%  "scalacheck-1-17"                   % s"$scalatestVersion.0",
    "org.scalatestplus"       %%  "mockito-5-8"                       % s"$scalatestVersion.0",
    "org.scalatestplus.play"  %%  "scalatestplus-play"                % "5.1.0",
    "org.scalamock"           %%  "scalamock"                         % "5.2.0",
    "org.pegdown"             %   "pegdown"                           % "1.6.0",
    "org.jsoup"               %   "jsoup"                             % "1.17.2",
    "org.playframework"       %%  "play-test"                         % PlayVersion.current,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test$playSuffix"         % hmrcMongoVersion,
    "com.vladsch.flexmark"    %   "flexmark-all"                        % "0.64.8"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
