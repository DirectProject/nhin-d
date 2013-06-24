@echo off
@echo Creating the windows service...
sc create DirectDnsResponderSvc binPath= "%~1\DirectDnsResponderSvc.exe" obj= "NT AUTHORITY\NetworkService" start= auto DisplayName= "Direct DNS Responder Service"
sc description DirectDnsResponderSvc "The Direct DNS Responder Service"

@echo Setting up virtual directories...
iisreset
%windir%\system32\inetsrv\appcmd add app /site.name:"Default Web Site" /path:/ConfigService /physicalPath:"%~1\ConfigService"
%windir%\system32\inetsrv\appcmd add app /site.name:"Default Web Site" /path:/ConfigUI /physicalPath:"%~1\ConfigUI"
%windir%\system32\inetsrv\appcmd add app /site.name:"Default Web Site" /path:/DnsService /physicalPath:"%~1\DnsService"

echo Installing test certificates...
ConfigConsole.exe test_certs_install
if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded

echo Setting up EventLog sources...
powershell -Command set-executionpolicy unrestricted -force
for /F "eol=; tokens=1,2* delims=," %%i in (event-sources.txt) do powershell -File createeventlogsource.ps1 %%i "%%j"
if %ERRORLEVEL% NEQ 0 goto :error

echo Setting up development domains and addresses...
ConfigConsole.exe batch setupdomains.txt
if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded

echo Creating the default admin for the Config UI...
AdminConsole.exe user_add admin admin
AdminConsole.exe user_status_set admin enabled
echo Succeeded

@rem this must be the last script called as it performs an EXIT /B 
echo Installing Developer Gateway...
call registerGateway.bat script 1 "%~1\DevAgentConfig.xml" N
if %ERRORLEVEL% NEQ 0 goto :error
echo Succeeded
goto :done

:error
pause

:done
powershell -Command "set-executionpolicy default -force"
echo ******************
echo.
echo Script complete. Please review status messages.
echo.
echo ******************
pause
