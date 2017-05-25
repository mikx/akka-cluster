package io.weave.cluster


import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging

object ApplicationMain extends App {

  case class Entry(port: String, props: Props)
  
  val config = ConfigFactory.load()
  val role = config.getStringList("akka.cluster.roles").get(0)
  
  val entries = Map(
      "SEED"  -> Entry("2551", Props(classOf[SeedEntry])), 
      "FRONT" -> Entry("2252", Props(classOf[FrontEntry])),
      "BACK"  -> Entry("2253", Props(classOf[BackEntry]))
  )  

  // create runtime configuration
  val overrideConfig = ConfigFactory.empty()
    .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(entries.get(role).map(_.port).getOrElse("0")))

  // create system
  val system = ActorSystem("ClusterSystem", overrideConfig.withFallback(config))
  
  // start simple listener
  val simpleClusterListener = system.actorOf(Props(classOf[SimpleClusterListener]), "SimpleClusterListener")
  
  // start main actor
  val main = entries.get(role).map(entry => system.actorOf(entry.props, role)).getOrElse(Actor.noSender)
  
  
  Logging(system, getClass).info(s"Container ${role} is up and running")
  Await.result(system.whenTerminated, Duration.Inf)

}


