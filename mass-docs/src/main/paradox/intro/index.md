# 概述

**MassData 数据平台提供了强大的提取、转换和加载（ETL）功能，基于Akka提供了无中心化的集群处理能力。** MassData
基于 Akka 框架构建。

## 系统、组件

**MassData** 由以下主要系统、组件组成：

- @ref[**数据连接器**](../connector/index.md)：基于 <a href="https://doc.akka.io/docs/akka/current/stream/index.html?language=scala" target="_blank">Akka Stream</a>，Massdata提供各种数据源的连接器。包括：读取、转换、写入等功能。Akka Stream 提供了功能强大的反应式流处理，Massdata数据连接器构建在 Akka Stream 之上。可提供批量、流式ETL数据处理。
- @ref[**任务调度**](../job/index.md)：MassData提供完善的任务调度功能，支持间隔调度和日历调度，支持任务依赖控制。
- @ref[**mass-rdi（反应式数据处理）**](../rdi/index.md)：提供数据处理业务所需支持的各种数据模拟，即可作为单独的程序执行，也可作为组件供引擎子系统调用执行。
- @ref[**Broker**](../broker/core.md)：解析业务流程，执行每个任务（子任务）的所有阶段业务逻辑，包括通用阶段（如日志、数据库操作、消息通知……），以及业务阶段（如文件采集等）。
- @ref[**Broker Leader-协调模块**](../broker/leader.md)：加载业务（数据）处理流程文件，生成任务并对任务进行调度，对调度资源和策略进行管理。
- @ref[**业务编排系统模块**](../console/orchestration/orchestration.md)：生成业务（数据）处理流程文件。
- @ref[**监、管系统**](../console/console/console.md)：对系统运行状况、任务执行情况进行监查，并可管理系统。
- **元数据管理系统**
- **数据治理**

基于 Akka Cluster 和 Akka Cluster role，各子系统以不同的角色存在于 **MassData** ，子系统之间基于 Akka/Akka Cluster 实现服务发现、
注册，消息通信，并提供丰富的引擎路由和负载均衡能力。

## 使用对象

**MassData** 产品最终使用对象为：

- 开发人员：根据方案制定的标准，格式，二次开发手册，进行业务开发。
- 测试人员：使用产品提供的工具，对开发人员的开发成果进行验证。
- 运维人员：使用产品加载开发人员提供的业务或者自行开发的业务。

## 关键技术

### 主要技术

- [Scala](http://scala-lang.org/)：主力开发语言
- [Akka](https://akka.io/)：整个平台基于 Akka/Akka Cluster/Akka HTTP 构建，使用无中心节点的集群方式。每个子系统将分配一类集群角色。
- [HTML5](https://developer.mozilla.org/zh-CN/docs/Web/Guide/HTML/HTML5)：基于 mxgraph 提供图形化的业务编排能力

### 特性

- 流式ELT（数据处理），基于 Akka Stream 平台提供流式数据处理能力，对于大数量使用 Kafka 做数据缓冲。
  [Alpakka](https://github.com/akka/alpakka) 提供了大多数据源的流式读、写能力。同时，系统基于 Akka Stream 也可以很
  方便地集成其它数据源。提供高性能。
- 多实例、多集群、容错：每个子系统都采用集群方式（通过集群类不同的角色进行区分）部署。提供高可用、小部分节点挂掉不至造成
  整个平台不可服务。
- 可监控：平台提供完善的监控点，可快速定位系统故障，便于排查问题。
- 可扩展：模块式的系统架构，各子系统提供丰富的二次开发接口。采集组件可扩展，用户可方便的扩展系统的采集、数据转换、存储能力。
- 图形化业务编排：基于 HTML5 技术提供易用的图形化业务编排工具。
- 仿真测试：提供完整的仿真测试能力。在关键行业或生产环境特殊情况下，在开始和运维时可快捷的建立一个类生产的仿真测试环境。

### 要求和限制

**操作系统**

- 服务器：Linux x64（Ubuntu 16.04+ RHEL/CentOS 7+）
- 客户端：IE11+ / Chrome / Firefox
