@rem Install DB::  Convenient method to install db while on a windows 7 machine without smtp.
@echo off
setlocal


set sqlSchemaFile=..\..\config\store\Schema.sql
set sqlUsersFile=..\..\installer\createuser.sql
set sqlReadonlyUsersFile=..\..\installer\createReadOnlyUser.sql

call :InstallDb
if %ERRORLEVEL% NEQ 0 goto :Done


@rem --------------------------------
:InstallDb
mkdir log
call createdatabase.bat .\sqlexpress DirectConfig %sqlSchemaFile% %sqlUsersFile% %sqlReadonlyUsersFile%
goto :EOF


@rem --------------------------------
:Done
endlocal
popd
exit /b %ERRORLEVEL%