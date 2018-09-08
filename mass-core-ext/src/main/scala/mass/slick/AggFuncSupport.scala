package mass.slick

import com.github.tminglei.slickpg.agg.{AggFuncRep, OrderedAggFuncRep}
import slick.ast.Library.SqlFunction
import slick.ast.{Library, LiteralNode, TypedType}
import slick.jdbc.{JdbcType, JdbcTypesComponent, PostgresProfile}
import slick.lifted.OptionMapperDSL

trait AggFuncSupport extends JdbcTypesComponent { driver: PostgresProfile =>
  import driver.api._

  object AggLibrary {
    // General-Purpose Aggregate Functions
    val ArrayAgg = new SqlFunction("array_agg")
    val StringAgg = new SqlFunction("string_agg")
    val XmlAgg = new SqlFunction("xmlagg")
    val JsonAgg = new SqlFunction("json_agg")
    val JsonbAgg = new SqlFunction("jsonb_agg")
    val JsonObjectAgg = new SqlFunction("json_object_agg")
    val JsonbObjectAgg = new SqlFunction("jsonb_object_agg")
    val Avg = new SqlFunction("avg")
    val BitAnd = new SqlFunction("bit_and")
    val BitOr = new SqlFunction("bit_or")
    val BoolAnd = new SqlFunction("bool_and")
    val BoolOr = new SqlFunction("bool_or")
    val Count = new SqlFunction("count")
    val Every = new SqlFunction("every")
    val Max = new SqlFunction("max")
    val Min = new SqlFunction("min")
    val Sum = new SqlFunction("sum")

    // Aggregate Functions for Statistics
    val Corr = new SqlFunction("corr")
    val CovarPop = new SqlFunction("covar_pop")
    val CovarSamp = new SqlFunction("covar_samp")
    val RegrAvgX = new SqlFunction("regr_avgx")
    val RegrAvgY = new SqlFunction("regr_avgy")
    val RegrCount = new SqlFunction("regr_count")
    val RegrIntercept = new SqlFunction("regr_intercept")
    val RegrR2 = new SqlFunction("regr_r2")
    val RegrSlope = new SqlFunction("regr_slope")
    val RegrSxx = new SqlFunction("regr_sxx")
    val RegrSxy = new SqlFunction("regr_sxy")
    val RegrSyy = new SqlFunction("regr_syy")
    val StdDev = new SqlFunction("stddev")
    val StdDevPop = new SqlFunction("stddev_pop")
    val StdDevSamp = new SqlFunction("stddev_samp")
    val Variance = new SqlFunction("variance")
    val VarPop = new SqlFunction("var_pop")
    val VarSamp = new SqlFunction("var_samp")

    // Ordered-Set Aggregate Functions
    val Mode = new SqlFunction("mode")
    val PercentileCont = new SqlFunction("percentile_cont")
    val PercentileDisc = new SqlFunction("percentile_disc")

    // Hypothetical-Set Aggregate Functions
    val Rank = new SqlFunction("rank")
    val DenseRank = new SqlFunction("dense_rank")
    val PercentRank = new SqlFunction("percent_rank")
    val CumeDist = new SqlFunction("cume_dist")
  }

  trait GeneralAggFunctions {
    import slick.lifted.FunctionSymbolExtensionMethods._

    def arrayAgg[T](c: Rep[Option[T]])(implicit tm: JdbcType[Seq[T]]) =
      AggFuncRep[Seq[T]](AggLibrary.ArrayAgg, Seq(c.toNode))

    def arrayAggEx[T, C[_]](c: Query[Rep[T], _, C])(implicit em: TypedType[Seq[T]]) = {
      AggAggregateLibrary.ArrayAgg.column[Seq[T]](c.toNode)
//      AggFuncRep[Seq[T]](AggLibrary.ArrayAgg, Seq(c.toNode))
    }

