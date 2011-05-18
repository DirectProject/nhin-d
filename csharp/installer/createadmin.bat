@echo off
setlocal
title Create admin user for the config UI...

call :initialize
call :create_user
goto :done

:initialize
set /p user_name=Choose user name (DEFAULT admin)? 
if "%user_name%"=="" set user_name=admin

set /p user_pswd=Choose user password (DEFAULT admin)? 
if "%user_pswd%"=="" set user_pswd=admin

:create_user
echo Creating the default admin for the Config UI...
AdminConsole.exe user_add %user_name% %user_pswd%
AdminConsole.exe user_status_set %user_name% enabled
@if ERRORLEVEL 1 goto :error
echo Succeeded
goto :done

:error
echo Failed createadmin.bat.  See logs... > CON
pause

:done
echo ******************
echo.
echo Script complete. Please review status messages.
echo.
echo ******************


