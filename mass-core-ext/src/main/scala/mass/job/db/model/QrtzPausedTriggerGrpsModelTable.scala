package mass.job.db.model

/** Entity class storing rows of table QrtzPausedTriggerGrpsModel
 *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
 *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true) */
case class QrtzPausedTriggerGrps(schedName: String, triggerGroup: String)

trait QrtzPausedTriggerGrpsModelTable {

  self: QrtzModels =>

  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** GetResult implicit for fetching QrtzPausedTriggerGrps objects using plain SQL queries */
  implicit def GetResultQrtzPausedTriggerGrps(implicit e0: GR[String]): GR[QrtzPausedTriggerGrps] = GR { prs =>
    import prs._
    QrtzPausedTriggerGrps.tupled((<<[String], <<[String]))
  }

  /** Table description of table qrtz_paused_trigger_grps. Objects of this class serve as prototypes for rows in queries. */
  class QrtzPausedTriggerGrpsModel(_tableTag: Tag)
      extends profile.api.Table[QrtzPausedTriggerGrps](_tableTag, "qrtz_paused_trigger_grps") {
    def * = (schedName, triggerGroup) <> (QrtzPausedTriggerGrps.tupled, QrtzPausedTriggerGrps.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      ((Rep.Some(schedName), Rep.Some(triggerGroup))).shaped.<>({ r =>
        import r._; _1.map(_ => QrtzPausedTriggerGrps.tupled((_1.get, _2.get)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Primary key of QrtzPausedTriggerGrpsModel (database name qrtz_paused_trigger_grps_pkey) */
    val pk = primaryKey("qrtz_paused_trigger_grps_pkey", (schedName, triggerGroup))
  }

  /** Collection-like TableQuery object for table QrtzPausedTriggerGrpsModel */
  lazy val QrtzPausedTriggerGrpsModel = new TableQuery(tag => new QrtzPausedTriggerGrpsModel(tag))
}
