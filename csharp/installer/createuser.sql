PRINT 'Creating user [$(DBUSER)]...'

USE [master]


CREATE LOGIN [$(DBUSER)] FROM WINDOWS WITH DEFAULT_DATABASE=[master]

USE [$(DBName)]
IF  NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'$(DBUSER)')
CREATE USER [$(DBUSER)] FOR LOGIN [$(DBUSER)]
EXEC sp_addrolemember N'db_datareader', N'$(DBUSER)'
EXEC sp_addrolemember N'db_datawriter', N'$(DBUSER)'
GO
