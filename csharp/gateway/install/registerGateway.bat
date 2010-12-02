@echo off
setlocal

if /I "%1"=="script" (
set serviceInstance=%2
set configFilePath=%~3
set restart=%4
) else (
call :AskVariables
)

set gatewayName=NHINDGateway
set filter=mail from=*

if "%serviceInstance%"=="" goto :Usage
if "%gatewayName%"=="" goto :Usage
if "%filter%"=="" goto :Usage
if "%configFilePath%"=="" goto :Usage

if "%restart%" NEQ "N" call :RestartSMTP
if %ERRORLEVEL% NEQ 0 goto :Error
call :Uninstall
if %ERRORLEVEL% NEQ 0 goto :Error
call :Install
if %ERRORLEVEL% NEQ 0 goto :Error
goto :Done

@rem -------------------------------
:AskVariables
@rem set /P serviceInstance=Service Instance Number [Return: default]
if "%serviceInstance%"=="" set serviceInstance=1

set /P configFilePath=Config file Path [Return: default]
if "%configFilePath%"=="" set configFilePath=%~dp0SmtpAgentConfig.xml

exit /b 0

@rem -------------------------------
:RestartSMTP
call :PrintHeading Restarting Smtp Service 
iisreset
goto :EOF

@rem -------------------------------
:Install
call :PrintHeading Installing

call :PrintBold Registering Dlls
call regasm.bat Health.Direct.SmtpAgent.dll
if %ERRORLEVEL% NEQ 0 goto :EOF

call :PrintBold Registering smtpEventHandler COM Dll
call regsvr32 /s smtpEventHandler.dll
if %ERRORLEVEL% NEQ 0 goto :EOF

call :PrintBold Ensuring Machine Stores
cscript /nologo agentsetup.vbs "%configFilePath%"
if %ERRORLEVEL% NEQ 0 goto :EOF

call :PrintBold Installing Event Handler
cscript /nologo smtpreg.vbs /add %serviceInstance% onArrival %gatewayName% NHINDirectGateway.MessageArrivalSink "%filter%"
if %ERRORLEVEL% NEQ 0 goto :EOF
cscript /nologo smtpreg.vbs /setprop %serviceInstance% onArrival %gatewayName% Sink ConfigFilePath "%configFilePath%"
if %ERRORLEVEL% NEQ 0 goto :EOF

goto :EOF

@rem -------------------------------
:Uninstall
call :PrintHeading Uninstalling Previous
cscript /nologo smtpreg.vbs /remove %serviceInstance% onArrival %gatewayName%
if %ERRORLEVEL% NEQ 0 goto :EOF
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
:PrintBold
shift
echo.
echo ###########
echo %*
echo ###########
goto :EOF

@rem -------------------------------
:Error
call :PrintHeading ERROR %ERRORLEVEL%
goto :Done

@rem -------------------------------
:Usage
echo registerGateway "script" virtualServerInstanceNumber configFilePath [iisreset Y or N]
goto :Done

@rem -------------------------------
:Done
endlocal
if ERRORLEVEL 1 exit /b %ERRORLEVEL%
