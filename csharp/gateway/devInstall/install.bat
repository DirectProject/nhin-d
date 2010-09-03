@rem This installs gateways on developer machines
@echo off
setlocal

if "%1%" == "nocopy" (
set srcbin=
set destbin=%~dp0
) else (
set srcbin=..\..\bin\debug
set destbin=C:\inetpub\nhinGateway
)

if NOT "%srcbin%" == "" call copybins.bat %destbin%

@rem --------------------------------
pushd %destbin%

call :PrintHeading "Installing Test Certificates"
call nhinConfigConsole.exe Test_Certs_Install
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded

call :PrintHeading "Installing Developer Gateway"
call registerGateway.bat script 1 "%destbin%\DevAgentConfig.xml" N
if %ERRORLEVEL% NEQ 0 goto :Done
popd

goto :Done


@rem -------------------------------
:PrintHeading
shift
echo ==============================
echo.
echo %~*
echo.
echo ==============================
goto :EOF

@rem -------------------------------
echo install [nocopy]
echo     nocopy: do not copy bits. Just install from local directory
goto :EOF

:Done
endlocal
popd
exit /b %ERRORLEVEL%