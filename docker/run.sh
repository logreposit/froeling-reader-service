#!/bin/sh

echo "Starting application ..."
java -Xmx64m -Djava.security.egd=file:/dev/./urandom -jar /opt/logreposit/froeling-reader-service/app.jar
