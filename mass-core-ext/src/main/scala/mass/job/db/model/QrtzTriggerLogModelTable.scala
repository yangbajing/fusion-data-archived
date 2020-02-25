package mass.job.db.model
// AUTO-GENERATED Slick data model for table QrtzTriggerLogModel
trait QrtzTriggerLogModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** Entity class storing rows of table QrtzTriggerLogModel
   *  @param id Database column id SqlType(bpchar), PrimaryKey, Length(24,false)
   *  @param triggerName Database column trigger_name SqlType(varchar), Length(200,true)
   *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true)
   *  @param jobName Database column job_name SqlType(varchar), Length(200,true)
   *  @param jobGroup Database column job_group SqlType(varchar), Length(200,true)
   *  @param startTime Database column start_time SqlType(timestamptz)
   *  @param completionTime Database column completion_time SqlType(timestamptz), Default(None)
   *  @param completionStatus Database column completion_status SqlType(int4)
   *  @param completionValue Database column completion_value SqlType(text), Default(None)
   *  @param createdAt Database column created_at SqlType(timestamptz) */
  case class QrtzTriggerLog(
      id: String,
      triggerName: String,
      triggerGroup: String,
      jobName: String,
      jobGroup: String,
      startTime: java.time.OffsetDateTime,
      completionTime: Option[java.time.OffsetDateTime] = None,
      completionStatus: mass.model.job.RunStatus,
      completionValue: Option[String] = None,
      createdAt: java.time.OffsetDateTime)

  /** GetResult implicit for fetching QrtzTriggerLog objects using plain SQL queries */
  implicit def GetResultQrtzTriggerLog(
      implicit e0: GR[String],
      e1: GR[java.time.OffsetDateTime],
      e2: GR[Option[java.time.OffsetDateTime]],
      e3: GR[mass.model.job.RunStatus],
      e4: GR[Option[String]]): GR[QrtzTriggerLog] = GR { prs =>
    import prs._
    QrtzTriggerLog.tupled(
      (
        <<[String],
        <<[String],
        <<[String],
        <<[String],
        <<[String],
        <<[java.time.OffsetDateTime],
        <<?[java.time.OffsetDateTime],
        <<[mass.model.job.RunStatus],
        <<?[String],
        <<[java.time.OffsetDateTime]))
  }

  /** Table description of table qrtz_trigger_log. Objects of this class serve as prototypes for rows in queries. */
  class QrtzTriggerLogModel(_tableTag: Tag) extends profile.api.Table[QrtzTriggerLog](_tableTag, "qrtz_trigger_log") {
    def * =
      (
        id,
        triggerName,
        triggerGroup,
        jobName,
        jobGroup,
        startTime,
        completionTime,
        completionStatus,
        completionValue,
        createdAt) <> (QrtzTriggerLog.tupled, QrtzTriggerLog.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (
        (
          Rep.Some(id),
          Rep.Some(triggerName),
          Rep.Some(triggerGroup),
          Rep.Some(jobName),
          Rep.Some(jobGroup),
          Rep.Some(startTime),
          completionTime,
          Rep.Some(completionStatus),
          completionValue,
          Rep.Some(createdAt))).shaped.<>({ r =>
        import r._;
        _1.map(_ => QrtzTriggerLog.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7, _8.get, _9, _10.get)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bpchar), PrimaryKey, Length(24,false) */
    val id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(24, varying = false))

    /** Database column trigger_name SqlType(varchar), Length(200,true) */
    val triggerName: Rep[String] = column[String]("trigger_name", O.Length(200, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Database column job_name SqlType(varchar), Length(200,true) */
    val jobName: Rep[String] = column[String]("job_name", O.Length(200, varying = true))

    /** Database column job_group SqlType(varchar), Length(200,true) */
    val jobGroup: Rep[String] = column[String]("job_group", O.Length(200, varying = true))

    /** Database column start_time SqlType(timestamptz) */
    val startTime: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("start_time")

    /** Database column completion_time SqlType(timestamptz), Default(None) */
    val completionTime: Rep[Option[java.time.OffsetDateTime]] =
      column[Option[java.time.OffsetDateTime]]("completion_time", O.Default(None))

    /** Database column completion_status SqlType(int4) */
    val completionStatus: Rep[mass.model.job.RunStatus] = column[mass.model.job.RunStatus]("completion_status")

    /** Database column completion_value SqlType(text), Default(None) */
    val completionValue: Rep[Option[String]] = column[Option[String]]("completion_value", O.Default(None))

    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("created_at")
  }

  /** Collection-like TableQuery object for table QrtzTriggerLogModel */
  lazy val QrtzTriggerLogModel = new TableQuery(tag => new QrtzTriggerLogModel(tag))
}
