package example.c

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import example.api.{Discover, Service}

import scala.concurrent.duration.DurationInt


object Boot extends App with LazyLogging {
  implicit val system = ActorSystem("example")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)

  Discover.dependency("a") ! "hello, from c"
  Discover.dependency("b") ! "hello, from c"

  Thread.sleep(1000)

  val svc = system.actorOf(Props[Service], "c")
}
