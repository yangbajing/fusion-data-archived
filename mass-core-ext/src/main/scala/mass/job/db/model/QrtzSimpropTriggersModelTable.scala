package mass.job.db.model

/** Entity class storing rows of table QrtzSimpropTriggersModel
 *  @param schedName Database column sched_name SqlType(varchar), Length(120,true)
 *  @param triggerName Database column trigger_name SqlType(varchar), Length(200,true)
 *  @param triggerGroup Database column trigger_group SqlType(varchar), Length(200,true)
 *  @param strProp1 Database column str_prop_1 SqlType(varchar), Length(512,true), Default(None)
 *  @param strProp2 Database column str_prop_2 SqlType(varchar), Length(512,true), Default(None)
 *  @param strProp3 Database column str_prop_3 SqlType(varchar), Length(512,true), Default(None)
 *  @param intProp1 Database column int_prop_1 SqlType(int4), Default(None)
 *  @param intProp2 Database column int_prop_2 SqlType(int4), Default(None)
 *  @param longProp1 Database column long_prop_1 SqlType(int8), Default(None)
 *  @param longProp2 Database column long_prop_2 SqlType(int8), Default(None)
 *  @param decProp1 Database column dec_prop_1 SqlType(numeric), Default(None)
 *  @param decProp2 Database column dec_prop_2 SqlType(numeric), Default(None)
 *  @param boolProp1 Database column bool_prop_1 SqlType(bool), Default(None)
 *  @param boolProp2 Database column bool_prop_2 SqlType(bool), Default(None) */
case class QrtzSimpropTriggers(
    schedName: String,
    triggerName: String,
    triggerGroup: String,
    strProp1: Option[String] = None,
    strProp2: Option[String] = None,
    strProp3: Option[String] = None,
    intProp1: Option[Int] = None,
    intProp2: Option[Int] = None,
    longProp1: Option[Long] = None,
    longProp2: Option[Long] = None,
    decProp1: Option[scala.math.BigDecimal] = None,
    decProp2: Option[scala.math.BigDecimal] = None,
    boolProp1: Option[Boolean] = None,
    boolProp2: Option[Boolean] = None)

trait QrtzSimpropTriggersModelTable {

  self: QrtzModels =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  /** GetResult implicit for fetching QrtzSimpropTriggers objects using plain SQL queries */
  implicit def GetResultQrtzSimpropTriggers(
      implicit e0: GR[String],
      e1: GR[Option[String]],
      e2: GR[Option[Int]],
      e3: GR[Option[Long]],
      e4: GR[Option[scala.math.BigDecimal]],
      e5: GR[Option[Boolean]]): GR[QrtzSimpropTriggers] = GR { prs =>
    import prs._
    QrtzSimpropTriggers.tupled(
      (
        <<[String],
        <<[String],
        <<[String],
        <<?[String],
        <<?[String],
        <<?[String],
        <<?[Int],
        <<?[Int],
        <<?[Long],
        <<?[Long],
        <<?[scala.math.BigDecimal],
        <<?[scala.math.BigDecimal],
        <<?[Boolean],
        <<?[Boolean]))
  }

  /** Table description of table qrtz_simprop_triggers. Objects of this class serve as prototypes for rows in queries. */
  class QrtzSimpropTriggersModel(_tableTag: Tag)
      extends profile.api.Table[QrtzSimpropTriggers](_tableTag, "qrtz_simprop_triggers") {
    def * =
      (
        schedName,
        triggerName,
        triggerGroup,
        strProp1,
        strProp2,
        strProp3,
        intProp1,
        intProp2,
        longProp1,
        longProp2,
        decProp1,
        decProp2,
        boolProp1,
        boolProp2) <> (QrtzSimpropTriggers.tupled, QrtzSimpropTriggers.unapply)

    /** Maps whole row to an option. Useful for outer joins. */
    def ? =
      (
        (
          Rep.Some(schedName),
          Rep.Some(triggerName),
          Rep.Some(triggerGroup),
          strProp1,
          strProp2,
          strProp3,
          intProp1,
          intProp2,
          longProp1,
          longProp2,
          decProp1,
          decProp2,
          boolProp1,
          boolProp2)).shaped.<>({ r =>
        import r._;
        _1.map(_ =>
          QrtzSimpropTriggers.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14)))
      }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column sched_name SqlType(varchar), Length(120,true) */
    val schedName: Rep[String] = column[String]("sched_name", O.Length(120, varying = true))

