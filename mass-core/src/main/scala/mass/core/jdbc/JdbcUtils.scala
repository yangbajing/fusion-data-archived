/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.jdbc

import java.sql._
import java.time._
import java.util.{Objects, Properties}

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import helloscala.common.Configuration
import helloscala.common.util.{StringUtils, TimeUtils}

import scala.collection.{immutable, mutable}

object JdbcUtils extends StrictLogging {

  def columnLabels(metadata: ResultSetMetaData): immutable.IndexedSeq[String] =
    (1 to metadata.getColumnCount).map(i => Option(metadata.getColumnLabel(i)).getOrElse(metadata.getColumnName(i)))

  @throws[SQLException]("if thrown by the JDBC API")
  def getResultSetValue(rs: ResultSet, index: Int): AnyRef = {
    val obj = rs.getObject(index)
    val className: String = if (obj == null) null else obj.getClass.getName

    obj match {
      case null =>
        null
      case blob: Blob =>
        blob.getBytes(1, blob.length().toInt)
      case clob: Clob =>
        clob.getSubString(1, clob.length().toInt)
      case _ if "oracle.sql.TIMESTAMP" == className || "oracle.sql.TIMESTAMPTZ" == className =>
        rs.getTimestamp(index)
      case _ if className.startsWith("oracle.sql.DATE") =>
        val metaDataClassName = rs.getMetaData.getColumnClassName(index)
        if ("java.sql.Timestamp" == metaDataClassName || "oracle.sql.TIMESTAMP" == metaDataClassName)
          rs.getTimestamp(index)
        else
          rs.getDate(index)
      case _: Date if "java.sql.Timestamp" == rs.getMetaData.getColumnClassName(index) =>
        rs.getTimestamp(index)
      case other =>
        other
    }
  }

  def resultSetToMap(rs: ResultSet): Map[String, Object] = {
    val metaData = rs.getMetaData
    (1 to metaData.getColumnCount).map { column =>
      val label = metaData.getColumnLabel(column)
      label -> getResultSetValue(rs, column) //rs.getObject(label)
    }.toMap
  }

  /**
   * 将所有 [TAG]name 命名参数替换成 ?
   * @param sql 采用命名参数编写的SQL语句
   * @param TAG 命名参数前缀，默认为 '?'
   * @return (转换后SQL语句，提取出的参数和索引)，索引从1开始编号
   */
  def namedParameterToQuestionMarked(sql: String, TAG: Char = '?'): (String, Map[String, Int]) = {
    val sqlBuf = new java.lang.StringBuilder()
    var paramBuf = new java.lang.StringBuilder()
    val params = mutable.Map.empty[String, Int]
    var idx = 0
    var isName = false
    sql.foreach {
      case TAG =>
        sqlBuf.append('?')
        isName = true
      case c @ (',' | ')') if isName =>
        sqlBuf.append(c)
        idx += 1
        params += (paramBuf.toString.trim -> idx)
        paramBuf = new java.lang.StringBuilder()
        isName = false
      case c if isName =>
        paramBuf.append(c)
      case c =>
        sqlBuf.append(c)
    }
    (sqlBuf.toString, params.toMap)
  }

  def preparedStatementCreator(sql: String, namedSql: String = ""): ConnectionPreparedStatementCreator =
    new ConnectionPreparedStatementCreatorImpl(sql, namedSql)

  def preparedStatementAction[R](args: Iterable[Any], func: PreparedStatementAction[R]): PreparedStatementAction[R] =
    new PreparedStatementActionImpl(args, func)

  def preparedStatementActionUseUpdate(args: Iterable[Any]): PreparedStatementAction[Int] =
    new PreparedStatementActionImpl(args, pstmt => {
      setStatementParameters(pstmt, args)
      pstmt.executeUpdate()
    })

  def preparedStatementActionUseUpdate(
      args: Map[String, Any],
      paramIndex: Map[String, Int]): PreparedStatementAction[Int] =
    new PreparedStatementActionImpl(args, pstmt => {
      for ((param, index) <- paramIndex) {
        setParameter(pstmt, index, args(param))
      }
      pstmt.executeUpdate()
    })

  def preparedStatementActionUseBatchUpdate(
      argsList: Iterable[Iterable[Any]]): PreparedStatementAction[scala.Array[Int]] =
    new PreparedStatementActionImpl(argsList, pstmt => {
      for (args <- argsList) {
        setStatementParameters(pstmt, args)
        pstmt.addBatch()
      }
      pstmt.executeBatch()
    })

