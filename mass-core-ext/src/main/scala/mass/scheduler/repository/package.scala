package mass.scheduler

import mass.slick.SlickProfile.api._

package object repository {

  def tJobDetail: TableQuery[JobDetailTable] = TableQuery[JobDetailTable]

  def tJobTrigger: TableQuery[JobTriggerTable] = TableQuery[JobTriggerTable]

  def tJobSchedule: TableQuery[JobScheduleTable] = TableQuery[JobScheduleTable]

  def tJobLog: TableQuery[JobLogTable] = TableQuery[JobLogTable]

}
