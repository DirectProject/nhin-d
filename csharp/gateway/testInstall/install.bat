@rem SAMPLE SCRIPT
@rem This installs 2 gateways on an internal Microsoft Test machine
@rem 
@echo off
setlocal

set destbin=C:\inetpub\nhinGateway
echo Copying Bins
pushd ..\install
call copybins.bat script ..\..\bin\debug %destbin% N
popd
if %ERRORLEVEL% NEQ 0 goto :Done

@rem Copy Domain Config files
for %%i in (RedmondAgentConfig.xml NHINDAgentConfig.xml) do xcopy /y /q /d %%i %destbin%

pushd %destbin%

call :PrintHeading "Installing Redmond Domain"
call registerGateway.bat script 1 %destbin%\RedmondAgentConfig.xml N
if %ERRORLEVEL% NEQ 0 goto :Done

call :PrintHeading "Installing nhind Domain"
call registerGateway.bat script 3 %destbin%\NHINDAgentConfig.xml N
if %ERRORLEVEL% NEQ 0 goto :Done

popd

goto :Done


@rem -------------------------------
:PrintHeading
shift
echo ==============================
echo.
echo %*
echo.
echo ==============================
goto :EOF

:Done
endlocal
exit /b %ERRORLEVEL%