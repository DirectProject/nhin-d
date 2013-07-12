/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Chris Lomonico  chris.lomonico@surescripts.com
    Joe Shook       jshook@krytiq.com
   
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Net.Mail;

using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class AddressFacts : ConfigStoreTestBase 
    {


        /// <summary>
        ///A test for UpdateDate
        ///</summary>
        [Fact]
        public void UpdateDateTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1,1), BuildEmailAddressDisplayName(1,1));
            DateTime expected = DateTime.UtcNow;
            Assert.NotEqual(addr.UpdateDate, expected);            
            addr.UpdateDate = expected;
            DateTime actual = addr.UpdateDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Type
        ///</summary>
        [Fact]
        public void TypeTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            const string expected = "test.type";
            Assert.NotEqual(addr.Type, expected);
            addr.Type = expected;
            string actual = addr.Type;
            Assert.Equal(expected, actual);

            
        }

        /// <summary>
        ///A test for Status
        ///</summary>
        [Fact]
        public void StatusTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            const EntityStatus expected = EntityStatus.Enabled;
            Assert.NotEqual(addr.Status, expected);
            addr.Status = expected;
            EntityStatus actual = addr.Status;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ID
        ///</summary>
        [Fact]
        public void IDTest()
        {

            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            const long expected = 777;
            Assert.NotEqual(addr.ID, expected);
            addr.ID = expected;
            long actual = addr.ID;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for HasType
        ///</summary>
        [Fact]
        public void HasTypeTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            bool actual = addr.HasType;
            Assert.False(actual);
            addr.Type = "testtype";
            actual = addr.HasType;
            Assert.True(actual);
            
        }

        /// <summary>
        ///A test for EmailAddress
        ///</summary>
        [Fact]
        public void EmailAddressTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            const string expected = "test@test.com";
            Assert.NotEqual(addr.EmailAddress, expected);
            addr.EmailAddress = expected;
            string actual = addr.EmailAddress;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for DomainID
        ///</summary>
        [Fact]
        public void DomainIDTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            const long expected = 777;
            Assert.NotEqual(addr.DomainID, expected);
            addr.DomainID = expected;
            long actual = addr.DomainID;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for DisplayName
        ///</summary>
        [Fact]
        public void DisplayNameTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            const string expected = "test display name";
            Assert.NotEqual(addr.DisplayName, expected);
            addr.DisplayName = expected;
            string actual = addr.DisplayName;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for CreateDate
        ///</summary>
        [Fact]
        public void CreateDateTest()
        {
            Address addr = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            DateTime expected = DateTime.UtcNow;
            Assert.NotEqual(addr.CreateDate, expected);
            addr.CreateDate = expected;
            DateTime actual = addr.CreateDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ToMailAddress
        ///</summary>
        [Fact]
        public void ToMailAddressTest()
        {
            Address target = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            MailAddress expected = new MailAddress(BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            MailAddress actual = target.ToMailAddress();
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Match
        ///</summary>
        [Fact]
        public void MatchTest1()
        {
            Address target = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            MailAddress address = new MailAddress(BuildEmailAddress(1, 1));
            Assert.True(target.Match(address));
            address = new MailAddress(BuildEmailAddress(2, 1));
            Assert.False(target.Match(address));
            
        }

        /// <summary>
        ///A test for Match
        ///</summary>
        [Fact]
        public void MatchTest()
        {
            Address target = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            string address = BuildEmailAddress(1, 1);
            Assert.True(target.Match(address));
            address = BuildEmailAddress(2, 1);
            Assert.False(target.Match(address));
            
        }

        /// <summary>
        ///A test for IsValidMailAddress
        ///</summary>
        [Fact]
        public void IsValidMailAddressTest()
        {
            Address target = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            Assert.True(target.IsValidMailAddress());
            target.EmailAddress = "bunk";
            Assert.False(target.IsValidMailAddress());
            
        }

        /// <summary>
        ///A test for CopyFixed
        ///</summary>
        [Fact]
        public void CopyFixedTest()
        {

            Address source = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            Address target = new Address();
            target.CopyFixed(source);
            Assert.Equal(source.ID, target.ID);
            Assert.Equal(source.CreateDate, target.CreateDate);
            Assert.Equal(source.DomainID, target.DomainID);
            Assert.Equal(source.UpdateDate, target.UpdateDate);
        }

        /// <summary>
        ///A test for ApplyChanges
        ///</summary>
        [Fact]
        public void ApplyChangesTest()
        {
            Address source = new Address(1, BuildEmailAddress(1, 1), BuildEmailAddressDisplayName(1, 1));
            Address target = new Address();
            target.ApplyChanges(source);

            Assert.Equal(source.DisplayName, target.DisplayName);
            Assert.Equal(source.Type, target.Type);
            Assert.Equal(source.Status, target.Status);
 
        }

        /// <summary>
        ///A test for Address Constructor
        ///</summary>
        [Fact]
        public void AddressConstructorTest3()
        {
            const long domainID = STARTID;
            string address = BuildEmailAddress(1, 1);
            Address target = new Address(domainID, address);
            Assert.Equal(domainID, target.DomainID);
            Assert.Equal(address, target.EmailAddress);
            
        }

        /// <summary>
        ///A test for Address Constructor
        ///</summary>
        [Fact]
        public void AddressConstructorTest2()
        {
            Address target = new Address();
            string dateTimeNow = DateTimeHelper.Now.ToString();
            Assert.Null(target.EmailAddress);
            Assert.Null(target.Type);
            Assert.Equal(0, target.DomainID);
            Assert.Equal(0, target.ID);
            Assert.Equal(dateTimeNow, target.CreateDate.ToString());
            Assert.Equal(dateTimeNow, target.UpdateDate.ToString());
        }

        /// <summary>
        ///A test for Address Constructor
        ///</summary>
        [Fact]
        public void AddressConstructorTest1()
        {
            const long domainID = STARTID;
            MailAddress address = new MailAddress(BuildEmailAddress(1, 1));
            Address target = new Address(domainID, address);
            Assert.Equal(domainID,target.DomainID);
            Assert.Equal(address, target.ToMailAddress());
        }

        /// <summary>
        ///A test for Address Constructor
        ///</summary>
        [Fact]
        public void AddressConstructorTest()
        {
            const long domainID = STARTID;
            string address = BuildEmailAddress(1, 1);
            string displayName = BuildEmailAddressDisplayName(1, 1);
            Address target = new Address(domainID, address, displayName);
            Assert.Equal(domainID, target.DomainID);
            Assert.Equal(address, target.EmailAddress);
            Assert.Equal(displayName, target.DisplayName);
        }
    }
}