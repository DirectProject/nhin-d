@echo off
setlocal
title Create Database for the Configuration Service

set schemafile=%~3
set userfile=%~4
set dbuser="IIS AppPool\DefaultAppPool"

call :initialize %~1 %~2
call :create_database
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
goto :eof

:create_database
@echo on
del /F/Q %databasename%.tmp %databasename%.sql
echo USE [%databasename%] > %databasename%.tmp
type %databasename%.tmp > %databasename%.sql & type "%schemafile%" >> %databasename%.sql
sqlcmd -S "%server%" %credentials% -Q "CREATE DATABASE [%databasename%]"
sqlcmd -S "%server%" %credentials% -i %databasename%.sql
@if ERRORLEVEL 1 goto :error
sqlcmd -S "%server%" %credentials% -i "%userfile%" -v DBUSER = %dbuser%
@if ERRORLEVEL 1 goto :error
@echo off
goto :eof

:error
@echo An error occurred while creating the database.
@pause
goto :eof

:finished
del /F/Q %databasename%.tmp %databasename%.sql
if "%DEBUGINSTALLER%" == "1" pause
