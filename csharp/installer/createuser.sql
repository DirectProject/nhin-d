PRINT 'Creating user [$(DBUSER)]...'

USE [master]
CREATE LOGIN [$(DBUSER)] FROM WINDOWS WITH DEFAULT_DATABASE=[master]

USE [$(DBName)]
CREATE USER [$(DBUSER)] FOR LOGIN [$(DBUSER)]
EXEC sp_addrolemember N'db_datareader', N'$(DBUSER)'
EXEC sp_addrolemember N'db_datawriter', N'$(DBUSER)'
GO
