@echo off
call mvn package -P product

if NOT %errorlevel%==0 pause