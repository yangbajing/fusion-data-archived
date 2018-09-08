/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.jdbc

import java.sql.PreparedStatement

@FunctionalInterface
trait PreparedStatementAction[R] {
  def apply(pstmt: PreparedStatement): R
}

class PreparedStatementActionImpl[R](
    val args: Iterable[Any],
    func: PreparedStatementAction[R]
) extends PreparedStatementAction[R] {
  override def apply(pstmt: PreparedStatement): R = func(pstmt)
}
