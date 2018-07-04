package mass.server.repository

import mass.slick.PgProfile
import mass.workflow.model.WfDetail
import PgProfile.api._

import scala.concurrent.Future

object WorkflowRepository {

  def apply(db: PgProfile.backend.DatabaseDef): WorkflowRepository = new WorkflowRepository(db)

}

class WorkflowRepository(db: PgProfile.backend.DatabaseDef) {

  import mass.workflow.model.WorkflowSchemas._

  def insertWfDetail(wfDetail: WfDetail): Future[WfDetail] =
    db.run(WfDetailRow returning WfDetailRow += wfDetail)

  def listWfDetail(): Future[Seq[WfDetail]] =
    db.run(WfDetailRow.result)

}
