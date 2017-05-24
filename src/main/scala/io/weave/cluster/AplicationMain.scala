package io.weave.cluster


import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging

object ApplicationMain extends App {

  val config = ConfigFactory.load()
  val role = config.getStringList("akka.cluster.roles").get(0)
  val port = if (role == "SEED") "2552" else "2251"  
  
  val overrideConfig = ConfigFactory.empty()
    .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(port))
  
  val system = ActorSystem("ClusterSystem", overrideConfig.withFallback(config))
  val simpleClusterListener = system.actorOf(Props(classOf[SimpleClusterListener]), "SimpleClusterListener")
  
  Logging(system, getClass).info("Seed server is up and running")
  Await.result(system.whenTerminated, Duration.Inf)

}
