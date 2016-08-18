
set build_name=%~1
set target=%~2
set POM_FILE=%~3
title start %build_name%...
echo start %build_name%...

set BASE_DIR=%~DP0
rem 设置环境变量
call %BASE_DIR%setEnv.cmd

call mvn %target% -DskipTests -Dmaven.javadoc.skip=true -f %POM_FILE%
if %errorlevel% == 0 (
    echo %build_name% success
    title %build_name% success
) else (
    echo %build_name% failed
    title %build_name% failed
    pause > nul
)
