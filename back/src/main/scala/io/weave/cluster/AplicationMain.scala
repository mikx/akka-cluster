package io.weave.cluster


import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging
import io.weave.cluster.user.UserActor

object ApplicationMain extends App {

  val config = ConfigFactory.load()
  val role = config.getStringList("akka.cluster.roles").get(0)
  
  // create system
  val system = ActorSystem("ClusterSystem", config)
  val userRegion = UserActor.startUserRegion(system)
  
  // start simple listener
  val simpleClusterListener = system.actorOf(Props(classOf[SimpleClusterListener]), "SimpleClusterListener")
  
  // main entry
  val main = system.actorOf(Props(classOf[BackEntry]), role)
  Logging(system, getClass).info(s"Container ${role} is up and running")

  // wait till done
  Await.result(system.whenTerminated, Duration.Inf)

}


