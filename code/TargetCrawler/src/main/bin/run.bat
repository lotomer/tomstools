@echo off
setlocal enabledelayedexpansion
title 网页爬虫紧张工作中，勿关我......
set curr_dir=%~dp0
set LIBDIR=%curr_dir%..\lib
set CLASSPATH="%curr_dir%..\resources"
call %curr_dir%setEnv.cmd "%LIBDIR%"

REM echo CLASSPATH=%CLASSPATH%

set EXE_CMD=call java
set MAIN_CLASS=org.tomstools.crawler.TargetCrawlerAppWithSpring
set JAVA_ARGS=-Dfile.encoding=UTF8 -Xms256m -Xmx1024m -Dapplication_xml=applicationContext.xml -DAPP_NAME=crawler
set APP_ARGS=

%EXE_CMD% %JAVA_ARGS% -cp %CLASSPATH% %MAIN_CLASS% %APP_ARGS%
