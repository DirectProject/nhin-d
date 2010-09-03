@rem Trivial batch file to move messages
@echo off
setlocal

set src=%1
set dest=%2
set interval=%3

if "%src%" == "" set src=C:\inetpub\mailroot\sendmail
if "%dest%" == "" set dest=Z:\
if "%interval%" == "" set interval=5

@rem ----------------------------------------------
:Loop
call :Transport
sleep %interval%
goto :Loop


@rem ----------------------------------------------
:Transport
call :Log "Transport Begin"

pushd %src%
move /y *.eml %dest%
popd 

call :Log "Transport End"
goto :EOF

@rem ----------------------------------------------
:Log
echo %date%%time%, %1
goto :EOF

@rem ----------------------------------------------
:Usage
echo filetransport srcFolderPath destFolderPath
goto :Done

@rem ----------------------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%