@echo off
call mvn package
if not %errorlevel% == 0 pause
