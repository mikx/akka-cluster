package io.weave.cluster.user

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ClusterSharding
import akka.actor.Props
import akka.actor.ActorRef
import akka.cluster.sharding.ClusterShardingSettings

object UserActor {

  final case class EntityEnvelope(id: String, payload: Any)
  
  val extractEntityId: ShardRegion.ExtractEntityId = {
    case EntityEnvelope(id, payload) => (id, payload)
  }

  val numberOfShards = 12

  val extractShardId: ShardRegion.ExtractShardId = {
    case EntityEnvelope(id, _) => (id.hashCode % numberOfShards).toString
    case ShardRegion.StartEntity(id) => (id.hashCode % numberOfShards).toString
  }
  
  def startUserRegion(system: ActorSystem): ActorRef = ClusterSharding(system).start(
    typeName = "User",
    entityProps = Props[UserActor],
    settings = ClusterShardingSettings(system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId
  )
  
  def props = Props[UserActor]
  
}


class UserActor extends Actor with ActorLogging {
  
  override def preStart() {
    log.info("My name is {}", self.path.name)
  }
  
  override val receive = {
    case UserActor.EntityEnvelope(id, msg) => sender ! s"${msg} from ${id}"
    case any => log.info("Received message {}", any)
  }
  
}