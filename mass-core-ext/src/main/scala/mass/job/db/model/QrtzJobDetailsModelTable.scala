package mass.job.db.model
// AUTO-GENERATED Slick data model for table QrtzJobDetailsModel
trait QrtzJobDetailsModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** Entity class storing rows of table QrtzJobDetailsModel
   *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
   *  @param jobName Database column job_name SqlType(varchar), Length(200,true)
   *  @param jobGroup Database column job_group SqlType(varchar), Length(200,true)
   *  @param description Database column description SqlType(text), Default(None)
   *  @param jobClassName Database column job_class_name SqlType(varchar), Length(250,true)
   *  @param isDurable Database column is_durable SqlType(bool)
   *  @param isNonconcurrent Database column is_nonconcurrent SqlType(bool)
   *  @param isUpdateData Database column is_update_data SqlType(bool)
   *  @param requestsRecovery Database column requests_recovery SqlType(bool)
   *  @param jobData Database column job_data SqlType(bytea), Default(None)
   *  @param createdAt Database column created_at SqlType(timestamptz) */
  case class QrtzJobDetails(
      schedName: String,
      jobName: String,
      jobGroup: String,
      description: Option[String] = None,
      jobClassName: String,
      isDurable: Boolean,
      isNonconcurrent: Boolean,
      isUpdateData: Boolean,
      requestsRecovery: Boolean,
      jobData: Option[Array[Byte]] = None,
      createdAt: java.time.OffsetDateTime)

  /** GetResult implicit for fetching QrtzJobDetails objects using plain SQL queries */
  implicit def GetResultQrtzJobDetails(
      implicit e0: GR[String],
      e1: GR[Option[String]],
      e2: GR[Boolean],
      e3: GR[Option[Array[Byte]]],
      e4: GR[java.time.OffsetDateTime]): GR[QrtzJobDetails] = GR { prs =>
    import prs._
    QrtzJobDetails.tupled(
      (
        <<[String],
        <<[String],
        <<[String],
        <<?[String],
        <<[String],
        <<[Boolean],
        <<[Boolean],
        <<[Boolean],
        <<[Boolean],
        <<?[Array[Byte]],
        <<[java.time.OffsetDateTime]))
  }

  /** Table description of table qrtz_job_details. Objects of this class serve as prototypes for rows in queries. */
  class QrtzJobDetailsModel(_tableTag: Tag) extends profile.api.Table[QrtzJobDetails](_tableTag, "qrtz_job_details") {
    def * =
      (
        schedName,
        jobName,
        jobGroup,
        description,
        jobClassName,
        isDurable,
        isNonconcurrent,
        isUpdateData,
        requestsRecovery,
        jobData,
        createdAt) <> (QrtzJobDetails.tupled, QrtzJobDetails.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (
        (
          Rep.Some(schedName),
          Rep.Some(jobName),
          Rep.Some(jobGroup),
          description,
          Rep.Some(jobClassName),
          Rep.Some(isDurable),
          Rep.Some(isNonconcurrent),
          Rep.Some(isUpdateData),
          Rep.Some(requestsRecovery),
          jobData,
          Rep.Some(createdAt))).shaped.<>({ r =>
        import r._;
        _1.map(_ =>
          QrtzJobDetails.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6.get, _7.get, _8.get, _9.get, _10, _11.get)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column job_name SqlType(varchar), Length(200,true) */
    val jobName: Rep[String] = column[String]("job_name", O.Length(200, varying = true))

    /** Database column job_group SqlType(varchar), Length(200,true) */
    val jobGroup: Rep[String] = column[String]("job_group", O.Length(200, varying = true))

    /** Database column description SqlType(text), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Default(None))

    /** Database column job_class_name SqlType(varchar), Length(250,true) */
    val jobClassName: Rep[String] = column[String]("job_class_name", O.Length(250, varying = true))

    /** Database column is_durable SqlType(bool) */
    val isDurable: Rep[Boolean] = column[Boolean]("is_durable")

    /** Database column is_nonconcurrent SqlType(bool) */
    val isNonconcurrent: Rep[Boolean] = column[Boolean]("is_nonconcurrent")

    /** Database column is_update_data SqlType(bool) */
    val isUpdateData: Rep[Boolean] = column[Boolean]("is_update_data")

    /** Database column requests_recovery SqlType(bool) */
    val requestsRecovery: Rep[Boolean] = column[Boolean]("requests_recovery")

    /** Database column job_data SqlType(bytea), Default(None) */
    val jobData: Rep[Option[Array[Byte]]] = column[Option[Array[Byte]]]("job_data", O.Default(None))

    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("created_at")

    /** Primary key of QrtzJobDetailsModel (database name qrtz_job_details_pkey) */
    val pk = primaryKey("qrtz_job_details_pkey", (schedName, jobName, jobGroup))

    /** Index over (schedName,jobGroup) (database name idx_qrtz_j_grp) */
    val index1 = index("idx_qrtz_j_grp", (schedName, jobGroup))

    /** Index over (schedName,requestsRecovery) (database name idx_qrtz_j_req_recovery) */
    val index2 = index("idx_qrtz_j_req_recovery", (schedName, requestsRecovery))
  }

  /** Collection-like TableQuery object for table QrtzJobDetailsModel */
  lazy val QrtzJobDetailsModel = new TableQuery(tag => new QrtzJobDetailsModel(tag))
}
