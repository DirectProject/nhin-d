@ECHO OFF

SET frameworkpath=%windir%\microsoft.net\framework64\v3.5

IF "%PROCESSOR_ARCHITECTURE%" == "x86" SET frameworkpath=%windir%\microsoft.net\framework\v3.5

IF NOT EXIST %frameworkpath% GOTO :FrameworkMissing

PATH "%frameworkpath%";%PATH%

IF "%VS90COMNTOOLS%"=="" GOTO :VisualStudioMissing

SET vcvarsallpath=%VS90COMNTOOLS%..\..\VC\vcvarsall.bat

IF NOT EXIST "%vcvarsallpath%" GOTO :VCVarsMissing

CALL "%vcvarsallpath%" %PROCESSOR_ARCHITECTURE%

GOTO :Finished

:FrameworkMissing
ECHO Unable to find .NET 3.5 framework path. Is .NET 3.5 installed? 
GOTO :EOF

:VisualStudioMissing
ECHO Unable to find VS90COMNTOOLS environment variable. Is Visual Studio 2008 installed?
GOTO :EOF

:VCVarsMissing
ECHO Unable to find VS90COMNTOOLS environment variable. Is Visual Studio 2008 installed?
GOTO :EOF

:Finished
ECHO Added %frameworkpath% to path...
GOTO :EOF
