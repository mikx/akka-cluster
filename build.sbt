
val akkaVersion     = "2.5.2"
val akkaHttpVersion = "10.0.6"

cleanKeepFiles += baseDirectory { base => base / "target/universal" } .value

lazy val commonSettings = Seq(

  name            := "WeaveCluster",
  organization    := "io.weave",
  version         := "1.0",
  
  scalaVersion    := "2.12.2",
  
  libraryDependencies ++= Seq(
    "com.typesafe.akka"  %% "akka-actor"             % akkaVersion,
    "com.typesafe.akka"  %% "akka-cluster"           % akkaVersion,
    "com.typesafe.akka"  %% "akka-cluster-sharding"  % akkaVersion,
    "com.typesafe.akka"  %% "akka-stream"            % akkaVersion,
    "com.lightbend.akka" %% "akka-management-cluster-http" % "0.3"
  ),

  resolvers ++= Seq(
    "spray repo"         at "http://repo.spray.io/",
    "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases/",
    "typesafe repo"      at "http://repo.typesafe.com/typesafe/releases/"
  )
)


lazy val root = (project in file(".")).settings(
  commonSettings,
  EclipseKeys.skipProject := true
).aggregate(core, front, back, seed)


lazy val core = (project in file("core")).settings(
  commonSettings,
  name := "cluster-core"
)


lazy val back = (project in file("back")).settings(
  commonSettings,
  name := "cluster-back"
).dependsOn(core).enablePlugins(JavaAppPackaging,DockerPlugin)


lazy val front = (project in file("front")).settings(
  commonSettings,
  name := "cluster-front",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"         % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion  % Test,
    "org.scalatest"     %% "scalatest"         % "3.0.1"          % Test
  )  
).dependsOn(core).enablePlugins(JavaAppPackaging,DockerPlugin)


lazy val seed = (project in file("seed")).settings(
  commonSettings,
  name := "cluster-seed"
).dependsOn(core).enablePlugins(JavaAppPackaging,DockerPlugin)

