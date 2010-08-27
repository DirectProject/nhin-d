@rem Batch file to copy test files into appropriate folders
@rem Execute as a custom build step
@echo off

if "%1"=="" goto :Usage

setlocal
set this=%~p0
set bin=%~f1

call :CopyFiles TestMessages
call :CopyFiles Certificates force
goto :Done

@rem--------------------------------------------------
:CopyFiles
if "%2" == "force" (if exist %bin%\%1 rmdir /s /q %bin%\%1)
if not exist %bin%\%1 md %bin%\%1
pushd %this%%1
xcopy /s /y /d * %bin%%1\
popd
goto :EOF

@rem--------------------------------------------------
:Usage
echo CopyTestFiles binPath
goto :Done

@rem--------------------------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%
