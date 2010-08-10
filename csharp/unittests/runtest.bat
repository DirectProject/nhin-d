@rem simple batch file to help run a specific nunit test
@echo off
setlocal

if "%1"=="" goto :Usage
if "%2"=="" goto :Usage

set assembly=%1
set testName=%2

set this=%~p0
set nunitbin=..\external\nunit\bin\net-2.0
set bin=..\bin\Debug

echo ************************************
echo ====================================
echo.
echo %2 [%1]
echo.
echo ====================================
echo ************************************

%nunitbin%\nunit-console.exe /run:%testName% %bin%\%assembly%
if %ERRORLEVEL% EQU 0 goto :Done

echo ************************************
echo ====================================
echo ERROR: %ERRORLEVEL%
echo ====================================
echo ************************************

goto :Done

@rem--------------------------------------------------
:Usage
echo assembly testName 
goto :Done

@rem--------------------------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%