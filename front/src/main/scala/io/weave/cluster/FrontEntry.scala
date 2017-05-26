package io.weave.cluster

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout

import io.weave.cluster.user.UserActor

class FrontEntry(userRegion: ActorRef) extends Actor with ActorLogging {

  val config = ConfigFactory.load()
  val role = config.getStringList("akka.cluster.roles").get(0)
  val interface = config.getString("cluster.bind-hostname")
  val port = 8080
  
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  implicit val mat = ActorMaterializer()
  
  import akka.http.scaladsl.server.Directives._
    
  val handler: Flow[HttpRequest, HttpResponse, Any] = Route.handlerFlow( 
    get {
      path(Segment) { id => 
       userRegion ! UserActor.EntityEnvelope(id, "Hello, stranger")
       complete(s"message ${id} sent")
      }
    }
  )

  val binding = Http(context.system).bindAndHandle(handler, interface, port)
  
  override def preStart() = {
    log.info("Started entry " + this.getClass.toString)
  }
  
  override def receive() = {
    case other => log.info("Received message {}", other)
  }

  override def postStop() = {
    binding.value.map(_.toOption).flatten.foreach(_.unbind())
  }

  
}