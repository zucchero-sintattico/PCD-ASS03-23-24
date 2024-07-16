package logic

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import logic.SimulationActor.{Command, RoadStepDone, Step}
import scala.concurrent.duration.DurationInt

object SimulationActor:
  sealed trait Command
  case object Start extends Command
  private case class Step(dt: Int) extends Command
  case object RoadStepDone extends Command
  def apply(dt: Int, numStep: Int, roadsBuildData: List[RoadBuildData]): Behavior[Command] =
    Behaviors.setup { context =>
      val roadActors = roadsBuildData.map(rbd => context.spawn(RoadActor(rbd.road, rbd.trafficLights, rbd.cars), rbd.road.agentID))
      context.self ! Start
      SimulationActor(roadActors).run(numStep)
    }

case class SimulationActor(roadActors: List[ActorRef[RoadActor.Command]]):
  private def run(step: Int): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessagePartial {
        case Step(dt) =>
          if step == 0 then endSimulation
          context.spawnAnonymous(
            Aggregator[RoadStepDone.type, Step](
              sendRequests = replyTo => roadActors.foreach(_ ! RoadActor.Step(dt, replyTo)),
              expectedReplies = roadActors.size,
              replyTo = context.self,
              aggregateReplies = replies => Step(dt),
              timeout = 5.seconds
            )
          )
          run(step-1)
      }
    }

  private def endSimulation: Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.stopped
    }



