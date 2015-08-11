@echo off
cd common
call clean.cmd
cd ..

cd apiserver
call clean.cmd
cd .. 
cd web
call clean.cmd
cd ..
pause