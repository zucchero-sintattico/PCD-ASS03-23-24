package logic

import scala.collection.immutable
import scala.reflect.ClassTag
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Aggregator:
  sealed trait Command
  private case class WrappedReply[Reply](reply: Reply) extends Command

  def apply[Reply: ClassTag, Aggregate](sendRequests: ActorRef[Reply] => Unit,
                                         expectedReplies: Int,
                                         replyTo: ActorRef[Aggregate],
                                         aggregateReplies: List[Reply] => Aggregate): Behavior[Command] =
    Behaviors.setup { context =>
      val replyAdapter = context.messageAdapter[Reply](WrappedReply(_))

      def collecting(replies: List[Reply]): Behavior[Command] =
        Behaviors.receiveMessage {
          case WrappedReply(reply) =>
            val newReplies = replies :+ reply.asInstanceOf[Reply]
            newReplies.size match
              case `expectedReplies` => replyToSender(newReplies)
              case _ => collecting(newReplies)
        }

      def replyToSender(replies: List[Reply] = List()): Behavior[Command] =
        val aggregateMessage = aggregateReplies(replies)
        replyTo ! aggregateMessage
        Behaviors.stopped

      expectedReplies match
        case 0 => replyToSender()
        case _ => sendRequests(replyAdapter); collecting(List())
    }