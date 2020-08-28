#!/bin/sh


if [ -z "${JAVA_REMOTE_DEBUG_PORT}" ]; then
  JAVA_REMOTE_DEBUG_OPTIONS=''
else
  echo ${JAVA_REMOTE_DEBUG_PORT}
  echo "JAVA remote debug mode enabled on port ".${JAVA_REMOTE_DEBUG_PORT}
  JAVA_REMOTE_DEBUG_OPTIONS='-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address='${JAVA_REMOTE_DEBUG_PORT}
fi

JAVA_OPTIONS='-Djava.security.egd=file:/dev/./urandom -Dspring.invoicesearch.active='${env}' -Dspring.profiles.active='${env}' -Dspring.config.location=output/passwords.properties,src/main/resources/'

JVM_OPTIONS=' -Xmx1024M -XX:+UseG1GC -XX:+UseStringDeduplication -XX:MaxGCPauseMillis=500 '

exec java $JVM_OPTIONS $JAVA_OPTIONS -jar /app.jar
