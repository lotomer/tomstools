@echo off
setlocal enabledelayedexpansion
set CURRENT_DIR=%~dp0
set JAR_FILE_NAME=%CURRENT_DIR%list.txt
set MAVEN_REPOSITORY=%CURRENT_DIR%jars

FOR /F "eol=; tokens=1,2,3,4 delims=: " %%i in (%JAR_FILE_NAME%) do (
    set groupId=%%i
    set artifactId=%%j
    set packaging=%%k
    set version=%%l
    rem set p=%MAVEN_REPOSITORY%\!groupId:.=\!\!artifactId!\!version!\!artifactId!-!version!.!packaging!
    set p=%MAVEN_REPOSITORY%\!artifactId!-!version!.!packaging!
    if not exist "!p!" (
        echo File not exists: !p!
    ) else (
        echo start install file: !p!
        call mvn install:install-file  -Dfile=!p!  -DgroupId=!groupId!  -DartifactId=!artifactId! -Dversion=!version! -Dpackaging=!packaging!
    )
)

pause
