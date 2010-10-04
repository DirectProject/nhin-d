@rem ----------------------------------------------------
@rem Batch File for Deploying New NHIND Gateway Binaries
@rem ----------------------------------------------------
@echo off

setlocal

if "%1"=="script" (
set bin=%2
set dest=%3
set backup=%4
) else (
call :AskVariables
)

if "%dest%"=="" goto :Usage
if "%bin%"=="" goto :Usage
if "%backup%"=="" goto :Usage

set platform_dir=Win32
if "%PROCESSOR_ARCHITECTURE%" == "AMD64" set platform_dir=x64

call :Restart
if %ERRORLEVEL% NEQ 0 goto :Error

if /I "%backup%" EQU "Y" call :BackupCurrent

call :EnsureDirs
if %ERRORLEVEL% NEQ 0 goto :Error

pushd %bin%
call :CopyBins
popd
if %ERRORLEVEL% NEQ 0 goto :Error
call :CopyInstall
if %ERRORLEVEL% NEQ 0 goto :Error

goto :Done

@rem -------------------------------
:Restart
call :PrintHeading Restarting Smtp Service 
iisreset
goto :EOF

@rem -------------------------------
:Standard
goto :EOF


@rem -------------------------------
:AskVariables
set /P backup=Backup Current? [Y/N  Return: N]
if "%backup%"=="" set backup=N

@rem set /P bin=Bin Path [Return: default]
if "%bin%"=="" set bin=..\..\bin\debug

set /P dest=Destination Path [Return: default]
if "%dest%"=="" set dest=C:\inetpub\nhinGateway

exit /b 0

@rem -------------------------------
:BackupCurrent
call :PrintHeading Backing up "%dest%"

call backup.bat "%dest%"
goto :EOF

@rem -------------------------------
:EnsureDirs
echo Ensuring Directories
if not exist %dest% md %dest%
goto :EOF

@rem -------------------------------
:CopyBins
call :PrintHeading Copying BINS to "%dest%"
call :CopyFiles dnsResolver.dll nhinCommon.dll nhinAgent.dll nhinSmtpAgent.dll Interop.ADODB.dll Interop.CDO.dll %platform_dir%\smtpEventHandler.dll
exit /b %ERRORLEVEL%

@rem -------------------------------
:CopyInstall
call :PrintHeading Copying INSTALL FILES
call :CopyFiles regasm.bat registerGateway.bat unregisterGateway.bat smtpreg.vbs agentsetup.vbs adsutil.vbs
exit /b %ERRORLEVEL%

@rem -------------------------------
:CopyFiles
for %%i in (%*) do (xcopy /y %%i %dest% || exit /b)
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
echo copybins "script" binPath destPath [backup: Y or N]
goto :Done

@rem -------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%
