# 业务文件规范

业务文件规范是指通过业务编排系统配置的 **数据业务文件**（简称 Job）的定义，业务内容使用XML格式进行配置。Job 由如下主要
部分组件：

- Job：业务文件，业务只有一个Job。
- Task：一个业务可以定义一个或多个 Task，Task 可以分支、聚合。Task 采用 DAG（有向无环图）的方式来定义依赖关系。每个 Task
  对应一个 Akka Stream 流处理任务。Task 通过 taskId 在同一个 Job 中唯一标识一个 Task，Task 可选 nextTask 来指定本任务执
  行完后紧接着的向一个任务。当 Task 未指定 nextTask 时则认为此 Task 为Job类的最后一个任务，同一个 Job 中只允许一个 Task
  不指定 nextTask 。
