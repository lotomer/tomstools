@echo off
setlocal enabledelayedexpansion
set LIBDIR=%curr_dir%..\lib
set CLASSPATH=%CLASSPATH%;%curr_dir%..\resources
call %curr_dir%setEnv.cmd "%LIBDIR%"

set EXE_CMD=call java
set MAIN_CLASS=org.tomstools.common.merge.MergeApp
set JAVA_ARGS=-Dfile.encoding=UTF8 -Dlog4j.configuration=log4j-sample.properties
set APP_ARGS=-DWEB_CONFIG_FILE=webFileConfig-sample.properties -compress:deleteSourceFile

echo CLASSPATH=%CLASSPATH%
%EXE_CMD% %JAVA_ARGS% -cp %CLASSPATH% %MAIN_CLASS% %APP_ARGS%

pause
