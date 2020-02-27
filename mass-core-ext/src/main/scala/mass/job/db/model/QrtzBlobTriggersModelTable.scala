package mass.job.db.model

/** Entity class storing rows of table QrtzBlobTriggersModel
 *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
 *  @param triggerName Database column trigger_name SqlType(varchar), Length(200,true)
 *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true)
 *  @param blobData Database column blob_data SqlType(bytea), Default(None) */
case class QrtzBlobTriggers(
    schedName: String,
    triggerName: String,
    triggerGroup: String,
    blobData: Option[Array[Byte]] = None)

trait QrtzBlobTriggersModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** GetResult implicit for fetching QrtzBlobTriggers objects using plain SQL queries */
  implicit def GetResultQrtzBlobTriggers(implicit e0: GR[String], e1: GR[Option[Array[Byte]]]): GR[QrtzBlobTriggers] =
    GR { prs =>
      import prs._
      QrtzBlobTriggers.tupled((<<[String], <<[String], <<[String], <<?[Array[Byte]]))
    }

  /** Table description of table qrtz_blob_triggers. Objects of this class serve as prototypes for rows in queries. */
  class QrtzBlobTriggersModel(_tableTag: Tag)
      extends profile.api.Table[QrtzBlobTriggers](_tableTag, "qrtz_blob_triggers") {
    def * = (schedName, triggerName, triggerGroup, blobData) <> (QrtzBlobTriggers.tupled, QrtzBlobTriggers.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      ((Rep.Some(schedName), Rep.Some(triggerName), Rep.Some(triggerGroup), blobData)).shaped.<>({ r =>
        import r._; _1.map(_ => QrtzBlobTriggers.tupled((_1.get, _2.get, _3.get, _4)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column trigger_name SqlType(varchar), Length(200,true) */
    val triggerName: Rep[String] = column[String]("trigger_name", O.Length(200, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Database column blob_data SqlType(bytea), Default(None) */
    val blobData: Rep[Option[Array[Byte]]] = column[Option[Array[Byte]]]("blob_data", O.Default(None))

    /** Primary key of QrtzBlobTriggersModel (database name qrtz_blob_triggers_pkey) */
    val pk = primaryKey("qrtz_blob_triggers_pkey", (schedName, triggerName, triggerGroup))

    /** Foreign key referencing QrtzTriggersModel (database name qrtz_blob_triggers_sched_name_fkey) */
    lazy val qrtzTriggersModelFk =
      foreignKey("qrtz_blob_triggers_sched_name_fkey", (schedName, triggerName, triggerGroup), QrtzTriggersModel)(
        r => (r.schedName, r.triggerName, r.triggerGroup),
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
  }

  /** Collection-like TableQuery object for table QrtzBlobTriggersModel */
  lazy val QrtzBlobTriggersModel = new TableQuery(tag => new QrtzBlobTriggersModel(tag))
}
