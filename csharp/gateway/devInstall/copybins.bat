@rem Copy all bits for the development gateway
@echo off
setlocal

set destbin=%1

set srcbin=..\..\bin\debug
if "%destbin%"=="" set destbin=C:\inetpub\nhinGateway

@rem ----------------------------------
call :PrintHeading "Copying from %srcbin% to %destbin%"
pushd ..\install
call copybins.bat script %srcbin% %destbin% N
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded
popd

for %%i in (DevAgentConfig.xml install.bat simple.eml setupdomains.txt) do xcopy /y /q /d %%i %destbin%

@rem --------------------------------
call :PrintHeading "Copying Certificates"
pushd %srcbin% 
if not exist %destbin%\Certificates md %destbin%\Certificates
xcopy /s /y /d Certificates\* %destbin%\Certificates
if %ERRORLEVEL% NEQ 0 goto :Done
Echo Succeeded
popd

goto :Done

@rem -------------------------------
:PrintHeading
shift
echo ==============================
echo.
echo %*
echo.
echo ==============================
goto :EOF

:Usage
echo copybins [destbin]
goto :Done

:Done
endlocal
popd
exit /b %ERRORLEVEL%