@echo off
setlocal
set msbuild_verbosity=/v:minimal

if "%1" EQU "help" goto :help

set MAJOR=1
set MINOR=0
set BUILD=0
set REVISION=0

call :check_environment

:enter-version-info
echo Enter the version information:
set /p MAJOR=  MAJOR (DEFAULT %MAJOR%)? 
set /p MINOR=  MINOR (DEFAULT %MINOR%)? 
set /p BUILD=  BUILD (DEFAULT %BUILD%)? 
set /p REVISION=  REVISION (DEFAULT %REVISION%)? 

if "%MAJOR%" EQU "" set MAJOR=1
if "%MINOR%" EQU "" set MINOR=0
if "%BUILD%" EQU "" set BUILD=0
if "%REVISION%" EQU "" set REVISION=0

set /p CONFIRM=Use '%MAJOR%.%MINOR%.%BUILD%.%REVISION%' as the version info? (DEFAULT=Y) 
if "%CONFIRM%" EQU "" set CONFIRM=Y
if "%CONFIRM%" NEQ "Y" goto :enter-version-info

@rem hg commit -m "Advancing version number to %major%.%minor%.%build%.%revision%..."

msbuild %msbuild_verbosity% ..\build.xml -t:prepare-installer
msbuild %msbuild_verbosity% installer-build.xml -p:MAJOR=%major% -p:MINOR=%minor% -p:BUILD=%build% -p:REVISION=%revision% -t:build-installer
goto :done

@rem determine if msbuild is in the path...
:check_environment
msbuild /? > nul 2> nul
if %ERRORLEVEL% equ 0 goto :eof
call setenv.bat
msbuild /? > nul 2> nul
if %ERRORLEVEL% equ 0 goto :eof
exit /b %ERRORLEVEL%
goto :eof

:help
echo usage: msb [target] ... [targetN]
echo            execute one or more targets found in %buildfile%
echo.
echo        msb verbose [target] ... [targetN]
echo            same as above with more verbose logging
echo.
echo        msb help
echo            this message
echo.
exit /B

:done
endlocal
goto :eof