  def preparedStatementActionUseBatchUpdate(
      argsList: Iterable[Map[String, Any]],
      paramIndex: Map[String, Int]): PreparedStatementAction[scala.Array[Int]] =
    new PreparedStatementActionImpl(argsList, pstmt => {
      for (args <- argsList) {
        for ((param, index) <- paramIndex) {
          setParameter(pstmt, index, args(param))
        }
        pstmt.addBatch()
      }
      pstmt.executeBatch()
    })

  def setStatementParameters(
      pstmt: PreparedStatement,
      args: Map[String, Any],
      paramIndex: Map[String, Int]): PreparedStatement = {
    for ((param, index) <- paramIndex) {
      setParameter(pstmt, index, args(param))
    }
    pstmt
  }

  def setStatementParameters(pstmt: PreparedStatement, args: Iterable[Any]): PreparedStatement = {
    var i = 0
    for (arg <- args) {
      i += 1
      setParameter(pstmt, i, arg)
    }
    pstmt
  }

  def setParameter(pstmt: PreparedStatement, i: Int, arg: Any): Unit = {
    val obj = arg match {
      case ldt: LocalDateTime => TimeUtils.toSqlTimestamp(ldt)
      case ld: LocalDate      => TimeUtils.toSqlDate(ld)
      case t: LocalTime       => TimeUtils.toSqlTime(t)
      case zdt: ZonedDateTime => TimeUtils.toSqlTimestamp(zdt)
      case ist: Instant       => TimeUtils.toSqlTimestamp(ist)
      case _                  => arg
    }
    pstmt.setObject(i, obj)
  }

  def closeStatement(stmt: Statement): Unit =
    if (stmt ne null) {
      try stmt.close()
      catch {
        case ex: SQLException =>
          logger.trace("Could not close JDBC Statement", ex)
        case ex: Throwable =>
          // We don't trust the JDBC driver: It might throw RuntimeException or Error.
          logger.trace("Unexpected exception on closing JDBC Statement", ex)
      }
    }

  def closeResultSet(rs: ResultSet): Unit =
    if (rs != null) {
      try rs.close()
      catch {
        case ex: SQLException =>
          logger.trace("Could not close JDBC ResultSet", ex)
        case ex: Throwable =>
          // We don't trust the JDBC driver: It might throw RuntimeException or Error.
          logger.trace("Unexpected exception on closing JDBC ResultSet", ex)
      }
    }

  def closeConnection(con: Connection): Unit =
    if (con != null) {
      try con.close()
      catch {
        case ex: SQLException =>
          logger.error("Could not close JDBC Connection", ex)
        case ex: Throwable =>
          // We don't trust the JDBC driver: It might throw RuntimeException or Error.
          logger.error("Unexpected exception on closing JDBC Connection", ex)
      }
    }

  def isString(sqlType: Int): Boolean =
    Types.VARCHAR == sqlType || Types.VARCHAR == Types.CHAR || Types.VARCHAR == Types.LONGNVARCHAR ||
      Types.VARCHAR == Types.LONGVARCHAR || Types.VARCHAR == Types.NCHAR || Types.VARCHAR == Types.NVARCHAR

  def isNumeric(sqlType: Int): Boolean =
    Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType || Types.DOUBLE == sqlType ||
      Types.FLOAT == sqlType || Types.INTEGER == sqlType || Types.NUMERIC == sqlType || Types.REAL == sqlType ||
      Types.SMALLINT == sqlType || Types.TINYINT == sqlType

  /**
   * 从SQL结果元数据中获取列表。将首先通过 label 获取，若 label 不存在再从 name 获取
   * @param resultSetMetaData SQL结果元数据
   * @param columnIndex 列索引，从1开始
   * @return 列名
   */
  def lookupColumnName(resultSetMetaData: ResultSetMetaData, columnIndex: Int): String = {
    val name = resultSetMetaData.getColumnLabel(columnIndex)
    if (StringUtils.isEmpty(name)) resultSetMetaData.getColumnName(columnIndex) else name
  }

