package io.weave.cluster

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.pattern.pipe
import akka.http.scaladsl.Http

class FrontEntry extends Actor with ActorLogging {

  val config = ConfigFactory.load()
  val role = config.getStringList("akka.cluster.roles").get(0)
  val interface = config.getString("cluster.bind-hostname")
  val port = 8080

  implicit val mat = ActorMaterializer()
  
  import akka.http.scaladsl.server.Directives._
  val handler: Flow[HttpRequest, HttpResponse, Any] = Route.handlerFlow( get { complete("Hello, stranger!") } )

  import context.dispatcher
  Http(context.system).bindAndHandle(handler, interface, port).pipeTo(self)
  
  override def preStart() = {
    log.info("Started entry " + this.getClass.toString)
  }
  
  override def receive() = {
    case other => log.info("Received message {}", other)
  }


  
}