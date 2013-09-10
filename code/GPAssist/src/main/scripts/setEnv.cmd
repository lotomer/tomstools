set LIBDIR=%~1

for /f %%i in ('dir /b/s %LIBDIR%\*.jar') do (
    set CLASSPATH=!CLASSPATH!;"%%i"
)
