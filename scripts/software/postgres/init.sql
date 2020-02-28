set timezone to 'Asia/Chongqing';

----------------------------------------
-- #functions
----------------------------------------

-- 将数组反序
create or replace function array_reverse(anyarray)
    returns anyarray as
$$
select array(
               select $1[i] from generate_subscripts($1, 1) as s (i) order by i desc
           );
$$
    language 'sql'
    strict
    immutable;
----------------------------------------
-- #functions
----------------------------------------

----------------------------------------
-- init tables, views, sequences  begin
----------------------------------------

----------------------------------------
-- init tables, views, sequences  end
----------------------------------------

-- change tables, views, sequences owner to massdata
-- DO
-- $$
--     DECLARE
--         r record;
--     BEGIN
--         FOR r IN SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'
--             LOOP
--                 EXECUTE 'alter table ' || r.table_name || ' owner to massdata;';
--             END LOOP;
--     END
-- $$;
-- DO
-- $$
--     DECLARE
--         r record;
--     BEGIN
--         FOR r IN select sequence_name from information_schema.sequences where sequence_schema = 'public'
--             LOOP
--                 EXECUTE 'alter sequence ' || r.sequence_name || ' owner to massdata;';
--             END LOOP;
--     END
-- $$;
-- DO
-- $$
--     DECLARE
--         r record;
--     BEGIN
--         FOR r IN select table_name from information_schema.views where table_schema = 'public'
--             LOOP
--                 EXECUTE 'alter table ' || r.table_name || ' owner to massdata;';
--             END LOOP;
--     END
-- $$;
-- grant all privileges on all tables in schema public to massdata;
-- grant all privileges on all sequences in schema public to massdata;
