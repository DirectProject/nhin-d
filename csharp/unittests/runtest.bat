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

echo Running %2 from %1

%nunitbin%\nunit-console.exe /run:%testName% %bin%\%assembly%

goto :Done

goto :EOF

@rem--------------------------------------------------
:Usage
echo assembly testName 
goto :Done


@rem--------------------------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%