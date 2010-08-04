@echo off
setlocal

if "%PROCESSOR_ARCHITECTURE%"=="AMD64" (
	set binPath=%SystemRoot%\System32
) ELSE (
	set binPath=%SystemRoot%\SysWOW64
)

echo.
echo.
echo Registering %~f1
echo.

%binPath%\regsvr32.exe /s %~f1
if %ERRORLEVEL% EQU 0 goto :Done

echo ==========================================
echo.
echo Could not register %~f1
if "%PROCESSOR_ARCHITECTURE%" EQU "AMD64" echo Please confirm the Dll is 64 bit
echo.
echo ==========================================

:Done
endlocal
exit /b %ERRORLEVEL%
