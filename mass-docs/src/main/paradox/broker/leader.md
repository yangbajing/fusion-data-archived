# Broker Leader （协调者）

leader：Sea集群将自动选择一个节点作为 Leader，Leader 将负责调度所有数据业务（job）。解析每个业务的业务定义文件获取每个业务的任务依赖关系。并根据各Broker节点的负载情况来分配任务。


- leader 所在节点是否分配 task ？

