package mass.connector

import helloscala.common.Configuration
import mass.connector.ConnectorType.ConnectorType

/**
 * 连接类型
 */
object ConnectorType extends Enumeration {
  type ConnectorType = Value
  val JDBC = Value(1, "jdbc")
  val HDFS = Value("hdfs")
  val HIVE = Value("hive")
  val HBase = Value("hbase")
  val CSV = Value("csv")
  val Xlsx = Value("xlsx")
  val Xls = Value("xls")
  val FTP = Value("ftp")
  val Elasticsearch = Value("elasticsearch")
  val MongoDB = Value("mongodb")
  val Cassandra = Value("cassandra")
}

// #Connector
/**
 * Connector
 *  -> SQL, CSV, Excel ……
 *    Connector2(Source) ->
 *                       <-> Flow1, Flow2, .... <-> 算是DataElement
 *  -> Connector2(Sink)
 * 数据连接
 */
trait Connector extends AutoCloseable {

  /**
   * 连接名，由用户设置。在整个应用业务生命周期内应保持唯一。
   */
  def name: String

  /**
   * 连接类型。不同的连接类型具有不同的配置选项，数据存取方式
   */
  def `type`: ConnectorType

  def setting: ConnectorSetting

  def configuration: Configuration = setting.parameters
}
// #Connector
