#!/bin/sh

BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`

if [ -z "$REPO" ]
then
  REPO="$BASEDIR"/lib
fi

CLASSPATH=$CLASSPATH_PREFIX:"$BASEDIR"/etc:"$REPO"/fw-benchmark-component-1.0-SNAPSHOT.jar:"$REPO"/commons-logging-1.1.jar:"$REPO"/plexus-classworlds-1.2-alpha-13.jar:"$REPO"/xbean-reflect-3.4.jar:"$REPO"/fw-benchmark-core-1.0-SNAPSHOT.jar:"$REPO"/fw-plexus-benchmark-application-1.0-SNAPSHOT.jar:"$REPO"/plexus-utils-1.4.5.jar:"$REPO"/commons-codec-1.2.jar:"$REPO"/commons-logging-api-1.1.jar:"$REPO"/commons-math-1.2.jar:"$REPO"/log4j-1.2.15.jar:"$REPO"/plexus-container-default-1.0-alpha-48.jar:"$REPO"/aspectjrt-1.5.3.jar:"$REPO"/commons-httpclient-3.0.1.jar
EXTRA_JVM_ARGUMENTS="-Xmx128m"

exec java $JAVA_OPTS \
  $EXTRA_JVM_ARGUMENTS \
  -jar "$BASEDIR/lib/${artifactId}-${pom.version}.jar" \
  "$@"
