import Dependencies.Libraries
name := "stock-exchange-platform"

version := "0.0.1"

scalaVersion := "2.13.11"

idePackagePrefix := Some("org.maximgran.stock_exchange_platform")
scalacOptions ++= List("-Ymacro-annotations", "-Ywarn-unused")

libraryDependencies ++= Seq(
  Libraries.cats,
  Libraries.catsEffect,
  Libraries.catsRetry,
  Libraries.log4cats,
  Libraries.newtype,
  Libraries.squants,
  Libraries.derevoCore,
  Libraries.logback % Runtime,
  Libraries.derevoCats,
  Libraries.derevoCirce,
  Libraries.monocleCore,
  Libraries.monocleLaw,
  Libraries.http4sClient,
  Libraries.http4sServer,
  Libraries.http4sCirce,
  Libraries.http4sDsl,
  Libraries.http4sJwtAuth,
  Libraries.refinedCats,
  Libraries.refinedCore,
  Libraries.refinedScalacheck,
  Libraries.circeRefined,
  Libraries.cirisRefined,
  Libraries.cirisEnum,
  Libraries.circeParser,
  Libraries.skunkCore,
  Libraries.skunkCirce,
  Libraries.redis4catsEffects,
  Libraries.redis4catsLog4cats
)
