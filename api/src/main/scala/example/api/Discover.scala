package example.api

import akka.NotUsed
import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorRefFactory, ActorSystem, Identify, Props, Stash}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigRenderOptions, ConfigValueFactory}
import example.api.Protocols.ServiceDef
import net.ceedubs.ficus.Ficus._
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}


object Discover {
  def resolve(svc: ServiceDef)(implicit arp: ActorRefFactory, to: Timeout, ec: ExecutionContext): Future[Option[ActorRef]] = {
    val fqsn = s"akka.tcp://${svc.system}@${svc.host}:${svc.port}/user/${svc.name}"
    println(s"discover service at $fqsn")
    (arp.actorSelection(fqsn) ? Identify(NotUsed)).mapTo[ActorIdentity].map(_.ref)
  }

  def serviceDefs(config: Config): List[ServiceDef] = {
    config.getAs[List[Config]]("example.services")
    .getOrElse(List.empty)
    .map(c ⇒ c.withValue("port", ConfigValueFactory.fromAnyRef(c.getInt("port"))))
    .map(
      _.root.render(ConfigRenderOptions.concise)
      .parseJson
      .convertTo[ServiceDef]
    )
  }

  def resolveDependencies(implicit system: ActorSystem, to: Timeout): Map[String, ActorRef] = {
    import system.dispatcher

    serviceDefs(system.settings.config)
    .map(svc ⇒ svc.name → resolve(svc))
    .toMap
    .map(e ⇒ e._1 → Await.result(e._2, to.duration))
    .map {
      case (n, Some(r)) ⇒
        println(s"resolved $n")
        n → Some(r)
      case (n, None) ⇒
        println(s"failed to resolve $n")
        n → None
    }.filter(_._2.isDefined).map(e ⇒ e._1 → e._2.get)
  }

  def props(name: String)(implicit to: Timeout): Props = Props(new Discover(name))
}

class Discover(name: String)(implicit to: Timeout) extends Actor with Stash with ActorLogging {
  import context.dispatcher

  Discover.serviceDefs(context.system.settings.config).find(_.name == name) match {
    case Some(d) ⇒ self ! d
    case None ⇒ throw new RuntimeException("unknown service")
  }

  def ready: Receive = {
    case d: ServiceDef ⇒ context.become(resolving(d))
    case _ ⇒ stash()
  }

  def resolving(d: ServiceDef): Receive = {
    Discover.resolve(d).onComplete(self ! _)

    {
      case Success(Some(r: ActorRef)) ⇒
        unstashAll()
        context.become(resolved(r))
      case Success(None) ⇒ // backoff
      case Failure(ex) ⇒ log.error(ex, "failed to resolve service {}", d)
      case _ ⇒ stash()
    }
  }

  def resolved(ref: ActorRef): Receive = {
    case m ⇒ ref forward m
  }

  def receive = ready
}
