FROM openjdk:11-jre

MAINTAINER Dominic Miglar <dominic.miglar@netunix.at>

RUN apt-get -y update && \
    apt-get -y install \
      git \
      default-libmysqlclient-dev \
      build-essential \
      libssl-dev \
      libxml2-dev \
      libcurl4-openssl-dev \
      uuid-dev \
      libjansson-dev \
      libwebsockets-dev

RUN cd /usr/src && \
    git clone https://github.com/horchi/linux-p4d/ && \
    cd linux-p4d && \
    git checkout 0.5.16 && \
    make clean all && \
    make install && \
    make inst-sysv-init

WORKDIR /opt/logreposit/froeling-reader-service

COPY run.sh    /opt/logreposit/froeling-reader-service/run.sh
COPY app.jar   /opt/logreposit/froeling-reader-service/app.jar
RUN  chmod 755 /opt/logreposit/froeling-reader-service/run.sh

ENTRYPOINT ["/opt/logreposit/froeling-reader-service/run.sh"]
