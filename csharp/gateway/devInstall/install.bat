@rem SAMPLE SCRIPT
@rem This installs gateways on an internal Microsoft Test machine
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
for %%i in (DevAgentConfig.xml) do xcopy /y /q /d %%i %destbin%

pushd %destbin%

call :PrintHeading "Installing Developer Gateway"
call registerGateway.bat script 1 %destbin%\DevAgentConfig.xml N
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