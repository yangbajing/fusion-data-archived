# 任务

## 调度类型

mass提供完善的任务调度功能，支持间隔调度和日历调度和更高级的依赖调度3种方式。

- 间隔调度：按指定的时间间隔调度任务，可配置任务将被执行次数（无限或N次）
- 日历调度：使用类似UNIX/Linux Crontab格式的策略进行基于日历时间的调度

任务属性：

1. `startTime: Option[OffsetDateTime]` 设置任务开始时间
0. `endTime: Option[OffsetDateTime]` 设置任务结束时间
0. `repeat: Int` 任务重复次数
0. `duration: Duration` 两次任务之间的间隔时间
0. `cronExpress: String` 类似CRON的基于日历时间的调度，设置 `cronExpress` 后 `repeat` 和 `cronExpress` 将无效

## 任务类型

- 代码任务（实现了SchedulerJob接口的任务，任务将和 mass 在同一个进程或执行引擎集群上执行），代码任务可做为mass平台的一个扩展。
- 应用程序任务（shell、jar等可执行程序，任务将在一个独立进程或执行引擎集群上执行）。通常的应用和业务都推荐使用应用程序任务进行提交。

## 应用程序任务提交方式

mass-scheduler提供两种任务提交方式：

1. REST接口上传zip包，应用可将任务打成zip包后提交到平台待执行。
0. 实现 `SchedulerJob` 接口，将代码打成jar包后放入 mass-scheduler/lib 目录，再通过管理界面启用（扩展mass-scheduler功能）。

## 配置

### DDL (Postgres)

**调用任务DDL**

@@snip [ddl-pg](../_code/pg-ddl.sql) { #ddl-job }
