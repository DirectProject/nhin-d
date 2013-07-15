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
    class MXManagerFacts : ConfigStoreTestBase
    {


        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            ConfigStore store = new ConfigStore(CONNSTR);
            MXManager target = new MXManager(store); // TODO: Initialize to an appropriate value
            ConfigStore actual = target.Store;
            Assert.Equal(target.Store, actual);
            
        }

        /*
        /// <summary>
        ///A test for Update
        ///</summary>
        public void UpdateTest11()
        {
         
  
            DomainManager domMgr = new DomainManager(new ConfigStore(CONNSTR));
            ConfigDatabase db = new ConfigDatabase(CONNSTR);

            InitDomainRecords(domMgr, db);

            //----------------------------------------------------------------------------------------------------
            //---get the first domain (knowing that id 1 should exist
            Domain dom = db.Domains.Get(string.Format(DOMAINNAMEPATTERN, 1));
            Assert.NotNull(dom);
            Assert.NotEqual(7777, dom.PostmasterID);
            dom.PostmasterID = 7777;
            domMgr.Update(dom);


            DomainManager mgr = new DomainManager(new ConfigStore(CONNSTR));
            ConfigDatabase db = new ConfigDatabase(CONNSTR);

            InitDomainRecords(mgr
                , db);

            //----------------------------------------------------------------------------------------------------
            //---get entry 1 and make sure that it exists and matches the expected name/patterns
            string expectedName = string.Format(DOMAINNAMEPATTERN, 1);
            Domain obj = mgr.Get(expectedName);

            Assert.NotNull(obj);
            Assert.Equal(expectedName, obj.Name);

            obj.PostmasterID = 777;

            mgr.Update(db, obj);

        }
        */

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public void UpdateTest()
        {
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            InitMXRecords();
            //----------------------------------------------------------------------------------------------------
            //---get the first domain (knowing that domain of 1 with smtp 1 should exist since the init passed)
            MX obj = mgr.Get(BuildSMTPDomainName(1,1));
            Assert.NotNull(obj);
            //----------------------------------------------------------------------------------------------------
            //---since we set pref to the same as the smtp[x].domain[y].test.com x value, check it here
            Assert.Equal(1, obj.Preference);
            obj.Preference = 4;
            mgr.Update(obj);
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public void RemoveTest1()
        {
            InitMXRecords();

            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            //----------------------------------------------------------------------------------------------------
            //---get the first domain (knowing that domain of 1 with smtp 1 should exist since the init passed)
            string name = BuildSMTPDomainName(1, 1);

            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {
                MX obj = db.MXs.Get(name);
                Assert.NotNull(obj);
                mgr.Remove(db, name);
                db.SubmitChanges();
                obj = db.MXs.Get(name);
                Assert.Null(obj);
            }
            
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public void RemoveTest()
        {
            InitMXRecords();

            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
           
            //----------------------------------------------------------------------------------------------------
            //---get the first domain (knowing that domain of 1 with smtp 1 should exist since the init passed)
            string name = BuildSMTPDomainName(1, 1);
            MX obj = mgr.Get(name);
            Assert.NotNull(obj);
            mgr.Remove(name);
            obj = mgr.Get(name);
            Assert.Null(obj);
           
        }

        /// <summary>
        ///A test for RemoveDomain
        ///</summary>
        [Fact]
        public void RemoveDomainTest1()
        {
            InitMXRecords();

            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));

            //----------------------------------------------------------------------------------------------------
            //---make sure that the domains are actually there
            for (int t = 1; t <= MAXSMTPCOUNT; t++)
            {
                string name = BuildSMTPDomainName(1, t);
                MX obj = mgr.Get(name);
                Assert.NotNull(obj);
            }

            //----------------------------------------------------------------------------------------------------
            //---get the first domain (knowing that domain of 1 with smtp 1 should exist since the init passed)
           
            mgr.RemoveDomain(1);
            //----------------------------------------------------------------------------------------------------
            //---there should be no items left with domain id of 1, use the count to check number of matching
            //---per domain as well as a loop to ensure that each entry has been removed for the given domain
            for (int t = 1; t <= MAXSMTPCOUNT; t++)
            {
                string name = BuildSMTPDomainName(1, t);
                MX obj = mgr.Get(name);
                Assert.Null(obj);
            }

            Assert.Equal(0, mgr.Count(1));
        }

        /// <summary>
        ///A test for RemoveDomain
        ///</summary>
        [Fact]
        public void RemoveDomainTest()
        {
            InitMXRecords();
            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {
                MXManager mgr = new MXManager(new ConfigStore(CONNSTR));

                //----------------------------------------------------------------------------------------------------
                //---make sure that the domains are actually there
                for (int t = 1; t <= MAXSMTPCOUNT; t++)
                {
                    string name = BuildSMTPDomainName(1, t);
                    MX obj = mgr.Get(db, name);
                    Assert.NotNull(obj);
                }

                //----------------------------------------------------------------------------------------------------
                //---get the first domain (knowing that domain of 1 with smtp 1 should exist since the init passed)

                mgr.RemoveDomain(db, 1);
                //----------------------------------------------------------------------------------------------------
                //---there should be no items left with domain id of 1, use the count to check number of matching
                //---per domain as well as a loop to ensure that each entry has been removed for the given domain
                for (int t = 1; t <= MAXSMTPCOUNT; t++)
                {
                    string name = BuildSMTPDomainName(1, t);
                    MX obj = mgr.Get(db, name);
                    Assert.Null(obj);
                }
                Assert.Equal(0, mgr.Count(1));
            }
            
        }

        /// <summary>
        ///A test for System.Collections.IEnumerable.GetEnumerator
        ///</summary>
        [Fact]
        public void GetEnumeratorTest1()
        {
            InitMXRecords();
            IEnumerable<MX> mgr = new MXManager(new ConfigStore(CONNSTR));
            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT, mgr.Count());
        }

        /// <summary>
        ///A test for GetEnumerator
        ///</summary>
        [Fact]
        public void GetEnumeratorTest()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            IEnumerator<MX> actual = mgr.GetEnumerator();
            int cnt = 0;
            while(actual.MoveNext()){
                cnt++;
            }

            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT, cnt);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest7()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            List<string> lst = AllMXDomainNames();
            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT, lst.Count());
            MX[] mxs = mgr.Get(lst.ToArray());
            Assert.Equal(mgr.Count(), mxs.Length);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest6()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            List<string> lst = AllMXDomainNames();
            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT, lst.Count());
            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {
                IEnumerable<MX> mxs = mgr.Get(db, lst.ToArray());
            
                Assert.Equal(mgr.Count(), mxs.Count());
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest5()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            string name = BuildSMTPDomainName(1, 1);
            MX mxActual = mgr.Get(name);
            Assert.NotNull(mxActual);
            //----------------------------------------------------------------------------------------------------
            //---check basic values
            Assert.Equal(name, mxActual.SMTPDomainName);
            Assert.Equal(1,mxActual.DomainID);
            //----------------------------------------------------------------------------------------------------
            //---preference should always be the same as smtp id used to create the name
            Assert.Equal(1, mxActual.Preference);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest4()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            string name = BuildSMTPDomainName(1, 1);
            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {
                MX mxActual = mgr.Get(db, name);

                //----------------------------------------------------------------------------------------------------
                //---check basic values
                Assert.Equal(name, mxActual.SMTPDomainName);
                Assert.Equal(1, mxActual.DomainID);
                //----------------------------------------------------------------------------------------------------
                //---preference should always be the same as smtp id used to create the name
                Assert.Equal(1, mxActual.Preference);
            }
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest3Last()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string,MX> mxsAll = mgr.ToDictionary(p => p.SMTPDomainName);

            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT, mxsAll.Count);
         
            //----------------------------------------------------------------------------------------------------
            //---grab the key at position 5 in the array, and use that as the "last" name to be passed in
            string val = mxsAll.Keys.ToArray()[4];

            MX[] mxs = mgr.Get(val, MAXDOMAINCOUNT * MAXSMTPCOUNT);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - 5
            Assert.Equal(MAXSMTPCOUNT * MAXDOMAINCOUNT - 5, mxs.Length);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            mxs = mgr.Get(val, 3);
            Assert.Equal(3, mxs.Length);

            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = mxsAll.Keys.ToArray().Last();
            mxs = mgr.Get(val, MAXDOMAINCOUNT * MAXSMTPCOUNT);
            Assert.Equal(0, mxs.Length);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = mxsAll.Keys.ToArray().First();
            mxs = mgr.Get(val, MAXDOMAINCOUNT * MAXSMTPCOUNT);
            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT -1, mxs.Length);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest3First()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));

            MX[] mxs = mgr.Get(String.Empty, MAXDOMAINCOUNT * MAXSMTPCOUNT);

            //----------------------------------------------------------------------------------------------------
            //---expected that all of the records will be returned
            Assert.Equal(MAXSMTPCOUNT * MAXDOMAINCOUNT, mxs.Length);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            mxs = mgr.Get(String.Empty, 3);
            Assert.Equal(3, mxs.Length);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest2Last()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, MX> mxsAll = mgr.ToDictionary(p => p.SMTPDomainName);

            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT, mxsAll.Count);

            //----------------------------------------------------------------------------------------------------
            //---grab the key at position 5 in the array, and use that as the "last" name to be passed in
            string val = mxsAll.Keys.ToArray()[4];
            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {
                MX[] mxs = mgr.Get(db, val, MAXDOMAINCOUNT * MAXSMTPCOUNT).ToArray();

                //----------------------------------------------------------------------------------------------------
                //---expected that the count of mxs will be  max count - 5
                Assert.Equal(MAXSMTPCOUNT * MAXDOMAINCOUNT - 5, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---try one with a limited number less than max count
                mxs = mgr.Get(db, val, 3).ToArray();
                Assert.Equal(3, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---get the last item and see to ensure that no records are returned
                val = mxsAll.Keys.ToArray().Last();
                mxs = mgr.Get(db, val, MAXDOMAINCOUNT * MAXSMTPCOUNT).ToArray();
                Assert.Equal(0, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---get the first item and see to ensure that MAX - 1 records are returned
                val = mxsAll.Keys.ToArray().First();
                mxs = mgr.Get(db, val, MAXDOMAINCOUNT * MAXSMTPCOUNT).ToArray();

                Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT - 1, mxs.Length);
            }
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest2First()
        {
            InitMXRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {
                MX[] mxs = mgr.Get(db, String.Empty, MAXDOMAINCOUNT * MAXSMTPCOUNT).ToArray();

                //----------------------------------------------------------------------------------------------------
                //---expected that all of the records will be returned
                Assert.Equal(MAXSMTPCOUNT * MAXDOMAINCOUNT, mxs.Length);

                //----------------------------------------------------------------------------------------------------
                //---try one with a limited number less than max count
                mxs = mgr.Get(db, String.Empty, 3).ToArray();

                Assert.Equal(3, mxs.Length);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest1()
        {
            InitMXRecords();

            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            string[] names = new string[] { BuildSMTPDomainName(1,1), BuildSMTPDomainName(2,1), BuildSMTPDomainName(3,1)};
            //----------------------------------------------------------------------------------------------------
            //---Expected that preference is 1 on all of these, so 3 should be returned
            MX[] actual = mgr.Get(names, 1);
            Assert.Equal(3, actual.Length);

            //----------------------------------------------------------------------------------------------------
            //---populate array with one that has a pref of 2
            names = new string[] { BuildSMTPDomainName(1, 2), BuildSMTPDomainName(2, 1), BuildSMTPDomainName(3, 1) };
            
            //----------------------------------------------------------------------------------------------------
            //---null pref should still yield 3 results
            actual = mgr.Get(names, null);
            Assert.Equal(3, actual.Length);

            //----------------------------------------------------------------------------------------------------
            //---should yield 2 results with pref of 1
            actual = mgr.Get(names, 1);
            Assert.Equal(2, actual.Length);

            //----------------------------------------------------------------------------------------------------
            //---should yield 1 results with pref of 2
            actual = mgr.Get(names, 2);
            Assert.Equal(1, actual.Length);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest()
        {
            InitMXRecords();

            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {

                string[] names = new string[] { BuildSMTPDomainName(1, 1), BuildSMTPDomainName(2, 1), BuildSMTPDomainName(3, 1) };
                //----------------------------------------------------------------------------------------------------
                //---Expected that preference is 1 on all of these, so 3 should be returned
                MX[] actual = mgr.Get(db, names, 1).ToArray();
                Assert.Equal(names.Length, actual.Length);

                //----------------------------------------------------------------------------------------------------
                //---populate array with one that has a pref of 2
                names = new string[] { BuildSMTPDomainName(1, 2), BuildSMTPDomainName(2, 1), BuildSMTPDomainName(3, 1) };

                //----------------------------------------------------------------------------------------------------
                //---null pref should still yield 3 results
                actual = mgr.Get(db, names, null).ToArray();
                Assert.Equal(3, actual.Length);

                //----------------------------------------------------------------------------------------------------
                //---should yield 2 results with pref of 1
                actual = mgr.Get(db, names, 1).ToArray();
                Assert.Equal(2, actual.Length);

                //----------------------------------------------------------------------------------------------------
                //---should yield 1 results with pref of 2
                actual = mgr.Get(db, names, 2).ToArray();
                Assert.Equal(1, actual.Length);
            }
            
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact]
        public void CountTest()
        {
            InitMXRecords();

            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            int actual = mgr.Count();
            Assert.Equal(MAXDOMAINCOUNT * MAXSMTPCOUNT, actual);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest3()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the mx records
            InitDomainRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Equal(0, mgr.Count());

            long domainId = 1; //--we always have domain id of 1 (unless someone changed testing values in base)
            string SMTPName = BuildSMTPDomainName(1,1);
            int preferece = 1;

            mgr.Add(domainId
                    , SMTPName
                    , preferece);
            Assert.Equal(1, mgr.Count());
            MX mx = mgr.Get(SMTPName);
            Assert.Equal(domainId, mx.DomainID);
            Assert.Equal(SMTPName, mx.SMTPDomainName);
            Assert.Equal(preferece, mx.Preference);

        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest2()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the mx records
            InitDomainRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Equal(0, mgr.Count());

            long domainId = 1; //--we always have domain id of 1 (unless someone changed testing values in base)
            string SMTPName = BuildSMTPDomainName(1, 1);

            mgr.Add(domainId
                    , SMTPName);
            Assert.Equal(1, mgr.Count());
            MX mx = mgr.Get(SMTPName);
            Assert.Equal(domainId, mx.DomainID);
            Assert.Equal(SMTPName, mx.SMTPDomainName);
            Assert.Equal(0, mx.Preference);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest1()
        {

            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the mx records
            InitDomainRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));

            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Equal(0, mgr.Count());

            long domainId = 1; //--we always have domain id of 1 (unless someone changed testing values in base)
            string SMTPName = BuildSMTPDomainName(1, 1);
            int preferece = 1;
            using (ConfigDatabase db = new ConfigDatabase(CONNSTR))
            {
                mgr.Add(db,domainId
                        , SMTPName
                        , preferece);
                db.SubmitChanges();
                Assert.Equal(1, mgr.Count());
               

            }
            MX mx = mgr.Get(SMTPName);
            Assert.Equal(domainId, mx.DomainID);
            Assert.Equal(SMTPName, mx.SMTPDomainName);
            Assert.Equal(preferece, mx.Preference);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the mx records
            InitDomainRecords();
            MXManager mgr = new MXManager(new ConfigStore(CONNSTR));
            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Equal(0, mgr.Count());

            long domainId = 1; //--we always have domain id of 1 (unless someone changed testing values in base)
            string SMTPName = BuildSMTPDomainName(1, 1);
            int preference = 10;
            MX mx = new MX(domainId
                           , SMTPName
                           , preference);

            mgr.Add(mx);
            Assert.Equal(1, mgr.Count());
            mx = mgr.Get(SMTPName);
            Assert.Equal(domainId, mx.DomainID);
            Assert.Equal(SMTPName, mx.SMTPDomainName);
            Assert.Equal(preference, mx.Preference);
            
        }

    }
}