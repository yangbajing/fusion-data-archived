# 数据连接器

基于 <a href="https://doc.akka.io/docs/akka/current/stream/index.html?language=scala" target="_blank">Akka Stream</a>，Seadata提供各种数据源的连接器。包括：读取、转换、写入等功能。
Akka Stream 提供了功能强大的反应式流处理，Seadata数据连接器构建在 Akka Stream 之上。可提供批量、流式ETL数据处理。

## 通用ETL处理模式

```
source ~> flow ~> sink
```

- source: 数据源
- flow: 处理流程，可有多个。
- sink: 数据汇，收集数据并进行操作。source和sink可以有不同的DataSource，这样就可以实现ETL/ELT等操作。
