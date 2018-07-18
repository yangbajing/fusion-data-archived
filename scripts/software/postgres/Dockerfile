FROM postgres:10.4

RUN localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8 \
  && echo 'export TZ=Asia/Shanghai' >> /etc/profile && echo 'export LANG=zh_CN.UTF-8' >> /etc/profile && . /etc/profile

COPY init.sql /docker-entrypoint-initdb.d/
