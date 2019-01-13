FROM ubuntu:18.04

MAINTAINER Dominic Miglar <dominic.miglar@netunix.at>

RUN apt-get -y update && \
    apt-get -y install libmysqlclient-dev build-essential libssl-dev libxml2-dev libcurl4-openssl-dev git

RUN cd /usr/src && \
    git clone https://github.com/horchi/linux-p4d/ && \
    cd linux-p4d && \
    make clean all && \
    make install && \
    make inst-sysv-init
