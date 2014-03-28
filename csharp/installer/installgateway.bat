@echo off
@rem this must be the last script called as it performs an EXIT /B 

title Installing Gateway...
echo Installing Gateway...
call registerGateway.bat script 1 "%~f1\SmtpAgentConfig.xml" N

if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded
goto :done

:error
echo Failed installgateway.bat.  See logs... > CON
pause

:done
echo ******************
echo.
echo Script complete. Please review status messages.
echo.
echo ******************


