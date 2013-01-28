@echo off
title Creating the windows service...
@echo Creating the windows service...


sc create DirectDnsResponderSvc binPath= "%~f1\DirectDnsResponderSvc.exe" obj= "NT AUTHORITY\NetworkService" start= auto DisplayName= "Direct DNS Responder Service"
sc description DirectDnsResponderSvc "The Direct DNS Responder Service"

@echo Starting The Direct DNS Responder Service...
sc start DirectDnsResponderSvc 

if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded
goto :done

:error
echo Failed installDnsResponder.bat.  See logs... > CON
pause

:done
echo ******************
echo.
echo Script complete. Please review status messages.
echo.
echo ******************

