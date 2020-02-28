package mass.job.db.model

/** Entity class storing rows of table QrtzTriggersModel
 *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
 *  @param triggerName Database column trigger_name SqlType(varchar), Length(200,true)
 *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true)
 *  @param jobName Database column job_name SqlType(varchar), Length(200,true)
 *  @param jobGroup Database column job_group SqlType(varchar), Length(200,true)
 *  @param description Database column description SqlType(text), Default(None)
 *  @param nextFireTime Database column next_fire_time SqlType(int8), Default(None)
 *  @param prevFireTime Database column prev_fire_time SqlType(int8), Default(None)
 *  @param priority Database column priority SqlType(int4), Default(None)
 *  @param triggerState Database column trigger_state SqlType(varchar), Length(16,true)
 *  @param triggerType Database column trigger_type SqlType(varchar), Length(8,true)
 *  @param startTime Database column start_time SqlType(int8)
 *  @param endTime Database column end_time SqlType(int8), Default(None)
 *  @param calendarName Database column calendar_name SqlType(varchar), Length(200,true), Default(None)
 *  @param misfireInstr Database column misfire_instr SqlType(int2), Default(None)
 *  @param jobData Database column job_data SqlType(bytea), Default(None)
 *  @param createdAt Database column created_at SqlType(timestamptz) */
case class QrtzTriggers(
    schedName: String,
    triggerName: String,
    triggerGroup: String,
    jobName: String,
    jobGroup: String,
    description: Option[String] = None,
    nextFireTime: Option[Long] = None,
    prevFireTime: Option[Long] = None,
    priority: Option[Int] = None,
    triggerState: String,
    triggerType: String,
    startTime: Long,
    endTime: Option[Long] = None,
    calendarName: Option[String] = None,
    misfireInstr: Option[Short] = None,
    jobData: Option[Array[Byte]] = None,
    createdAt: java.time.OffsetDateTime)

