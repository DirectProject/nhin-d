@rem SAMPLE SCRIPT
@rem This installs gateways on an internal Microsoft Test machine
@rem 
@echo off
setlocal

set srcbin=..\..\bin\debug
set destbin=C:\inetpub\nhinGateway

@rem -----------------
call :PrintHeading "Copying Standard Gateway Bins"
pushd ..\install
call copybins.bat script %srcbin% %destbin% N
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded
popd


@rem Copy Domain Config files
for %%i in (DevAgentConfig.xml) do xcopy /y /q /d %%i %destbin%

@rem --------------------------------
call :PrintHeading "Copying Certificates"
pushd %srcbin% 
if not exist %destbin%\Certificates md %destbin%\Certificates
xcopy /s /q /y /d Certificates\* %destbin%\Certificates
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded
popd


@rem --------------------------------
pushd %destbin%

call :PrintHeading "Installing Test Certificates"
call nhinConfigConsole.exe Test_Certs_Install
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded

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
popd
exit /b %ERRORLEVEL%