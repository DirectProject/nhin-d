@echo off

setlocal

if "%1"=="" goto :Usage

call :EnsureBackupFolder %1
call :Backup %1
goto :Done

@rem --------------------------------------
:EnsureBackupFolder
echo Creating backup folder
set datestamp=%date%
set timestamp=%time%
set datestamp=%datestamp:~-4,4%%datestamp:~-10,2%%datestamp:~-7,2%
set timestamp=%timestamp:~0,2%%timestamp:~3,2%%timestamp:~6,2%
if "%timestamp:~0,1%"==" " set timestamp=%timestamp:~1%

set bakfolderPath=%1_%datestamp%_%timestamp%
if not exist %bakfolderPath% md %bakfolderPath%
goto :EOF

@rem --------------------------------------
:Backup
pushd %1
xcopy /y /d * %bakfolderPath%
popd
goto :EOF

@rem --------------------------------------
:Usage
echo Backs up the given folder path into a folder with the same name but with a timestamp appended
echo folderPath
goto :Done

@rem --------------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%