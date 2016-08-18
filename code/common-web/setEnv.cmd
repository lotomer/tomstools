@echo off
set DEV_TOOL_PATH=Z:

rem ================================================
rem =                设置环境变量
rem ================================================
set JDK_1.6=%DEV_TOOL_PATH%\jdk1.6
set JDK_1.7=%DEV_TOOL_PATH%\jdk1.7
set JDK_1.8=%DEV_TOOL_PATH%\jdk1.8
set MAVEN_HOME=%DEV_TOOL_PATH%\maven
set JAVA_HOME=%JDK_1.7%
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

set LOCAL=jilin
set LOCAL_VERSION=1.0.0
