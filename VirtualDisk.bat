
@echo off
SET CURR_DIR=%~DP0
SET CURR_DIR=%CURR_DIR:~0,-1%
SET PANFU=Z:

ECHO ɾ�������̣� %PANFU%
SUBST /D %PANFU%
ECHO.
ECHO ��ʼ������%PANFU%=%CURR_DIR%
SUBST %PANFU% %CURR_DIR%
IF "%ERRORLEVEL%"=="0"  (
    ECHO ���������̳ɹ���
    ECHO.
) ELSE (
    ECHO ����������ʧ�ܡ�
    ECHO.
)

START EXPLORER %PANFU%
echo finish.
