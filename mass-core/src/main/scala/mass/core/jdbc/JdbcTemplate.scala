/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.jdbc

import java.sql.{Connection, ResultSet, SQLException}

import helloscala.common.Configuration
import javax.sql.DataSource

import scala.annotation.varargs

trait JdbcTemplate {

  // 数据库源
  val dataSource: DataSource

  /**
   * 使用事物来执行 function（ func 内可包含多个数据库操作函数)
   * @param func 要执行的函数
   * @tparam R 返回值类型
   * @return
   */
  def withTransaction[R](func: Connection => R): R

  /**
   * 执行单条SQL语句
   * @param sql
   * @return true: 返回值为ResultSet，false: 其它
   */
  def execute(sql: String): Boolean

  /**
   * 执行批量修改语句的Java API版
   * @param sql SQL语句
   * @param argsList 批量数据
   * @return
   */
  def batchUpdate(sql: String, argsList: java.util.Collection[java.util.Collection[Object]]): Array[Int]

  /**
   * 执行修改
   * @param sql SQL语句
   * @return 受影响的数据库行数量
   */
  def update(sql: String): Int

  /**
   * 通过参数执行修改
   * @param sql SQL语句
   * @param args 参数
   * @param connection JDBC连接对象
   * @return 受影响的数据库行数量
   */
  def update(sql: String, args: Iterable[Any])(implicit connection: Connection = JdbcTemplate.EmptyConnection): Int

  def javaUpdate(sql: String, args: java.util.Collection[Object]): Int =
    javaUpdate(sql, args, JdbcTemplate.EmptyConnection)

  def javaUpdate(sql: String, args: java.util.Collection[Object], conn: Connection): Int

  /**
   * 通过参数执行批量修改
   * @param sql SQL语句
   * @param argsList 参数
   * @param connection JDBC连接对象
   * @return 受影响的数据库行数量
   */
  def updateBatch(sql: String, argsList: Iterable[Iterable[Any]])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Array[Int]

  /**
   * 通过命名参数执行修改
   * @param sql SQL语句
   * @param args 命名参数
   * @param connection 隐式参数：JDBC Connection 对象
   * @return 受影响的数据库行数量
   */
  def namedUpdate(sql: String, args: Map[String, Any])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Int

  /**
   * 通过命名参数执行批量修改
   * @param sql SQL语句
   * @param argsList 命名参数
   * @param connection 隐式参数：JDBC Connection 对象
   * @return 受影响的数据库行数量
   */
  def namedUpdateBatch(sql: String, argsList: Iterable[Map[String, Any]])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Array[Int]

  /**
   * 获取匹配条件的记录行数量 Java API
   * @param sql SQL语句
   * @param args 参数
   * @return 匹配条件的记录行数量
   */
  @varargs
  def count(sql: String, args: Any*): Long

  /**
   * 获取匹配条件的记录行数量
   * @param sql SQL语句
   * @param args 参数
   * @param connection 隐式参数：JDBC Connection 对象
   * @return 匹配条件的记录行数量
   */
  def size(sql: String, args: Seq[Any])(implicit connection: Connection = JdbcTemplate.EmptyConnection): Long

  /**
   * 通过命名参数获取匹配条件的记录行数量
   * @param sql SQL语句
   * @param args 参数
   * @param connection 隐式参数：JDBC Connection 对象
   * @return 匹配条件的记录行数量
   */
  def namedSize(sql: String, args: Map[String, Any])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Long

  def listForMap(sql: String, args: Seq[Any])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): List[Map[String, Object]]

  def namedListForMap(sql: String, args: Map[String, Any])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): List[Map[String, Object]]

  def listForObject[R](sql: String, args: Seq[Any], rowMapper: ResultSet => R)(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): List[R]

  def namedListForObject[R](sql: String, args: Map[String, Any], rowMapper: ResultSet => R)(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): List[R]

  def findForMap(sql: String, args: Seq[Any])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Option[Map[String, Object]]

  def namedFindForMap(sql: String, args: Map[String, Any])(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Option[Map[String, Object]]

  def findForObject[R](sql: String, args: Seq[Any], rowMapper: ResultSet => R)(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Option[R]

  def namedFindForObject[R](sql: String, args: Map[String, Any], rowMapper: ResultSet => R)(
      implicit connection: Connection = JdbcTemplate.EmptyConnection): Option[R]

  /**
   * 执行JDBC SQL语句。externalConn == null时，此函数将执行事物。否则由外部调用函数控制
   *
   * @param externalConn 数据库连接对象。传入null时将从 dataSource 中新获得一个连接，非null时直接得用传入的连接
   * @param pscFunc      PreparedStatement创建函数
   * @param actionFunc   PreparedStatement调用动作，此函数包含整个的ResultSet结果获取动作
   * @param useTransaction 是否使用事物
   * @tparam R 返回值类型
   * @return 成功执行SQL语句，并返回 actionFunc 执行后的结果。失败将抛出异常
   */
  @throws[SQLException]("数据库操作错误")
  def execute[R](
      externalConn: Connection,
      pscFunc: ConnectionPreparedStatementCreator,
      actionFunc: PreparedStatementAction[R],
      useTransaction: Boolean): R

}

object JdbcTemplate {

  val EmptyConnection: Connection = null

  def apply(dataSource: DataSource, configuration: Configuration): JdbcTemplate =
    apply(
      dataSource,
      configuration.getOrElse[Boolean]("useTransaction", true),
      configuration.getOrElse[Boolean]("ignoreWarnings", true),
      configuration.getOrElse[Boolean]("allowPrintLog", true)
    )

  def apply(
      dataSource: DataSource,
      useTransaction: Boolean = true,
      ignoreWarnings: Boolean = true,
      allowPrintLog: Boolean = false
  ): JdbcTemplate =
    new JdbcTemplateImpl(dataSource, useTransaction, ignoreWarnings, allowPrintLog)

}
