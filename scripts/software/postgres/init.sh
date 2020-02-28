#!/bin/sh

psql -U postgres -d template1 -c "create extension adminpack;create extension hstore;"
psql -U postgres -d postgres -c "create user massdata with nosuperuser replication encrypted password 'Massdata.2018';"
psql -U postgres -d postgres -c "create database massdata owner = massdata template = template1;"

psql -U massdata -d massdata -f /data/init.sql
psql -U massdata -d massdata -f /data/workflow.sql
psql -U massdata -d massdata -f /data/job.sql
