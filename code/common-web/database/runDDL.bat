@echo off
set CURR_DIR=%~dp0
set CURR_DIR=%CURR_DIR:~0,-1%

call %CURR_DIR%/runsql.bat %CURR_DIR%\DDL

pause