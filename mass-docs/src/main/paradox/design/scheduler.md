# 任务调度

## 调度类型

mass提供完善的任务调度功能，支持间隔调度和日历调度和更高级的依赖调度3种方式。

- 间隔调度：按指定的时间间隔调度任务，可配置任务将被执行次数（无限或N次）
- 日历调度：使用类似UNIX/Linux Crontab格式的策略进行基于日历时间的调度
- 依赖调度：在间隔调度和日历调度之上，任务还将依赖某一或N个任务的执行状态决定是否启动

同时，每种调度都拥有些相同的配置属性：

1. `startTime: Option[OffsetDateTime]` 设置任务开始时间
2. `endTime: Option[OffsetDateTime]` 设置任务结束时间

## 任务类型

- 代码任务（实现了SchedulerJob接口的任务，任务将和 mass 在同一个进程或执行引擎集群上执行）
- 应用程序任务（shell、jar等可执行程序，任务将在一个独立进程或执行引擎集群上执行）

## 配置

### DDL (Postgres)

**调用任务DDL**

@@snip [ddl-pg](../_code/pg-ddl.sql) { #ddl-job }
