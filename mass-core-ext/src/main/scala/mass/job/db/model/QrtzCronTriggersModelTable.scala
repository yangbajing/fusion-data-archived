package mass.job.db.model

/** Entity class storing rows of table QrtzCronTriggersModel
 *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
 *  @param triggerName Database column trigger_name SqlType(varchar), Length(200,true)
 *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true)
 *  @param cronExpression Database column cron_expression SqlType(varchar), Length(120,true)
 *  @param timeZoneId Database column time_zone_id SqlType(varchar), Length(80,true), Default(None) */
case class QrtzCronTriggers(
    schedName: String,
    triggerName: String,
    triggerGroup: String,
    cronExpression: String,
    timeZoneId: Option[String] = None)

trait QrtzCronTriggersModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** GetResult implicit for fetching QrtzCronTriggers objects using plain SQL queries */
  implicit def GetResultQrtzCronTriggers(implicit e0: GR[String], e1: GR[Option[String]]): GR[QrtzCronTriggers] = GR {
    prs =>
      import prs._
      QrtzCronTriggers.tupled((<<[String], <<[String], <<[String], <<[String], <<?[String]))
  }

  /** Table description of table qrtz_cron_triggers. Objects of this class serve as prototypes for rows in queries. */
  class QrtzCronTriggersModel(_tableTag: Tag)
      extends profile.api.Table[QrtzCronTriggers](_tableTag, "qrtz_cron_triggers") {
    def * =
      (schedName, triggerName, triggerGroup, cronExpression, timeZoneId) <> (QrtzCronTriggers.tupled, QrtzCronTriggers.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      ((Rep.Some(schedName), Rep.Some(triggerName), Rep.Some(triggerGroup), Rep.Some(cronExpression), timeZoneId)).shaped
        .<>({ r =>
          import r._; _1.map(_ => QrtzCronTriggers.tupled((_1.get, _2.get, _3.get, _4.get, _5)))
        }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column trigger_name SqlType(varchar), Length(200,true) */
    val triggerName: Rep[String] = column[String]("trigger_name", O.Length(200, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Database column cron_expression SqlType(varchar), Length(120,true) */
    val cronExpression: Rep[String] = column[String]("cron_expression", O.Length(120, varying = true))

    /** Database column time_zone_id SqlType(varchar), Length(80,true), Default(None) */
    val timeZoneId: Rep[Option[String]] =
      column[Option[String]]("time_zone_id", O.Length(80, varying = true), O.Default(None))

    /** Primary key of QrtzCronTriggersModel (database name qrtz_cron_triggers_pkey) */
    val pk = primaryKey("qrtz_cron_triggers_pkey", (schedName, triggerName, triggerGroup))

    /** Foreign key referencing QrtzTriggersModel (database name qrtz_cron_triggers_sched_name_fkey) */
    lazy val qrtzTriggersModelFk =
      foreignKey("qrtz_cron_triggers_sched_name_fkey", (schedName, triggerName, triggerGroup), QrtzTriggersModel)(
        r => (r.schedName, r.triggerName, r.triggerGroup),
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
  }

  /** Collection-like TableQuery object for table QrtzCronTriggersModel */
  lazy val QrtzCronTriggersModel = new TableQuery(tag => new QrtzCronTriggersModel(tag))
}
