
val akkaVersion     = "2.5.2"
val akkaHttpVersion = "10.0.6"

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

cleanKeepFiles += baseDirectory { base => base / "target/universal" } .value

dockerExposedPorts := Seq(2552)

lazy val root = (project in file(".")).settings(

  name            := "WeaveCluster",
  organization    := "io.weave",
  version         := "1.0",
  
  scalaVersion    := "2.12.2",

  resolvers ++= Seq(
    "spray repo"         at "http://repo.spray.io/",
    "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases/",
    "typesafe repo"      at "http://repo.typesafe.com/typesafe/releases/"
  ),
  
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor"        % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster"      % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"       % akkaVersion,
    "com.typesafe.akka" %% "akka-http"         % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion  % Test,
    "org.scalatest"     %% "scalatest"         % "3.0.1"          % Test
  )
  
)
