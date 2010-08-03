@echo off
setlocal

if /I "%1"=="script" (
set gatewayName=%1
set instanceNumber=%2
set filter=%3
set configFilePath=%4
) else (
call :AskVariables
)

if "%serviceInstance%"=="" goto :Usage
if "%gatewayName%"=="" goto :Usage
if "%filter%"=="" goto :Usage
if "%configFilePath%"=="" goto :Usage

call :RestartSMTP
if %ERRORLEVEL% NEQ 0 goto :Error
call :Uninstall
if %ERRORLEVEL% NEQ 0 goto :Error
call :Install
if %ERRORLEVEL% NEQ 0 goto :Error
goto :Done

@rem -------------------------------
:AskVariables
set /P gatewayName=Gateway Name [Return: default]
if "%gatewayName%"=="" set gatewayName=NHINDGateway

set /P serviceInstance=Service Instance Number [Return: default]
if "%serviceInstance%"=="" SET serviceInstance=1

set /P filter=Mail filter [Return: default]
if "%filter%"=="" set filter=mail from=*

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

echo Registering Dlls
call regasm.bat nhinSmtpAgent.dll
if %ERRORLEVEL% NEQ 0 goto :EOF
call regsvr.bat smtpEventHandler.dll
if %ERRORLEVEL% NEQ 0 goto :EOF

echo Installing Event Handler
cscript smtpreg.vbs /add %serviceInstance% onArrival %gatewayName% NHINDirectGateway.MessageArrivalSink "%filter%"
if %ERRORLEVEL% NEQ 0 goto :EOF
cscript smtpreg.vbs /setprop %serviceInstance% onArrival %gatewayName% Sink ConfigFilePath "%configFilePath%"
if %ERRORLEVEL% NEQ 0 goto :EOF

goto :EOF

@rem -------------------------------
:Uninstall
call :PrintHeading Uninstalling Previous
cscript smtpreg.vbs /remove %serviceInstance% onArrival %gatewayName%
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
:Error
call :PrintHeading ERROR %ERRORLEVEL%
goto :Done

@rem -------------------------------
:Usage
echo registerGateway "script" gatewayName instanceNumber filter configFilePath
goto :Done

@rem -------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%
