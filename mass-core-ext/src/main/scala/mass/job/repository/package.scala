package mass.job

import mass.db.slick.SlickProfile.api._

package object repository {
  def tJobSchedule: TableQuery[JobScheduleTable] = TableQuery[JobScheduleTable]

  def tJobLog: TableQuery[TriggerLogTable] = TableQuery[TriggerLogTable]
}
