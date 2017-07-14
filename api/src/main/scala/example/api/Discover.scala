package example.api

import akka.NotUsed
import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorRefFactory, Identify, PoisonPill, Props, Stash}
import akka.pattern.{Backoff, BackoffSupervisor, ask}
import akka.util.Timeout
import com.typesafe.config.Config
import example.api.Protocols.ServiceDef

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object Discover {
  def dependency(name: String)(implicit ctx: ActorRefFactory, to: Timeout): ActorRef = {
    ctx.actorOf(props(name), s"service-discovery-$name")
  }

  def allDependencies(cfg: Config)(implicit ctx: ActorRefFactory, to: Timeout): Map[String, ActorRef] = {
    ServiceDef.list(cfg)
    .map(_.name)
    .map(name ⇒
      name → dependency(name)
    ).toMap
  }

  def props(name: String)(implicit to: Timeout): Props = Props(new Discover(name))
}

class Discover(name: String)(implicit to: Timeout) extends Actor with Stash with ActorLogging {
  ServiceDef.named(name, context.system.settings.config) match {
    case Some(svc) ⇒ self ! svc
    case None ⇒ throw new RuntimeException("unknown service")
  }

  def ready: Receive = {
    case d: ServiceDef ⇒ context.become(resolving(d))
    case _ ⇒ stash()
  }

  def resolving(d: ServiceDef): Receive = {
    context.actorOf(resolveWithBackoff(d), "backoff")

    {
      case Resolver.Resolved(ref: ActorRef) ⇒
        unstashAll()
        context.become(resolved(ref))

      case _ ⇒ stash()
    }
  }

  def resolved(ref: ActorRef): Receive = {
    case m ⇒ ref forward m
  }

  def receive = ready

  def resolveWithBackoff(d: ServiceDef): Props = BackoffSupervisor.props(
    Backoff.onStop(
      Resolver.props(d),
      childName = "resolver",
      minBackoff = 1 second,
      maxBackoff = 10 seconds,
      randomFactor = 0
    )
  )
}

object Resolver {
  def props(d: ServiceDef)(implicit to: Timeout, ref: ActorRef) = Props(new Resolver(d))

  case object Resolve
  case class Resolved(ref: ActorRef)
}

class Resolver(d: ServiceDef)(implicit to: Timeout, ref: ActorRef) extends Actor with ActorLogging {
  import context.dispatcher

  override def preStart() = context.self.tell(Resolver.Resolve, ref)

  def receive = {
    case Resolver.Resolve ⇒
      log.debug("resolving {}", d)
      resolve(d).onComplete(self ! _)

    case Success(Some(r: ActorRef)) ⇒
      log.debug("successfully resolved {}", d)
      sender ! Resolver.Resolved(r)
      context.parent ! PoisonPill
      context.stop(self)

    case Success(None) ⇒
      log.warning("failed to resolve {}", d)
      context.stop(self)

    case Failure(ex) ⇒
      log.error(ex, "failure resolving {}", d)
      throw ex
  }

  def resolve(svc: ServiceDef): Future[Option[ActorRef]] = {
    val fqsn = s"akka.tcp://${svc.system}@${svc.host}:${svc.port}/user/${svc.name}"
    log.info("resolving service at {}", fqsn)
    (context.actorSelection(fqsn) ? Identify(NotUsed)).mapTo[ActorIdentity].map(_.ref)
  }
}
