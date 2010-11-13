@echo off
@echo Creating the windows service...
sc create DirectDnsResponderSvc binPath= "%~1\DirectDnsResponderSvc.exe" obj= "NT AUTHORITY\NetworkService" start= auto DisplayName= "Direct DNS Responder Service"
sc description DirectDnsResponderSvc "The Direct DNS Responder Service"

@echo Setting up virtual directories...
%windir%\system32\inetsrv\appcmd add app /site.name:"Default Web Site" /path:/ConfigService /physicalPath:"%~1\ConfigService"

echo Installing test certificates...
call ConfigConsole.exe test_certs_install
if %ERRORLEVEL% NEQ 0 goto :done
echo Succeeded

echo Setting up EventLog sources...
for /F "eol=; tokens=1,2* delims=," %%i in (event-sources.txt) do eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO %%i /D "%%j"

echo Setting up development domains and addresses...
ConfigConsole.exe batch setupdomains.txt
if %ERRORLEVEL% NEQ 0 goto :done
echo Succeeded

echo Installing Developer Gateway...
registerGateway.bat script 1 "%~1\DevAgentConfig.xml" N
if %ERRORLEVEL% NEQ 0 goto :done
echo Succeeded
goto :done

:done
if "%DEBUGINSTALLER%" == "1" pause
