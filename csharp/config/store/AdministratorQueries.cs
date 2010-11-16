using System;
using System.Data.Linq;
using System.Linq;

namespace Health.Direct.Config.Store
{
    public static class AdministratorQueries
    {
        static readonly Func<ConfigDatabase, string, IQueryable<Administrator>> Administrators = CompiledQuery.Compile(
            (ConfigDatabase db, string username) =>
            from administrator in db.Administrators
            where administrator.Username == username
            select administrator
            );

        static readonly Func<ConfigDatabase, string, int, IQueryable<Administrator>> AllAdministrators = CompiledQuery.Compile(
            (ConfigDatabase db, string lastUsername, int maxResults) =>
            (from administrator in db.Administrators
             //where lastUsername.CompareTo(administrator.Username) > 0
             orderby administrator.Username
             select administrator).Take(maxResults)
            );
        
        static readonly Func<ConfigDatabase, long, IQueryable<Administrator>> IDToAdministrator = CompiledQuery.Compile(
            (ConfigDatabase db, long administratorID) =>
            from administrator in db.Administrators
            where administrator.ID == administratorID
            select administrator
            );
        
        public static ConfigDatabase GetDB(this Table<Administrator> table)
        {
            return (ConfigDatabase)table.Context;
        }

        public static Administrator Get(this Table<Administrator> table, string username)
        {
            return Administrators(GetDB(table), username).SingleOrDefault();
        }

        public static IQueryable<Administrator> Get(this Table<Administrator> table, string lastUsername, int maxResults)
        {
            return AllAdministrators(GetDB(table), lastUsername, maxResults);
        }

        public static Administrator Get(this Table<Administrator> table, long administratorID)
        {
            return IDToAdministrator(GetDB(table), administratorID).SingleOrDefault();
        }
        
        public static void ExecDelete(this Table<Administrator> table, string username)
        {
            table.Context.ExecuteCommand("DELETE from Administrators where Username = {0}", username);
        }

        public static void ExecSetStatus(this Table<Address> table, string username, EntityStatus status)
        {
            table.Context.ExecuteCommand("UPDATE Administrators set Status={0}, UpdateDate={1} where Username={2}", status, DateTimeHelper.Now, username);
        }
    }
}