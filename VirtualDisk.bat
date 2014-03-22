
@echo off
SET CURR_DIR=%~DP0
SET CURR_DIR=%CURR_DIR:~0,-1%
SET PANFU=Z:

ECHO 删除虚拟盘： %PANFU%
SUBST /D %PANFU%
ECHO.
ECHO 开始虚拟盘%PANFU%=%CURR_DIR%
SUBST %PANFU% %CURR_DIR%
IF "%ERRORLEVEL%"=="0"  (
    ECHO 创建虚拟盘成功。
    ECHO.
) ELSE (
    ECHO 创建虚拟盘失败。
    ECHO.
)

START EXPLORER %PANFU%
echo finish.
