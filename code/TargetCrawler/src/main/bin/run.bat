@echo off
setlocal enabledelayedexpansion
set curr_dir=%~dp0
set LIBDIR=%curr_dir%..\lib
call %curr_dir%setEnv.cmd "%LIBDIR%"
set CLASSPATH=%CLASSPATH%;%curr_dir%..\resources
REM echo CLASSPATH=%CLASSPATH%

set EXE_CMD=call java
set MAIN_CLASS=org.tomstools.crawler.TargetCrawlerAppWithSpring
set JAVA_ARGS=-Dfile.encoding=UTF8 -Xms256m -Xmx1024m -Dapplication_xml=applicationContext.xml -DAPP_NAME=crawler
set APP_ARGS=

%EXE_CMD% %JAVA_ARGS% -cp %CLASSPATH% %MAIN_CLASS% %APP_ARGS%
