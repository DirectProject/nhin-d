@echo off

@echo Unregistering the Gateway...
call unregisterGateway.bat

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