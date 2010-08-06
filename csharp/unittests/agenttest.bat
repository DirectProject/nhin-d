@rem Run specific test, since at some point all MAY be too many..
@echo off

call runtest.bat agentUnitTests.dll AgentTests.BasicAgentTests
if %ERRORLEVEL% NEQ 0 goto :Error

call runtest.bat agentUnitTests.dll AgentTests.AgentConfigTest
if %ERRORLEVEL% NEQ 0 goto :Error

call runtest.bat SmtpAgentUnitTests.dll SmtpAgentTests.TestSmtpAgent
if %ERRORLEVEL% NEQ 0 goto :Error

goto :Done

@rem-------------------------------
:Done
exit /b %ERRORLEVEL%