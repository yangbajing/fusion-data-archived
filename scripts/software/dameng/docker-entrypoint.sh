#!/usr/bin/env bash
set -Eeo pipefail
# TODO swap to -Eeuo pipefail above (after handling all potentially-unset variables)

# usage: file_env VAR [DEFAULT]
#    ie: file_env 'XYZ_DB_PASSWORD' 'example'
# (will allow for "$XYZ_DB_PASSWORD_FILE" to fill in the value of
#  "$XYZ_DB_PASSWORD" from a file, especially for Docker's secrets feature)
file_env() {
	local var="$1"
	local fileVar="${var}_FILE"
	local def="${2:-}"
	if [ "${!var:-}" ] && [ "${!fileVar:-}" ]; then
		echo >&2 "error: both $var and $fileVar are set (but are exclusive)"
		exit 1
	fi
	local val="$def"
	if [ "${!var:-}" ]; then
		val="${!var}"
	elif [ "${!fileVar:-}" ]; then
		val="$(< "${!fileVar}")"
	fi
	export "$var"="$val"
	unset "$fileVar"
}

if [ "${1:0:1}" = '-' ]; then
	set -- dmdba "$@"
fi

#if [ "$1" = 'dm7' ] && [ "$(id -u)" = '0' ]; then
if [ "$1" = 'dm7' ]; then
    if [ ! -f /opt/dmdbms/data/$DB_NAME/dm.ini ]; then
        /opt/dmdbms/bin/dminit PATH=/opt/dmdbms/data EXTENT_SIZE=$EXTENT_SIZE PAGE_SIZE=$PAGE_SIZE CHARSET=1 LENGTH_IN_CHAR=1 \
            SYSDBA_PWD=$SYSDBA_PWD SYSAUDITOR_PWD=$SYSAUDITOR_PWD DB_NAME=$DB_NAME INSTANCE_NAME=$INSTANCE_NAME \
            PORT_NUM=$PORT_NUM TIME_ZONE=$TIME_ZONE
        /opt/dmdbms/script/root/dm_service_installer.sh -t dmserver -i /opt/dmdbms/data/$DB_NAME/dm.ini -p $INSTANCE_NAME -m open
    fi
    sleep 1
    /etc/init.d/DmService$INSTANCE_NAME start
    sleep 2
#    /opt/dmdbms/bin/dmserver /opt/dmdbms/data/$DB_NAME/dm.ini -noconsole
fi



