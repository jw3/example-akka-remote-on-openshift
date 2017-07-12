package example.api

import akka.NotUsed
import akka.actor.{ActorIdentity, ActorRef, ActorRefFactory, ActorSystem, Identify}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigRenderOptions, ConfigValueFactory}
import example.api.Protocols.ServiceDef
import net.ceedubs.ficus.Ficus._
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}


object Discover {
  def service(svc: ServiceDef)(implicit arp: ActorRefFactory, to: Timeout, ec: ExecutionContext): Future[Option[ActorRef]] = {
    val fqsn = s"akka.tcp://${svc.system}@${svc.host}:${svc.port}/user/${svc.name}"
    println(s"discover service at $fqsn")
    (arp.actorSelection(fqsn) ? Identify(NotUsed)).mapTo[ActorIdentity].map(_.ref)
  }

  def dependencies(implicit system: ActorSystem, to: Timeout): Map[String, Future[Option[ActorRef]]] = {
    import system.dispatcher

    val dependencies = system.settings.config.getAs[List[Config]]("example.services")
                       .getOrElse(List.empty)
                       .map(c ⇒ c.withValue("port", ConfigValueFactory.fromAnyRef(c.getInt("port"))))
                       .map(
                         _.root.render(ConfigRenderOptions.concise)
                         .parseJson
                         .convertTo[ServiceDef]
                       )

    dependencies.map(svc ⇒ svc.name → service(svc)).toMap
  }

  def resolveDependencies(implicit system: ActorSystem, to: Timeout): Map[String, ActorRef] = {
    dependencies.map(e ⇒ e._1 → Await.result(e._2, to.duration)).map {
      case (n, Some(r)) ⇒
        println(s"resolved $n")
        n → Some(r)
      case (n, None) ⇒
        println(s"failed to resolve $n")
        n → None
    }.filter(_._2.isDefined).map(e ⇒ e._1 → e._2.get)
  }
}
