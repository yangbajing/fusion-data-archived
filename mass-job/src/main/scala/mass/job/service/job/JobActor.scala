package mass.job.service.job

import mass.message.job.JobMessage

//import akka.actor.{ Actor, Props }
//import akka.pattern._
//import mass.job.JobSystem
//import mass.message.job._
//
//import scala.concurrent.Future
//
object JobActor {
  val job = 'job

  @inline def message(msg: JobMessage): (Symbol, JobMessage) = job -> msg

//  def props: (Props, Symbol) = Props(new JobActor) -> job
}

//class JobActor extends Actor with JobService {
//  import context.dispatcher
//
//  override val jobSystem: JobSystem = JobSystem(context.system)
//
//  override def receive: Receive = {
//    case message: JobMessage => receiveMessage(message).pipeTo(sender())
//    case event: JobEvent     => receiveEvent(event)
//  }
//
//  def receiveMessage(v: JobMessage): Future[JobResponse] = v match {
//    case req: JobPageReq         => handlePage(req)
//    case req: JobFindReq         => handleFind(req)
//    case req: JobUploadJobReq    => handleUploadJob(req)
//    case req: JobListReq         => handleList(req)
//    case req: JobGetAllOptionReq => handleGetAllOption(req)
//    case req: JobCreateReq       => handleCreateJob(req)
//    case req: JobUpdateReq       => handleUpdate(req)
//    case req: JobUploadFilesReq  => handleUploadFiles(req)
//  }
//
//  def receiveEvent(v: JobEvent): Unit = v match {
//    case event: JobExecutionEvent => executionJob(event)
//  }
//}
