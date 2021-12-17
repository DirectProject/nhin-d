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
using System.Threading.Tasks;
using Health.Direct.Config.Store.Entity;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class DomainManagerFacts : ConfigStoreTestBase
    {
        private new static DomainManager CreateManager()
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
        public async Task RemoveAllTest1()
        {
            InitDomainRecords();
            var target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT, (await target.Get(string.Empty, MAXDOMAINCOUNT + 1)).Count);

            await using (var db = CreateConfigDatabase())
            {
                await DomainUtil.RemoveAll(db);
            }

            Assert.Empty((await target.Get(string.Empty, MAXDOMAINCOUNT + 1)));
        }

        /// <summary>
        ///A test for RemoveAll
        ///</summary>
        [Fact]
        public async Task RemoveAllTest()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT, (await target.Get(string.Empty, MAXDOMAINCOUNT + 1)).Count);
            await using (ConfigDatabase db = CreateConfigDatabase())
            {
                await DomainUtil.RemoveAll(db);
            }
            Assert.Empty((await target.Get(string.Empty, MAXDOMAINCOUNT + 1)));
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest1()
        {
            InitDomainRecords();
            var target = CreateManager();
            var name = BuildDomainName(GetRndDomainID());
            Assert.NotNull(await target.Get(name));
            await target.Remove(name);
            Assert.Null(await target.Get(name));
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest()
        {
            InitDomainRecords();
            var target = CreateManager();
            string name = BuildDomainName(GetRndDomainID());
            Assert.NotNull(target.Get(name));
            await using (ConfigDatabase db = CreateConfigDatabase())
            {
                await target.Remove(name);
                await db.SaveChangesAsync();
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
        public async Task GetTest7()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string[] names = TestDomainNames.ToArray();
            var actual = await target.Get(names);
            Assert.Equal(names.Length, actual.Count);
            foreach (var dom in actual)
            {
                Assert.Contains(dom.Name, names);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest6()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string[] names = TestDomainNames.ToArray();
            await using var db = CreateConfigDatabase();
            var actual = await target.Get(db, names);
            Assert.Equal(names.Length, actual.Count);
            foreach (Domain dom in actual)
            {
                Assert.Contains(dom.Name, names);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest5()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string name = BuildDomainName(GetRndDomainID());
            var actual = await target.Get(name);
            Assert.Equal(name, actual.Name);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest4()
        {
            InitDomainRecords();
            DomainManager target = CreateManager();
            string name = BuildDomainName(GetRndDomainID());
            await using var db = CreateConfigDatabase();
            var actual = await target.Get(db, name);
            Assert.Equal(name, actual.Name);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest3Last()
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

            var mxs = await mgr.Get(val, MAXDOMAINCOUNT + 1);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - pos
            Assert.Equal(MAXDOMAINCOUNT - pos, mxs.Count);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than pos
            mxs = await mgr.Get(allKeys[0], pos - 1);
            Assert.Equal(pos - 1, mxs.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = allKeys.Last();
            mxs = await mgr.Get(val, MAXDOMAINCOUNT + 1);
            Assert.Equal(0, mxs.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = mxsAll.Keys.ToArray().First();
            mxs = await mgr.Get(val, MAXDOMAINCOUNT + 1);
            Assert.Equal(MAXDOMAINCOUNT - 1, mxs.Count);
        }

        [Fact]
        public async Task GetTest3First()
        {
            InitDomainRecords();
            DomainManager mgr = CreateManager();

            var mxs = await mgr.Get(String.Empty, MAXDOMAINCOUNT + 1);

            //----------------------------------------------------------------------------------------------------
            //---expected that all of the records will be returned
            Assert.Equal(MAXDOMAINCOUNT, mxs.Count);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            int pos = GetRndDomainID();
            mxs = await mgr.Get(String.Empty, pos - 1);
            Assert.Equal(pos - 1, mxs.Count);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest2Last()
        {
            InitDomainRecords();
            await using ConfigDatabase db = CreateConfigDatabase();
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

            var mxs = await mgr.Get(db, val, MAXDOMAINCOUNT + 1);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - pos
            Assert.Equal(MAXDOMAINCOUNT - pos, mxs.Count);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than pos
            mxs = await mgr.Get(db, allKeys[0], pos - 1);
            Assert.Equal(pos - 1, mxs.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = allKeys.Last();
            mxs = await mgr.Get(val, MAXDOMAINCOUNT + 1);
            Assert.Empty(mxs);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = allKeys.First();
            mxs = await mgr.Get(db, val, MAXDOMAINCOUNT + 1);
            Assert.Equal(MAXDOMAINCOUNT - 1, mxs.Count);
        }

        [Fact]
        public async Task GetTest2First()
        {
            InitDomainRecords();
            await using var db = CreateConfigDatabase();
            DomainManager mgr = CreateManager();

            var mxs = await mgr.Get(db, String.Empty, MAXDOMAINCOUNT + 1);

            //----------------------------------------------------------------------------------------------------
            //---expected that all of the records will be returned
            Assert.Equal(MAXDOMAINCOUNT, mxs.Count);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            int pos = GetRndDomainID();
            mxs = await mgr.Get(db, String.Empty, pos - 1);
            Assert.Equal(pos - 1, mxs.Count);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest1()
        {
            InitDomainRecords();

            DomainManager mgr = CreateManager();

            string[] names = new[] { BuildDomainName(1), BuildDomainName(2), BuildDomainName(3) };

            //----------------------------------------------------------------------------------------------------
            //---new status should still yield 3 results
            var actual = await mgr.Get(names, EntityStatus.New);
            Assert.Equal(names.Length, actual.Count);

            //----------------------------------------------------------------------------------------------------
            //---pass in null and expect matching results
            actual = await mgr.Get(names, null);
            Assert.Equal(names.Length, actual.Count);

            //----------------------------------------------------------------------------------------------------
            //---disabled status should still yield no results
            actual = await mgr.Get(names, EntityStatus.Disabled);
            Assert.Equal(0, actual.Count);

            //----------------------------------------------------------------------------------------------------
            //---null pref should still yield same results
            actual = await mgr.Get(names, EntityStatus.Enabled);
            Assert.Equal(0, actual.Count);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest()
        {
            InitDomainRecords();

            DomainManager mgr = CreateManager();
            await using ConfigDatabase db = CreateConfigDatabase();
            string[] names = new[] { BuildDomainName(1), BuildDomainName(2), BuildDomainName(3) };

            //----------------------------------------------------------------------------------------------------
            //---new status should still yield 3 results
            var actual = await mgr.Get(db, names, EntityStatus.New);
            Assert.Equal(names.Length, actual.Count);

            //----------------------------------------------------------------------------------------------------
            //---pass in null and expect matching results
            actual = await mgr.Get(db, names, null);
            Assert.Equal(names.Length, actual.Count);

            //----------------------------------------------------------------------------------------------------
            //---disabled status should still yield no results
            actual = await mgr.Get(db, names, EntityStatus.Disabled);
            Assert.Empty(actual);

            //----------------------------------------------------------------------------------------------------
            //---null pref should still yield same results
            actual = await mgr.Get(db, names, EntityStatus.Enabled);
            Assert.Equal(0, actual.Count);
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact]
        public async Task CountTest()
        {
            InitDomainRecords();
            DomainManager mgr = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT, await mgr.Count());
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest3()
        {
            await using var db = CreateConfigDatabase();
            DomainManager target = CreateManager();
            await DomainUtil.RemoveAll(db);
            Assert.Equal(0, await target.Count());
            string name = BuildDomainName(GetRndDomainID());
            await target.Add(db, name);
            await db.SaveChangesAsync();
            Assert.NotNull(await target.Get(name));
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest2()
        {
            var target = CreateManager();
            await using (var db = CreateConfigDatabase())
            {
                await DomainUtil.RemoveAll(db);
            }

            Assert.Equal(0, await target.Count());
            string name = BuildDomainName(GetRndDomainID());
            await target.Add(name);
            Assert.NotNull(await target.Get(name));
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest1()
        {
            var target = CreateManager();
            
            await using (var db = CreateConfigDatabase())
            {
                await DomainUtil.RemoveAll(db);
            }

            Assert.Equal(0, await target.Count());
            await using (var db = CreateConfigDatabase())
            {
                string name = BuildDomainName(GetRndDomainID());
                Domain domain = new Domain(name);
                target.Add(db, domain);
                await db.SaveChangesAsync();
                Assert.NotNull(await target.Get(name));
            }
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest()
        {
            var target = CreateManager();
            
            await using (var db = CreateConfigDatabase())
            {
                await DomainUtil.RemoveAll(db);
            }

            Assert.Equal(0, await target.Count());
            string name = BuildDomainName(GetRndDomainID());
            var domain = new Domain(name);
            await target.Add(domain);
            Assert.NotNull(await target.Get(name));
        }
    }
}