/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.jdbc

import java.lang.reflect.{Field, Modifier}
import java.sql._
import java.time._
import java.util.{Objects, Properties}

import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import helloscala.common.Configuration
import helloscala.common.util._
import javax.sql.DataSource

import scala.collection.{immutable, mutable}
import scala.reflect.ClassTag

object JdbcUtils extends StrictLogging {
  val BeanIgnoreClass = classOf[BeanIgnore]
  // Check for JDBC 4.1 getObject(int, Class) method - available on JDK 7 and higher

  private val getObjectWithTypeAvailable =
    ClassUtils.hasMethod(classOf[ResultSet], "getObject", classOf[Int], classOf[Class[_]])

  def columnLabels(metadata: ResultSetMetaData): immutable.IndexedSeq[String] =
    (1 to metadata.getColumnCount).map(i => Option(metadata.getColumnLabel(i)).getOrElse(metadata.getColumnName(i)))

  def getResultSetValue(
      rs: ResultSet,
      index: Int,
      requiredType: Class[_],
      defaultTimeZone: ZoneOffset = TimeUtils.ZONE_CHINA_OFFSET): Any = {
    val columnType = rs.getMetaData.getColumnType(index)

    if (requiredType == null) {
      getResultSetValue(rs, index)
    } else if (classOf[String] == requiredType) {
      rs.getString(index)
    } else if (classOf[BigDecimal] == requiredType) {
      rs.getBigDecimal(index)
    } else if (classOf[java.sql.Timestamp] == requiredType) {
      rs.getTimestamp(index)
    } else if (classOf[java.sql.Date] == requiredType) {
      rs.getDate(index)
    } else if (classOf[LocalDate] == requiredType) {
      rs.getDate(index).toLocalDate
    } else if (classOf[LocalTime] == requiredType) {
      rs.getTime(index).toLocalTime
    } else if (classOf[OffsetDateTime] == requiredType) {
      if (Types.BIGINT == columnType) {
        TimeUtils.toOffsetDateTime(rs.getLong(index))
      } else if (Types.INTEGER == columnType) {
        Instant.ofEpochSecond(rs.getInt(index)).atOffset(defaultTimeZone)
      } else {
        rs.getTimestamp(index).toInstant.atOffset(defaultTimeZone)
      }
    } else if (classOf[ZonedDateTime] == requiredType) {
      if (Types.BIGINT == columnType) {
        Instant.ofEpochMilli(rs.getInt(index)).atZone(defaultTimeZone)
      } else if (Types.INTEGER == columnType) {
        Instant.ofEpochSecond(rs.getInt(index)).atZone(defaultTimeZone)
      } else {
        rs.getTimestamp(index).toInstant.atZone(defaultTimeZone)
      }
    } else if (classOf[LocalDateTime] == requiredType) {
      if (Types.BIGINT == columnType) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getInt(index)), defaultTimeZone)
      } else if (Types.INTEGER == columnType) {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(rs.getInt(index)), defaultTimeZone)
      } else {
        rs.getTimestamp(index).toLocalDateTime
      }
    } else if (classOf[java.sql.Time] == requiredType) {
      rs.getTime(index)
    } else if (classOf[scala.Array[Byte]] == requiredType) {
      rs.getBytes(index)
    } else if (classOf[Blob] == requiredType) {
      rs.getBlob(index)
    } else if (classOf[Clob] == requiredType) {
      rs.getClob(index)
    } else if (requiredType.isEnum) {
      rs.getObject(index) match {
        case s: String => s
        case n: Number => NumberUtils.convertNumberToTargetClass(n, classOf[Integer])
        case _         => rs.getString(index)
      }
    } else {
      var value: Any = null
      if (classOf[Boolean] == requiredType || classOf[java.lang.Boolean] == requiredType) {
        value = rs.getBoolean(index)
      } else if (classOf[Byte] == requiredType || classOf[java.lang.Byte] == requiredType) {
        value = rs.getByte(index)
      } else if (classOf[Short] == requiredType || classOf[java.lang.Short] == requiredType) {
        value = rs.getShort(index)
      } else if (classOf[Int] == requiredType || classOf[Integer] == requiredType) {
        value = rs.getInt(index)
      } else if (classOf[Long] == requiredType || classOf[java.lang.Long] == requiredType) {
        value = rs.getLong(index)
      } else if (classOf[Float] == requiredType || classOf[java.lang.Float] == requiredType) {
        value = rs.getFloat(index)
      } else if (classOf[Double] == requiredType || classOf[java.lang.Double] == requiredType || classOf[Number] == requiredType) {
        value = rs.getDouble(index)
      } else {
        // Some unknown type desired -> rely on getObject.
        if (getObjectWithTypeAvailable) {
          try value = rs.getObject(index, requiredType)
          catch {
            case err: AbstractMethodError =>
              logger.debug("JDBC driver does not implement JDBC 4.1 'getObject(int, Class)' method", err)
            case ex: SQLFeatureNotSupportedException =>
              logger.debug("JDBC driver does not support JDBC 4.1 'getObject(int, Class)' method", ex)
            case ex: SQLException =>
              logger.debug("JDBC driver has limited support for JDBC 4.1 'getObject(int, Class)' method", ex)
          }
          //        } else {
          //          // Corresponding SQL types for JSR-310, left up
          //          // to the caller to convert them (e.g. through a ConversionService).
          //          val typeName = requiredType.getSimpleName
          //          value = typeName match {
          //            case "ZonedDateTime" => rs.getTimestamp(index).toInstant.atZone(TimeUtils.ZONE_CHINA_OFFSET)
          //            case "LocalDateTime" => rs.getTimestamp(index).toLocalDateTime
          //            case "LocalDate"     => rs.getDate(index).toLocalDate
          //            case "LocalTime"     => rs.getTime(index).toLocalTime
          //            case _ =>
          //              // Fall back to getObject without type specification, again
          //              // left up to the caller to convert the value if necessary.
          //              getResultSetValue(rs, index)
          //          }
        }
      }

      if (rs.wasNull()) null else value
    }
  }

  /**
   * Retrieve a JDBC column value from a ResultSet, using the most appropriate
   * value type. The returned value should be a detached value object, not having
   * any ties to the active ResultSet: in particular, it should not be a Blob or
   * Clob object but rather a byte array or String representation, respectively.
   * <p>Uses the {@code getObject(index)} method, but includes additional "hacks"
   * to get around Oracle 10g returning a non-standard object for its TIMESTAMP
   * datatype and a {@code java.sql.Date} for DATE columns leaving out the
   * time portion: These columns will explicitly be extracted as standard
   * {@code java.sql.Timestamp} object.
   *
   * @param rs    is the ResultSet holding the data
   * @param index is the column index
   * @return the value object
   * @throws SQLException if thrown by the JDBC API
   * @see java.sql.Blob
   * @see java.sql.Clob
   * @see java.sql.Timestamp
   */
  @throws[SQLException]
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

  private def filterFields(fields: scala.Array[Field]): Map[String, Field] = {
    val result = mutable.Map.empty[String, Field]
    val len = fields.length
    var i = 0
    while (i < len) {
      val field = fields(i)
      val anns = field.getDeclaredAnnotations
      val isInvalid = Modifier.isStatic(field.getModifiers) ||
        anns.exists(ann => ann.annotationType() == BeanIgnoreClass)
      if (!isInvalid) {
        field.setAccessible(true)
        result.put(field.getName, field)
      }
      i += 1
    }
    result.toMap
  }

  def resultSetToBean[T](rs: ResultSet)(implicit ev1: ClassTag[T]): T = resultSetToBean(rs, toPropertiesName = true)

  def resultSetToBean[T](rs: ResultSet, toPropertiesName: Boolean)(implicit ev1: ClassTag[T]): T = {
    val dest = ev1.runtimeClass.newInstance().asInstanceOf[T]
    val cls = dest.getClass
    val fields = filterFields(cls.getDeclaredFields)
    val metaData = rs.getMetaData
    var col = 1
    val columnCount = metaData.getColumnCount
    while (col <= columnCount) {
      var label = metaData.getColumnLabel(col)
      if (toPropertiesName) {
        label = StringUtils.convertUnderscoreNameToPropertyName(label)
      }
      for (field <- fields.get(label)) {
        val requiredType = field.getType
        val value = getResultSetValue(rs, col, requiredType)
        field.set(dest, value)
      }
      col += 1
    }
    dest
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

  def closeDataSource(ds: HikariDataSource): Unit = {
    if (ds != null) {
      try ds.close()
      catch {
        case ex: SQLException =>
          logger.error("Could not close JDBC Connection", ex)
        case ex: Throwable =>
          // We don't trust the JDBC driver: It might throw RuntimeException or Error.
          logger.error("Unexpected exception on closing JDBC Connection", ex)
      }
    }
  }

  def closeDataSource(ds: DataSource): Unit = ds match {
    case hds: HikariDataSource => closeDataSource(hds)
    case _                     => // do nothing
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
