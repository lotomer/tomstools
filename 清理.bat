@echo off
set build_name=clean
set target=clean

set BASE_DIR=%~dp0

call %BASE_DIR%\build.cmd "%build_name%" "%target%" "%BASE_DIR%core\pom.xml"

call %BASE_DIR%\build.cmd "%build_name%" "%target%" "%BASE_DIR%tas\pom.xml"