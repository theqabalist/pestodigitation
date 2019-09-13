scalacOptions ++= Seq(
  "-Ywarn-unused",
  "-Ypartial-unification",
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  compilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.8"),
  compilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.3.0-M4")
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.6.0",
  "io.chrisdavenport" %% "cats-par" % "0.2.1",
  "org.typelevel" %% "cats-mtl-core" % "0.4.0"
)

val http4sVersion = "0.19.0"

libraryDependencies ++= Seq(
  "http4s-blaze-client",
  "http4s-async-http-client",
  "http4s-circe"
).map("org.http4s" %% _ % http4sVersion)

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "circe-core",
  "circe-generic",
  "circe-parser"
).map("io.circe" %% _ % circeVersion)
