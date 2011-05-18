@echo off
@echo Stopping services...

net stop DirectDnsResponderSvc

@echo Dropping the DNS Responder Service...
sc delete DirectDnsResponderSvc

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