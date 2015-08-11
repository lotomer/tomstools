@echo off
set USER=root
set PASSWORD=root123
set HOST=192.168.172.128
set DATABASE=test
set CHARSET=utf8
set CMD=mysql
set DIR=%1
if "%DIR%" == "" (
    echo invalid arguments!
    echo Usage:
    echo "    $0 directory"
    exit 1
)
echo ====================================
for /f "tokens=*" %%i in ('dir /b/s/a %DIR%\*.sql') do (
    rem echo "%execmd% -h%host% -u%user% -p%passwd% %database%<%%i"
    echo execute "%%i"
    %CMD% -h%HOST% -u%USER% -p%PASSWORD% --default-character-set=%CHARSET% -D%DATABASE%<"%%i"
)
echo ====================================
if "%errorlevel%"=="1" echo run failed!