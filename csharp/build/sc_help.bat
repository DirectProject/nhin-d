@ECHO OFF
SETLOCAL

SET proj_name=%~n1
SET template=vs2005

IF "%proj_name%" == "" GOTO :MissingLibraryName

REM Determine the location of the appropriate Program Files directory
REM ------------------------------------------------------------------
SET programfiles32=%PROGRAMFILES(x86)%
IF "%programfiles32%" == "" SET programfiles32=%PROGRAMFILES%

REM Define common properties
REM ------------------------
SET source_lib=%~f1
SET source_xml=%~p1\%~n1.xml
SET target_chm=%proj_name%.chm
SET lcid=1033
SET output_path=%CD%\temp_help
SET chm_path=%output_path%\chm
SET htmlworkshop=%programfiles32%\HTML Help Workshop

SET sandcastle=%programfiles32%\Sandcastle
SET sandcastle_xforms=%sandcastle%\ProductionTransforms
SET sandcastle_presentation=%sandcastle%\Presentation
SET sandcastle_config=%sandcastle_presentation%\%template%\configuration\sandcastle.config

REM Add HTML Workshop and Sandcastle to the path (SETLOCAL keeps this from infecting your regular PATH variable)
REM ------------------------------------------------------------------------------------------------------------
PATH %htmlworkshop%;%DXROOT%\ProductionTools;%PATH%

REM Create the temporary working directory
REM --------------------------------------
IF EXIST "%output_path%" RMDIR /s/q "%output_path%"
MKDIR "%output_path%"
PUSHD "%output_path%"

REM Start the processing of the library file...
MRefBuilder "%source_lib%" "/out:%CD%\reflection.org"

REM Transform the file through a series of XSLTs. More of these could be specified, check the transforms directory of Sandcastle
REM ----------------------------------------------------------------------------------------------------------------------------
COPY "%source_xml%" "%CD%\comments.xml"
XslTransform "/xsl:%sandcastle_xforms%\ApplyVSDocModel.xsl" "%CD%\reflection.org" "/xsl:%sandcastle_xforms%\AddFriendlyFilenames.xsl" "/out:%CD%\temp.xml" /arg:IncludeAllMembersTopic=false /arg:IncludeInheritedOverloadTopics=false
XslTransform "/xsl:%sandcastle_xforms%\AddGuidFilenames.xsl" "%CD%\temp.xml" "/out:%CD%\reflection.xml"
XslTransform "/xsl:%sandcastle_xforms%\ReflectionToManifest.xsl" "%CD%\reflection.xml" "/out:%CD%\manifest.xml"
XslTransform "/xsl:%sandcastle_xforms%\CreateVSToc.xsl" "%CD%\reflection.xml" "/out:%CD%\toc.xml"

REM Create target directories and copy from the sandcastle template
REM ---------------------------------------------------------------
IF NOT EXIST "%chm_path%\icons" MKDIR "%chm_path%\icons"
IF NOT EXIST "%chm_path%\scripts" MKDIR "%chm_path%\scripts"
IF NOT EXIST "%chm_path%\styles" MKDIR "%chm_path%\styles"

COPY "%sandcastle_presentation%\%template%\icons\*" "%chm_path%\icons"
COPY "%sandcastle_presentation%\%template%\scripts\*" "%chm_path%\scripts"
COPY "%sandcastle_presentation%\%template%\styles\*" "%chm_path%\styles"

REM Build the output for the CHM file
REM ---------------------------------
BuildAssembler /config:"%sandcastle_config%" "%CD%\manifest.xml"
XslTransform "%sandcastle_xforms%\ReflectionToChmContents.xsl" "%CD%\reflection.xml" /arg:html=html "/out:%CD%\test.hhc"

ChmBuilder /project:%proj_name% "/html:%output_path%\Output\html" "/lcid:%lcid%" "/toc:%CD%\toc.xml" "/out:%chm_path%"
DBCSFix "/d:%chm_path%" "/l:%lcid%"
HHC "%chm_path%\%proj_name%.hhp"

COPY "%chm_path%\%target_chm%" "%CD%\.."
GOTO :Finished

:MissingLibraryName
ECHO No source library path was supplied. 
ECHO Please be sure to include the path to the library 
ECHO that you want the help file built from.
GOTO :EOF

:Finished
POPD
RMDIR /s /q "%output_path%"
GOTO :EOF
