@rem Install Gateway with Service
@echo off
setlocal

set configFile=%~f1
if "%configFile%" == "" set configFile=DevAgentWithServiceConfig.xml
call install.bat %configFile%
goto :Done

:Usage
echo install_withservice [configFilePath (default DevAgentWithServiceConfig.xml)]
goto :EOF

:Done
endlocal
popd
exit /b %ERRORLEVEL%