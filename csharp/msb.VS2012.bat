@echo off
setlocal
set default_buildfile=build.VS2012.xml
set msbuild_verbosity=/v:minimal

if "%1" EQU "help" goto :help

call :check_environment

rem this is here if we want to support different names of the build file in the future
if exist %default_buildfile% (
	set buildfile=%default_buildfile%
)

if not exist %buildfile% goto :error_missing_buildfile

for %%i in (%*) do call :append_arg %%i
echo on
msbuild %msbuild_verbosity% %buildfile%%options%
@echo off
goto :done

:append_arg
set arg=%1
if "%arg%" EQU "verbose" (
	set msbuild_verbosity=/v:normal
) else if "%arg%" NEQ "help" (
	set options=%options% -t:%arg%
)
goto :eof

@rem determine if msbuild is in the path...
:check_environment
msbuild /? > nul 2> nul
if %ERRORLEVEL% equ 0 goto :eof
call setenv.bat
msbuild /? > nul 2> nul
if %ERRORLEVEL% equ 0 goto :eof
exit /b %ERRORLEVEL%
goto :eof

:error_missing_buildfile
echo Missing build file - %buildfile%!
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
