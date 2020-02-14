package akka.mass

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystemImpl
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.adapter._

import scala.concurrent.Await
import scala.concurrent.duration._

object AkkaUtils {
  /**
   * Shut down an actor system and wait for termination.
   * On failure debug output will be logged about the remaining actors in the system.
   *
   * If verifySystemShutdown is true, then an exception will be thrown on failure.
   */
  def shutdownActorSystem(
      actorSystem: ActorSystem[_],
      duration: Duration = 10.seconds,
      verifySystemShutdown: Boolean = false): Unit = {
    actorSystem.terminate()
    try Await.ready(actorSystem.whenTerminated, duration)
    catch {
      case _: TimeoutException =>
        val msg = "Failed to stop [%s] within [%s] \n%s".format(
          actorSystem.name,
          duration,
          actorSystem.toClassic.asInstanceOf[ActorSystemImpl].printTree)
        if (verifySystemShutdown) throw new RuntimeException(msg)
        else println(msg)
    }
  }
}
