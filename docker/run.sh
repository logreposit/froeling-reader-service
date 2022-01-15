#!/bin/sh

echo "Starting application ..."
java -Djava.security.egd=file:/dev/./urandom -jar /opt/logreposit/froeling-reader-service/app.jar
