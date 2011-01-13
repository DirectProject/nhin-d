/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
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
             where administrator.Username.CompareTo(lastUsername) > 0
             orderby administrator.Username
             select administrator).Take(maxResults)
            );
        
        static readonly Func<ConfigDatabase, long, IQueryable<Administrator>> IDToAdministrator = CompiledQuery.Compile(
            (ConfigDatabase db, long administratorID) =>
            from administrator in db.Administrators
            where administrator.ID == administratorID
            select administrator
            );

        //static readonly Func<ConfigDatabase, string, string, IQueryable<Administrator>> CheckPassword = CompiledQuery.Compile(
        //    (ConfigDatabase db, string username, string passwordHash) =>
        //    from administrator in db.Administrators
        //    where username == administrator.Username && passwordHash == administrator.PasswordHashDB
        //    select administrator
        //    );

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
        
        //public static bool CheckPasswordHash(this Table<Administrator> table, string username, string passwordHash)
        //{
        //    return CheckPassword(GetDB(table), username, passwordHash).Any();
        //}

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