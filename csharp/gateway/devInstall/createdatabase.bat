@echo off
setlocal
title Create Database for the Configuration Service

set schemafile=%~3
set userfile=%~4
set userReadOnlyfile=%~5
set dbuser="IIS APPPOOL\ASP.NET v4.0"
set dbMonitorUser="NT AUTHORITY\NETWORK SERVICE"

call :initialize %~1 %~2
call :create_database
goto :finished

:initialize
set server=%~1
set credentials=-E
set databasename=%~2
goto :eof

:create_database
@echo on
del /F/Q %databasename%.tmp %databasename%.sql
echo USE [%databasename%] > %databasename%.tmp
type %databasename%.tmp > %databasename%.sql & type "%schemafile%" >> %databasename%.sql
sqlcmd -S "%server%" %credentials% -i createdatabase.sql -v DBName = %databasename% -o ".\log\createDatabase.log"
@if ERRORLEVEL 1 goto :error
sqlcmd -S "%server%" %credentials% -i %databasename%.sql -v DBName = %databasename% -o ".\log\databaseSchema.log"
@if ERRORLEVEL 1 goto :error
sqlcmd -S "%server%" %credentials% -i "%userfile%" -v DBUSER = %dbuser% -v DBName = %databasename% -o ".\log\createDbUsers.log"
sqlcmd -S "%server%" %credentials% -i "%userReadOnlyFile%" -v DBUSER = %dbMonitorUser% -v DBName = %databasename% -o ".\log\createDbUsers.log"
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
