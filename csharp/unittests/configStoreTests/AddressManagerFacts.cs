/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
    Joe Shook     Joseph.Shook@Surescripts.com

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
using System.Threading.Tasks;
using Health.Direct.Config.Store.Entity;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class AddressManagerFacts : ConfigStoreTestBase
    {
        public AddressManagerFacts()
        {
            ConfigurationManager.AppSettings["EnabledAllDomainAddresses"] = null;
        }

        private new static AddressManager CreateManager()
        {
            return new AddressManager(CreateConfigStore());
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            var store = CreateConfigStore();
            var mgr = new AddressManager(store);
            var actual = mgr.Store;
            Assert.Equal(mgr.Store, actual);
        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public async Task UpdateTest2()
        {
            await InitAddressRecords();

            var mgr = CreateManager();
            IEnumerable<Address> addresses = await mgr.Get(1, String.Empty, MAXADDRESSCOUNT);
            Assert.Equal(MAXADDRESSCOUNT, addresses.Count());
            const string testType = "testtype";
            foreach (Address add in addresses)
            {
                Assert.Equal(EntityStatus.New, add.Status);
                add.Status = EntityStatus.Enabled;
                add.Type = testType;
            }
            await mgr.Update(addresses);

            addresses = await mgr.Get(1, String.Empty, MAXADDRESSCOUNT);
            foreach (Address add in addresses)
            {
                Assert.Equal(EntityStatus.Enabled, add.Status);
                Assert.Equal(testType, add.Type);
            }
        }

        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public async Task UpdateTest1()
        {
            await InitAddressRecords();

            var mgr = CreateManager();
            var add = await mgr.Get(BuildEmailAddress(1, 1));
            Assert.NotNull(add);
            const string testType = "testtype";
            Assert.Equal(EntityStatus.New, add.Status);
            add.Status = EntityStatus.Enabled;
            add.Type = testType;
            await mgr.Update(add);

            add = await mgr.Get(add.EmailAddress);
            Assert.Equal(EntityStatus.Enabled, add.Status);
            Assert.Equal(testType, add.Type);
        }

        /// <summary>
        ///A test for System.Collections.IEnumerable.GetEnumerator
        ///</summary>
        [Fact]
        public async Task GetEnumeratorTest1()
        {
            await InitAddressRecords();
            IEnumerable<Address> mgr = CreateManager();
            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mgr.Count());
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact]
        public async Task SetStatusTest1()
        {
            await InitAddressRecords();
            var mgr = CreateManager();
            const long domainID = STARTID;
            const EntityStatus status = EntityStatus.Enabled;
            await mgr.SetStatus(domainID, status);
            var addresses = await mgr.Get(domainID, String.Empty, MAXADDRESSCOUNT);
            Assert.Equal(MAXADDRESSCOUNT, addresses.Count);
            foreach (var address in addresses)
            {
                Assert.Equal(domainID, address.DomainID);
                Assert.Equal(status, address.Status);
            }
        }

        /// <summary>
        ///A test for RemoveDomain
        ///</summary>
        [Fact]
        public async Task RemoveDomainTest1()
        {
            await InitAddressRecords();
            var mgr = CreateManager();
            const long domainId = 1;

            await using var db = CreateConfigDatabase();
            //----------------------------------------------------------------------------------------------------
            //---make sure that we have max addresses for the given domain
            var addresses = await mgr.Get(db, domainId, string.Empty, MAXADDRESSCOUNT + 1);
            Assert.Equal(MAXADDRESSCOUNT, addresses.Count);

            await mgr.RemoveDomain(domainId);
            addresses = await mgr.Get(db, domainId, string.Empty, MAXADDRESSCOUNT + 1);
            Assert.Empty(addresses);
        }

        /// <summary>
        ///A test for RemoveDomain
        ///</summary>
        [Fact]
        public async Task RemoveDomainTest()
        {
            await InitAddressRecords();
            var mgr = CreateManager();
            const long domainId = 1;

            await using ConfigDatabase db = CreateConfigDatabase();
            //----------------------------------------------------------------------------------------------------
            //---make sure that we have max addresses for the given domain
            var addresses = await mgr.Get(db, domainId, string.Empty, MAXADDRESSCOUNT + 1);
            Assert.Equal(MAXADDRESSCOUNT, addresses.Count);

            await mgr.RemoveDomain(db, domainId);
            await db.SaveChangesAsync();
            addresses = await mgr.Get(db, domainId, string.Empty, MAXADDRESSCOUNT + 1);
            Assert.Empty(addresses);
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest2()
        {
            await InitAddressRecords();
            var mgr = CreateManager();
            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            Assert.Equal(emailAddresses.Length, (await mgr.Get(emailAddresses)).Count);
            await mgr.Remove(emailAddresses);
            Assert.Empty((await mgr.Get(emailAddresses)));
            Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT - emailAddresses.Length, await mgr.Count());
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest1()
        {
            await InitAddressRecords();
            var mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            await mgr.Get(emailAddress);
            Assert.NotNull(emailAddress);
            await mgr.Remove(emailAddress);
            Assert.Null(await mgr.Get(emailAddress));
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest()
        {
            await InitAddressRecords();

            var mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            await mgr.Get(emailAddress);
            Assert.NotNull(emailAddress);
            await mgr.Remove(emailAddress);
            Assert.Null(await mgr.Get(emailAddress));
        }

        /// <summary>
        ///A test for GetEnumerator
        ///</summary>
        [Fact]
        public async Task GetEnumeratorTest()
        {
            await InitAddressRecords();
            IEnumerable<Address> mgr = CreateManager();
            Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT, mgr.Count());
        }

        /// <summary>
        ///A test for GetByDomainTest1
        ///</summary>
        [Fact]
        public async Task GetByDomainTest1()
        {
            await InitAddressRecords();
            var mgr = CreateManager();
            string domainName = BuildDomainName(1);
            var addresses = await mgr.GetAllForDomain(domainName.ToUpper()
                , int.MaxValue);
            Assert.Equal(MAXADDRESSCOUNT, addresses.Count);
            foreach (var addr in addresses)
            {
                Assert.Equal(1, addr.DomainID);
            }
        }

        /// <summary>
        ///A test for GetByDomainTest
        ///</summary>
        [Fact]
        public async Task GetByDomainTest()
        {
            await InitAddressRecords();
            await using ConfigDatabase db = CreateConfigDatabase();
            await InitAddressRecords();
            var mgr = CreateManager();
            string domainName = BuildDomainName(1);
            var addresses = await mgr.GetAllForDomain(db, domainName.ToUpper(), int.MaxValue);
            Assert.Equal(MAXADDRESSCOUNT, addresses.Count);
            foreach (var addr in addresses)
            {
                Assert.Equal(1, addr.DomainID);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest13()
        {
            await InitAddressRecords();
            var mgr = CreateManager();
            await using ConfigDatabase db = CreateConfigDatabase();
            var emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            var actual = await mgr.Get(db, emailAddresses);
            Assert.Equal(emailAddresses.Length, actual.Count);

            for (var t = 0; t < actual.Count; t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest12()
        {
            await InitAddressRecords();
            var mgr = CreateManager();

            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            var actual = await mgr.Get(emailAddresses, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count);

            for (int t = 0; t < actual.Count; t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
        }

        /// <summary>
        /// Test the ability to validate an address based on the address or domain existing
        ///</summary>
        [Fact]
        public async Task Get_AddressOrDomainTest()
        {
            await InitAddressRecords();
            string addressType = "SMTP";

            var dMgr = new DomainManager(CreateConfigStore());
            var domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.New;
            await dMgr.Add(domain);
            domain = new Domain("address2.domain2.com");
            domain.Status = EntityStatus.Enabled;
            await dMgr.Add(domain);

            var mgr = CreateManager();

            string[] emailAddresses = new[] { "NewGuy@address1.domain1.com", "AnotherNewGuy@address1.domain1.com" };

            var actual = await mgr.Get(emailAddresses, EntityStatus.New);
            Assert.Empty(actual);

            //
            // Now search with domainSearchEnabled = true
            //
            actual = await mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Empty(actual);

            actual = await mgr.Get(emailAddresses, true, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count);

            for (var t = 0; t < actual.Count; t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            emailAddresses = new[] { "NewGuy@address2.domain2.com", "AnotherNewGuy@address2.domain2.com" };
            actual = await mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Equal(emailAddresses.Length, actual.Count);

            //
            // domainSearchEnabled and no status.
            //
            actual = await mgr.Get(emailAddresses, true);
            Assert.Equal(emailAddresses.Length, actual.Count);
            for (var t = 0; t < actual.Count; t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }
        }

        /// <summary>
        /// Test the ability to validate an address based on the address and domain existing
        ///</summary>
        [Fact]
        public async Task Get_AddressAndDomainTest()
        {
            await InitAddressRecords();
            string addressType = "SMTP";

            var dMgr = new DomainManager(CreateConfigStore());
            var domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.New;
            await dMgr.Add(domain);

            //
            // test@address1.domain10.com aready exists
            //

            var mgr = CreateManager();

            string[] emailAddresses = new[] { "NewGuy@Domain1.test.com", "AnotherNewGuy@address1.domain1.com", "test@Address1.domain10.com" };

            var actual = await mgr.Get(emailAddresses, EntityStatus.New);
            Assert.Single(actual);

            //
            // Now search with domainSearchEnabled = true
            //
            actual = await mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Empty(actual);

            actual = await mgr.Get(emailAddresses, true, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count);

            for (int t = 0; t < actual.Count; t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }
        }

        /// <summary>
        /// Test the ability to validate an address based on the address and domain existing
        ///</summary>
        [Fact]
        public async Task Get_RoutedAddress()
        {
            await InitAddressRecords();
            var dMgr = new DomainManager(CreateConfigStore());
            var domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.Enabled;
            await dMgr.Add(domain);

            string addressType = "Undeliverable";
            var aMgr = new AddressManager(CreateConfigStore());
            var address = new MailAddress("badinbox1@address1.domain1.com");
            await aMgr.Add(address, EntityStatus.Enabled, addressType);
            //
            // test@address1.domain10.com aready exists
            //

            var mgr = CreateManager();

            string[] emailAddresses = new[] { "BadInbox1@address1.domain1.com" };

            IEnumerable<Address> actual = await mgr.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            actual = await mgr.Get(emailAddresses, EntityStatus.Enabled);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            actual = await mgr.Get(emailAddresses, true);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            actual = await mgr.Get(emailAddresses);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest11()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();
            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            IEnumerable<Address> actual = await mgr.Get(emailAddresses);
            Assert.Equal(emailAddresses.Length, actual.Count());

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest10()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            Address add = await mgr.Get(emailAddress);
            Assert.Equal(emailAddress, add.EmailAddress);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest9()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();
            string emailAddress = BuildEmailAddress(1, 1);
            await using ConfigDatabase db = CreateConfigDatabase();
            Address add = await mgr.Get(db, emailAddress);
            Assert.Equal(emailAddress, add.EmailAddress);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest8()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();

            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            await using ConfigDatabase db = CreateConfigDatabase();
            IEnumerable<Address> actual = await mgr.Get(db, emailAddresses, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count());

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest7()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();
            await using ConfigDatabase db = CreateConfigDatabase();
            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await mgr.Get(db, addressIDs);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest6()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();

            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await mgr.Get(addressIDs);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest5()
        {

            await InitAddressRecords();
            AddressManager mgr = CreateManager();
            await using ConfigDatabase db = CreateConfigDatabase();
            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await mgr.Get(db, addressIDs, EntityStatus.New);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest4()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();

            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await mgr.Get(addressIDs, EntityStatus.New);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTestLast3()
        {
            await InitAddressRecords();
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

                var adds = await mgr.Get(db, val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);

                //----------------------------------------------------------------------------------------------------
                //---expected that the count of mxs will be  max count - 5
                Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT - 5, adds.Count);

                //----------------------------------------------------------------------------------------------------
                //---try one with a limited number less than max count
                adds = await mgr.Get(val, 3);
                Assert.Equal(3, adds.Count);

                //----------------------------------------------------------------------------------------------------
                //---get the last item and see to ensure that no records are returned
                val = mxsAll.Keys.ToArray().Last();
                adds = await mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
                Assert.Empty(adds);

                //----------------------------------------------------------------------------------------------------
                //---get the first item and see to ensure that MAX - 1 records are returned
                val = mxsAll.Keys.ToArray().First();
                adds = await mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
                Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT - 1, adds.Count);
            }
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTestLast2()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, Address> mxsAll = mgr.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mxsAll.Count);

            //----------------------------------------------------------------------------------------------------
            //---grab the key at position 5 in the array, and use that as the "last" name to be passed in
            string val = mxsAll.Keys.ToArray()[4];

            var adds = await mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - 5
            Assert.Equal(MAXADDRESSCOUNT * MAXDOMAINCOUNT - 5, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            adds = await mgr.Get(val, 3);
            Assert.Equal(3, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = mxsAll.Keys.ToArray().Last();
            adds = await mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
            Assert.Equal(0, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = mxsAll.Keys.ToArray().First();
            adds = await mgr.Get(val, MAXDOMAINCOUNT * MAXADDRESSCOUNT);
            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT - 1, adds.Count);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest1()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, Address> mxsAll = mgr.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mxsAll.Count);

            var adds = await mgr.Get(1, string.Empty, MAXADDRESSCOUNT);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count for a domain 
            Assert.Equal(MAXADDRESSCOUNT, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the first item in the list to be used as the last item 
            string val = adds[0].EmailAddress;
            adds = await mgr.Get(1, val, MAXADDRESSCOUNT);

            //----------------------------------------------------------------------------------------------------
            //---expected that there should be MAXADDRESSCOUNT - 1 now
            Assert.Equal(MAXADDRESSCOUNT - 1, adds.Count);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [Fact]
        public async Task GetTest()
        {
            await InitAddressRecords();
            await using (ConfigDatabase db = CreateConfigDatabase())
            {
                AddressManager mgr = CreateManager();

                //----------------------------------------------------------------------------------------------------
                //---get the full dictionary using the smtp domain name as the key and pick one to start at
                Dictionary<string, Address> mxsAll = mgr.ToDictionary(p => p.EmailAddress);

                Assert.Equal(MAXDOMAINCOUNT * MAXADDRESSCOUNT, mxsAll.Count);

                var adds = await mgr.Get(db, 1, string.Empty, MAXADDRESSCOUNT);

                //----------------------------------------------------------------------------------------------------
                //---expected that the count of mxs will be  max count for a domain 
                Assert.Equal(MAXADDRESSCOUNT, adds.Count);

                //----------------------------------------------------------------------------------------------------
                //---get the first item in the list to be used as the last item 
                string val = adds[0].EmailAddress;
                adds = await mgr.Get(db, 1, val, MAXADDRESSCOUNT);

                //----------------------------------------------------------------------------------------------------
                //---expected that there should be MAXADDRESSCOUNT - 1 now
                Assert.Equal(MAXADDRESSCOUNT - 1, adds.Count);
            }
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact]
        public async Task CountTest()
        {
            await InitAddressRecords();
            AddressManager mgr = CreateManager();
            Assert.Equal(MAXADDRESSCOUNT, await mgr.Count(1));
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest3()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the address records
            await InitDomainRecords();
            AddressManager mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Empty(mgr);

            const long domainId = 1;
            string email = BuildEmailAddress(1, 1);
            string displayName = BuildEmailAddressDisplayName(1, 1);
            Address address = new Address(domainId, email, displayName);

            await mgr.Add(address);
            Assert.Equal(1, await mgr.Count());
            address = await mgr.Get(email);
            Assert.Equal(domainId, address.DomainID);
            Assert.Equal(email, address.EmailAddress);
            Assert.Equal(displayName, address.DisplayName);
            Assert.Equal(EntityStatus.New, address.Status);
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest1Async()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the address records
            await InitDomainRecords();
            var mgr = CreateManager();

            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Equal(0, await mgr.Count());

            const long domainId = 1;
            string email = BuildEmailAddress(1, 1);
            string displayName = BuildEmailAddressDisplayName(1, 1);
            var address = new Address(domainId, email, displayName);
            await using (CreateConfigDatabase())
            {
                await mgr.Add(address);
            }
            Assert.Equal(1, await mgr.Count());
            address = await mgr.Get(email);
            Assert.Equal(domainId, address.DomainID);
            Assert.Equal(email, address.EmailAddress);
            Assert.Equal(displayName, address.DisplayName);
            Assert.Equal(EntityStatus.New, address.Status);
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest()
        {
            await InitDomainRecords();
            AddressManager mgr = CreateManager();
            List<Address> addresses = new List<Address>();

            for (int i = 1; i <= MAXADDRESSCOUNT; i++)
            {
                addresses.Add(new Address(STARTID, BuildEmailAddress(STARTID, i)));
            }
            Assert.Equal(0, await mgr.Count());
            await mgr.Add(addresses);
            Assert.Equal(MAXADDRESSCOUNT, await mgr.Count());
            var aa = await mgr.Get(string.Empty, MAXADDRESSCOUNT + 1);
            Assert.Equal(MAXADDRESSCOUNT, aa.Count);
        }
    }
}