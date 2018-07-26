package mass.server.repository

import mass.slick.SlickProfile
import mass.workflow.model.WfDetail
import SlickProfile.api._

import scala.concurrent.Future

object WorkflowRepository {

  def apply(db: SlickProfile.backend.DatabaseDef): WorkflowRepository =
    new WorkflowRepository(db)

}

class WorkflowRepository(db: SlickProfile.backend.DatabaseDef) {

  import mass.workflow.model.WorkflowSchemas._

  def insertWfDetail(wfDetail: WfDetail): Future[WfDetail] =
    db.run(WfDetailRow returning WfDetailRow += wfDetail)

  def listWfDetail(): Future[Seq[WfDetail]] =
    db.run(WfDetailRow.result)

}
