package example.api

import akka.actor.{Actor, ActorLogging}

class Service extends Actor with ActorLogging {
  def receive = {
    case m ⇒ log.info("received message {}", m)
  }
}
