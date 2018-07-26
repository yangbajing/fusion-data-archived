package mass.connector.sql

import com.zaxxer.hikari.HikariDataSource
import mass.core.jdbc.JdbcUtils

object TestSchema {

  lazy val postgres: HikariDataSource = JdbcUtils.createHikariDataSource(
    "poolName" -> "postgres",
    "maximumPoolSize" -> "2",
    "dataSourceClassName" -> "org.postgresql.ds.PGSimpleDataSource",
    "dataSource.serverName" -> "localhost",
    "dataSource.portNumber" -> "5432",
    "dataSource.databaseName" -> "massdata",
    "dataSource.user" -> "massdata",
    "dataSource.password" -> "massdata"
  )

  lazy val mysql: HikariDataSource = JdbcUtils.createHikariDataSource(
    "poolName" -> "mysql",
    "maximumPoolSize" -> "2",
    "jdbcUrl" -> "jdbc:mysql://localhost:3306/massdata?useSSL=false&characterEncoding=utf8",
    "username" -> "massdata",
    "password" -> "Massdata.2018",
    "dataSource.cachePrepStmts" -> "true",
    "dataSource.prepStmtCacheSize" -> "250",
    "dataSource.prepStmtCacheSqlLimit" -> "2048"
  )

}
