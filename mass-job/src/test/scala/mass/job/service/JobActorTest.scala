package mass.job.service
import akka.actor.{Actor, ActorSystem}
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec

class JobActorTest extends TestKit(ActorSystem("test")) with HelloscalaSpec {

  class Parent extends Actor {
    override def receive: Receive = {
      case "hello" => //testActor.tell()
    }
  }
}
