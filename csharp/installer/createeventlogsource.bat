@echo off
title Setting up EventLog sources...
echo Setting up EventLog sources...

powershell -Command set-executionpolicy unrestricted -force
for /F "eol=; tokens=1,2* delims=," %%i in (event-sources.txt) do powershell -File createeventlogsource.ps1 %%i "%%j"
if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded
goto :done

:error
echo Failed createeventlogsource.bat.  See logs... > CON
pause

:done
powershell -Command "set-executionpolicy default -force"
echo ******************
echo.
echo Script complete. Please review status messages.
echo.
echo ******************

