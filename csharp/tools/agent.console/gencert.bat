@rem Endpoint Cert generation
@echo off

if "%1" == "" goto :Usage
if "%2" == "" goto :Usage
if "%3" == "" goto :Usage

call :MakeCert %1 %2 %3
goto :Done

:MakeCert
makecert -pe -n "CN=%1" -ss my -sr LocalMachine -a sha1 -sky signature -in "%2" -is MY -ir LocalMachine %3\%1.cer 
goto :EOF

:Usage
echo Generate an end cert signed by a CA
echo gencert name CA outputFilePath
echo     Currently, the CA must be in your local machine "Personal" store
echo     If you used genca.bat, it is
echo.
goto :Done

:Done
endlocal
exit /b %ERRORLEVEL%


