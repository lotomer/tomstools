@echo off
call mvn install
if NOT %errorlevel%==0 pause