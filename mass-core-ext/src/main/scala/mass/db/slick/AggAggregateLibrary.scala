package mass.db.slick

import slick.ast.Library.SqlAggregateFunction

object AggAggregateLibrary {
  val ArrayAgg = new SqlAggregateFunction("array_agg")
}
