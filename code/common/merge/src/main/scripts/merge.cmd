@echo off
set CURRENT_DIR=%~dp0
set CURRENT_DIR=%CURRENT_DIR:~0,-1%

set main_class=org.tomstools.common.merge.MergeApp
set YUI_COMPRESS_JAR=%CURRENT_DIR%/../lib/yuicompressor.jar
set LOG4J_JAR=%CURRENT_DIR%/../lib/log4j.jar
set TOMSTOOLS_COMMON=%CURRENT_DIR%/../lib/TomsTools-common-1.0.0.jar
set TOMSTOOLS_MERGE=%CURRENT_DIR%/../lib/TomsTools-merge-1.0.0.jar

set CLASSPATH=%CLASSPATH%;%LOG4J_JAR%
set CLASSPATH=%CLASSPATH%;%YUI_COMPRESS_JAR%
set CLASSPATH=%CLASSPATH%;%TOMSTOOLS_COMMON%
set CLASSPATH=%CLASSPATH%;%TOMSTOOLS_MERGE%

call java -cp %CLASSPATH% %main_class%
echo.
pause