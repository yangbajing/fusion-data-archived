package mass.connector.sql

import helloscala.common.test.HelloscalaSpec
import org.scalatest.BeforeAndAfterAll

import scala.xml.XML

class SQLConnectorTest extends HelloscalaSpec with BeforeAndAfterAll {
  val postgresConfig =
    """        <connector id="postgres" type="jdbc">
      |            <props>
      |                <prop key="poolName" value="postgres"/>
      |                <prop key="maximumPoolSize" value="2"/>
      |                <prop key="username" value="massdata"/>
      |                <prop key="password" value="massdata"/>
      |                <prop key="dataSourceClassName" value="org.postgresql.ds.PGSimpleDataSource"/>
      |                <prop key="dataSource.serverName" value="localhost"/>
      |                <prop key="dataSource.portNumber" value="5432"/>
      |                <prop key="dataSource.databaseName" value="massdata"/>
      |            </props>
      |        </connector>
      |""".stripMargin
  val mysqlConfig =
    """        <connector id="mysql" type="jdbc">
      |            <props>
      |                <prop key="poolName" value="mysql"/>
      |                <prop key="maximumPoolSize" value="2"/>
      |                <prop key="jdbcUrl">
      |                    <value><![CDATA[jdbc:mysql://127.0.0.1:3306/massdata?useSSL=false&characterEncoding=utf8]]></value>
      |                </prop>
      |                <prop key="username" value="massdata"/>
      |                <prop key="password" value="Massdata.2018"/>
      |            </props>
      |        </connector>
      |""".stripMargin

  var postgresConnector: SQLConnector = _
  var mysqlConnector: SQLConnector = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    val parser = new SQLConnectorParser()

    postgresConnector = parser.parseFromXML(XML.loadString(postgresConfig))
    postgresConnector.setting.parameters.get[Map[String, String]](null).foreach(println)

    mysqlConnector = parser.parseFromXML(XML.loadString(mysqlConfig))
    mysqlConnector.setting.parameters.get[Map[String, String]](null).foreach(println)
  }

  override protected def afterAll(): Unit = {
    if (postgresConnector ne null) {
      postgresConnector.close()
    }
    if (mysqlConnector ne null) {
      mysqlConnector.close()
    }
    super.afterAll()
  }

  "SQLConnector" should {

  }

}
