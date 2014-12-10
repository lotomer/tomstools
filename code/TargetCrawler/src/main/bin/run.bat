@echo off
rem chcp 65001
setlocal enabledelayedexpansion
title 开始爬取，请勿关我......
set curr_dir=%~dp0
set LIBDIR=%curr_dir%..\lib
set CLASSPATH="%curr_dir%..\resources"
call %curr_dir%setEnv.cmd "%LIBDIR%"

REM echo CLASSPATH=%CLASSPATH%

set EXE_CMD=call java
set MAIN_CLASS=org.tomstools.crawler.TargetCrawlerAppWithSpring
set JAVA_ARGS=-Dfile.encoding=UTF8 -Xms256m -Xmx1024m -Dapplication_xml=applicationContext.xml -DAPP_NAME=crawler  -Dfile.md5=AFEBCABDBACFEBEBCBDEADBCDEAADBD -Dpublic=75c9e9e5b1c3f7620eb1c7cfede8e731f68fddc378699d6c6fc1b1fcb86945c87aafe17cdf6cb9b3
set APP_ARGS=

%EXE_CMD% %JAVA_ARGS% -cp %CLASSPATH% %MAIN_CLASS% %APP_ARGS%
