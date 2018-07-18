create user "MASSDATA" identified by "Massdata.2018";
grant "PUBLIC","RESOURCE" to "MASSDATA";
grant select on V$INSTANCE to MASSDATA;
grant select on V$MPP_CFG_ITEM to MASSDATA;
grant select on V$DATABASE to MASSDATA;
grant select on V$DM_INI to MASSDATA;