    /** Database column trigger_name SqlType(varchar), Length(200,true) */
    val triggerName: Rep[String] = column[String]("trigger_name", O.Length(200, varying = true))

    /** Database column trigger_group SqlType(varchar), Length(200,true) */
    val triggerGroup: Rep[String] = column[String]("trigger_group", O.Length(200, varying = true))

    /** Database column str_prop_1 SqlType(varchar), Length(512,true), Default(None) */
    val strProp1: Rep[Option[String]] =
      column[Option[String]]("str_prop_1", O.Length(512, varying = true), O.Default(None))

    /** Database column str_prop_2 SqlType(varchar), Length(512,true), Default(None) */
    val strProp2: Rep[Option[String]] =
      column[Option[String]]("str_prop_2", O.Length(512, varying = true), O.Default(None))

    /** Database column str_prop_3 SqlType(varchar), Length(512,true), Default(None) */
    val strProp3: Rep[Option[String]] =
      column[Option[String]]("str_prop_3", O.Length(512, varying = true), O.Default(None))

    /** Database column int_prop_1 SqlType(int4), Default(None) */
    val intProp1: Rep[Option[Int]] = column[Option[Int]]("int_prop_1", O.Default(None))

    /** Database column int_prop_2 SqlType(int4), Default(None) */
    val intProp2: Rep[Option[Int]] = column[Option[Int]]("int_prop_2", O.Default(None))

    /** Database column long_prop_1 SqlType(int8), Default(None) */
    val longProp1: Rep[Option[Long]] = column[Option[Long]]("long_prop_1", O.Default(None))

    /** Database column long_prop_2 SqlType(int8), Default(None) */
    val longProp2: Rep[Option[Long]] = column[Option[Long]]("long_prop_2", O.Default(None))

    /** Database column dec_prop_1 SqlType(numeric), Default(None) */
    val decProp1: Rep[Option[scala.math.BigDecimal]] =
      column[Option[scala.math.BigDecimal]]("dec_prop_1", O.Default(None))

    /** Database column dec_prop_2 SqlType(numeric), Default(None) */
    val decProp2: Rep[Option[scala.math.BigDecimal]] =
      column[Option[scala.math.BigDecimal]]("dec_prop_2", O.Default(None))

    /** Database column bool_prop_1 SqlType(bool), Default(None) */
    val boolProp1: Rep[Option[Boolean]] = column[Option[Boolean]]("bool_prop_1", O.Default(None))

    /** Database column bool_prop_2 SqlType(bool), Default(None) */
    val boolProp2: Rep[Option[Boolean]] = column[Option[Boolean]]("bool_prop_2", O.Default(None))

    /** Primary key of QrtzSimpropTriggersModel (database name qrtz_simprop_triggers_pkey) */
    val pk = primaryKey("qrtz_simprop_triggers_pkey", (schedName, triggerName, triggerGroup))

    /** Foreign key referencing QrtzTriggersModel (database name qrtz_simprop_triggers_sched_name_fkey) */
    lazy val qrtzTriggersModelFk =
      foreignKey("qrtz_simprop_triggers_sched_name_fkey", (schedName, triggerName, triggerGroup), QrtzTriggersModel)(
        r => (r.schedName, r.triggerName, r.triggerGroup),
        onUpdate = ForeignKeyAction.NoAction,
        onDelete = ForeignKeyAction.NoAction)
  }

  /** Collection-like TableQuery object for table QrtzSimpropTriggersModel */
  lazy val QrtzSimpropTriggersModel = new TableQuery(tag => new QrtzSimpropTriggersModel(tag))
}
