@echo off
title Creating the windows service...
@echo Creating the windows service...


sc create DirectMonitorWinSrv binPath= "%~f1\DirectMonitorWinSrv.exe" obj= "NT AUTHORITY\NetworkService" start= auto DisplayName= "Direct Monitor Service"
sc description DirectMonitorWinSrv "The Direct Monitor Service"

@echo Starting the direct monitor service...
sc start DirectMonitorWinSrv

if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded
goto :done

:error
echo Failed installMonitorServer.bat.  See logs... > CON
pause

:done
echo ******************
echo.
echo Script complete. Please review status messages.
echo.
echo ******************