    def stringAgg[P, R](c: Rep[P], delimiter: String)(implicit om: OptionMapperDSL.arg[String, P]#to[String, R]) =
      AggFuncRep[String](AggLibrary.StringAgg, Seq(c.toNode, LiteralNode(delimiter)))
    /* do it yourself like this */
    //    def jsonAgg[T](c: Rep[Option[T]])(implicit tm: JdbcType[JSONType]) = AggFuncRep[JSONType](AggLibrary.JsonAgg, Seq(c.toNode))
    def avg[T: JdbcType](c: Rep[Option[T]]) = AggFuncRep[T](AggLibrary.Avg, Seq(c.toNode))
    def bitAnd[T: JdbcType](c: Rep[Option[T]]) = AggFuncRep[T](AggLibrary.BitAnd, Seq(c.toNode))
    def bitOr[T: JdbcType](c: Rep[Option[T]]) = AggFuncRep[T](AggLibrary.BitOr, Seq(c.toNode))

    def boolAnd[P, R](c: Rep[P])(implicit om: OptionMapperDSL.arg[Boolean, P]#to[Boolean, R]) =
      AggFuncRep[Boolean](AggLibrary.BoolAnd, Seq(c.toNode))

    def boolOr[P, R](c: Rep[P])(implicit om: OptionMapperDSL.arg[Boolean, P]#to[Boolean, R]) =
      AggFuncRep[Boolean](AggLibrary.BoolOr, Seq(c.toNode))
    def count_*()(implicit tm: JdbcType[Long]) = AggFuncRep[Long](Library.CountAll, Nil)
    def count[T](c: Rep[T])(implicit tm: JdbcType[Long]) = AggFuncRep[Long](AggLibrary.Count, Seq(c.toNode))

    def every[P, R](c: Rep[P])(implicit om: OptionMapperDSL.arg[Boolean, P]#to[Boolean, R]) =
      AggFuncRep[Boolean](AggLibrary.Every, Seq(c.toNode))
    def max[T: JdbcType](c: Rep[Option[T]]) = AggFuncRep[T](AggLibrary.Max, Seq(c.toNode))
    def min[T: JdbcType](c: Rep[Option[T]]) = AggFuncRep[T](AggLibrary.Min, Seq(c.toNode))
    def sum[T: JdbcType](c: Rep[Option[T]]) = AggFuncRep[T](AggLibrary.Sum, Seq(c.toNode))
  }

  trait StatisticsAggFunctions {

    def corr[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.Corr, Seq(c1.toNode, c2.toNode))

    def covarPop[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.CovarPop, Seq(c1.toNode, c2.toNode))

    def covarSamp[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.CovarSamp, Seq(c1.toNode, c2.toNode))

    def regrAvgX[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrAvgX, Seq(c1.toNode, c2.toNode))

    def regrAvgY[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrAvgY, Seq(c1.toNode, c2.toNode))

    def regrCount[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Long],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Long, R]) =
      AggFuncRep[Long](AggLibrary.RegrCount, Seq(c1.toNode, c2.toNode))

    def regrIntercept[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrIntercept, Seq(c1.toNode, c2.toNode))

    def regrR2[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrR2, Seq(c1.toNode, c2.toNode))

    def regrSlope[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrSlope, Seq(c1.toNode, c2.toNode))

    def regrSxx[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrSxx, Seq(c1.toNode, c2.toNode))

    def regrSxy[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrSxy, Seq(c1.toNode, c2.toNode))

    def regrSyy[P1, P2, R](c1: Rep[P1], c2: Rep[P2])(
        implicit tm: JdbcType[Double],
        om: OptionMapperDSL.arg[Double, P1]#arg[Double, P2]#to[Double, R]) =
      AggFuncRep[Double](AggLibrary.RegrSyy, Seq(c1.toNode, c2.toNode))

    def stdDev[T](c: Rep[Option[T]])(implicit tm: JdbcType[T], tm1: Numeric[T]) =
      AggFuncRep[T](AggLibrary.StdDev, Seq(c.toNode))

    def stdDevPop[T](c: Rep[Option[T]])(implicit tm: JdbcType[T], tm1: Numeric[T]) =
      AggFuncRep[T](AggLibrary.StdDevPop, Seq(c.toNode))

    def stdDevSamp[T](c: Rep[Option[T]])(implicit tm: JdbcType[T], tm1: Numeric[T]) =
      AggFuncRep[T](AggLibrary.StdDevSamp, Seq(c.toNode))

    def variance[T](c: Rep[Option[T]])(implicit tm: JdbcType[T], tm1: Numeric[T]) =
      AggFuncRep[T](AggLibrary.Variance, Seq(c.toNode))

    def varPop[T](c: Rep[Option[T]])(implicit tm: JdbcType[T], tm1: Numeric[T]) =
      AggFuncRep[T](AggLibrary.VarPop, Seq(c.toNode))

    def varSamp[T](c: Rep[Option[T]])(implicit tm: JdbcType[T], tm1: Numeric[T]) =
      AggFuncRep[T](AggLibrary.VarSamp, Seq(c.toNode))
  }

  trait OrderedSetAggFunctions {
    def mode() = OrderedAggFuncRep(AggLibrary.Mode, Nil)

    def percentileCont(f: Double) =
      OrderedAggFuncRep.withTypes[Double, Double](AggLibrary.PercentileCont, Seq(LiteralNode(f)))

    def percentileCont(f: Seq[Double])(implicit tm: JdbcType[Seq[Double]]) =
      OrderedAggFuncRep.withTypes[Double, Seq[Double]](AggLibrary.PercentileCont, Seq(LiteralNode(tm, f)))
    def percentileDisc(f: Double) = OrderedAggFuncRep(AggLibrary.PercentileDisc, Seq(LiteralNode(f)))

    /** NOTES: to use it correctly, you need specify the [T] by manual like this: {{{ percentileDisc[String](Seq(0.5,0.3)).within(t.name) }}} */
    def percentileDisc[T](f: Seq[Double])(implicit tm: JdbcType[Seq[Double]], tm1: JdbcType[T], tm2: JdbcType[Seq[T]]) =
      OrderedAggFuncRep.withTypes[T, Seq[T]](AggLibrary.PercentileDisc, Seq(LiteralNode(tm, f)))
  }

  trait HypotheticalSetAggFunctions {

    def rank[T](v: T)(implicit tm: JdbcType[T]) =
      OrderedAggFuncRep.withTypes[Any, Long](AggLibrary.Rank, Seq(LiteralNode(tm, v)))

    def denseRank[T](v: T)(implicit tm: JdbcType[T]) =
      OrderedAggFuncRep.withTypes[Any, Long](AggLibrary.DenseRank, Seq(LiteralNode(tm, v)))

    def percentRank[T](v: T)(implicit tm: JdbcType[T]) =
      OrderedAggFuncRep.withTypes[Any, Double](AggLibrary.PercentRank, Seq(LiteralNode(tm, v)))

    def cumeDist[T](v: T)(implicit tm: JdbcType[T]) =
      OrderedAggFuncRep.withTypes[Any, Double](AggLibrary.CumeDist, Seq(LiteralNode(tm, v)))
  }
}

object AggFuncSupport extends AggFuncSupport with PostgresProfile {
  val GeneralAggFunctions = new GeneralAggFunctions {}
  val StatisticsAggFunctions = new StatisticsAggFunctions {}
  val OrderedSetAggFunctions = new OrderedSetAggFunctions {}
  val HypotheticalSetAggFunctions = new HypotheticalSetAggFunctions {}
}
