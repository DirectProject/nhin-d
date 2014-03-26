@rem Install Gateway with Service
@echo off
setlocal

set destBin=C:\inetpub\nhinGateway
set installerDir=..\..\installer
set sqlSchemaFile=..\..\config\store\Schema.sql
set sqlUsersFile=..\..\installer\createuser.sql
set sqlReadonlyUsersFile=..\..\installer\createReadOnlyUser.sql

call :InstallDb
if %ERRORLEVEL% NEQ 0 goto :Done

call :InsertAdminUser
if %ERRORLEVEL% NEQ 0 goto :Done

call :Install
if %ERRORLEVEL% NEQ 0 goto :Done

call :InstallCerts
if %ERRORLEVEL% NEQ 0 goto :Done

goto :Done


@rem --------------------------------
:InstallDb
mkdir log
call createdatabase.bat (localdb)\Projects DirectConfig %sqlSchemaFile% %sqlUsersFile% %sqlReadonlyUsersFile%
goto :EOF


:InsertAdminUser
..\..\bin\debug\AdminConsole.exe user_add Admin Admin
..\..\bin\debug\AdminConsole.exe user_status_set Admin enabled
goto :EOF


@rem --------------------------------
:Install
set configFile=%~f1
if "%configFile%" == "" set configFile=DevAgentWithServiceConfig.xml
call install.bat %configFile%
goto :EOF

@rem --------------------------------
:InstallCerts
echo ****
echo Installing Certs in Configuration Service
echo.
echo ****
pushd %destbin%
call ConfigConsole.exe batch setupdomains.txt
popd
goto :EOF

@rem --------------------------------
:Usage
echo install_withservice [configFilePath (default DevAgentWithServiceConfig.xml)]
goto :EOF

@rem --------------------------------
:Done
endlocal
popd
exit /b %ERRORLEVEL%