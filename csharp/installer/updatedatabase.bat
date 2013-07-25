@echo off
setlocal
title Update Database for the Configuration Service

set schemafile=%~3
set dbuser="IIS AppPool\DefaultAppPool"

call :initialize %~1 %~2
call :update_database
goto :finished

:initialize
set /p server=Server and instance name (DEFAULT '%~1')? 
if "%server%"=="" set server=%~1

set /p sqluser=Username to connect (DEFAULT use trusted connection)? 
if "%sqluser%" NEQ "" set /p sqlpass=Password for '%sqluser%' (DEFAULT use trusted connection)?
if "%sqlpass%"=="" set credentials=-E
if "%credentials%"=="" set credentials=-U %sqluser% -P %sqlpass%

set /p databasename=Database name (DEFAULT '%~2')? 
if "%databasename%"=="" set databasename=%~2

if "%sqluser%" EQU "" set /p CONFIRM=Are these values correct - server=%server% using Trusted Connection? (DEFAULT=Y) 
if "%sqluser%" NEQ "" set /p CONFIRM=Are these values correct - server=%server% user=%sqluser% password=%sqlpass%? (DEFAULT=Y) 
if "%CONFIRM%" EQU "" set CONFIRM=Y
if "%CONFIRM%" NEQ "Y" goto :initialize
goto :eof

:update_database
@echo on
del /F/Q mdnMonitorSchema.tmp mdnMonitorSchema.sql
echo USE [%databasename%] > mdnMonitorSchema.tmp
type "%schemafile%" >> mdnMonitorSchema.sql
sqlcmd -S "%server%" %credentials% -i mdnMonitorSchema.sql -v DBName = %databasename%
@if ERRORLEVEL 1 goto :error
@echo off
goto :eof

:error
@echo An error occurred while creating the database.
@pause
goto :eof

:finished
rem: del /F/Q mdnMonitorSchema.tmp mdnMonitorSchema.sql
if "%DEBUGINSTALLER%" == "1" pause
