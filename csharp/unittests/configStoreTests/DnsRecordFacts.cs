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

using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class DnsRecordFacts : ConfigStoreTestBase
    {

        /// <summary>
        ///A test for UpdateDate
        ///</summary>
        [Fact]
        public void UpdateDateTest()
        {
            DnsRecord target = new DnsRecord(); 
            DateTime expected = new DateTime(); 
            target.UpdateDate = expected;
            DateTime actual = target.UpdateDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for TypeID
        ///</summary>
        [Fact]
        public void TypeIDTest()
        {
            DnsRecord target = new DnsRecord(); 
            const int expected = 777; 
            
            target.TypeID = expected;
            int actual = target.TypeID;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ID
        ///</summary>
        [Fact]
        public void RecordIDTest()
        {
            DnsRecord target = new DnsRecord();
            const long expected = 777;
            
            target.ID = expected;
            long actual = target.ID;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for RecordData
        ///</summary>
        [Fact]
        public void RecordDataTest()
        {
            DnsRecord target = new DnsRecord();
            byte[] expected = System.Text.Encoding.UTF8.GetBytes("this is just a test");
            target.RecordData = expected;
            byte[] actual  = target.RecordData;
            
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Notes
        ///</summary>
        [Fact]
        public void NotesTest()
        {
            DnsRecord target = new DnsRecord();
            const string expected = "these are some test notes";
            target.Notes = expected;
            string actual = target.Notes;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for DomainName
        ///</summary>
        [Fact]
        public void DomainNameTest()
        {
            DnsRecord target = new DnsRecord();
            string expected = BuildDomainName(1);
            target.DomainName = expected;
            string actual = target.DomainName;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for CreateDate
        ///</summary>
        [Fact]
        public void CreateDateTest()
        {
            DnsRecord target = new DnsRecord();
            DateTime expected = DateTime.UtcNow;
            target.CreateDate = expected;
            DateTime actual = target.CreateDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ValidateHasData
        ///</summary>
        [Fact]
        public void ValidateHasDataTest()
        {
            DnsRecord target = new DnsRecord();
            bool failed;
            try{
                target.ValidateHasData();
                failed = false;
            }catch{

                failed = true;
            }
            Assert.True(failed);
            try{
                target.RecordData = System.Text.Encoding.UTF8.GetBytes("this is some test string");
                target.ValidateHasData();
                failed = false;
            }catch{

                failed = true;
            }
            
            Assert.False(failed);
        }

        /// <summary>
        ///A test for CopyFixed
        ///</summary>
        [Fact]
        public void CopyFixedTest()
        {
            DnsRecord target = new DnsRecord(); 
            DnsRecord source = new DnsRecord {ID = 777, DomainName = BuildDomainName(1), TypeID = 3};

            target.CopyFixed(source);
            Assert.Equal(777, target.ID);
            Assert.Equal(BuildDomainName(1), target.DomainName);
            Assert.Equal(3, target.TypeID);

            
        }

        /// <summary>
        ///A test for ApplyChanges
        ///</summary>
        [Fact]
        public void ApplyChangesTest()
        {
            DnsRecord target = new DnsRecord();
            DnsRecord source = new DnsRecord();
            DateTime update = DateTimeHelper.Now;
            byte[] bytes = System.Text.Encoding.UTF8.GetBytes("test this");
            source.RecordData = bytes;
            source.Notes = "some notes here";
            source.UpdateDate = update;
            target.ApplyChanges(source);
            Assert.Equal(bytes, target.RecordData);
            Assert.Equal("some notes here", target.Notes);
            Assert.True(update <= target.UpdateDate );
            
        }

    }
}
