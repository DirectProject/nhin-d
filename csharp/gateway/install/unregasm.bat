@echo off
setlocal

if "%PROCESSOR_ARCHITECTURE%"=="x86" (
	set fxpath=%SystemRoot%\Microsoft.NET\Framework\v2.0.50727\RegAsm.exe
) ELSE (
	set fxpath=%SystemRoot%\Microsoft.NET\Framework64\v2.0.50727\RegAsm.exe
)

%fxpath% "%~f1" /tlb:%~n1.tlb /unregister

endlocal