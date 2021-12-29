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
using Xunit.Abstractions;

namespace Health.Direct.Config.Store.Tests
{
    [Collection("ManagerFacts")]
    public class AddressManagerFacts : ConfigStoreTestBase
    {
        private readonly ITestOutputHelper _testOutputHelper;
        private readonly DirectDbContext _dbContext;
        private readonly AddressManager _addressManager;

        public AddressManagerFacts(ITestOutputHelper testOutputHelper)
        {
            _testOutputHelper = testOutputHelper;
            _dbContext = CreateConfigDatabase();
            ConfigurationManager.AppSettings["EnabledAllDomainAddresses"] = null;
            _addressManager = new AddressManager(_dbContext);
        }

        
        /// <summary>
        ///A test for Update
        ///</summary>
        [Fact]
        public async Task UpdateTest2()
        {
            await InitAddressRecords(_dbContext);

            IEnumerable<Address> addresses = await _addressManager.Get(1, String.Empty, MaxAddressCount);
            Assert.Equal(MaxAddressCount, addresses.Count());
            const string testType = "testtype";
            foreach (Address add in addresses)
            {
                Assert.Equal(EntityStatus.New, add.Status);
                add.Status = EntityStatus.Enabled;
                add.Type = testType;
            }
            await _addressManager.Update(addresses);

            addresses = await _addressManager.Get(1, String.Empty, MaxAddressCount);
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
            await InitAddressRecords(_dbContext);

            
            var add = await _addressManager.Get(BuildEmailAddress(1, 1));
            Assert.NotNull(add);
            const string testType = "testtype";
            Assert.Equal(EntityStatus.New, add.Status);
            add.Status = EntityStatus.Enabled;
            add.Type = testType;

            await _addressManager.Update(add);

            add = await _addressManager.Get(add.EmailAddress);
            Assert.Equal(EntityStatus.Enabled, add.Status);
            Assert.Equal(testType, add.Type);
        }

        /// <summary>
        ///A test for System.Collections.IEnumerable.GetEnumerator
        ///</summary>
        [Fact]
        public async Task GetEnumeratorTest1()
        {
            await InitAddressRecords(_dbContext);
            Assert.Equal(MaxDomainCount * MaxAddressCount, await _addressManager.Count());
        }

        /// <summary>
        ///A test for SetStatus
        ///</summary>
        [Fact]
        public async Task SetStatusTest1()
        {
            await InitAddressRecords(_dbContext);
            
            const long domainID = StartId;
            const EntityStatus status = EntityStatus.Enabled;
            await _addressManager.SetStatus(domainID, status);
            var addresses = await _addressManager.Get(domainID, String.Empty, MaxAddressCount);
            Assert.Equal(MaxAddressCount, addresses.Count);
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
            await InitAddressRecords(_dbContext);
            
            const long domainId = 1;

            
            //----------------------------------------------------------------------------------------------------
            //---make sure that we have max addresses for the given domain
            var addresses = await _addressManager.Get(domainId, string.Empty, MaxAddressCount + 1);
            Assert.Equal(MaxAddressCount, addresses.Count);

            await _addressManager.RemoveDomain(domainId);
            addresses = await _addressManager.Get(domainId, string.Empty, MaxAddressCount + 1);
            Assert.Empty(addresses);
        }

