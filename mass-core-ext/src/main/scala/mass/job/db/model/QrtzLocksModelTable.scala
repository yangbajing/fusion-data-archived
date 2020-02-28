package mass.job.db.model

/** Entity class storing rows of table QrtzLocksModel
 *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
 *  @param lockName Database column lock_name SqlType(varchar), Length(40,true) */
case class QrtzLocks(schedName: String, lockName: String)

trait QrtzLocksModelTable {

  self: QrtzModels =>

  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** GetResult implicit for fetching QrtzLocks objects using plain SQL queries */
  implicit def GetResultQrtzLocks(implicit e0: GR[String]): GR[QrtzLocks] = GR { prs =>
    import prs._
    QrtzLocks.tupled((<<[String], <<[String]))
  }

  /** Table description of table qrtz_locks. Objects of this class serve as prototypes for rows in queries. */
  class QrtzLocksModel(_tableTag: Tag) extends profile.api.Table[QrtzLocks](_tableTag, "qrtz_locks") {
    def * = (schedName, lockName) <> (QrtzLocks.tupled, QrtzLocks.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      ((Rep.Some(schedName), Rep.Some(lockName))).shaped.<>({ r =>
        import r._; _1.map(_ => QrtzLocks.tupled((_1.get, _2.get)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column lock_name SqlType(varchar), Length(40,true) */
    val lockName: Rep[String] = column[String]("lock_name", O.Length(40, varying = true))

    /** Primary key of QrtzLocksModel (database name qrtz_locks_pkey) */
    val pk = primaryKey("qrtz_locks_pkey", (schedName, lockName))
  }

  /** Collection-like TableQuery object for table QrtzLocksModel */
  lazy val QrtzLocksModel = new TableQuery(tag => new QrtzLocksModel(tag))
}
