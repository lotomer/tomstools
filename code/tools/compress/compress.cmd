@echo off
set current_dir=%~dp0
set current_dir=%current_dir:~0,-1%
set depends_lib=%current_dir%\yuicompressor-2.4.6.jar


set java_args=
echo java %java_args% -Xbootclasspath/a:%depends_lib% -jar %current_dir%\mycompress.jar %*
java %java_args% -Xbootclasspath/a:%depends_lib% -jar %current_dir%\mycompress.jar %*

exit %errorlevel%