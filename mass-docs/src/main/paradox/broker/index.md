# Broker 节点

@@toc { depth=2 }

@@@ index

* [engine/engine](engine/engine.md)
* [leader/leader](leader/leader.md)
* [data/data](data/data.md)

@@@

mass-broker 作为执行节点，每个节点都拥有调度、执行组件、消息通信功能。集群中将启动一个集群单例actor：BrokerLeader 来作为
**协调者**，控制具体的业务调度，所有节点都会通过一个集群单例代理actor：BrokerLeaderProxy。
