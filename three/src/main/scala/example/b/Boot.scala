package example.b

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigRenderOptions}
import com.typesafe.scalalogging.LazyLogging
import example.api.{Discover, Service}
import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.DurationInt


object Boot extends App with LazyLogging {
  implicit val system = ActorSystem("example")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)

  system.settings.config.as[List[Config]]("example.services").foreach(c ⇒ println(c.root.render(ConfigRenderOptions.concise)))

  val svc = system.actorOf(Props[Service], "b")
  println(svc.path)

  Thread.sleep(1000)

  Discover.resolveDependencies.foreach(println)
}
