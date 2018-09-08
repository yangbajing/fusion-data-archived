package mass.workflow.etl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.rdp.RdpSystem
import mass.rdp.etl.EtlWorkflow
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class EtlWorkflowTest extends TestKit(ActorSystem("etl-test")) with HelloscalaSpec with BeforeAndAfterAll {
  var rdpSystem: RdpSystem = _
  var etlWorkflow: EtlWorkflow = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    rdpSystem = RdpSystem(system)
    etlWorkflow = EtlWorkflow.fromXML(TestStub.graphXmlConfig, rdpSystem).get
  }

  override protected def afterAll(): Unit = {
    etlWorkflow.close()
    super.afterAll()
  }

  "EtlWorkflow" should {

    "show" in {
      etlWorkflow.connectors must not be empty
      etlWorkflow.connectors.foreach(c => println(s"connector: $c"))
      println(etlWorkflow.graph)

      etlWorkflow.connectors.foreach(println)
      println(etlWorkflow.graph.name)
      println(etlWorkflow.graph.graphSource)
      etlWorkflow.graph.graphFlows.foreach(println)
      println(etlWorkflow.graph.graphSink)
    }

    "run" in {
      val execution = etlWorkflow.run()
      val result = Await.result(execution.future, Duration.Inf)
      println(result)
    }

  }

}
