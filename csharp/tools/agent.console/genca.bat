@rem Cert generation
@echo off

if "%1" == "" goto :Usage
if "%2" == "" goto :Usage

call :MakeCert %1 %2
goto :Done

:MakeCert
makecert -pe -n "CN=%1" -ss my -sr LocalMachine -a sha1 -sky signature -r "%2\%1.cer"
goto :EOF

:Usage
echo Generate a CA cert
echo genca name
goto :Done

:Done
endlocal
exit /b %ERRORLEVEL%


