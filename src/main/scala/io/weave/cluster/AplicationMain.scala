package io.weave.cluster


import akka.actor.ActorSystem
import scala.concurrent.Await
import akka.event.Logging
import scala.concurrent.duration.Duration

object ApplicationMain extends App {
  
  val system = ActorSystem("MyActorSystem")
  val pingActor = system.actorOf(PingActor.props, "pingActor")
  pingActor ! PingActor.Initialize
  
  Logging(system, getClass).info("Weave server up and running")
  Await.result(system.whenTerminated, Duration.Inf)

}
