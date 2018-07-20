# 工作流

工作流包含两部份：

- connectors: 连接器配置，定义工作流需要使用的数据源连接器
- graph: 流程图（DAG）定义

工作流使用XML格式进行定义，示例配置如下：

@@snip [pg2mysql-graph.xml](code/workflow/pg2mysql-graph.xml)

## Connectors

见 @ref:[数据连接器](../connector/index.md)

## Graph

类似Akka Stream的数据流图，这里是一个DAG，定义了ETL的处理流程。整个graph必需为闭合状态才有效并可被执行，不然RDP将分析workflow抛出异常。

- source: 数据来源，它只有一个out。用来决定从哪里获得数据流。
- flow: 数据流元素将流经的处理过程。flow至少有一个in和out。
- sink: 数据收集汇，它只有一个in。用来决定数据流最终被被存储到哪里。

@@snip [pg2mysql-graph.xml](../../../../../mass-rdp-core/src/test/resources/mass/core/workflow/etl/EtlWorkflowTest.xml) { #graph_example }

**graph.source**

指Source要引用的connector，若在当前workflow配置文件内找不到connector定义，则从系统全局的connectors库寻找。

**graph.flows**

flow是实际对数据进行各种转换、过滤操作的阶段。

**graph.flows.flow.script**

script，配置flow怎样处理数据的脚本。包含以下主要功能：

- **type**：可以是 Scala、Java、Javascript、Python代码
- **src**：或者指定`jar`、`zip`可执行程序包或`.scala`, `.py`、`.js`可执行代码文件文件路径格式支持：`file://`, `http://`,
  `classpath://`, `ftp://`, `hdfs://` 若指定了package，则 script 段内嵌代码无效。

