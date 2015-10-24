Build a (LocalDB) instance for testing.  

Important to match SQLCMD version with sqlLocalDb.  See below I had to pick the SQLCMD.exe location.

SqlLocalDb create Projects -s
"C:\Program Files\Microsoft SQL Server\110\Tools\Binn\SQLCMD.exe" -S "(localdb)\Projects" -E -i createdatabase.sql -v DBName=DirectConfig
"C:\Program Files\Microsoft SQL Server\110\Tools\Binn\SQLCMD.exe" -S "(localdb)\Projects" -E -i "..\..\config\store\Schema.sql" -v DBName=DirectConfig
"C:\Program Files\Microsoft SQL Server\110\Tools\Binn\SQLCMD.exe" -S (localdb)\Projects -E -i seeddatabase.sql -v DBName=DirectConfig


LocalDB was easier to get installed on TeamCity server at CodeBetter.com.

