package mass.job.db.model
// AUTO-GENERATED Slick data model for table QrtzCalendarsModel
trait QrtzCalendarsModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** Entity class storing rows of table QrtzCalendarsModel
   *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
   *  @param calendarName Database column calendar_name SqlType(varchar), Length(200,true)
   *  @param calendar Database column calendar SqlType(bytea) */
  case class QrtzCalendars(schedName: String, calendarName: String, calendar: Array[Byte])

  /** GetResult implicit for fetching QrtzCalendars objects using plain SQL queries */
  implicit def GetResultQrtzCalendars(implicit e0: GR[String], e1: GR[Array[Byte]]): GR[QrtzCalendars] = GR { prs =>
    import prs._
    QrtzCalendars.tupled((<<[String], <<[String], <<[Array[Byte]]))
  }

  /** Table description of table qrtz_calendars. Objects of this class serve as prototypes for rows in queries. */
  class QrtzCalendarsModel(_tableTag: Tag) extends profile.api.Table[QrtzCalendars](_tableTag, "qrtz_calendars") {
    def * = (schedName, calendarName, calendar) <> (QrtzCalendars.tupled, QrtzCalendars.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      ((Rep.Some(schedName), Rep.Some(calendarName), Rep.Some(calendar))).shaped.<>({ r =>
        import r._; _1.map(_ => QrtzCalendars.tupled((_1.get, _2.get, _3.get)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column calendar_name SqlType(varchar), Length(200,true) */
    val calendarName: Rep[String] = column[String]("calendar_name", O.Length(200, varying = true))

    /** Database column calendar SqlType(bytea) */
    val calendar: Rep[Array[Byte]] = column[Array[Byte]]("calendar")

    /** Primary key of QrtzCalendarsModel (database name qrtz_calendars_pkey) */
    val pk = primaryKey("qrtz_calendars_pkey", (schedName, calendarName))
  }

  /** Collection-like TableQuery object for table QrtzCalendarsModel */
  lazy val QrtzCalendarsModel = new TableQuery(tag => new QrtzCalendarsModel(tag))
}
