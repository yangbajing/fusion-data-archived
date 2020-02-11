package mass.workflow.repository

import mass.slick.SlickProfile.api._
import mass.workflow.model.WfDetail
import slick.sql.{ FixedSqlAction, FixedSqlStreamingAction }

object WorkflowRepo extends WorkflowRepo

trait WorkflowRepo {
  def insertWfDetail(wfDetail: WfDetail): FixedSqlAction[WfDetail, NoStream, Effect.Write] =
    tWFDetail returning tWFDetail += wfDetail

  def listWfDetail(): FixedSqlStreamingAction[Seq[WfDetail], WfDetail, Effect.Read] =
    tWFDetail.result
}
