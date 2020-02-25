package mass.workflow.repository

import java.time.OffsetDateTime

import mass.db.slick.PgProfile.api._
import mass.workflow.model.WfDetail

class WfDetailTable(tag: Tag) extends Table[WfDetail](tag, "wf_detail") {
  def name = column[String]("name", O.PrimaryKey)
  def content = column[String]("content", O.SqlType("content"))
  def createdAt = column[OffsetDateTime]("created_at")

  def * = (name, content, createdAt).mapTo[WfDetail]
}
