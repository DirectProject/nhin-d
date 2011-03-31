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
using Health.Direct.Common.Extensions;

namespace Health.Direct.Config.Store
{
    /// <summary>
    /// Simple Property Store
    /// </summary>
    public class PropertyManager : IEnumerable<Property>
    {
        readonly ConfigStore m_store;
        
        internal PropertyManager(ConfigStore store)
        {
            m_store = store;
        }
        
        internal ConfigStore Store
        {
            get
            {
                return m_store;
            }
        }

        public void Add(string name, string value)
        {
            this.Add(new Property(name, value));
        }
        
        public void Add(Property property)        
        {
            if (property == null)
            {
                throw new ArgumentNullException("property");
            }
            
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, property);
                db.SubmitChanges();
            }
        }
        
        public void Add(ConfigDatabase db, Property property)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            db.Properties.InsertOnSubmit(property);
        }

        public void Add(IEnumerable<Property> properties)
        {
            using(ConfigDatabase db = this.Store.CreateContext())
            {
                this.Add(db, properties);
                db.SubmitChanges();
            }
        }

        public void Add(ConfigDatabase db, IEnumerable<Property> properties)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            
            db.Properties.InsertAllOnSubmit(properties);
        }
        
        public Property Get(string name)
        {
            using(ConfigDatabase db = this.Store.CreateReadContext())
            {
                return Get(db, name);
            }
        }
        
        public Property Get(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("name");
            }
            
            return db.Properties.Get(name).SingleOrDefault();
        }

        public Property[] Get(string[] names)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return Get(db, names).ToArray();
            }
        }

        public IEnumerable<Property> Get(ConfigDatabase db, string[] names)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (names.IsNullOrEmpty())
            {
                throw new ArgumentException("names");
            }

            return db.Properties.Get(names);
        }

        public Property[] GetStartsWith(string namePrefix)
        {
            using (ConfigDatabase db = this.Store.CreateReadContext())
            {
                return GetStartsWith(db, namePrefix).ToArray();
            }
        }

        public IEnumerable<Property> GetStartsWith(ConfigDatabase db, string namePrefix)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(namePrefix))
            {
                throw new ArgumentException("name");
            }

            return db.Properties.GetNameStartsWith(namePrefix);
        }

        public void Set(Property property)
        {
            if (property == null)
            {
                throw new ArgumentNullException("property");
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                Set(db, property);
                db.SubmitChanges();
            }
        }

        public void Set(IEnumerable<Property> properties)
        {
            if (properties == null)
            {
                throw new ArgumentNullException("properties");
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (Property property in properties)
                {
                    Set(db, property);
                }
                db.SubmitChanges();
            }
        }
        
        protected void Set(ConfigDatabase db, Property property)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }

            Property existing = this.Get(db, property.Name);
            if (existing == null)
            {
                db.Properties.InsertOnSubmit(property);
            }
            else
            {
                db.Properties.Attach(existing);
                existing.Value = property.Value;
            }
        }
        
        public void Remove(string name)
        {
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("name");
            }
            
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                this.Remove(db, name);
            }
        }

        public void Remove(string[] names)
        {
            if (names.IsNullOrEmpty())
            {
                throw new ArgumentException("names");
            }

            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach(string name in names)
                {
                    Remove(db, name);
                }
            }
        }

        public void Remove(ConfigDatabase db, string name)
        {
            if (db == null)
            {
                throw new ArgumentNullException("db");
            }
            if (string.IsNullOrEmpty(name))
            {
                throw new ArgumentException("name");
            }
            
            db.Properties.ExecDelete(name);
        }
        
        public IEnumerator<Property> GetEnumerator()
        {
            using (ConfigDatabase db = this.Store.CreateContext())
            {
                foreach (Property property in db.Properties)
                {
                    yield return property;
                }
            }
        }
    
        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}
