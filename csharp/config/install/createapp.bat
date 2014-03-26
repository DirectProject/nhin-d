@rem **Sample** script to create the Config Web Application in IIS
@rem PLEASE PLEASE Improve & extend as per your needs

@echo off

setlocal

echo *******************************************************
echo.
echo Setting up Config Service Application in IIS
echo.
echo *******************************************************

set appPath=%1
if "%appPath%" EQU "" goto :Usage
set appcmd=%systemroot%\system32\inetsrv\appcmd.exe

call :Install
if %ERRORLEVEL% NEQ 0 goto :Done
goto :Done

@rem ------------------------------------------
:Install
%appcmd% ADD APPPOOL -name:ConfigService
%appcmd% ADD APP /site.name:"Default Web Site" /path:/ConfigService /physicalPath:%appPath% /applicationPool:ConfigService
goto :EOF

@rem ------------------------------------------
:Usage
echo createApp appPath
goto :EOF

@rem ------------------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%
