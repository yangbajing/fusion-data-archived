# 数据处理子系统

**mass** 的数据处理部分将基于 Reactive Extension 的思想进行设计，采集 **Akka Stream** 做为实现框架，
基于 **Alpakka** 进行落地实现。

- [Akka Stream](https://doc.akka.io/docs/akka/current/stream/index.html)：
- [Alpakka](https://developer.lightbend.com/docs/alpakka/current/)：

数据处理组件分两部分：

1. 数据处理程序：可单独或由引擎调用执行的程序
2. 数据转换组件：一个具体的数据处理功能，如：格式化日期、转换大小写等

## 数据处理程序

TODO

## 数据转换组件
