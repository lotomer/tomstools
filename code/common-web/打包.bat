@echo off
set build_name=build
set target=package

set BASE_DIR=%~dp0

call %BASE_DIR%\build.cmd "%build_name%" "%target%" "%BASE_DIR%parent\pom.xml"

rem call %BASE_DIR%\build.cmd "%build_name%" "%target%" "%BASE_DIR%core\pom.xml"
rem if NOT %errorlevel% == 0 goto exit
rem call %BASE_DIR%\build.cmd "%build_name%" "%target%" "%BASE_DIR%local\pom.xml"
rem if NOT %errorlevel% == 0 goto exit
rem call %BASE_DIR%\build.cmd "%build_name%" "%target%" "%BASE_DIR%tas\pom.xml"
rem if NOT %errorlevel% == 0 goto exit

:exit
