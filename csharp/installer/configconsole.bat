@rem todo: look for a better way to handle this
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct.MessageSink /D "Direct Project - MessageSink Source"
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct /D "Direct Project - Default Source"
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct.Audit /D "Direct Project - Audit Source"
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO Health.Direct.Config.Service /D "Direct Project Config Service Source"
ConfigConsole.exe %*
if "%DEBUGINSTALLER%" == "1" pause
