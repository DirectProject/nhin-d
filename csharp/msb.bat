@echo off
setlocal
set default_buildfile=build.xml
set msbuild_verbosity=/v:minimal

if "%1" EQU "help" goto :help

rem this is here if we want to support different names of the build file in the future
if exist %default_buildfile% (
	set buildfile=%default_buildfile%
)

if not exist %buildfile% goto :error_missing_buildfile

for %%i in (%*) do call :append_arg %%i
msbuild %msbuild_verbosity% %buildfile%%options%
goto :done

:append_arg
if "%1" EQU "verbose" (
	set msbuild_verbosity=/v:normal
) else (
  if "%1" NEQ "help" set options=%options% -t:%1
)
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
