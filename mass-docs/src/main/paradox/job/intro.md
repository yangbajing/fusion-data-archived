# 简介

## 架构

![Job调度架构](../static/SchedulerPlatformArchitecture.svg)

- **应用管理**：提供命令行和Web图形界面客户端，用户可对Job进行配置、管理，启动、停止控制Job，监控系统运行状况。
- **调度控制中心（role: job-control）**：mass-job的核心，Job调度、控制实现。分配Job到各Agent执行……
- **执行器Agent（role: job-agent）**：执行job的Agent节点。一个节点可以同时作为job-agent和job-main。

## Job类型

- **应用程序Job**：Shell、Jar、Python、NodeJS等可执行程序，Job将在一个独立进程或执行引擎集群上运行。通常的应用和业务都推荐使用应用程序Job进行提交。
- **代码Job**：实现了`SchedulerJob`接口的Job，Job将和 MassData 在同一个进程或执行引擎集群上执行，代码Job可做为MassData平台的一个扩展。

*代码Job接口定义如下：*

@@snip [JobItem](../../../../../mass-core/src/main/scala/mass/core/job/SchedulerJob.scala) { #SchedulerJob }

*Job配置（JobItem）属性：*

@@snip [JobItem](../../../../../mass-core/src/main/protobuf/mass/model/job/job.proto) { #JobItem }

## 调度类型

MassData提供完善的Job调度功能，支持简单调度（时间间隔）、日历调度和事件触发三种。同时，调度Job提供 `beforeStart`、`afterStart`、`beforeStart`和`afterStop`回调函数，
用户可据此实现对Job启动、退出时做更自定义操作，如：实现Job依赖等。

- 简单调度（时间间隔）：按指定的时间间隔调度Job，可配置Job将被执行次数（无限或N次）
- 日历调度：使用类似UNIX/Linux Crontab格式的策略进行基于日历时间的调度
- 事件触发：由某个事件触发调度执行。如：某个数据同步Job完成后发出事件通知分析系统对新数据进行分析。

同时，每个Job在执行成功完成后还可以触发多个依赖Job运行，只需要在Job配置时指定需要依赖运行的Job ID即可。

*触发配置（JobTrigger）属性：*

@@snip [JobItem](../../../../../mass-core/src/main/protobuf/mass/model/job/job.proto) { #JobTrigger }

## 创建Job

mass-job 提供多种Job创建方式：

1. REST接口上传zip包，应用可将Job打成zip包后提交到平台待执行。
2. 通过管理界面编辑配置Job，并将需要执行的程序（脚本、Jar包）上传。
3. 通过在线IDE编写代码（实现了`SchedulerJob`接口）。

***Zip包***

Zip包里面需要包含配置文件和可执行程序，配置文件使用 [HOCON](https://github.com/lightbend/config) 格式。每个配置文件代表一个Job。

@@snip [sample.conf](../../../../../mass-job/src/universal/examples/sample-job/sample.conf)

## 其它

### 远程调度

MassData通过SSH支持Job调度支持远程启动程序，要使用此功能需要配置SSL免密码登录（通过shell脚本实现）。
