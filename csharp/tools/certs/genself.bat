@rem Endpoint Cert generation
@echo off
setlocal

set cn=%~1
set outputPath=%2

if "%cn%" == "" goto :Usage
if "%outputpath%" == "" goto :Usage

makecert -r -pe -n "CN=%cn%,E=%cn%" -ss My -sr LocalMachine -a sha1 -sky signature %outputPath%\%cn%.cer -m 18 -eku "1.3.6.1.5.5.7.3.4"

goto :EOF

:Usage
echo *************************************
echo.
echo Generate a self-signed end cert
echo gencert domainName outputFilePath
echo   DomainName:  CN= this name
echo   outputFilePath: Write a cer file containing the cert here
echo.
echo Your private key is automatically saved to your LocalMachine Personal Store. 
echo. 
echo Your CA certificate must be in your Local Machine "Personal" certificate store
echo.
echo The certificate usage is set to Email
echo.
echo *************************************
goto :Done

:Done
endlocal
exit /b %ERRORLEVEL%