        /// <summary>
        ///A test for RemoveDomain
        ///</summary>
        [Fact]
        public async Task RemoveDomainTest()
        {
            await InitAddressRecords(_dbContext);
            
            const long domainId = 1;

            
            //----------------------------------------------------------------------------------------------------
            //---make sure that we have max addresses for the given domain
            var addresses = await _addressManager.Get(domainId, string.Empty, MaxAddressCount + 1);
            Assert.Equal(MaxAddressCount, addresses.Count);

            await _addressManager.RemoveDomain(domainId);
            addresses = await _addressManager.Get(domainId, string.Empty, MaxAddressCount + 1);
            Assert.Empty(addresses);
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest2()
        {
            await InitAddressRecords(_dbContext);
            
            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            Assert.Equal(emailAddresses.Length, (await _addressManager.Get(emailAddresses)).Count);
            await _addressManager.Remove(emailAddresses);
            Assert.Empty((await _addressManager.Get(emailAddresses)));
            Assert.Equal(MaxAddressCount * MaxDomainCount - emailAddresses.Length, await _addressManager.Count());
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest1()
        {
            await InitAddressRecords(_dbContext);
            
            string emailAddress = BuildEmailAddress(1, 1);
            await _addressManager.Get(emailAddress);
            Assert.NotNull(emailAddress);
            await _addressManager.Remove(emailAddress);
            Assert.Null(await _addressManager.Get(emailAddress));
        }

        /// <summary>
        ///A test for Remove
        ///</summary>
        [Fact]
        public async Task RemoveTest()
        {
            await InitAddressRecords(_dbContext);

            
            string emailAddress = BuildEmailAddress(1, 1);
            await _addressManager.Get(emailAddress);
            Assert.NotNull(emailAddress);
            await _addressManager.Remove(emailAddress);
            Assert.Null(await _addressManager.Get(emailAddress));
        }

        /// <summary>
        ///A test for GetEnumerator
        ///</summary>
        [Fact]
        public async Task GetEnumeratorTest()
        {
            await InitAddressRecords(_dbContext);
            Assert.Equal(MaxAddressCount * MaxDomainCount, await _addressManager.Count());
        }

        /// <summary>
        ///A test for GetByDomainTest1
        ///</summary>
        [Fact]
        public async Task GetByDomainTest1()
        {
            await InitAddressRecords(_dbContext);
            
            string domainName = BuildDomainName(1);
            var addresses = await _addressManager.GetAllForDomain(domainName.ToUpper()
                , int.MaxValue);
            Assert.Equal(MaxAddressCount, addresses.Count);
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
            await InitAddressRecords(_dbContext);
            
            string domainName = BuildDomainName(1);
            var addresses = await _addressManager.GetAllForDomain(domainName.ToUpper(), int.MaxValue);
            Assert.Equal(MaxAddressCount, addresses.Count);
            foreach (var addr in addresses)
            {
                Assert.Equal(1, addr.DomainID);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest13()
        {
            await InitAddressRecords(_dbContext);
            
            
            var emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            var actual = await _addressManager.Get(emailAddresses);
            Assert.Equal(emailAddresses.Length, actual.Count);

            for (var t = 0; t < actual.Count; t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest12()
        {
            await InitAddressRecords(_dbContext);
            

            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            var actual = await _addressManager.Get(emailAddresses, EntityStatus.New);
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
            await InitAddressRecords(_dbContext);
            string addressType = "SMTP";

            var domainManager = new DomainManager(_dbContext);
            var domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.New;
            await domainManager.Add(domain);
            domain = new Domain("address2.domain2.com");
            domain.Status = EntityStatus.Enabled;
            await domainManager.Add(domain);
            

            string[] emailAddresses = new[] { "NewGuy@address1.domain1.com", "AnotherNewGuy@address1.domain1.com" };

            var actual = await _addressManager.Get(emailAddresses, EntityStatus.New);
            Assert.Empty(actual);

            //
            // Now search with domainSearchEnabled = true
            //
            actual = await _addressManager.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Empty(actual);

            actual = await _addressManager.Get(emailAddresses, true, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count);

            for (var t = 0; t < actual.Count; t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            emailAddresses = new[] { "NewGuy@address2.domain2.com", "AnotherNewGuy@address2.domain2.com" };
            actual = await _addressManager.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Equal(emailAddresses.Length, actual.Count);

            //
            // domainSearchEnabled and no status.
            //
            actual = await _addressManager.Get(emailAddresses, true);
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
            await InitAddressRecords(_dbContext);
            string addressType = "SMTP";

            var domainManager = new DomainManager(_dbContext);
            var domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.New;
            await domainManager.Add(domain);

            //
            // test@address1.domain10.com aready exists
            //

            

            string[] emailAddresses = new[] { "NewGuy@Domain1.test.com", "AnotherNewGuy@address1.domain1.com", "test@Address1.domain10.com" };

            var actual = await _addressManager.Get(emailAddresses, EntityStatus.New);
            Assert.Single(actual);

            //
            // Now search with domainSearchEnabled = true
            //
            actual = await _addressManager.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Empty(actual);

            actual = await _addressManager.Get(emailAddresses, true, EntityStatus.New);
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
            await InitAddressRecords(_dbContext);
            var domainManager = new DomainManager(_dbContext);
            var domain = new Domain("address1.domain1.com");
            domain.Status = EntityStatus.Enabled;
            await domainManager.Add(domain);

            string addressType = "Undeliverable";
            var address = new MailAddress("badinbox1@address1.domain1.com");
            await _addressManager.Add(address, EntityStatus.Enabled, addressType);
            //
            // test@address1.domain10.com already exists
            //

            

            string[] emailAddresses = new[] { "BadInbox1@address1.domain1.com" };

            IEnumerable<Address> actual = await _addressManager.Get(emailAddresses, true, EntityStatus.Enabled);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            actual = await _addressManager.Get(emailAddresses, EntityStatus.Enabled);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            actual = await _addressManager.Get(emailAddresses, true);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }

            actual = await _addressManager.Get(emailAddresses);
            Assert.Single(actual);

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(emailAddresses, e => e.Equals(actual.ToArray()[t].EmailAddress, StringComparison.OrdinalIgnoreCase));
                Assert.Equal(EntityStatus.Enabled, actual.ToArray()[t].Status);
                Assert.Equal(addressType, actual.ToArray()[t].Type);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest11()
        {
            await InitAddressRecords(_dbContext);
            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            IEnumerable<Address> actual = await _addressManager.Get(emailAddresses);
            Assert.Equal(emailAddresses.Length, actual.Count());

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest10()
        {
            await InitAddressRecords(_dbContext);
            string emailAddress = BuildEmailAddress(1, 1);
            Address add = await _addressManager.Get(emailAddress);
            Assert.Equal(emailAddress, add.EmailAddress);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest9()
        {
            await InitAddressRecords(_dbContext);
            string emailAddress = BuildEmailAddress(1, 1);
            
            Address add = await _addressManager.Get(emailAddress);
            Assert.Equal(emailAddress, add.EmailAddress);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest8()
        {
            await InitAddressRecords(_dbContext);

            string[] emailAddresses = new[] { BuildEmailAddress(1, 1), BuildEmailAddress(2, 1), BuildEmailAddress(3, 1) };
            
            IEnumerable<Address> actual = await _addressManager.Get(emailAddresses, EntityStatus.New);
            Assert.Equal(emailAddresses.Length, actual.Count());

            for (int t = 0; t < actual.Count(); t++)
            {
                Assert.Contains(actual.ToArray()[t].EmailAddress, emailAddresses);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest7()
        {
            await InitAddressRecords(_dbContext);
            
            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await _addressManager.Get(addressIDs);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest6()
        {
            await InitAddressRecords(_dbContext);
            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await _addressManager.Get(addressIDs);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest5()
        {
            await InitAddressRecords(_dbContext);
            
            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await _addressManager.Get(addressIDs, EntityStatus.New);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest4()
        {
            
            await InitAddressRecords(_dbContext);
            long[] addressIDs = new long[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            IEnumerable<Address> actual = await _addressManager.Get(addressIDs, EntityStatus.New);
            Assert.Equal(addressIDs.Length, actual.Count());
            for (int t = 0; t < addressIDs.Length; t++)
            {
                Assert.Contains(actual.ToArray()[t].ID, addressIDs);
                Assert.Equal(EntityStatus.New, actual.ToArray()[t].Status);
            }
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTestLast3()
        {
            await InitAddressRecords(_dbContext);

            
            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, Address> mxsAll = _addressManager.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MaxDomainCount * MaxAddressCount, mxsAll.Count);

            //----------------------------------------------------------------------------------------------------
            //---grab the key at position 5 in the array, and use that as the "last" name to be passed in
            string val = mxsAll.Keys.OrderBy(k => k).ToArray()[4];

            var adds = await _addressManager.Get(val, MaxDomainCount * MaxAddressCount);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - 5
            Assert.Equal(MaxAddressCount * MaxDomainCount - 5, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            adds = await _addressManager.Get(val, 3);
            Assert.Equal(3, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = mxsAll.Keys.OrderBy(k => k).ToArray().Last();
            adds = await _addressManager.Get(val, MaxDomainCount * MaxAddressCount);
            Assert.Empty(adds);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = mxsAll.Keys.ToArray().First();
            adds = await _addressManager.Get(val, MaxDomainCount * MaxAddressCount);
            Assert.Equal(MaxDomainCount * MaxAddressCount - 1, adds.Count);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTestLast2()
        {
            await InitAddressRecords(_dbContext);
            

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, Address> mxsAll = _addressManager.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MaxDomainCount * MaxAddressCount, mxsAll.Count);

            //----------------------------------------------------------------------------------------------------
            //---grab the key at position 5 in the array, and use that as the "last" name to be passed in
            string val = mxsAll.Keys.OrderBy(k => k).ToArray()[4];

            var adds = await _addressManager.Get(val, MaxDomainCount * MaxAddressCount);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count - 5
            Assert.Equal(MaxAddressCount * MaxDomainCount - 5, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---try one with a limited number less than max count
            adds = await _addressManager.Get(val, 3);
            Assert.Equal(3, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the last item and see to ensure that no records are returned
            val = mxsAll.Keys.OrderBy(k => k).ToArray().Last();
            adds = await _addressManager.Get(val, MaxDomainCount * MaxAddressCount);
            Assert.Equal(0, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the first item and see to ensure that MAX - 1 records are returned
            val = mxsAll.Keys.ToArray().First();
            adds = await _addressManager.Get(val, MaxDomainCount * MaxAddressCount);
            Assert.Equal(MaxDomainCount * MaxAddressCount - 1, adds.Count);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest1()
        {
            await InitAddressRecords(_dbContext);
            

            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            Dictionary<string, Address> mxsAll = _addressManager.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MaxDomainCount * MaxAddressCount, mxsAll.Count);

            var adds = await _addressManager.Get(1, string.Empty, MaxAddressCount);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count for a domain 
            Assert.Equal(MaxAddressCount, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the first item in the list to be used as the last item 
            string val = adds[0].EmailAddress;
            adds = await _addressManager.Get(1, val, MaxAddressCount);

            //----------------------------------------------------------------------------------------------------
            //---expected that there should be MAXADDRESSCOUNT - 1 now
            Assert.Equal(MaxAddressCount - 1, adds.Count);
        }

        /// <summary>
        ///A test for GetByAgentName
        ///</summary>
        [Fact]
        public async Task GetTest()
        {
            await InitAddressRecords(_dbContext);
            
            //----------------------------------------------------------------------------------------------------
            //---get the full dictionary using the smtp domain name as the key and pick one to start at
            var mxsAll = _addressManager.ToDictionary(p => p.EmailAddress);

            Assert.Equal(MaxDomainCount * MaxAddressCount, mxsAll.Count);

            var adds = await _addressManager.Get(1, string.Empty, MaxAddressCount);

            //----------------------------------------------------------------------------------------------------
            //---expected that the count of mxs will be  max count for a domain 
            Assert.Equal(MaxAddressCount, adds.Count);

            //----------------------------------------------------------------------------------------------------
            //---get the first item in the list to be used as the last item 
            string val = adds[0].EmailAddress;
            adds = await _addressManager.Get(1, val, MaxAddressCount);

            //----------------------------------------------------------------------------------------------------
            //---expected that there should be MaxAddressCount - 1 now
            Assert.Equal(MaxAddressCount - 1, adds.Count);
        }

        /// <summary>
        ///A test for Count
        ///</summary>
        [Fact]
        public async Task CountTest()
        {
            await InitAddressRecords(_dbContext);
            
            Assert.Equal(MaxAddressCount, await _addressManager.Count(1));
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [Fact]
        public async Task AddTest3()
        {
            //----------------------------------------------------------------------------------------------------
            //---only init the domain records which will force a cleaning of the address records
            InitDomainRecords(_dbContext);
            

            //----------------------------------------------------------------------------------------------------
            //---make sure there are no mx records that exist
            Assert.Empty(_addressManager);

            const long domainId = 1;
            string email = BuildEmailAddress(1, 1);
            string displayName = BuildEmailAddressDisplayName(1, 1);
            Address address = new Address(domainId, email, displayName);

            await _addressManager.Add(address);
            Assert.Equal(1, await _addressManager.Count());
            address = await _addressManager.Get(email);
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
            InitDomainRecords(_dbContext);
            
            List<Address> addresses = new List<Address>();

            for (int i = 1; i <= MaxAddressCount; i++)
            {
                addresses.Add(new Address(StartId, BuildEmailAddress(StartId, i)));
            }
            Assert.Equal(0, await _addressManager.Count());
            await _addressManager.Add(addresses);
            Assert.Equal(MaxAddressCount, await _addressManager.Count());
            var aa = await _addressManager.Get(string.Empty, MaxAddressCount + 1);
            Assert.Equal(MaxAddressCount, aa.Count);
        }
    }
}