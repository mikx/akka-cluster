package io.weave.cluster

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.cluster.ClusterEvent._
import akka.cluster.Cluster

class SimpleClusterListener extends Actor with ActorLogging {
 
  val cluster = Cluster(context.system)
 
  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }
  
  // un-subscribe to cluster changes when stopped
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }
 
  def receive = {
    case state: CurrentClusterState => log.info("Current members: {}", state.members.mkString(", "))
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) => log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => ()
  }
  
}
