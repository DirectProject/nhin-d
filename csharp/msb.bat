echo off
setlocal
set default_buildfile=build.xml

rem this is here if we want to support different names of the build file in the future
if exist %default_buildfile% (
	set buildfile=%default_buildfile%
)

if not exist %buildfile% goto :error_missing_buildfile

for %%i in (%*) do call :append_arg %%i
msbuild %buildfile%%options%
goto :done

:append_arg
set options=%options% -t:%1
goto :eof

:error_missing_buildfile
echo Missing build file - %buildfile%!
goto :eof

:done
endlocal
goto :eof
