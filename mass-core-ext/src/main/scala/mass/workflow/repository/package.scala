package mass.workflow

import mass.db.slick.SlickProfile.api._

package object repository {
  def tWFDetail: TableQuery[WfDetailTable] = TableQuery[WfDetailTable]
}
