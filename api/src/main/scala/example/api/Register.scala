package example.api

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.Config
import example.api.Protocols.ServiceReg
import net.ceedubs.ficus.Ficus._


// register a service with consul
object Register {
  def service(name: String, ref: ActorRef)(implicit system: ActorSystem): ServiceReg = {
    val config = system.settings.config.as[Config]("akka.remote.netty.tcp")
    val addr = ref.path.address.copy(
      protocol = "akka.tcp",
      host = config.getAs[String]("hostname"),
      port = config.getAs[Int]("port")
    )

    println(s"register ${ref.path.toStringWithAddress(addr)} as $name")
    ServiceReg(name)
  }
}
