@ECHO OFF

SET frameworkpath=%windir%\microsoft.net\framework64\v4.0.30319
SET arch=%1%
IF "%1" == "" SET arch=%PROCESSOR_ARCHITECTURE%

IF "%arch%" == "x86" SET frameworkpath=%windir%\microsoft.net\framework\v4.0.30319

IF NOT EXIST %frameworkpath% GOTO :FrameworkMissing

IF "%VS110COMNTOOLS%"=="" GOTO :VisualStudioMissing

SET vcvarsallpath=%VS110COMNTOOLS%..\..\VC\vcvarsall.bat

IF NOT EXIST "%vcvarsallpath%" GOTO :VCVarsMissing

CALL "%vcvarsallpath%" %arch%

PATH "%frameworkpath%";%PATH%

GOTO :Finished

:FrameworkMissing
ECHO Unable to find .NET 4.5 framework path. Is .NET 4.5 installed? 
GOTO :EOF

:VisualStudioMissing
ECHO Unable to find VS110COMNTOOLS environment variable. Is Visual Studio 2012 installed?
GOTO :EOF

:VCVarsMissing
ECHO Unable to find VS110COMNTOOLS environment variable. Is Visual Studio 2012 installed?
GOTO :EOF

:Finished
ECHO Added %frameworkpath% to path...
GOTO :EOF
