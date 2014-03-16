/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;

using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class DomainManagerFacts : ConfigStoreTestBase
    {
        private static new DomainManager CreateManager()
        {
            return new DomainManager(CreateConfigStore());
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            DomainManager target = CreateManager();
            ConfigStore actual = target.Store;
            Assert.Equal(target.Store, actual);
        }

        /// <summary>
        ///A test for RemoveAll
        ///</summary>
        [Fact]
        public void RemoveAllTest1()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT, target.Get(string.Empty, MAXDOMAINCOUNT + 1).Count());
            target.RemoveAll();
            Assert.Equal(0, target.Get(string.Empty, MAXDOMAINCOUNT + 1).Count());
        }

        /// <summary>
        ///A test for RemoveAll
        ///</summary>
        [Fact]
        public void RemoveAllTest()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT, target.Get(string.Empty, MAXDOMAINCOUNT + 1).Count());
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                target.RemoveAll(db);
            }
            Assert.Equal(0, target.Get(string.Empty, MAXDOMAINCOUNT + 1).Count());
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public void RemoveTest1()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string name = BuildDomainName(GetRndDomainID());
            Assert.NotNull(target.Get(name));
            target.Remove(name);
            Assert.Null(target.Get(name));
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public void RemoveTest()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string name = BuildDomainName(GetRndDomainID());
            Assert.NotNull(target.Get(name));
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                target.Remove(name);
                db.SubmitChanges();
            }
            Assert.Null(target.Get(name));
        }

        /// <summary>
        ///A test for GetEnumerator
        ///</summary>
        [Fact]
        public void GetEnumeratorTest()
        {
            InitDomainRecords();
            IEnumerable<Domain> mgr = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT, mgr.Count());
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest7()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string[] names = TestDomainNames.ToArray();
            Domain[] actual = target.Get(names);
            Assert.Equal(names.Length, actual.Length);
            foreach (Domain dom in actual)
            {
                Assert.True(names.Contains(dom.Name));
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest6()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string[] names = TestDomainNames.ToArray();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                Domain[] actual = target.Get(db,names).ToArray();
                Assert.Equal(names.Length, actual.Length);
                foreach (Domain dom in actual)
                {
                    Assert.True(names.Contains(dom.Name));
                }
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest5()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string name = BuildDomainName(GetRndDomainID());
            Domain actual = target.Get(name);
            Assert.Equal(name, actual.Name);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest4()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string name = BuildDomainName(GetRndDomainID());
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                Domain actual = target.Get(db, name);
                Assert.Equal(name, actual.Name);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest3Last()
        {
            InitDomainRecords();
            DomainManager mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the domain name as the key and pick one to start at
            Dictionary<string, Domain> mxsAll = mgr.ToDictionary(p => p.Name);

            Assert.Equal(MAXDOMAINCOUNT, mxsAll.Count);

            int pos = GetRndDomainID();
            //----------------------------------------------------------------------------------------------------
            //---grab the key at position pos-1 in the array, and use that as the "last" name to be passed in
            string[] allKeys = mxsAll.Keys.ToArray();
            string val = allKeys[pos - 1];

            Domain[] mxs = mgr.Get(val, MAXDOMAINCOUNT + 1);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - pos
            Assert.Equal(MAXDOMAINCOUNT - pos, mxs.Length);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than pos
            mxs = mgr.Get(allKeys[0], pos - 1);
            Assert.Equal(pos - 1, mxs.Length);
            
            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = allKeys.Last();
            mxs = mgr.Get(val, MAXDOMAINCOUNT + 1);
            Assert.Equal(0, mxs.Length);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = mxsAll.Keys.ToArray().First();
            mxs = mgr.Get(val, MAXDOMAINCOUNT + 1);
            Assert.Equal(MAXDOMAINCOUNT  - 1, mxs.Length);

        }

        [Fact]
        public void GetTest3First()
        {
            InitDomainRecords();
            DomainManager mgr = CreateManager();

            Domain[] mxs = mgr.Get(String.Empty, MAXDOMAINCOUNT + 1);

            //----------------------------------------------------------------------------------------------------
            //---expected that all of the records will be returned
            Assert.Equal(MAXDOMAINCOUNT, mxs.Length);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            int pos = GetRndDomainID();
            mxs = mgr.Get(String.Empty, pos - 1);
            Assert.Equal(pos - 1, mxs.Length);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest2Last()
        {
            InitDomainRecords();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                DomainManager mgr = CreateManager();

                //----------------------------------------------------------------------------------------------------
                //---get the full dictionary using the domain name as the key and pick one to start at
                Dictionary<string, Domain> mxsAll = mgr.ToDictionary(p => p.Name);

                Assert.Equal(MAXDOMAINCOUNT, mxsAll.Count);

                int pos = GetRndDomainID();
                //----------------------------------------------------------------------------------------------------
                //---grab the key at position pos-1 in the array, and use that as the "last" name to be passed in
                string[] allKeys = mxsAll.Keys.ToArray();
                string val = allKeys[pos - 1];

                Domain[] mxs = mgr.Get(db,val, MAXDOMAINCOUNT + 1).ToArray();

                //----------------------------------------------------------------------------------------------------
                //---expected that the count of mxs will be  max count - pos
                Assert.Equal(MAXDOMAINCOUNT - pos, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---try one with a limited number less than pos
                mxs = mgr.Get(db, allKeys[0], pos - 1).ToArray();
                Assert.Equal(pos - 1, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---get the last item and see to ensure that no records are returned
                val = allKeys.Last();
                mxs = mgr.Get(val, MAXDOMAINCOUNT + 1);
                Assert.Equal(0, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---get the first item and see to ensure that MAX - 1 records are returned
                val = allKeys.First();
                mxs = mgr.Get(db, val, MAXDOMAINCOUNT + 1).ToArray();
                Assert.Equal(MAXDOMAINCOUNT - 1, mxs.Length);
            }

        }

        [Fact]
        public void GetTest2First()
        {
            InitDomainRecords();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                DomainManager mgr = CreateManager();

                Domain[] mxs = mgr.Get(db,String.Empty, MAXDOMAINCOUNT + 1).ToArray();

                //----------------------------------------------------------------------------------------------------
                //---expected that all of the records will be returned
                Assert.Equal(MAXDOMAINCOUNT, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---try one with a limited number less than max count
                int pos = GetRndDomainID();
                mxs = mgr.Get(db,String.Empty, pos - 1).ToArray();
                Assert.Equal(pos - 1, mxs.Length);
            }
        }
        
        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest1()
        {
            InitDomainRecords();

            DomainManager mgr = CreateManager();

            string[] names = new[] { BuildDomainName(1), BuildDomainName(2), BuildDomainName(3) };

            //----------------------------------------------------------------------------------------------------
            //---new status should still yield 3 results
            Domain[] actual = mgr.Get( names, EntityStatus.New);
            Assert.Equal(names.Length, actual.Length);

            //----------------------------------------------------------------------------------------------------
            //---pass in null and expect matching results
            actual = mgr.Get( names, null).ToArray();
            Assert.Equal(names.Length, actual.Length);

            //----------------------------------------------------------------------------------------------------
            //---disabled status should still yield no results
            actual = mgr.Get( names, EntityStatus.Disabled);
            Assert.Equal(0, actual.Length);


            //----------------------------------------------------------------------------------------------------
            //---null pref should still yield same results
            actual = mgr.Get( names, EntityStatus.Enabled);
            Assert.Equal(0, actual.Length);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest()
        {

            InitDomainRecords();

            DomainManager mgr = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                string[] names = new[] { BuildDomainName(1), BuildDomainName(2), BuildDomainName(3) };

                //----------------------------------------------------------------------------------------------------
                //---new status should still yield 3 results
                Domain[] actual = mgr.Get(db, names, EntityStatus.New).ToArray();
                Assert.Equal(names.Length, actual.Length);

                //----------------------------------------------------------------------------------------------------
                //---pass in null and expect matching results
                actual = mgr.Get(db, names, null).ToArray();
                Assert.Equal(names.Length, actual.Length);

                //----------------------------------------------------------------------------------------------------
                //---disabled status should still yield no results
                actual = mgr.Get(db, names, EntityStatus.Disabled).ToArray();
                Assert.Equal(0, actual.Length);


                //----------------------------------------------------------------------------------------------------
                //---null pref should still yield same results
                actual = mgr.Get(db, names, EntityStatus.Enabled).ToArray();
                Assert.Equal(0, actual.Length);
            }
            
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact]
        public void CountTest()
        {
            InitDomainRecords();
            DomainManager mgr = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT, mgr.Count());
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest3()
        {
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                DomainManager target = CreateManager();
                target.RemoveAll();
                Assert.Equal(0, target.Count());
                string name = BuildDomainName(GetRndDomainID());
                target.Add(db, name);
                db.SubmitChanges();
                Assert.NotNull(target.Get(name));
            }
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest2()
        {
            DomainManager target = CreateManager();
            target.RemoveAll();
            Assert.Equal(0, target.Count());
            string name = BuildDomainName(GetRndDomainID());
            target.Add(name);
            Assert.NotNull(target.Get(name));
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest1()
        {
             
            DomainManager target = CreateManager();
            target.RemoveAll();
            Assert.Equal(0, target.Count());
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                string name = BuildDomainName(GetRndDomainID());
                Domain domain = new Domain(name);
                target.Add(db, domain);
                db.SubmitChanges();
                Assert.NotNull(target.Get(name));
            }
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest()
        {
            DomainManager target = CreateManager();
            target.RemoveAll();
            Assert.Equal(0, target.Count());
            string name = BuildDomainName(GetRndDomainID());
            Domain domain = new Domain(name);
            target.Add( domain);
            Assert.NotNull(target.Get(name));
        }
    }
}