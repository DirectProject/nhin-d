@rem This installs gateways on developer machines
@echo off
setlocal

@rem these are the defaults, exceptions below...
set srcbin=
set destbin=%~dp0

@rem nocopy can occur in the first or second param
if "%1%" NEQ "nocopy" (
set configFile=%~f1
if "%2" NEQ "nocopy" (
set srcbin=..\..\bin\debug
set destbin=C:\inetpub\nhinGateway\
)
)

if NOT "%srcbin%" == "" call copybins.bat %destbin%
if NOT "%configFile%" == "" xcopy /y "%configFile%" "%destbin%DevAgentConfig.xml"


@rem --------------------------------
pushd %destbin%

call :PrintHeading "Installing Test Certificates"
call nhinConfigConsole.exe Test_Certs_Install
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded

call :PrintHeading "Installing Developer Gateway"
call registerGateway.bat script 1 "%destbin%DevAgentConfig.xml" N
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

@rem -------------------------------
echo install [nocopy]
echo install configpath [nocopy]
echo     nocopy: do not copy bits. Just install from local directory
echo     configpath: path to the xml configuration file
goto :EOF

:Done
endlocal
popd
pause
exit /b %ERRORLEVEL%