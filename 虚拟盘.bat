@echo off
setlocal enabledelayedexpansion
set panfu=Y
set currdir=%~dp0
set currdir=%currdir:~0,-1%
set code_dir=%currdir%\code
set title=����%panfu%��
title %title%
rem call %code_dir%\checkVariables.cmd
if NOT "%ERR_MSG%" == "" (
    pause>nul
    exit /B 1
)

echo ================
echo ��ǰĿ¼��%currdir%
echo ================
echo.
echo ɾ��Դ�̷�%panfu%
subst /D %panfu%: >nul
if %errorlevel% == 0 (
    echo       ж�سɹ���
) else (
    echo       ж��ʧ�ܣ�����%panfu%�̲����ڡ�
)
rem ����ת��ű�
set file_name=%currdir%\��ת��ԭĿ¼.bat
echo start Explorer.exe  "%currdir%" > %file_name%

echo �����̷�%panfu%
subst %panfu%: %currdir% > nul
if %errorlevel% == 0 (
    echo       ���سɹ���
) else (
    echo       ����ʧ�ܡ�
)
echo.
echo ===============================================
echo ��ѯ�Ƿ��Ѿ���ӵ�ע����Ա��û���¼ʱ�Զ�����
echo.
reg query HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v %title% 2>nul 1>nul
if "%errorlevel%"=="1" (
    echo ע����в����ڣ�������ӵ�ע���...
    reg add HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v %title% /t REG_SZ /d """%~0"""
    rem echo ��ӵ�ע�����ϡ�
) else (
    echo �Ѿ�������ע���������ӡ�
    reg add HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v %title% /t REG_SZ /d """%~0""" /f
)
echo ===============================================
echo.
