# vim:set ft=dockerfile:
FROM centos:6

RUN set -x \
    && echo '*   -  nofile    65536' >> /etc/security/limits.conf \
    && localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8 \
    && yum -y install wget e2fsprogs \
    && mkdir /docker-entrypoint-initdb.d
ENV LANG zh_CN.utf8
ENV TZ Asia/Shanghai
ENV SYSDBA_PWD Massdata.2018
ENV SYSAUDITOR_PWD Massdata.2018
#ENV DB_PATH /opt/dmdbms/data
ENV DB_NAME DAMENG
ENV INSTANCE_NAME DMSERVER
ENV EXTENT_SIZE 16
ENV PAGE_SIZE 16
ENV PORT_NUM 5236
ENV TIME_ZONE +08:00

COPY auto_install.xml /
COPY docker-entrypoint.sh /usr/local/bin

COPY DM7Install.bin /
#RUN wget -c https://hl.hualongdata.com/Software/DM/DM7Install.bin

RUN set -x \
    && chmod +x /usr/local/bin/docker-entrypoint.sh && ln -sf /usr/local/bin/docker-entrypoint.sh / \
    && . /etc/profile && chmod +x /DM7Install.bin && ./DM7Install.bin -q /auto_install.xml \
    && rm auto_install.xml DM7Install.bin && chmod +x /docker-entrypoint.sh
#VOLUME /opt/dmdbms/data
ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE $PORT_NUM
CMD ["dm7"]
