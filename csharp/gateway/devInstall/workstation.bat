@rem Install DB::  Convenient method to install db while on a windows 7 machine without smtp.
@rem Install certs into cert store.
@echo off
setlocal


set sqlSchemaFile=..\..\config\store\Schema.sql
set sqlUsersFile=..\..\installer\createuser.sql
set sqlReadonlyUsersFile=..\..\installer\createReadOnlyUser.sql
set srcbin=..\..\bin\debug
set installdir="..\..\installer"

call :InstallDb
if %ERRORLEVEL% NEQ 0 goto :Done

call :Install_Test_Certs
if %ERRORLEVEL% NEQ 0 goto :Done

call :Install_Eventlog_Sources
if %ERRORLEVEL% NEQ 0 goto :Done


@rem --------------------------------
:InstallDb
call :PrintHeading "Installing DirectConfig database"
mkdir log
call createdatabase.bat (localdb)\Projects DirectConfig %sqlSchemaFile% %sqlUsersFile% %sqlReadonlyUsersFile% 
goto :EOF



:Install_Test_Certs
@rem --------------------------------
pushd %srcbin%
call :PrintHeading "Installing Test Certificates"
call ConfigConsole.exe Test_Certs_Install
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded
popd
goto :EOF


:Install_Eventlog_Sources
@rem --------------------------------
pushd %installdir%
call createeventlogsource.bat 
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded
popd
goto :EOF



@rem -------------------------------
:PrintHeading
shift
echo ==============================
echo.
echo %*
echo.
echo ==============================
goto :EOF


@rem --------------------------------
:Done
endlocal
popd
exit /b %ERRORLEVEL%