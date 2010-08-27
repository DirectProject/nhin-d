@rem ----------------------------------------------------
@rem Batch File for Deploying New NHIND Config Service Binaries
@rem ----------------------------------------------------
@echo off

setlocal

if "%1"=="script" (
set dest=%2
set backup=%3
set bin=..\service
) else (
call :AskVariables
)

if "%dest%"=="" goto :Usage
if "%bin%"=="" goto :Usage
if "%backup%"=="" goto :Usage

call :Restart
if %ERRORLEVEL% NEQ 0 goto :Error

if /I "%backup%" EQU "Y" call :BackupCurrent

call :EnsureDirs
if %ERRORLEVEL% NEQ 0 goto :Error

pushd %bin%
call :CopyBins
popd

goto :Done

@rem -------------------------------
:Restart
call :PrintHeading Restarting IIS
iisreset
goto :EOF

@rem -------------------------------
:AskVariables
set /P backup=Backup Current? [Y/N  Return: N]
if "%backup%"=="" set backup=N

set /P dest=Destination Path [Return: default]
if "%dest%"=="" set dest=C:\inetpub\nhindConfigService

set bin=..\service
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
call :CopyFiles *.cs *.svc *.xml
xcopy /y *.config %dest%
pushd bin
xcopy /y * %dest%\bin
popd
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
