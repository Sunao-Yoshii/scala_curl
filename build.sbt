organization := "net.white-azalea"

name := "scala_curl"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.apache.httpcomponents" % "httpmime" % "4.3.1",
  "org.apache.httpcomponents" % "httpclient" % "4.3.1",
  "org.scalatra" %% "scalatra" % "2.2.2" % "test",
  "org.specs2" %% "specs2" % "2.3.6" % "test",
  "org.scalatra" %% "scalatra-specs2" % "2.2.1" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

parallelExecution in Test := false