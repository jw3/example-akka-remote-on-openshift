package example.c

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import example.api.{Discover, Register, Service}

import scala.concurrent.duration.DurationInt


object Boot extends App with LazyLogging {
  implicit val system = ActorSystem("example")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)

  system.actorOf(Discover.props("a")) ! "hello, from c"
  system.actorOf(Discover.props("b")) ! "hello, from c"

  Thread.sleep(1000)

  val svc = system.actorOf(Props[Service], "c")
  Register.service("c", svc)
}
