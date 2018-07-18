set timezone to 'Asia/Chongqing';
select now();
create user massdata with nosuperuser
  replication
  encrypted password 'Massdata.2018';
create database massdata owner = massdata template = template0 encoding = 'UTF-8' lc_ctype = 'zh_CN.UTF-8' lc_collate = 'zh_CN.UTF-8';
\c massdata;
create extension adminpack;
create extension hstore;

----------------------------------------
-- init tables, views, sequences  begin
----------------------------------------

-- #ddl-job start
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
-- #ddl-job end

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

----------------------------------------
-- init tables, views, sequences  end
----------------------------------------

-- change tables, views, sequences owner to massdata
DO $$DECLARE r record;
BEGIN
  FOR r IN SELECT table_name
           FROM information_schema.tables
           WHERE table_schema = 'public'
  LOOP
    EXECUTE 'alter table ' || r.table_name || ' owner to massdata;';
  END LOOP;
END$$;
DO $$DECLARE r record;
BEGIN
  FOR r IN select sequence_name
           from information_schema.sequences
           where sequence_schema = 'public'
  LOOP
    EXECUTE 'alter sequence ' || r.sequence_name || ' owner to massdata;';
  END LOOP;
END$$;
DO $$DECLARE r record;
BEGIN
  FOR r IN select table_name
           from information_schema.views
           where table_schema = 'public'
  LOOP
    EXECUTE 'alter table ' || r.table_name || ' owner to massdata;';
  END LOOP;
END$$;
-- grant all privileges on all tables in schema public to massdata;
-- grant all privileges on all sequences in schema public to massdata;
