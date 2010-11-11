@echo Installing the windows service
sc create DirectDnsResponderSvc binPath= "%~dp0\DirectDnsResponderSvc.exe" obj= "NT AUTHORITY\NetworkService" start= auto DisplayName= "Direct DNS Responder Service"
sc description DirectDnsResponderSvc "The Direct DNS Responder Service"

@echo Setting up virtual directories
%windir%\system32\inetsrv\appcmd add app /site.name:"Default Web Site" /path:/ConfigService /physicalPath:"{app}\ConfigService"

install.bat DevAgentWithServiceConfig.xml nocopy

@rem todo: look for a better way to handle this
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct.MessageSink /D "Direct Project - MessageSink Source"
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct /D "Direct Project - Default Source"
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct.Audit /D "Direct Project - Audit Source"
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct.Config.Service /D "Direct Project Config Service Source"
ConfigConsole.exe batch setupdomains.txt
if "%DEBUGINSTALLER%" == "1" pause
