

## Quartz Table 

| table                     | 说明                                                         |
| ------------------------- | ------------------------------------------------------------ |
| qrtz_job_details          | 存储每一个已配置的 Job 的详细信息                            |
| qrtz_triggers             | 存储已配置的 Trigger 的信息                                  |
| qrtz_cron_triggers        | 存储 Cron Trigger，包括 Cron 表达式和时区信息                |
| qrtz_simple_triggers      | 存储简单的 Trigger，包括重复次数，间隔，以及已触的次数       |
| qrtz_paused_triggers_grps | 存储已暂停的 Trigger 组的信息                                |
| qrtz_blob_triggers        | 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候) |
| qrtz_scheduler_state      | 存储少量的有关 Scheduler 的状态信息，和别的 Scheduler 实例（假如是用于一个集群中） |
| qrtz_calendars            | 以 Blob 类型存储 Quartz 的 Calendar 信息                     |
| qrtz_fired_triggers       | 存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息 |
| qrtz_locks                | 存储程序的非观锁的信息(假如使用了悲观锁)                     |
| qrtz_simprop_triggers     |                                                              |
