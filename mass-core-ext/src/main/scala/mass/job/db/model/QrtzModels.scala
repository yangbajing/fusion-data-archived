package mass.job.db.model
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object QrtzModels extends QrtzModels {
  val profile = mass.db.slick.PgProfile
}

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.)
    Each generated XXXXTable trait is mixed in this trait hence allowing access to all the TableQuery lazy vals.
 */
trait QrtzModels
    extends QrtzSchedulerStateModelTable
    with QrtzSimpleTriggersModelTable
    with QrtzTriggerLogModelTable
    with QrtzLocksModelTable
    with QrtzFiredTriggersModelTable
    with QrtzCronTriggersModelTable
    with QrtzCalendarsModelTable
    with QrtzPausedTriggerGrpsModelTable
    with QrtzBlobTriggersModelTable
    with QrtzJobDetailsModelTable
    with QrtzSimpropTriggersModelTable
    with QrtzTriggersModelTable {
  val profile: mass.db.slick.PgProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(
    QrtzBlobTriggersModel.schema,
    QrtzCalendarsModel.schema,
    QrtzCronTriggersModel.schema,
    QrtzFiredTriggersModel.schema,
    QrtzJobDetailsModel.schema,
    QrtzLocksModel.schema,
    QrtzPausedTriggerGrpsModel.schema,
    QrtzSchedulerStateModel.schema,
    QrtzSimpleTriggersModel.schema,
    QrtzSimpropTriggersModel.schema,
    QrtzTriggerLogModel.schema,
    QrtzTriggersModel.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema
}
