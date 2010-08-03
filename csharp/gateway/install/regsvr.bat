@echo off
setlocal

if "%PROCESSOR_ARCHITECTURE%"=="x86" (
	set binPath=%SystemRoot%\SysWOW64
) ELSE (
	set binPath=%SystemRoot%\System32
)
	
%binPath%\regsvr32.exe /s %~f1

endlocal
