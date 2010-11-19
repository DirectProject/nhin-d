@rem Install Gateway with Service
@echo off
setlocal

set destBin=C:\inetpub\nhinGateway

call :Install
if %ERRORLEVEL% NEQ 0 goto :Done

call :InstallCerts
if %ERRORLEVEL% NEQ 0 goto :Done

goto :Done

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