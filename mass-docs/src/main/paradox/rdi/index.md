# 反应式数据处理（mass-rdi）

**mass-rdi** 的数据处理部分将基于 Reactive Extension 的思想进行设计，采集 **Akka Stream** 做为实现框架，基于 **Alpakka** 进行落地实现。

## 特性

**有效提供工作效率**

mass-rdp可数据工程师**提高**对复杂数据的处理能力，**提高**对各种数据来源、数据格式的数据的入库效率，**提高**对业务快速变化的响应。

**丰富的数据源支持**

mass-rdi 支持结构化、非结构化数据处理，支持对文本格式数据（csv、XML、JSON等）处理。支持传统关系型数据库：PostgreSQL、MySQL、Oracle、SQL Server，
支持NoSQL数据存储：HDFS、Hive、HBase、Cassandra、MongoDB。同时，mass-rdp还支持国产数据库：达梦数据库、GBase。

**反应式架构设计**

mass-rdi 采用 ReactiveStreams（反映式流处理） 设计数据处理功能（ETL/ELT），拥有高性、能吞吐、可扩展、容错、回压（流速控制）等特性。

**适用性**

mass-rdi 做为一款全方位的数据处理工具，适用于多种业务产场。

- 数据采集
- 数据清洗
- 任务调度管理

mass-rdi 可以单独使用，也可以集成到其它业务系统里面。

**可扩展**

mass-rdi 提供了完善的接口和丰富的扩展点，支持对 mass-rdi 进行二次开发，或将其嵌入到特定的应用系统中。

@@toc { depth=1 }

@@@ index

* [core](core.md)
* [workflow](workflow.md)
* [extension](extension.md)

@@@
