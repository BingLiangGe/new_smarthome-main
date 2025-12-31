FROM openjdk:11-jre

MAINTAINER ykl <ykl@lj.com>
RUN echo "Asia/Shanghai" > /etc/timezone

WORKDIR /opt
COPY docker-entrypoint.sh  /opt/
RUN chmod +x /opt/docker-entrypoint.sh

ARG JAR_FILE

ENV JAR_FILE=${JAR_FILE} \
    EUREKA_URI=""

ADD ${JAR_FILE} \
    /opt/

CMD ["./docker-entrypoint.sh"]

