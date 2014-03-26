@rem ----------------------------------------------------
@rem Batch File for Deploying Dns Service binaries
@rem ----------------------------------------------------
@echo off

setlocal

cd ..
set bin=%cd%
set dest=%1

if "%dest%"=="" set dest=C:\inetpub\DirectDnsWebService

call :EnsureDirs
if %ERRORLEVEL% NEQ 0 goto :Error

pushd %bin%
call :CopyBins
popd

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
call :CopyFiles *.svc 
call :CopyFiles *.cs
call :CopyFiles *.xml 
call :CopyFiles *.aspx
call :CopyFiles *.config
pushd bin
xcopy /y * "%dest%"\bin\
popd

exit /b %ERRORLEVEL%

@rem -------------------------------
:CopyFiles
for %%i in (%*) do (xcopy /y %%i "%dest%" || exit /b)
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
echo copybins binPath
goto :Done

@rem -------------------------------
:Done
endlocal
exit /b %ERRORLEVEL%
