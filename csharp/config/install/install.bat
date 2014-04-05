@rem Simple wrapper around copybins
@echo off

setlocal

set destbin=C:\inetpub\ConfigService
set configFile=%~f1
if "%configFile%"=="" set configFile=..\service\web.config

call copybins.bat script %destbin% N %configFile%

:Done
endlocal
popd
exit /b %ERRORLEVEL%