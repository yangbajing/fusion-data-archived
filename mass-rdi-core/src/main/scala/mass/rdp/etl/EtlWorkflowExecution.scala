package mass.rdp.etl

import akka.Done
import mass.connector.sql.JdbcSinkResult
import mass.core.workflow.WorkflowExecution

import scala.concurrent.{Future, Promise}

trait EtlResult

case class SqlEtlResult(data: JdbcSinkResult) extends EtlResult

class EtlWorkflowExecution(promise: Promise[EtlResult], funcClose: () => Unit)
    extends WorkflowExecution[EtlResult] {

  override def future: Future[EtlResult] = promise.future

  override def terminate(): Future[Done] = ???

  override def close(): Unit = funcClose()

}
