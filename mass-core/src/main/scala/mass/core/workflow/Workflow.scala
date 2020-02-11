package mass.core.workflow

import akka.Done

import scala.concurrent.Future

/**
 * 工作流
 */
trait Workflow[Result] {
  /**
   * 运行工作流
   */
  def run(): WorkflowExecution[Result]
}

/**
 * 工作流执行对象
 */
trait WorkflowExecution[Result] extends AutoCloseable {
  def future: Future[Result]

  /**
   * 终止工作流
   * @return
   */
  def terminate(): Future[Done]
}
