#!/bin/sh

echo "Starting application ..."
java -Xmx 50m -Djava.security.egd=file:/dev/./urandom -jar /opt/logreposit/froeling-reader-service/app.jar
