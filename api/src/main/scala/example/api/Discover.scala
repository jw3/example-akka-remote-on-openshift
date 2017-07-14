package example.api

import akka.NotUsed
import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorRefFactory, Identify, PoisonPill, Props, Stash}
import akka.pattern.{AskTimeoutException, Backoff, BackoffSupervisor, ask}
import akka.util.Timeout
import com.typesafe.config.Config

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

class Resolver(svc: ServiceDef)(implicit to: Timeout, ref: ActorRef) extends Actor with ActorLogging {
  import context.dispatcher

  val sysname = s"akka.tcp://${svc.system}@${svc.host}:${svc.port}"
  val fqsname = s"$sysname/user/${svc.name}"

  def receive = {
    case Resolver.Resolve ⇒
      log.info("resolving service at {}", fqsname)
      (context.actorSelection(fqsname) ? Identify(NotUsed)).onComplete(self ! _)

    case Success(id: ActorIdentity) ⇒ id.ref match {
      case Some(idref) ⇒
        log.debug("successfully resolved {}", svc)
        sender ! Resolver.Resolved(idref)
        context.parent ! PoisonPill
        context.stop(self)

      case None ⇒
        log.warning("failed to resolve {}", svc)
        context.stop(self)
    }

    case Failure(_: AskTimeoutException) ⇒
      log.warning(s"resolving query timed out. is there an actor system running at {}", sysname)
      context.stop(self)

    case Failure(ex) ⇒
      log.warning("failure {} resolving {}", ex.getMessage, svc)
      context.stop(self)
  }

  override def preStart() = context.self.tell(Resolver.Resolve, ref)
}
