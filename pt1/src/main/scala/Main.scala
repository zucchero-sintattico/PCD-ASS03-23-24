import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.AskPattern.*
import akka.util.Timeout
import logic.SimulationHandlerActor
import utils.RoadSimStatistics
import view.{StatisticalView, ViewClickRelayActor, ViewListenerRelayActor}

object StartSystem:
  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val dt = 1
      val mainView = StatisticalView()
      val logView = RoadSimStatistics()
      val viewListenerRelayActor = context.spawn(ViewListenerRelayActor(List(mainView, logView)), "viewListenerRelayActor")
      val simulationHandlerActor = context.spawn(SimulationHandlerActor(viewListenerRelayActor), "simulationHandlerActor")
      val viewClickRelayActor = context.spawn(ViewClickRelayActor(mainView, simulationHandlerActor), "viewClickRelayActor")
      Behaviors.same
    }

@main def Main(): Unit =
  val actorSys = ActorSystem(StartSystem(), "root")
  println("ASS03")

//import scala.util.{Success, Failure}  
//import scala.concurrent.duration.DurationInt
//object TestAsk:
//  def apply(): Behavior[NotUsed] =
//    Behaviors.setup { context =>
//      val hello = context.spawn(Hello(), "hello")
//      val world = context.spawn(World(hello), "world")
//      world ! World.Start
////      {
////        case World.SayWorld => println("World!")
////      }
//      world ! World.Wait
//      world ! World.Wait
//      world ! World.Wait
//      Behaviors.same
//    }
//
//object Hello:
//  sealed trait Command
//  final case class SayHello(replyTo: ActorRef[World.SayWorld.type]) extends Command
//  def apply(): Behavior[Command] =
//    Behaviors.receiveMessage {
//      case SayHello(replyTo) =>
//        print("Hello, ")
//        replyTo ! World.SayWorld
//        Behaviors.same
//    }
//import scala.concurrent.Future.*
//import scala.concurrent.duration.DurationInt
//object World:
//  sealed trait Command
//  case object SayWorld extends Command
//  case object SayFail extends Command
//  case object Wait extends Command
//  case object Start extends Command
//  given Timeout = 3.seconds
//  def apply(hello: ActorRef[Hello.Command]): Behavior[Command] =
//    Behaviors.receive { (ctx, msg) =>
//      msg match
//        case SayWorld =>
//          println("World!")
//          Behaviors.same
//        case Wait =>
//          println("waiting")
//          Behaviors.same
//        case Start =>
//          println("Start")
//          ctx.ask(hello, Hello.SayHello.apply) {
//            case Success(_) => SayWorld
//            case Failure(_) => SayFail
//          }
//          Behaviors.same
//        case SayFail =>
//            println("Fail")
//            Behaviors.same
//    }