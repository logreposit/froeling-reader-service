FROM openjdk:11-jre

MAINTAINER Dominic Miglar <dominic.miglar@netunix.at>

RUN apt-get -y update && \
    apt-get -y install default-libmysqlclient-dev build-essential libssl-dev libxml2-dev libcurl4-openssl-dev git

RUN cd /usr/src && \
    git clone https://github.com/horchi/linux-p4d/ && \
    cd linux-p4d && \
    make clean all && \
    make install && \
    make inst-sysv-init

ADD run.sh /opt/logreposit/froeling-reader-service/run.sh
RUN chmod +x /opt/logreposit/froeling-reader-service/run.sh
ADD app.jar /opt/logreposit/froeling-reader-service/app.jar

WORKDIR /opt/logreposit/froeling-reader-service

ENTRYPOINT ["/opt/logreposit/froeling-reader-service/run.sh"]
