@echo off
cd common
call build.cmd
cd ..

cd web
call build.cmd
cd ..
pause