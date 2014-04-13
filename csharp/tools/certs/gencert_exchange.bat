@rem Endpoint Cert generation
@rem Generate a Cert marked for "exchange" instead of just "signature"
@echo off
setlocal

set cn=%~1
set ca=%~2

if "%cn%" == "" goto :Usage
if "%ca%" == "" goto :Usage
if "%3" == "" goto :Usage

call :MakeCert %1 %2 %3
goto :Done

:MakeCert
makecert -pe -n "CN=%cn%,E=%cn%" -ss my -sr LocalMachine -a sha1 -sky exchange -in "%ca%" -is MY -cy end -ir LocalMachine %3\%cn%.cer -m 18 -eku "1.3.6.1.5.5.7.3.4"

goto :EOF

:Usage
echo *************************************
echo.
echo Generate an end cert signed by a CA
echo gencert SubjectName CASubjectName outputFilePath
echo   SubjectName:  CN= this name
echo   CASubjectName: Your CA cert has CN= this name
echo   outputFilePath: Write a cer file containing the cert here
echo.
echo Your private key is automatically saved to your LocalMachine Personal Store. 
echo. 
echo Your CA certificate must be in your Local Machine "Personal" certificate store
echo If you used genca.bat to generate your CA, it was automatically placed there
echo.
echo The certificate usage is set to Email
echo.
echo *************************************
goto :Done

:Done
endlocal
exit /b %ERRORLEVEL%


