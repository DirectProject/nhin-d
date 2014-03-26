/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.Linq;
using System.Data.Linq.SqlClient;

namespace Health.Direct.Config.Store
{
    public static class PropertyQueries
    {
        const string Sql_DeleteProperty = "DELETE from Properties where Name = {0}";

        static readonly Func<ConfigDatabase, string, IQueryable<Property>> ValuesByName = CompiledQuery.Compile(
            (ConfigDatabase db, string name) =>
            from anchor in db.Properties
            where anchor.Name == name
            select anchor
            );
        static readonly Func<ConfigDatabase, string, IQueryable<Property>> ValuesByNameStartsWith = CompiledQuery.Compile(
            (ConfigDatabase db, string name) =>
            from property in db.Properties
            where property.Name.StartsWith(name)  // This translates to 'Like'
            select property
            );
        
        public static ConfigDatabase GetDB(this Table<Property> table)
        {
            return (ConfigDatabase) table.Context;
        }
        
        public static IQueryable<Property> Get(this Table<Property> table, string name)
        {
            return ValuesByName(table.GetDB(), name);
        }

        public static IQueryable<Property> Get(this Table<Property> table, string[] names)
        {
            return from property in table.GetDB().Properties
                   where names.Contains(property.Name)
                   select property;
        }
        
        public static IQueryable<Property> GetNameStartsWith(this Table<Property> table, string namePrefix)
        {
            return ValuesByNameStartsWith(table.GetDB(), namePrefix);
        }

        public static void ExecDelete(this Table<Property> table, string name)
        {
            table.Context.ExecuteCommand(Sql_DeleteProperty, name);
        }
    }
}
