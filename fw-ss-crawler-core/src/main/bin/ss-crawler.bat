@echo off
@setlocal
set CMD_LINE_ARGS=%*
set EXTRA_JVM_ARGUMENTS=-Xmx512m
java %JAVA_OPTS% %EXTRA_JVM_ARGUMENTS% -jar .\lib\${artifactId}-${pom.version}.jar %CMD_LINE_ARGS%
@endlocal
