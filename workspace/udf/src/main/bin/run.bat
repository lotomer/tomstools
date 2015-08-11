@echo off
setlocal enabledelayedexpansion
set curr_dir=%~dp0
set LIBDIR=%curr_dir%..\lib
call %curr_dir%setEnv.cmd "%LIBDIR%"
set CLASSPATH=%CLASSPATH%;%curr_dir%..\resources
REM echo CLASSPATH=%CLASSPATH%

set EXE_CMD=call java
set MAIN_CLASS=org.tomstools.prediction.LotteryPredictionApp
set JAVA_ARGS=-Dfile.encoding=UTF8 -Xms256m -Xmx1024m
set APP_ARGS=%*
rem set APP_ARGS=-out . http://www.bookba.net/Html/Book/27/27887/Index.html -regexp 5280\w*?.html -regexpFilter:include "<div id=\"content\" .*?>(.*?)</div>" -regexpFilter:exclude "<a .*?</a>"  -threadCount 5

%EXE_CMD% %JAVA_ARGS% -cp %CLASSPATH% %MAIN_CLASS% %APP_ARGS%

pause