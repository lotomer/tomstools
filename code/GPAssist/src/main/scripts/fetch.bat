@echo off
setlocal enabledelayedexpansion
set curr_dir=%~dp0
set LIBDIR=%curr_dir%..\lib
call %curr_dir%setEnv.cmd "%LIBDIR%"
set CLASSPATH=%CLASSPATH%;%curr_dir%..\resources
REM echo CLASSPATH=%CLASSPATH%

set EXE_CMD=call java
set MAIN_CLASS=org.tomstools.html.fetcher.AgencyFetcherApp
set JAVA_ARGS=-Dfile.encoding=UTF8
set APP_ARGS=-b %date:~0,4%-%date:~5,2%-%date:~8,2%

%EXE_CMD% %JAVA_ARGS% -cp %CLASSPATH% %MAIN_CLASS% %APP_ARGS%

pause