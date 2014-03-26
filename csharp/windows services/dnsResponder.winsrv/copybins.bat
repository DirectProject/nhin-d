@rem ----------------------------------------------------
@rem Batch File for Deploying New Gateway Binaries
@rem ----------------------------------------------------
@echo off

setlocal

set dest=%1
set bin=%2


if "%dest%"=="" set dest=C:\inetpub\DirectDnsServer
if "%bin%"=="" set bin=..\..\bin\debug

if /I "%backup%" EQU "Y" call :BackupCurrent

call :EnsureDirs
if %ERRORLEVEL% NEQ 0 goto :Error

pushd %bin%
call :CopyBins
popd
if %ERRORLEVEL% NEQ 0 goto :Error
xcopy /y /d install.bat "%dest%"

goto :Done

@rem -------------------------------
:EnsureDirs
echo Ensuring Directories
if not exist %dest% md %dest%
pushd %dest%
del /s /q *
popd 
goto :EOF

@rem -------------------------------
:CopyBins
call :PrintHeading Copying BINS to "%dest%"

call :CopyFiles Health.Direct.Common.dll ^
  Health.Direct.Config.Client.dll ^
  Health.Direct.Config.Store.dll ^
  Nlog.dll ^
  Health.Direct.Diagnostics.NLog.dll ^
  Health.Direct.DnsResponder.dll ^
  Health.Direct.DnsResponder.dll ^
  DirectDnsResponderSvc.exe ^
  DirectDnsResponderSvc.exe.config ^

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
echo copybins destPath binPath
goto :Done

@rem -------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%