  def execute[R](
      pscFunc: ConnectionPreparedStatementCreator,
      actionFunc: PreparedStatementAction[R],
      ignoreWarnings: Boolean = true,
      allowPrintLog: Boolean = true,
      useTransaction: Boolean = false,
      autoClose: Boolean = false
  )(implicit con: Connection): R = {
    assert(Objects.nonNull(con), "con: Connection must not be null")
    assert(Objects.nonNull(pscFunc), "Connection => PreparedStatement must not be null")
    assert(Objects.nonNull(actionFunc), "PreparedStatement => R must not be null")

    var pstmt: PreparedStatement = null
    val isAutoCommit = con.getAutoCommit
    var commitSuccess = false
    var beginTime: Instant = null
    try {
      if (autoClose && useTransaction) {
        con.setAutoCommit(false)
      }

      if (allowPrintLog) {
        beginTime = Instant.now()
      }

      val connection = con
      pstmt = pscFunc.apply(connection)
      val result = actionFunc.apply(pstmt)
      JdbcUtils.handleWarnings(ignoreWarnings, allowPrintLog, pstmt)
      commitSuccess = true
      result
    } catch {
      case sqlEx: SQLException =>
        //        if (logger.underlying.isDebugEnabled) {
        //          val metaData = pstmt.getParameterMetaData
        //          val parameterTypes = (1 to metaData.getParameterCount).map(idx => metaData.getParameterTypeName(idx))
        //          handleSqlLogs(beginTime, parameterTypes, pscFunc, actionFunc)
        //        }
        throw sqlEx
    } finally {
      val parameterTypes =
        try {
          if (allowPrintLog) {
            val metaData = pstmt.getParameterMetaData
            (1 to metaData.getParameterCount).map(idx => metaData.getParameterTypeName(idx))
          } else
            Nil
        } catch {
          case e: Exception =>
            handleSqlLogs(beginTime, Nil, pscFunc, actionFunc)
            logger.warn("获取parameterTypes异常", e)
            Nil
        }

      closeStatement(pstmt)

      if (autoClose) {
        if (useTransaction) {
          try {
            if (commitSuccess) {
              con.commit()
            } else {
              con.rollback()
            }
          } catch {
            case ex: Exception =>
              logger.error("提交或回滚事物失败", ex)
          }
          con.setAutoCommit(isAutoCommit)
        }
        JdbcUtils.closeConnection(con)
      }

      if (allowPrintLog) {
        handleSqlLogs(beginTime, parameterTypes, pscFunc, actionFunc)
      }
    }
  }

  def handleSqlLogs(
      beginTime: Instant,
      parameterTypes: Seq[String],
      pscFunc: ConnectionPreparedStatementCreator,
      actionFunc: PreparedStatementAction[_]): Unit = {
    val endTime = Instant.now()
    val dua = java.time.Duration.between(beginTime, endTime)
    val sql = pscFunc match {
      case pscFuncImpl: ConnectionPreparedStatementCreatorImpl =>
        pscFuncImpl.getSql
      case _ => ""
    }

    var dumpParameters = ""
    if (parameterTypes.nonEmpty) {
      val parameters = actionFunc match {
        case actionFuncImpl: PreparedStatementActionImpl[_] =>
          parameterTypes.zip(actionFuncImpl.args).map {
            case (paramType, value) => s"\t\t$paramType: $value"
          }
        case _ =>
          parameterTypes.map(paramType => s"\t\t$paramType:")
      }
      dumpParameters = "\n" + parameters.mkString("\n")
    }

    logger.info(s"[$dua] $sql $dumpParameters")
  }

  def handleWarnings(ignoreWarnings: Boolean, allowPrintLog: Boolean, stmt: Statement): Unit =
    if (ignoreWarnings) {
      if (allowPrintLog) {
        var warningToLog = stmt.getWarnings
        while (warningToLog != null) {
          logger.warn(
            "SQLWarning ignored: SQL state '" + warningToLog.getSQLState + "', error code '" + warningToLog.getErrorCode + "', message [" + warningToLog.getMessage + "]")
          warningToLog = warningToLog.getNextWarning
        }
      }
    } else {
      handleWarnings(stmt.getWarnings)
    }

  @inline
  @throws[SQLWarning]
  protected def handleWarnings(warning: SQLWarning): Unit = if (warning != null) throw warning

  def createHikariDataSource(data: (String, String), datas: (String, String)*): HikariDataSource = {
    val props = new Properties()
    props.put(data._1, data._2)
    for ((key, value) <- datas) {
      props.put(key, value)
    }
    createHikariDataSource(props)
  }

  def createHikariDataSource(data: Map[String, String]): HikariDataSource = {
    val props = new Properties()
    for ((key, value) <- data) {
      props.put(key, value)
    }
    createHikariDataSource(props)
  }

  private val REMOVED_KEYS = List("useTransaction",
                                  "ignoreWarnings",
                                  "allowPrintLog",
                                  "maxConnections",
                                  "numThreads",
                                  "registerMbeans",
                                  "queueSize")

  @inline def createHikariDataSource(config: Configuration): HikariDataSource =
    createHikariDataSource(config.getProperties(null))

  @inline def createHikariDataSource(config: Config): HikariDataSource =
    createHikariDataSource(Configuration(config))

  @inline def createHikariDataSource(props: Properties): HikariDataSource =
    createHikariDataSource(new HikariConfig(REMOVED_KEYS.foldLeft(props) { (props, key) =>
      props.remove(key); props
    }))

  def createHikariDataSource(config: HikariConfig): HikariDataSource =
    new HikariDataSource(config)

}
