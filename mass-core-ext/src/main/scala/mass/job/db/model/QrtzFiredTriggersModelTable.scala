package mass.job.db.model
// AUTO-GENERATED Slick data model for table QrtzFiredTriggersModel
trait QrtzFiredTriggersModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** Entity class storing rows of table QrtzFiredTriggersModel
   *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
   *  @param entryId Database column entry_id SqlType(varchar), Length(95,true)
   *  @param triggerName Database column trigger_name SqlType(varchar), Length(200,true)
   *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true)
   *  @param instanceName Database column instance_name SqlType(varchar), Length(200,true)
   *  @param firedTime Database column fired_time SqlType(int8)
   *  @param schedTime Database column sched_time SqlType(int8)
   *  @param priority Database column priority SqlType(int4)
   *  @param state Database column state SqlType(varchar), Length(16,true)
   *  @param jobName Database column job_name SqlType(varchar), Length(200,true), Default(None)
   *  @param jobGroup Database column job_group SqlType(varchar), Length(200,true), Default(None)
   *  @param isNonconcurrent Database column is_nonconcurrent SqlType(bool), Default(None)
   *  @param requestsRecovery Database column requests_recovery SqlType(bool), Default(None) */
  case class QrtzFiredTriggers(
      schedName: String,
      entryId: String,
      triggerName: String,
      triggerGroup: String,
      instanceName: String,
      firedTime: Long,
      schedTime: Long,
      priority: Int,
      state: String,
      jobName: Option[String] = None,
      jobGroup: Option[String] = None,
      isNonconcurrent: Option[Boolean] = None,
      requestsRecovery: Option[Boolean] = None)

  /** GetResult implicit for fetching QrtzFiredTriggers objects using plain SQL queries */
  implicit def GetResultQrtzFiredTriggers(
      implicit e0: GR[String],
      e1: GR[Long],
      e2: GR[Int],
      e3: GR[Option[String]],
      e4: GR[Option[Boolean]]): GR[QrtzFiredTriggers] = GR { prs =>
    import prs._
    QrtzFiredTriggers.tupled(
      (
        <<[String],
        <<[String],
        <<[String],
        <<[String],
        <<[String],
        <<[Long],
        <<[Long],
        <<[Int],
        <<[String],
        <<?[String],
        <<?[String],
        <<?[Boolean],
        <<?[Boolean]))
  }

  /** Table description of table qrtz_fired_triggers. Objects of this class serve as prototypes for rows in queries. */
  class QrtzFiredTriggersModel(_tableTag: Tag)
      extends profile.api.Table[QrtzFiredTriggers](_tableTag, "qrtz_fired_triggers") {
    def * =
      (
        schedName,
        entryId,
        triggerName,
        triggerGroup,
        instanceName,
        firedTime,
        schedTime,
        priority,
        state,
        jobName,
        jobGroup,
        isNonconcurrent,
        requestsRecovery) <> (QrtzFiredTriggers.tupled, QrtzFiredTriggers.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (
        (
          Rep.Some(schedName),
          Rep.Some(entryId),
          Rep.Some(triggerName),
          Rep.Some(triggerGroup),
          Rep.Some(instanceName),
          Rep.Some(firedTime),
          Rep.Some(schedTime),
          Rep.Some(priority),
          Rep.Some(state),
          jobName,
          jobGroup,
          isNonconcurrent,
          requestsRecovery)).shaped.<>(
        { r =>
          import r._;
          _1.map(
            _ =>
              QrtzFiredTriggers.tupled(
                (_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10, _11, _12, _13)))
        },
        (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column entry_id SqlType(varchar), Length(95,true) */
    val entryId: Rep[String] = column[String]("entry_id", O.Length(95, varying = true))

    /** Database column trigger_name SqlType(varchar), Length(200,true) */
    val triggerName: Rep[String] = column[String]("trigger_name", O.Length(200, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Database column instance_name SqlType(varchar), Length(200,true) */
    val instanceName: Rep[String] = column[String]("instance_name", O.Length(200, varying = true))

    /** Database column fired_time SqlType(int8) */
    val firedTime: Rep[Long] = column[Long]("fired_time")

    /** Database column sched_time SqlType(int8) */
    val schedTime: Rep[Long] = column[Long]("sched_time")

    /** Database column priority SqlType(int4) */
    val priority: Rep[Int] = column[Int]("priority")

    /** Database column state SqlType(varchar), Length(16,true) */
    val state: Rep[String] = column[String]("state", O.Length(16, varying = true))

    /** Database column job_name SqlType(varchar), Length(200,true), Default(None) */
    val jobName: Rep[Option[String]] =
      column[Option[String]]("job_name", O.Length(200, varying = true), O.Default(None))

    /** Database column job_group SqlType(varchar), Length(200,true), Default(None) */
    val jobGroup: Rep[Option[String]] =
      column[Option[String]]("job_group", O.Length(200, varying = true), O.Default(None))

    /** Database column is_nonconcurrent SqlType(bool), Default(None) */
    val isNonconcurrent: Rep[Option[Boolean]] = column[Option[Boolean]]("is_nonconcurrent", O.Default(None))

    /** Database column requests_recovery SqlType(bool), Default(None) */
    val requestsRecovery: Rep[Option[Boolean]] = column[Option[Boolean]]("requests_recovery", O.Default(None))

    /** Primary key of QrtzFiredTriggersModel (database name qrtz_fired_triggers_pkey) */
    val pk = primaryKey("qrtz_fired_triggers_pkey", (schedName, entryId))

    /** Index over (schedName,instanceName,requestsRecovery) (database name idx_qrtz_ft_inst_job_req_rcvry) */
    val index1 = index("idx_qrtz_ft_inst_job_req_rcvry", (schedName, instanceName, requestsRecovery))

    /** Index over (schedName,jobName,jobGroup) (database name idx_qrtz_ft_j_g) */
    val index2 = index("idx_qrtz_ft_j_g", (schedName, jobName, jobGroup))

    /** Index over (schedName,jobGroup) (database name idx_qrtz_ft_jg) */
    val index3 = index("idx_qrtz_ft_jg", (schedName, jobGroup))

    /** Index over (schedName,triggerName,triggerGroup) (database name idx_qrtz_ft_t_g) */
    val index4 = index("idx_qrtz_ft_t_g", (schedName, triggerName, triggerGroup))

    /** Index over (schedName,triggerGroup) (database name idx_qrtz_ft_tg) */
    val index5 = index("idx_qrtz_ft_tg", (schedName, triggerGroup))

    /** Index over (schedName,instanceName) (database name idx_qrtz_ft_trig_inst_name) */
    val index6 = index("idx_qrtz_ft_trig_inst_name", (schedName, instanceName))
  }

  /** Collection-like TableQuery object for table QrtzFiredTriggersModel */
  lazy val QrtzFiredTriggersModel = new TableQuery(tag => new QrtzFiredTriggersModel(tag))
}
