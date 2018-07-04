package mass.workflow.model

import java.time.OffsetDateTime

import mass.slick.PgProfile.api._

object WorkflowSchemas extends WorkflowSchemas

abstract class WorkflowSchemas {

  class WfDetailRow(tag: Tag) extends Table[WfDetail](tag, "wf_detail") {
    def name = column[String]("name", O.PrimaryKey)

    def content = column[String]("content", O.SqlType("content"))

    def createdAt = column[OffsetDateTime]("created_at")

    def * = (name, content, createdAt).mapTo[WfDetail]
  }

  val WfDetailRow = TableQuery[WfDetailRow]

}
