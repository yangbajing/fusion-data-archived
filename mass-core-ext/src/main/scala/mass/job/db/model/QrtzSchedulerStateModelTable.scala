package mass.job.db.model
// AUTO-GENERATED Slick data model for table QrtzSchedulerStateModel
trait QrtzSchedulerStateModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** Entity class storing rows of table QrtzSchedulerStateModel
   *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
   *  @param instanceName Database column instance_name SqlType(varchar), Length(200,true)
   *  @param lastCheckinTime Database column last_checkin_time SqlType(int8)
   *  @param checkinInterval Database column checkin_interval SqlType(int8) */
  case class QrtzSchedulerState(schedName: String, instanceName: String, lastCheckinTime: Long, checkinInterval: Long)

  /** GetResult implicit for fetching QrtzSchedulerState objects using plain SQL queries */
  implicit def GetResultQrtzSchedulerState(implicit e0: GR[String], e1: GR[Long]): GR[QrtzSchedulerState] = GR { prs =>
    import prs._
    QrtzSchedulerState.tupled((<<[String], <<[String], <<[Long], <<[Long]))
  }

  /** Table description of table qrtz_scheduler_state. Objects of this class serve as prototypes for rows in queries. */
  class QrtzSchedulerStateModel(_tableTag: Tag)
      extends profile.api.Table[QrtzSchedulerState](_tableTag, "qrtz_scheduler_state") {
    def * =
      (schedName, instanceName, lastCheckinTime, checkinInterval) <> (QrtzSchedulerState.tupled, QrtzSchedulerState.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      ((Rep.Some(schedName), Rep.Some(instanceName), Rep.Some(lastCheckinTime), Rep.Some(checkinInterval))).shaped.<>({
        r =>
          import r._; _1.map(_ => QrtzSchedulerState.tupled((_1.get, _2.get, _3.get, _4.get)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column instance_name SqlType(varchar), Length(200,true) */
    val instanceName: Rep[String] = column[String]("instance_name", O.Length(200, varying = true))

    /** Database column last_checkin_time SqlType(int8) */
    val lastCheckinTime: Rep[Long] = column[Long]("last_checkin_time")

    /** Database column checkin_interval SqlType(int8) */
    val checkinInterval: Rep[Long] = column[Long]("checkin_interval")

    /** Primary key of QrtzSchedulerStateModel (database name qrtz_scheduler_state_pkey) */
    val pk = primaryKey("qrtz_scheduler_state_pkey", (schedName, instanceName))
  }

  /** Collection-like TableQuery object for table QrtzSchedulerStateModel */
  lazy val QrtzSchedulerStateModel = new TableQuery(tag => new QrtzSchedulerStateModel(tag))
}
