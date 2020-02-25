package mass.workflow

import mass.db.slick.PgProfile.api._

package object repository {
  def tWFDetail: TableQuery[WfDetailTable] = TableQuery[WfDetailTable]
}
