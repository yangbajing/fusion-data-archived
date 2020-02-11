package example.akkastream.basic

import akka.actor.ActorSystem
import akka.stream.FanInShape.{ Init, Name }
import akka.stream._
import akka.stream.scaladsl.{ Balance, Flow, GraphDSL, Merge, MergePreferred, RunnableGraph, Sink, Source }

import scala.collection.immutable
import scala.io.StdIn

case class PriorityWorkerPoolShape[In, Out](jobsIn: Inlet[In], priorityJobsIn: Inlet[In], resultsOut: Outlet[Out])
    extends Shape {
  override def inlets: immutable.Seq[Inlet[_]] = jobsIn :: priorityJobsIn :: Nil

  override def outlets: immutable.Seq[Outlet[_]] = resultsOut :: Nil

  override def deepCopy(): Shape =
    PriorityWorkerPoolShape(jobsIn.carbonCopy(), priorityJobsIn.carbonCopy(), resultsOut.carbonCopy())
}

case class PriorityWorkerPoolShape2[In, Out](_init: Init[Out] = Name("PriorityWorkerPoolShape2"))
    extends FanInShape[Out](_init) {
  override protected def construct(init: Init[Out]): FanInShape[Out] =
    PriorityWorkerPoolShape2(init)

  val jobsIn: Inlet[In] = newInlet[In]("jobsIn")
  val priorityJobsIn: Inlet[In] = newInlet[In]("priorityJobsIn")
  // Outlet[Out] 使用名字 "out" 将被自动创建
}

object PriorityWorkerPool {
  def apply[In, Out](worker: Flow[In, Out, Any], workerCount: Int) =
    GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val priorityMerge = b.add(MergePreferred[In](1))
      val balance = b.add(Balance[In](workerCount))
      val resultsMerge = b.add(Merge[Out](workerCount))

      for (i <- 0 until workerCount)
        balance.out(i) ~> worker ~> resultsMerge.in(i)

      // 在合并优先和普通作业后发送到平衡器
      priorityMerge ~> balance

      PriorityWorkerPoolShape(priorityMerge.in(0), priorityMerge.preferred, resultsMerge.out)
    }
}

object GraphComponent extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val worker1 = Flow[String].map("step 1 " + _)
  val worker2 = Flow[String].map("step 2 " + _)

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val priorityPool1 = b.add(PriorityWorkerPool(worker1, 4))
    val priorityPool2 = b.add(PriorityWorkerPool(worker2, 2))

    Source(1 to 10).map("job: " + _) ~> priorityPool1.jobsIn
    Source(1 to 10).map("priority job: " + _) ~> priorityPool1.priorityJobsIn

    priorityPool1.resultsOut ~> priorityPool2.jobsIn
    Source(1 to 10).map("one-step, priority " + _) ~> priorityPool2.priorityJobsIn

    priorityPool2.resultsOut ~> Sink.foreach(println)
    ClosedShape
  })

  g.run()

  StdIn.readLine()
  system.terminate()
}
