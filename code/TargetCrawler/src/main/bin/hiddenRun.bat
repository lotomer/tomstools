@echo off
set curr_dir=%~dp0
set tmp_script_file="%curr_dir%run.vbs"
echo CreateObject("WScript.Shell").Run "cmd /c %curr_dir%run.bat",0 > %tmp_script_file%
cscript.exe /e:vbscript %tmp_script_file%
del %tmp_script_file%