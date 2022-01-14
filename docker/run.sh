#!/bin/sh

echo "Starting application ..."
java -Xmx128m -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom -jar /opt/logreposit/froeling-reader-service/app.jar
