@rem Run specific test, since all MAY be too many..
@echo off

call :Run AgentTests.BasicAgentTests
if %ERRORLEVEL% NEQ 0 goto :Error

call :Run AgentTests.AgentConfigTest
if %ERRORLEVEL% NEQ 0 goto :Error

goto :Done

@rem-------------------------------
:Run
call runtest.bat agentUnitTests.dll %1
goto :EOF

@rem-------------------------------
:Done
exit /b %ERRORLEVEL%