set timezone to 'Asia/Chongqing';

-- #ddl-workflow
drop table if exists public.wf_detail;
create table public.wf_detail
(
    name       varchar(128) not null primary key,
    content    text         not null,
    created_at timestamptz
);
comment on column public.wf_detail.content
    is '工作流配置（XML）';
comment on column public.wf_detail.created_at
    is '创建时间';
-- #ddl-workflow
