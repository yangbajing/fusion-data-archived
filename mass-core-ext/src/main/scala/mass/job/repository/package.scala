package mass.job

import mass.slick.SlickProfile.api._

package object repository {
  def tJobSchedule: TableQuery[JobScheduleTable] = TableQuery[JobScheduleTable]

  def tJobLog: TableQuery[JobLogTable] = TableQuery[JobLogTable]
}
