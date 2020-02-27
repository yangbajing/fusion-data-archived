package mass.job.db.model

/** Entity class storing rows of table QrtzSimpleTriggersModel
 *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
 *  @param triggerName Database column trigger_name SqlType(varchar), Length(200,true)
 *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true)
 *  @param repeatCount Database column repeat_count SqlType(int8)
 *  @param repeatInterval Database column repeat_interval SqlType(int8)
 *  @param timesTriggered Database column times_triggered SqlType(int8) */
case class QrtzSimpleTriggers(
    schedName: String,
    triggerName: String,
    triggerGroup: String,
    repeatCount: Long,
    repeatInterval: Long,
    timesTriggered: Long)

trait QrtzSimpleTriggersModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** GetResult implicit for fetching QrtzSimpleTriggers objects using plain SQL queries */
  implicit def GetResultQrtzSimpleTriggers(implicit e0: GR[String], e1: GR[Long]): GR[QrtzSimpleTriggers] = GR { prs =>
    import prs._
    QrtzSimpleTriggers.tupled((<<[String], <<[String], <<[String], <<[Long], <<[Long], <<[Long]))
  }

  /** Table description of table qrtz_simple_triggers. Objects of this class serve as prototypes for rows in queries. */
  class QrtzSimpleTriggersModel(_tableTag: Tag)
      extends profile.api.Table[QrtzSimpleTriggers](_tableTag, "qrtz_simple_triggers") {
    def * =
      (schedName, triggerName, triggerGroup, repeatCount, repeatInterval, timesTriggered) <> (QrtzSimpleTriggers.tupled, QrtzSimpleTriggers.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (
        (
          Rep.Some(schedName),
          Rep.Some(triggerName),
          Rep.Some(triggerGroup),
          Rep.Some(repeatCount),
          Rep.Some(repeatInterval),
          Rep.Some(timesTriggered))).shaped.<>({ r =>
        import r._; _1.map(_ => QrtzSimpleTriggers.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column trigger_name SqlType(varchar), Length(200,true) */
    val triggerName: Rep[String] = column[String]("trigger_name", O.Length(200, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Database column repeat_count SqlType(int8) */
    val repeatCount: Rep[Long] = column[Long]("repeat_count")

    /** Database column repeat_interval SqlType(int8) */
    val repeatInterval: Rep[Long] = column[Long]("repeat_interval")

    /** Database column times_triggered SqlType(int8) */
    val timesTriggered: Rep[Long] = column[Long]("times_triggered")

    /** Primary key of QrtzSimpleTriggersModel (database name qrtz_simple_triggers_pkey) */
    val pk = primaryKey("qrtz_simple_triggers_pkey", (schedName, triggerName, triggerGroup))

    /** Foreign key referencing QrtzTriggersModel (database name qrtz_simple_triggers_sched_name_fkey) */
    lazy val qrtzTriggersModelFk =
      foreignKey("qrtz_simple_triggers_sched_name_fkey", (schedName, triggerName, triggerGroup), QrtzTriggersModel)(
        r => (r.schedName, r.triggerName, r.triggerGroup),
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
  }

  /** Collection-like TableQuery object for table QrtzSimpleTriggersModel */
  lazy val QrtzSimpleTriggersModel = new TableQuery(tag => new QrtzSimpleTriggersModel(tag))
}
