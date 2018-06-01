package example.akkastream.basic

import java.nio.ByteOrder

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{BidiFlow, Flow, GraphDSL, Sink, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object BidiShapeDemo extends App {
  trait Message
  case class Ping(id: Int) extends Message
  case class Pong(id: Int) extends Message

  def toBytes(msg: Message): ByteString = {
    implicit val order = ByteOrder.LITTLE_ENDIAN
    msg match {
      case Ping(id) ⇒ ByteString.newBuilder.putByte(1).putInt(id).result() // byte 1 is Ping
      case Pong(id) ⇒ ByteString.newBuilder.putByte(2).putInt(id).result() // byte 2 is Pong
    }
  }

  def fromBytes(bytes: ByteString): Message = {
    implicit val order = ByteOrder.LITTLE_ENDIAN
    val it = bytes.iterator
    it.getByte match {
      case 1     ⇒ Ping(it.getInt)
      case 2     ⇒ Pong(it.getInt)
      case other ⇒ throw new RuntimeException(s"parse error: expected 1|2 got $other")
    }
  }

  val codecVerbose = BidiFlow.fromGraph(GraphDSL.create() { b ⇒
    // construct and add the top flow, going outbound 构造并添加顶部流，出站
    val outbound = b.add(Flow[Message].map(toBytes))
    // construct and add the bottom flow, going inbound 构造并添加底部流，入站
    val inbound = b.add(Flow[ByteString].map(fromBytes))
    // fuse them together into a BidiShape 把它们融合成一个 BiDiShape
    BidiShape.fromFlows(outbound, inbound)
  })

  // 和上面一样，简写版
  val codec = BidiFlow.fromFunctions(toBytes _, fromBytes _)

  val framing = BidiFlow.fromGraph(GraphDSL.create() { b ⇒
    implicit val order = ByteOrder.LITTLE_ENDIAN

    def addLengthHeader(bytes: ByteString) = ByteString.newBuilder.putInt(bytes.length).append(bytes).result()

    class FrameParser extends GraphStage[FlowShape[ByteString, ByteString]] {

      val in: Inlet[ByteString] = Inlet[ByteString]("FrameParser.in")
      val out: Outlet[ByteString] = Outlet[ByteString]("FrameParser.out")
      override val shape: FlowShape[ByteString, ByteString] = FlowShape.of(in, out)

      override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

        // this holds the received but not yet parsed bytes 保持已接收但尚未解析的字节
        var stash = ByteString.empty
        // this holds the current message length or -1 if at a boundary 保持当前消息的长度，若消息越界则为-1
        var needed = -1

        setHandler(out, new OutHandler {
          override def onPull(): Unit = {
            if (isClosed(in)) {
              println("onPull run()")
              run()
            } else {
              println("onPull pull(in)")
              pull(in)
            }
          }
        })
        setHandler(in, new InHandler {
          override def onPush(): Unit = {
            println("onPush")
            val bytes = grab(in)
            stash = stash ++ bytes
            run()
          }

          override def onUpstreamFinish(): Unit = {
            // either we are done
            if (stash.isEmpty) {
              println("onUpstreamFinish completeStage()")
              completeStage()
            } // or we still have bytes to emit
            // wait with completion and let run() complete when the
            // rest of the stash has been sent downstream
            else if (isAvailable(out)) {
              println("onUpstreamFinish run()")
              run()
            }
          }
        })

        private def run(): Unit = {
          if (needed == -1) {
            // are we at a boundary? then figure out next length
            if (stash.length < 4) {
              if (isClosed(in)) completeStage()
              else pull(in)
            } else {
              needed = stash.iterator.getInt
              stash = stash.drop(4)
              run() // cycle back to possibly already emit the next chunk
            }
          } else if (stash.length < needed) {
            // we are in the middle of a message, need more bytes,
            // or have to stop if input closed
            if (isClosed(in)) completeStage()
            else pull(in)
          } else {
            // we have enough to emit at least one message, so do it
            val emit = stash.take(needed)
            stash = stash.drop(needed)
            needed = -1
            push(out, emit)
          }
        }
      }
    }

    val outbound = b.add(Flow[ByteString].map(addLengthHeader))
    val inbound = b.add(Flow[ByteString].via(new FrameParser))
    BidiShape.fromFlows(outbound, inbound)
  })

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val stack = codec.atop(framing)

  // test it by plugging it into its own inverse and closing the right end
  val pingpong = Flow[Message].collect { case Ping(id) ⇒ Pong(id) }
  val flow = stack.atop(stack.reversed).join(pingpong)
  val f = Source((0 to 9).map(Ping)).via(flow).limit(20).runWith(Sink.seq)
  val result = Await.result(f, 1.second) //should ===((0 to 9).map(Pong))
  println(result)

  StdIn.readLine()
  system.terminate()
}
