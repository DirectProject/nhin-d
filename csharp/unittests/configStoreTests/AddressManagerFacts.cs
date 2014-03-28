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
using System.Configuration;
using System.Linq;
using System.Net.Mail;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class AddressManagerFacts : ConfigStoreTestBase
    {
        public AddressManagerFacts()
        {
            ConfigurationManager.AppSettings["EnabledAllDomainAddresses"] = null;
        }

        private static new AddressManager CreateManager()
        {
            return new AddressManager(CreateConfigStore());
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            ConfigStore store = CreateConfigStore();
            AddressManager mgr = new AddressManager(store); 
            ConfigStore actual = mgr.Store;
            Assert.Equal(mgr.Store, actual);
        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public void UpdateTest2()
        {

            InitAddressRecords();

            AddressManager mgr = CreateManager();
            IEnumerable<Address> addresses = mgr.Get(1, String.Empty, MAXADDRESSCOUNT);
            Assert.Equal(MAXADDRESSCOUNT, addresses.Count());
            const string testType = "testtype";
            foreach (Address add in addresses)
            {
                Assert.Equal(add.Status, EntityStatus.New);
                add.Status = EntityStatus.Enabled;
                add.Type = testType;
            }
            mgr.Update(addresses);

            addresses = mgr.Get(1, String.Empty, MAXADDRESSCOUNT);
            foreach (Address add in addresses)
            {
                Assert.Equal(EntityStatus.Enabled, add.Status);
                Assert.Equal(testType,add.Type);
            }
        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public void UpdateTest1()
        {
            InitAddressRecords();

            AddressManager mgr = CreateManager();
            Address add = mgr.Get(BuildEmailAddress(1,1));
            Assert.NotNull(add);
            const string testType = "testtype";
            Assert.Equal(add.Status, EntityStatus.New);
            add.Status = EntityStatus.Enabled;
            add.Type = testType;
            mgr.Update(add);

            add = mgr.Get(add.EmailAddress);
            Assert.Equal(EntityStatus.Enabled, add.Status);
            Assert.Equal(testType, add.Type);
            
        }

        /// <summary>
        ///A test for System.Collections.IEnumerable.GetEnumerator
        ///</summary>
        [Fact]
        public void GetEnumeratorTest1()
        {
            InitAddressRecords();
            IEnumerable<Address> mgr = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mgr.Count());
        }


        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact]
        public void SetStatusTest1()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager(); 
            const long domainID = STARTID;
            const EntityStatus status = EntityStatus.Enabled;
            mgr.SetStatus(domainID, status);
            Address[] adds = mgr.Get(domainID, String.Empty, MAXADDRESSCOUNT);
            Assert.Equal(MAXADDRESSCOUNT, adds.Count());
            foreach (Address add in adds)
            {
                Assert.Equal(domainID, add.DomainID);
                Assert.Equal(status, add.Status);
            }
        }

        /// <summary>
        ///A test for RemoveDomain
        ///</summary>
        [Fact]
        public void RemoveDomainTest1()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager(); 
            const long domainID = 1;

            using (ConfigDatabase db = CreateConfigDatabase())
            {
                //----------------------------------------------------------------------------------------------------
                //---make sure that we have max addresses for the given domain
                Address[] adds = mgr.Get(db, domainID, string.Empty, MAXADDRESSCOUNT + 1).ToArray();
                Assert.Equal(MAXADDRESSCOUNT, adds.Count());

                mgr.RemoveDomain(domainID);
                adds = mgr.Get(db, domainID, string.Empty, MAXADDRESSCOUNT + 1).ToArray();
                Assert.Equal(0, adds.Count());

            }
            
        }

        /// <summary>
        ///A test for RemoveDomain
        ///</summary>
        [Fact]
        public void RemoveDomainTest()
        {

            InitAddressRecords();
            AddressManager mgr = CreateManager();
            const long domainID = 1;

            using (ConfigDatabase db = CreateConfigDatabase())
            {
                //----------------------------------------------------------------------------------------------------
                //---make sure that we have max addresses for the given domain
                Address[] adds = mgr.Get(db, domainID, string.Empty, MAXADDRESSCOUNT + 1).ToArray();
                Assert.Equal(MAXADDRESSCOUNT, adds.Count());

                mgr.RemoveDomain(db,domainID);
                adds = mgr.Get(db, domainID, string.Empty, MAXADDRESSCOUNT + 1).ToArray();
                Assert.Equal(0, adds.Count());

            }
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public void RemoveTest2()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            Assert.Equal(emailAddresses.Length, mgr.Get(emailAddresses).Count());
            mgr.Remove(emailAddresses);
            Assert.Equal(0, mgr.Get(emailAddresses).Count());
            Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT - emailAddresses.Length, mgr.Count());
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public void RemoveTest1()
        {

            InitAddressRecords();
            AddressManager mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            mgr.Get(emailAddress);
            Assert.NotNull(emailAddress);
            mgr.Remove(emailAddress);
            Assert.Null(mgr.Get(emailAddress));
            
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public void RemoveTest()
        {

            InitAddressRecords();
            
            AddressManager mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            mgr.Get(emailAddress);
            Assert.NotNull(emailAddress);
            mgr.Remove(emailAddress);
            Assert.Null(mgr.Get(emailAddress));
            
            
        }

        /// <summary>
        ///A test for GetEnumerator
        ///</summary>
        [Fact]
        public void GetEnumeratorTest()
        {

            InitAddressRecords();
            IEnumerable<Address> mgr = CreateManager();
            Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT, mgr.Count());
        }


        /// <summary>
        ///A test for GetByDomainTest1
        ///</summary>
        [Fact]
        public void GetByDomainTest1()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            string domainName = BuildDomainName(1);
            Address[] addrs = mgr.GetAllForDomain(domainName.ToUpper()
                , int.MaxValue);
            Assert.Equal(MAXADDRESSCOUNT, addrs.Length);
            foreach (Address addr in addrs)
            {
                Assert.Equal(1, addr.DomainID);
            }

        }

        /// <summary>
        ///A test for GetByDomainTest
        ///</summary>
        [Fact]
        public void GetByDomainTest()
        {
            InitAddressRecords();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                InitAddressRecords();
                AddressManager mgr = CreateManager();
                string domainName = BuildDomainName(1);
                Address[] addrs = mgr.GetAllForDomain(db
                    , domainName.ToUpper()
                    , int.MaxValue).ToArray();
                Assert.Equal(MAXADDRESSCOUNT, addrs.Length);
                foreach (Address addr in addrs)
                {
                    Assert.Equal(1, addr.DomainID);
                }
            }

        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest13()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                string[] emailAddresses  = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
                IEnumerable<Address> actual = mgr.Get(db, emailAddresses);
                Assert.Equal(emailAddresses.Length, actual.Count());
                
                for(int t=0;t<actual.Count();t++){
                    Assert.True(emailAddresses.Contains(actual.ToArray()[t].EmailAddress));
                }

            }

            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest12()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();

            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            IEnumerable<Address> actual = mgr.Get(emailAddresses, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count());

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Contains(actual.ToArray()[t].EmailAddress));
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
            
        }


        /// <summary>
        /// Test the ability to validate an address based on the address or domain existing
        ///</summary>
        [Fact]
        public void Get_AddressOrDomainTest()
        {              
            InitAddressRecords();
            string addressType = "SMTP";

            DomainManager dMgr = new DomainManager(CreateConfigStore());
            Domain domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.New;
            dMgr.Add(domain);
            domain = new Domain("address2.domain2.com");
            domain.Status = EntityStatus.Enabled;
            dMgr.Add(domain);
            
            AddressManager mgr = CreateManager();

            string[] emailAddresses = new[] { "NewGuy@address1.domain1.com", "AnotherNewGuy@address1.domain1.com" };
            
            IEnumerable<Address> actual = mgr.Get(emailAddresses, EntityStatus.New);
            Assert.Equal(0, actual.Count());

            //
            // Now search with domainSearchEnabled = true
            //
            actual = mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Equal(0, actual.Count());

            actual = mgr.Get(emailAddresses, true, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count());


            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Contains(actual.ToArray()[t].EmailAddress));
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            emailAddresses = new[] { "NewGuy@address2.domain2.com", "AnotherNewGuy@address2.domain2.com" };
            actual = mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Equal(emailAddresses.Length, actual.Count());

            //
            // domainSearchEnabled and no status.
            //
            actual = mgr.Get(emailAddresses, true);
            Assert.Equal(emailAddresses.Length, actual.Count());
            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Contains(actual.ToArray()[t].EmailAddress));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }
        }


        /// <summary>
        /// Test the ability to validate an address based on the address and domain existing
        ///</summary>
        [Fact]
        public void Get_AddressAndDomainTest()
        {
            InitAddressRecords();
            string addressType = "SMTP";

            DomainManager dMgr = new DomainManager(CreateConfigStore());
            Domain domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.New;
            dMgr.Add(domain);

            //
            // test@address1.domain10.com aready exists
            //

            AddressManager mgr = CreateManager();

            string[] emailAddresses = new[] { "NewGuy@Domain1.test.com", "AnotherNewGuy@address1.domain1.com", "test@Address1.domain10.com" };

            IEnumerable<Address> actual = mgr.Get(emailAddresses, EntityStatus.New);
            Assert.Equal(1, actual.Count());

            //
            // Now search with domainSearchEnabled = true
            //
            actual = mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Equal(0, actual.Count());

            actual = mgr.Get(emailAddresses, true, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count());


            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Any(e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase)));
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

        }


        /// <summary>
        /// Test the ability to validate an address based on the address and domain existing
        ///</summary>
        [Fact]
        public void Get_RoutedAddress()
        {
            InitAddressRecords();
            DomainManager dMgr = new DomainManager(CreateConfigStore());
            Domain domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.Enabled;
            dMgr.Add(domain);

            string addressType = "Undeliverable";
            AddressManager aMgr = new AddressManager(CreateConfigStore());
            MailAddress address = new MailAddress("badinbox1@address1.domain1.com");
            aMgr.Add(address, EntityStatus.Enabled, addressType);
            //
            // test@address1.domain10.com aready exists
            //

            AddressManager mgr = CreateManager();

            string[] emailAddresses = new[] { "BadInbox1@address1.domain1.com" };

            IEnumerable<Address> actual = mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Equal(1, actual.Count());


            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Any(e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase)));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }





            actual = mgr.Get(emailAddresses, EntityStatus.Enabled);
            Assert.Equal(1, actual.Count());


            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Any(e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase)));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }




            actual = mgr.Get(emailAddresses, true);
            Assert.Equal(1, actual.Count());


            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Any(e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase)));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }




            actual = mgr.Get(emailAddresses);
            Assert.Equal(1, actual.Count());


            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Any(e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase)));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }



        }


        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest11()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            IEnumerable<Address> actual = mgr.Get(emailAddresses);
            Assert.Equal(emailAddresses.Length, actual.Count());

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.True(emailAddresses.Contains(actual.ToArray()[t].EmailAddress));
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest10()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            Address add = mgr.Get(emailAddress);
            Assert.Equal(emailAddress, add.EmailAddress);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest9()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                Address add = mgr.Get(db,emailAddress);
                Assert.Equal(emailAddress, add.EmailAddress);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest8()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();

            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                IEnumerable<Address> actual = mgr.Get(db,emailAddresses, EntityStatus.New);
                Assert.Equal(emailAddresses.Length, actual.Count());

                for (int t = 0; t < actual.Count(); t++)
                {
                    Assert.True(emailAddresses.Contains(actual.ToArray()[t].EmailAddress));
                    Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
                }
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest7()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
                IEnumerable<Address> actual = mgr.Get(db, addressIDs);
                Assert.Equal(addressIDs.Length, actual.Count());
                for (int t = 0; t < addressIDs.Length; t++)
                {
                    Assert.True(addressIDs.Contains(actual.ToArray()[t].ID));
                }
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest6()
        {

            InitAddressRecords();
            AddressManager mgr = CreateManager();

            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = mgr.Get(addressIDs);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.True(addressIDs.Contains(actual.ToArray()[t].ID));
            }
                       
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest5()
        {

            InitAddressRecords();
            AddressManager mgr = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
                IEnumerable<Address> actual = mgr.Get(db, addressIDs, EntityStatus.New);
                Assert.Equal(addressIDs.Length, actual.Count());
                for (int t = 0; t < addressIDs.Length; t++)
                {
                    Assert.True(addressIDs.Contains(actual.ToArray()[t].ID));
                    Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
                }
            }
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest4()
        {

            InitAddressRecords();
            AddressManager mgr = CreateManager();

            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = mgr.Get(addressIDs, EntityStatus.New);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.True(addressIDs.Contains(actual.ToArray()[t].ID));
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTestLast3()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                //----------------------------------------------------------------------------------------------------
                //---get the full dictionary using the smtp domain name as the key and pick one to start at
                Dictionary<string, Address> mxsAll = mgr.ToDictionary(p => p.EmailAddress);

                Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mxsAll.Count);

                //----------------------------------------------------------------------------------------------------
                //---grab the key at position 5 in the array, and use that as the "last" name to be passed in
                string val = mxsAll.Keys.ToArray()[4];

                Address[] adds = mgr.Get(db, val, MAXDOMAINCOUNT * MAXADDRESSCOUNT).ToArray();

                //----------------------------------------------------------------------------------------------------
                //---expected that the count of mxs will be  max count - 5
                Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT - 5, adds.Length);

                //----------------------------------------------------------------------------------------------------
                //---try one with a limited number less than max count
                adds = mgr.Get(val, 3);
                Assert.Equal(3, adds.Length);

                //----------------------------------------------------------------------------------------------------
                //---get the last item and see to ensure that no records are returned
                val = mxsAll.Keys.ToArray().Last();
                adds = mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
                Assert.Equal(0, adds.Length);

                //----------------------------------------------------------------------------------------------------
                //---get the first item and see to ensure that MAX - 1 records are returned
                val = mxsAll.Keys.ToArray().First();
                adds = mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
                Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT - 1, adds.Length);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTestLast2()
        {

            InitAddressRecords();
            AddressManager mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, Address> mxsAll = mgr.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mxsAll.Count);

            //----------------------------------------------------------------------------------------------------
            //---grab the key at position 5 in the array, and use that as the "last" name to be passed in
            string val = mxsAll.Keys.ToArray()[4];

            Address[] adds = mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT).ToArray();

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - 5
            Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT - 5, adds.Length);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            adds = mgr.Get(val, 3);
            Assert.Equal(3, adds.Length);

            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = mxsAll.Keys.ToArray().Last();
            adds = mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
            Assert.Equal(0, adds.Length);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = mxsAll.Keys.ToArray().First();
            adds = mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT - 1, adds.Length);
     
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest1()
        {


            InitAddressRecords();
            AddressManager mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, Address> mxsAll = mgr.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mxsAll.Count);

            Address[] adds = mgr.Get(1,string.Empty, MAXADDRESSCOUNT).ToArray();

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count for a domain 
            Assert.Equal(MAXADDRESSCOUNT, adds.Length);

            //----------------------------------------------------------------------------------------------------
            //---get the first item in the list to be used as the last item 
            string val = adds[0].EmailAddress;
            adds = mgr.Get(1, val, MAXADDRESSCOUNT);

            //----------------------------------------------------------------------------------------------------
            //---expected that there should be MAXADDRESSCOUNT - 1 now
            Assert.Equal(MAXADDRESSCOUNT - 1, adds.Length);
            
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public void GetTest()
        {
            InitAddressRecords();
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                AddressManager mgr = CreateManager();

                //----------------------------------------------------------------------------------------------------
                //---get the full dictionary using the smtp domain name as the key and pick one to start at
                Dictionary<string, Address> mxsAll = mgr.ToDictionary(p => p.EmailAddress);

                Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mxsAll.Count);

                Address[] adds = mgr.Get(db,1, string.Empty, MAXADDRESSCOUNT).ToArray();

                //----------------------------------------------------------------------------------------------------
                //---expected that the count of mxs will be  max count for a domain 
                Assert.Equal(MAXADDRESSCOUNT, adds.Length);

                //----------------------------------------------------------------------------------------------------
                //---get the first item in the list to be used as the last item 
                string val = adds[0].EmailAddress;
                adds = mgr.Get(db, 1, val, MAXADDRESSCOUNT).ToArray();

                //----------------------------------------------------------------------------------------------------
                //---expected that there should be MAXADDRESSCOUNT - 1 now
                Assert.Equal(MAXADDRESSCOUNT - 1, adds.Length);
            }
            
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact]
        public void CountTest()
        {
            InitAddressRecords();
            AddressManager mgr = CreateManager(); 
            Assert.Equal(MAXADDRESSCOUNT, mgr.Count(1));
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest3()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the address records
            InitDomainRecords();
            AddressManager mgr = CreateManager(); 

            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Equal(0, mgr.Count());

            const long domainId = 1;
            string email = BuildEmailAddress(1, 1);
            string displayName = BuildEmailAddressDisplayName(1,1);
            Address addr = new Address(domainId, email, displayName);

            mgr.Add(addr);
            Assert.Equal(1, mgr.Count());
            addr = mgr.Get(email);
            Assert.Equal(domainId, addr.DomainID);
            Assert.Equal(email, addr.EmailAddress);
            Assert.Equal(displayName, addr.DisplayName);
            Assert.Equal(EntityStatus.New, addr.Status);
            
        }

        

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest1()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the address records
            InitDomainRecords();
            AddressManager mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Equal(0, mgr.Count());

            const long domainId = 1;
            string email = BuildEmailAddress(1, 1);
            string displayName = BuildEmailAddressDisplayName(1, 1);
            Address addr = new Address(domainId, email, displayName);
            using (CreateConfigDatabase())
            {
                mgr.Add(addr);
            }
            Assert.Equal(1, mgr.Count());
            addr = mgr.Get(email);
            Assert.Equal(domainId, addr.DomainID);
            Assert.Equal(email, addr.EmailAddress);
            Assert.Equal(displayName, addr.DisplayName);
            Assert.Equal(EntityStatus.New, addr.Status);
            
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public void AddTest()
        {
            InitDomainRecords();
            AddressManager mgr = CreateManager(); 
            List<Address> addresses = new List<Address>();
            
            for (int i = 1; i <= MAXADDRESSCOUNT; i++)
            {
                addresses.Add(new Address(STARTID, BuildEmailAddress(STARTID, i)));
            }
            Assert.Equal(0, mgr.Count());
            mgr.Add(addresses);
            Assert.Equal(MAXADDRESSCOUNT, mgr.Count());
            Address[] aa = mgr.Get(string.Empty, MAXADDRESSCOUNT + 1).ToArray();
            Assert.Equal(MAXADDRESSCOUNT, aa.Length);
        }
    }
}