@rem todo: look for a better way to handle this
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO nhinConfigService /D "Installed NHIN Config Service Event Source"
eventcreate /ID 1 /L APPLICATION /T INFORMATION /SO nhinAudit /D "Installed NHIN Audit Source"
nhinConfigConsole.exe %*
if "%DEBUGINSTALLER%" == "1" pause
