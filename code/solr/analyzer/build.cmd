@echo off
call mvn package
if NOT %errorlevel%==0 pause