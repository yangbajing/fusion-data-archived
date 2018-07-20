# mass-broker

**mass-broker** 作为 mass 的集群化的核心节点，每一个节点都具备调度、执行的完整能力。同时，部署到任一节点的组件资源（数据
处理组件）都将被自动分发到所有节点。

![mass 引擎架构图](../../static/BrokerAppSystem.png) <br/>*mass 引擎架构图*

Sea引擎分3大部分：

1. engine：执行引擎，接收Broker Leader指派的业务任务（job-task）并解析 task。根据 task 解析后的每个任务步骤（task-step），
   调用相应数据组件进行处理。
2. component repository：保存所有的数据组件，引擎将自动同步组件仓库里的组件到每一个节点。

## engine（engine-worker）

engine是一个逻辑概念，每个 mass-engine 节点可启动一个或多个 engine（Worker），默认为主机CPU核数。（同一个节点同时可执行任务数）