trait QrtzTriggersModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** GetResult implicit for fetching QrtzTriggers objects using plain SQL queries */
  implicit def GetResultQrtzTriggers(
      implicit e0: GR[String],
      e1: GR[Option[String]],
      e2: GR[Option[Long]],
      e3: GR[Option[Int]],
      e4: GR[Long],
      e5: GR[Option[Short]],
      e6: GR[Option[Array[Byte]]],
      e7: GR[java.time.OffsetDateTime]): GR[QrtzTriggers] = GR { prs =>
    import prs._
    QrtzTriggers.tupled(
      (
        <<[String],
        <<[String],
        <<[String],
        <<[String],
        <<[String],
        <<?[String],
        <<?[Long],
        <<?[Long],
        <<?[Int],
        <<[String],
        <<[String],
        <<[Long],
        <<?[Long],
        <<?[String],
        <<?[Short],
        <<?[Array[Byte]],
        <<[java.time.OffsetDateTime]))
  }

  /** Table description of table qrtz_triggers. Objects of this class serve as prototypes for rows in queries. */
  class QrtzTriggersModel(_tableTag: Tag) extends profile.api.Table[QrtzTriggers](_tableTag, "qrtz_triggers") {
    def * =
      (
        schedName,
        triggerName,
        triggerGroup,
        jobName,
        jobGroup,
        description,
        nextFireTime,
        prevFireTime,
        priority,
        triggerState,
        triggerType,
        startTime,
        endTime,
        calendarName,
        misfireInstr,
        jobData,
        createdAt) <> (QrtzTriggers.tupled, QrtzTriggers.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (
        (
          Rep.Some(schedName),
          Rep.Some(triggerName),
          Rep.Some(triggerGroup),
          Rep.Some(jobName),
          Rep.Some(jobGroup),
          description,
          nextFireTime,
          prevFireTime,
          priority,
          Rep.Some(triggerState),
          Rep.Some(triggerType),
          Rep.Some(startTime),
          endTime,
          calendarName,
          misfireInstr,
          jobData,
          Rep.Some(createdAt))).shaped.<>(
        { r =>
          import r._;
          _1.map(
            _ =>
              QrtzTriggers.tupled(
                (
                  _1.get,
                  _2.get,
                  _3.get,
                  _4.get,
                  _5.get,
                  _6,
                  _7,
                  _8,
                  _9,
                  _10.get,
                  _11.get,
                  _12.get,
                  _13,
                  _14,
                  _15,
                  _16,
                  _17.get)))
        },
        (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column trigger_name SqlType(varchar), Length(200,true) */
    val triggerName: Rep[String] = column[String]("trigger_name", O.Length(200, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Database column job_name SqlType(varchar), Length(200,true) */
    val jobName: Rep[String] = column[String]("job_name", O.Length(200, varying = true))

    /** Database column job_group SqlType(varchar), Length(200,true) */
    val jobGroup: Rep[String] = column[String]("job_group", O.Length(200, varying = true))

    /** Database column description SqlType(text), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Default(None))

    /** Database column next_fire_time SqlType(int8), Default(None) */
    val nextFireTime: Rep[Option[Long]] = column[Option[Long]]("next_fire_time", O.Default(None))

    /** Database column prev_fire_time SqlType(int8), Default(None) */
    val prevFireTime: Rep[Option[Long]] = column[Option[Long]]("prev_fire_time", O.Default(None))

    /** Database column priority SqlType(int4), Default(None) */
    val priority: Rep[Option[Int]] = column[Option[Int]]("priority", O.Default(None))

    /** Database column trigger_state SqlType(varchar), Length(16,true) */
    val triggerState: Rep[String] = column[String]("trigger_state", O.Length(16, varying = true))

    /** Database column trigger_type SqlType(varchar), Length(8,true) */
    val triggerType: Rep[String] = column[String]("trigger_type", O.Length(8, varying = true))

    /** Database column start_time SqlType(int8) */
    val startTime: Rep[Long] = column[Long]("start_time")

    /** Database column end_time SqlType(int8), Default(None) */
    val endTime: Rep[Option[Long]] = column[Option[Long]]("end_time", O.Default(None))

    /** Database column calendar_name SqlType(varchar), Length(200,true), Default(None) */
    val calendarName: Rep[Option[String]] =
      column[Option[String]]("calendar_name", O.Length(200, varying = true), O.Default(None))

    /** Database column misfire_instr SqlType(int2), Default(None) */
    val misfireInstr: Rep[Option[Short]] = column[Option[Short]]("misfire_instr", O.Default(None))

    /** Database column job_data SqlType(bytea), Default(None) */
    val jobData: Rep[Option[Array[Byte]]] = column[Option[Array[Byte]]]("job_data", O.Default(None))

    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("created_at")

    /** Primary key of QrtzTriggersModel (database name qrtz_triggers_pkey) */
    val pk = primaryKey("qrtz_triggers_pkey", (schedName, triggerName, triggerGroup))

    /** Foreign key referencing QrtzJobDetailsModel (database name qrtz_triggers_sched_name_fkey) */
    lazy val qrtzJobDetailsModelFk =
      foreignKey("qrtz_triggers_sched_name_fkey", (schedName, jobName, jobGroup), QrtzJobDetailsModel)(
        r => (r.schedName, r.jobName, r.jobGroup),
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)

    /** Index over (schedName,calendarName) (database name idx_qrtz_t_c) */
    val index1 = index("idx_qrtz_t_c", (schedName, calendarName))

    /** Index over (schedName,triggerGroup) (database name idx_qrtz_t_g) */
    val index2 = index("idx_qrtz_t_g", (schedName, triggerGroup))

    /** Index over (schedName,jobGroup) (database name idx_qrtz_t_jg) */
    val index3 = index("idx_qrtz_t_jg", (schedName, jobGroup))

    /** Index over (schedName,triggerGroup,triggerState) (database name idx_qrtz_t_n_g_state) */
    val index4 = index("idx_qrtz_t_n_g_state", (schedName, triggerGroup, triggerState))

    /** Index over (schedName,triggerName,triggerGroup,triggerState) (database name idx_qrtz_t_n_state) */
    val index5 = index("idx_qrtz_t_n_state", (schedName, triggerName, triggerGroup, triggerState))

    /** Index over (schedName,nextFireTime) (database name idx_qrtz_t_next_fire_time) */
    val index6 = index("idx_qrtz_t_next_fire_time", (schedName, nextFireTime))

    /** Index over (schedName,misfireInstr,nextFireTime) (database name idx_qrtz_t_nft_misfire) */
    val index7 = index("idx_qrtz_t_nft_misfire", (schedName, misfireInstr, nextFireTime))

    /** Index over (schedName,triggerState,nextFireTime) (database name idx_qrtz_t_nft_st) */
    val index8 = index("idx_qrtz_t_nft_st", (schedName, triggerState, nextFireTime))

    /** Index over (schedName,misfireInstr,nextFireTime,triggerState) (database name idx_qrtz_t_nft_st_misfire) */
    val index9 = index("idx_qrtz_t_nft_st_misfire", (schedName, misfireInstr, nextFireTime, triggerState))

    /** Index over (schedName,misfireInstr,nextFireTime,triggerGroup,triggerState) (database name idx_qrtz_t_nft_st_misfire_grp) */
    val index10 =
      index("idx_qrtz_t_nft_st_misfire_grp", (schedName, misfireInstr, nextFireTime, triggerGroup, triggerState))

    /** Index over (schedName,triggerState) (database name idx_qrtz_t_state) */
    val index11 = index("idx_qrtz_t_state", (schedName, triggerState))
  }

  /** Collection-like TableQuery object for table QrtzTriggersModel */
  lazy val QrtzTriggersModel = new TableQuery(tag => new QrtzTriggersModel(tag))
}
