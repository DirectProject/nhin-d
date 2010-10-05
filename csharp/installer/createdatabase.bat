setlocal
set server=%~1
set databasename=%~2
set schemafile=%~3
set userfile=%~4
set dbuser="IIS AppPool\DefaultAppPool"

sqlcmd -S "%server%" -E -Q "CREATE DATABASE %databasename%"
sqlcmd -S "%server%" -E -i "%schemafile%"
sqlcmd -S "%server%" -E -i "%userfile%" -v DBUSER = %dbuser%
