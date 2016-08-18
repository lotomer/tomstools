@echo off
setlocal enabledelayedexpansion
set CURRENT_DIR=%~dp0
set JAR_FILE_NAME=%CURRENT_DIR%list.txt
set MAVEN_REPOSITORY=C:\Users\%username%\.m2\repository_b
set OUT_PATH=%CURRENT_DIR%jars

FOR /F "eol=; tokens=1,2,3,4 delims=: " %%i in (%JAR_FILE_NAME%) do (
    set groupId=%%i
    set artifactId=%%j
    set packaging=%%k
    set version=%%l
    set p=!groupId:.=\!\!artifactId!\!version!
    set fileName=!artifactId!-!version!.!packaging!
    set old=%MAVEN_REPOSITORY%\!p!\!fileName!
    rem set new=%OUT_PATH%\!p!
    set new=%OUT_PATH%
    echo copy !fileName!
    if not exist !new! mkdir !new!
    copy /Y !old! !new! > nul
)

pause