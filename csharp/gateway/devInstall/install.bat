@rem This installs gateways on developer machines
@echo off
setlocal

@rem these are the defaults, exceptions below...
set srcbin=
set destbin="%~dp0"

@rem nocopy can occur in the first or second param
if '%1%' NEQ 'nocopy' (
set configFile=%~f1
if '%2' NEQ 'nocopy' (
set srcbin=..\..\bin\debug
set destbin="C:\inetpub\nhinGateway\"
)
)

call :GetConfigFile %destbin% DevAgentConfig.xml

if NOT "%srcbin%" == "" call copybins.bat %destbin%
if NOT "%configFile%" == "" xcopy /y "%configFile%" %configDestPath%


@rem --------------------------------
pushd %destbin%

call :PrintHeading "Installing Test Certificates"
call ConfigConsole.exe Test_Certs_Install
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded

call :PrintHeading "Installing Developer Gateway"
call registerGateway.bat script 1 %configDestPath% N
if %ERRORLEVEL% NEQ 0 goto :Done
popd

goto :Done

@rem -------------------------------
:GetConfigFile
set configDestPath="%~dp1%2"
echo configDestPath set to "%configDestPath%"
goto :EOF

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
exit /b %ERRORLEVEL%