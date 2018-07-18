FROM centos:6

COPY auto_install.xml .
COPY DM7Install.bin .

EXPOSE 5236

RUN echo '*   -  nofile    65536' >> /etc/security/limits.conf && \
    localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8 && \
    echo 'export TZ=Asia/Shanghai' >> /etc/profile && echo 'export LANG=zh_CN.UTF-8' >> /etc/profile && . /etc/profile


