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

  val config = ConfigFactory.load()
  val role = config.getStringList("akka.cluster.roles").get(0)
  
  // create system
  val system = ActorSystem("ClusterSystem", config)
  
  // start simple listener
  val simpleClusterListener = system.actorOf(Props(classOf[SimpleClusterListener]), "SimpleClusterListener")
  
  // main entry
  val main = system.actorOf(Props(classOf[SeedEntry]), role)
  Logging(system, getClass).info(s"Container ${role} is up and running")

  // wait till done
  Await.result(system.whenTerminated, Duration.Inf)

}


