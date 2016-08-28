@echo off
setlocal enabledelayedexpansion
set panfu=Y
set currdir=%~dp0
set currdir=%currdir:~0,-1%
set code_dir=%currdir%\code
set title=虚拟%panfu%盘
title %title%
rem call %code_dir%\checkVariables.cmd
if NOT "%ERR_MSG%" == "" (
    pause>nul
    exit /B 1
)

echo ================
echo 当前目录：%currdir%
echo ================
echo.
echo 删除源盘符%panfu%
subst /D %panfu%: >nul
if %errorlevel% == 0 (
    echo       卸载成功。
) else (
    echo       卸载失败：可能%panfu%盘不存在。
)
rem 生成转向脚本
set file_name=%currdir%\跳转到原目录.bat
echo start Explorer.exe  "%currdir%" > %file_name%

echo 挂载盘符%panfu%
subst %panfu%: %currdir% > nul
if %errorlevel% == 0 (
    echo       挂载成功。
) else (
    echo       挂载失败。
)
echo.
echo ===============================================
echo 查询是否已经添加到注册表以便用户登录时自动运行
echo.
reg query HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v %title% 2>nul 1>nul
if "%errorlevel%"=="1" (
    echo 注册表中不存在，正在添加到注册表...
    reg add HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v %title% /t REG_SZ /d """%~0"""
    rem echo 添加到注册表完毕。
) else (
    echo 已经存在于注册表，不再添加。
    reg add HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v %title% /t REG_SZ /d """%~0""" /f
)
echo ===============================================
echo.
