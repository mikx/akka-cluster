package io.weave.cluster


import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.event.Logging
import akka.actor.Props

object ApplicationMain extends App {
  
  val system = ActorSystem("ClusterSystem")
  val simpleClusterListener = system.actorOf(Props(classOf[SimpleClusterListener]), "SimpleClusterListener")
  
  Logging(system, getClass).info("Seed server is up and running")
  Await.result(system.whenTerminated, Duration.Inf)

}
