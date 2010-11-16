@rem Endpoint Cert generation
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
makecert -pe -n "CN=%cn%" -ss my -sr LocalMachine -a sha1 -sky signature -in "%ca%" -is MY -cy end -ir LocalMachine %3\%cn%.cer 
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
echo *************************************
goto :Done

:Done
endlocal
exit /b %ERRORLEVEL%


