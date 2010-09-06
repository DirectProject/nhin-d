@ECHO OFF

SETLOCAL

SET vcredist=vcredist_x86.exe
IF "%PROCESSOR_ARCHITECTURE%" == "AMD64" SET vcredist=vcredist_x64.exe

%vcredist%
