@rem Cert generation
@echo off
setlocal

set cn=%~1

if "%cn%" == "" goto :Usage
if "%2" == "" goto :Usage

call :MakeCert %1 %2
goto :Done

:MakeCert
makecert -pe -n "CN=%cn%" -ss my -sr LocalMachine -a sha1 -sky signature -cy authority -r "%2\%cn%.cer"
goto :EOF

:Usage
echo ***********************************
echo Generate a Root cert. 
echo.
echo genca name outputFilePath
echo    name: Your CA's cert's name. Sets the CN= property
echo    outputFolderPath: Write a cer file containing the certificate to this Folder
echo.
echo The private key for your cert is placed in your LocalMachine Cert Store
echo That lets you use the CA cert to issue new end certificates by using genca.bat
echo.
echo ***********************************
goto :Done

:Done
endlocal
exit /b %ERRORLEVEL%


