@echo off
@echo Stopping services...

net stop DirectMonitorWinSrv

@echo Dropping the direct monitor service...
sc delete DirectMonitorWinSrv

if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded
goto :done

:error
pause

:done
echo ******************
echo.
echo Script complete. Please review status messages.
echo.
echo ******************
pause