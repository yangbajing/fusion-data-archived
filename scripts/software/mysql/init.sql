set names 'utf8mb4';
create user 'massdata'@'%'
  identified by 'Massdata.2018';
create database massdata
  character set = 'utf8mb4';
grant select on mysql.* to 'massdata'@'%';

use massdata;
-- init tables, views, sequences  begin
create table test (
  id         bigint auto_increment primary key,
  name       varchar(255),
  created_at timestamp
);
-- init tables, views, sequences  end

grant all on massdata.* to 'massdata'@'%';
