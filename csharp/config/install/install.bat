@rem Simple wrapper around copybins
@echo off

setlocal

set destbin=C:\inetpub\nhindConfigService
set configFile=%~f1
if "%configFile%"=="" set configFile=web.config

call copybins.bat script %destbin% N %configFile%

:Done
endlocal
popd
exit /b %ERRORLEVEL%