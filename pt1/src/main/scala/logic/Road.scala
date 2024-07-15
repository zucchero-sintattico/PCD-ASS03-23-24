package logic

import akka.actor.typed.ActorRef
import logic.Car.CarPerception

object Road:
  sealed trait Command
  final case class getCurrentPerception(replyTo: ActorRef[Car.Command]) extends Command
  final case class sendCurrentPerception(currentPerception: CarPerception) extends Command
