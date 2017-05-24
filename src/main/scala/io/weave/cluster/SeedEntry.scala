package io.weave.cluster

import akka.actor.ActorLogging
import akka.actor.Actor

class SeedEntry extends Actor with ActorLogging {
  
  override def preStart() = {
    log.info("Started entry " + this.getClass.toString)
  }
  
  override def receive() = {
    case other => log.info("Received message {}", other)
  }
  
}