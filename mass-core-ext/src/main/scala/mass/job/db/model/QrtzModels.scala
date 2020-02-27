package mass.job.db.model

import javax.inject.{ Inject, Singleton }
import mass.db.slick.PgProfile

@Singleton
class QrtzModels @Inject() (val profile: PgProfile)
    extends QrtzSchedulerStateModelTable
    with QrtzSimpleTriggersModelTable
    with QrtzTriggerLogModelTable
    with QrtzLocksModelTable
    with QrtzFiredTriggersModelTable
    with QrtzCronTriggersModelTable
    with QrtzCalendarsModelTable
    with QrtzPausedTriggerGrpsModelTable
    with QrtzBlobTriggersModelTable
    with QrtzJobDetailsModelTable
    with QrtzSimpropTriggersModelTable
    with QrtzTriggersModelTable {
  import profile.api._

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(
    QrtzBlobTriggersModel.schema,
    QrtzCalendarsModel.schema,
    QrtzCronTriggersModel.schema,
    QrtzFiredTriggersModel.schema,
    QrtzJobDetailsModel.schema,
    QrtzLocksModel.schema,
    QrtzPausedTriggerGrpsModel.schema,
    QrtzSchedulerStateModel.schema,
    QrtzSimpleTriggersModel.schema,
    QrtzSimpropTriggersModel.schema,
    QrtzTriggerLogModel.schema,
    QrtzTriggersModel.schema).reduceLeft(_ ++ _)
}
