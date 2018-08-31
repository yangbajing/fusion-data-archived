-- create user massdata nosuperuser encrypted password 'Massdata.2018';
-- create database massdata with owner=massdata template=template1;
set timezone to 'Asia/Chongqing';

-- #ddl-job

drop table if exists public.job_detail;
create table public.job_detail (
  key        varchar(64) primary key,
  data       hstore      not null,
  conf       jsonb       null,
  created_at timestamptz not null
);
comment on column public.job_detail.data
is '传给任务的参数，Map[String, String] 形式的JSON对象';

drop table if exists public.job_trigger;
create table public.job_trigger (
  key          varchar(64)  not null primary key,
  cron_express varchar(255) null,
  duration     bigint       null,
  repeat       int          null,
  start_time   timestamptz  null,
  end_time     timestamptz  null,
  conf         jsonb        null,
  created_at   timestamptz  not null
);
comment on table public.job_trigger
is '调度任务触发配置';
comment on column job_trigger.cron_express
is 'CRON定时任务配置，当此配置设置时，repeat、duration将无效';

drop table if exists public.job_schedule;
create table public.job_schedule (
  id          char(24) primary key,
  detail_key  varchar(64) not null,
  trigger_key varchar(64) not null,
  status      int         not null default 1,
  created_at  timestamptz not null
);
comment on column public.job_schedule.status
is '调度任务闫：0 未启用，1 启用';

drop table if exists public.job_log;
create table public.job_log (
  id                char(24) primary key,
  job_id            char(24), -- FK job_schedule.id
  start_time        timestamptz not null,
  completion_time   timestamptz,
  completion_status int,
  completion_value  text
);

-- #ddl-job

-- #ddl-workflow

drop table if exists public.wf_detail;
create table public.wf_detail (
  name       varchar(128) not null primary key,
  content    text         not null,
  created_at timestamptz
);
comment on column public.wf_detail.content
is '工作流配置（XML）';
comment on column public.wf_detail.created_at
is '创建时间';

-- #ddl-workflow
