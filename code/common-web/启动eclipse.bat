@echo off
set BASE_DIR=%~dp0

call %BASE_DIR%setEnv.cmd

cd /d %DEV_TOOL_PATH%\eclipse
rem set JAVA_OPTS=-Xms1024m -Xmx2048m -XX:MaxPermSize=256m -Xbootclasspath/a:lombok.jar -javaagent:lombok.jar -Dfile.encoding=UTF-8
set JAVA_OPTS=-Xms1024m -Xmx2048m -XX:MaxPermSize=512m -Dfile.encoding=UTF-8
start eclipse.exe -data "%BASE_DIR%workspace"  -vmargs %JAVA_OPTS%

