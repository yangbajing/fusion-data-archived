# 数据连接器

基于 <a href="https://doc.akka.io/docs/akka/current/stream/index.html?language=scala" target="_blank">Akka Stream</a>，Massdata提供各种数据源的连接器。包括：读取、转换、写入等功能。
Akka Stream 提供了功能强大的反应式流处理，Massdata数据连接器构建在 Akka Stream 之上。可提供批量、流式ETL数据处理。

已有数据连接器：

- HDFS
- HBase
- JDBC: PostgreSQL、MySQL、Oracle、MS SQL Server、达梦数据库、GBase
- Elasticsearch
- Cassandra
- MongoDB
- FTP/sFTP
- File: txt、csv
- XML
- JSON
- Excel(xls/xlsx)

同时，基于Akka Stream良好的扩展性和 msdata 平台的模块化设计，可以很方便的添加新的数据连接器来支持各种数据源。

## 通用数据处理模式

```
source ~> flow....flow ~> sink
```

- source: 数据源
- flow: 处理流程，可有多个。
- sink: 数据汇，收集数据并进行操作。source和sink可以有不同的DataSource，这样就可以实现ETL/ELT等操作。
