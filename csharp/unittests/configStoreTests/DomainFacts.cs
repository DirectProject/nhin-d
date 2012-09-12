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
    class DomainFacts : ConfigStoreTestBase
    {
        /// <summary>
        ///A test for UpdateDate
        ///</summary>
        [Fact]
        public void UpdateDateTest()
        {
            Domain target = new Domain(BuildDomainName(GetRndDomainID()));
            DateTime expected = DateTime.UtcNow;
            target.UpdateDate = expected;
            DateTime actual = target.UpdateDate;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        ///A test for Status
        ///</summary>
        [Fact]
        public void StatusTest()
        {
            Domain target = new Domain(BuildDomainName(GetRndDomainID()));
            const EntityStatus expected = EntityStatus.Disabled;
            target.Status = expected;
            EntityStatus actual = target.Status;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for Name
        ///</summary>
        [Fact]
        public void NameTest()
        {
            Domain target = new Domain();
            string expected = BuildDomainName(GetRndDomainID()); 
            target.Name = expected;
            string actual = target.Name;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for ID
        ///</summary>
        [Fact]
        public void IDTest()
        {
            long expected = GetRndDomainID();
            Domain target = new Domain(BuildDomainName(expected)) {ID = expected};
            long actual = target.ID;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for CreateDate
        ///</summary>
        [Fact]
        public void CreateDateTest()
        {
            Domain target = new Domain(BuildDomainName(GetRndDomainID()));
            DateTime expected = DateTime.UtcNow; 
            target.CreateDate = expected;
            DateTime actual = target.CreateDate;
            Assert.Equal(expected, actual);
            
        }

        /// <summary>
        ///A test for IsValidEmailDomain
        ///</summary>
        [Fact]
        public void IsValidEmailDomainTest1()
        {
            Domain target = new Domain(BuildDomainName(GetRndDomainID()));
            Assert.True(target.IsValidEmailDomain());
            target.Name = "bunk.";
            Assert.False( target.IsValidEmailDomain());
        }

        /// <summary>
        ///A test for IsValidEmailDomain
        ///</summary>
        [Fact]
        public void IsValidEmailDomainTest()
        {
            Assert.True(Domain.IsValidEmailDomain(BuildDomainName(GetRndDomainID())));
            Assert.False(Domain.IsValidEmailDomain("bunk."));
            
        }

        /// <summary>
        ///A test for CopyFixed
        ///</summary>
        [Fact]
        public void CopyFixedTest()
        {
            Domain source = new Domain(BuildDomainName(GetRndDomainID()));
            Domain target = new Domain();
            target.CopyFixed(source);
            Assert.Equal(source.ID, target.ID);
            Assert.Equal(source.CreateDate, target.CreateDate);
            Assert.Equal(source.Name, target.Name);
            Assert.Equal(source.UpdateDate, target.UpdateDate);
        }

        /// <summary>
        ///A test for ApplyChanges
        ///</summary>
        [Fact]
        public void ApplyChangesTest()
        {
            Domain source = new Domain(BuildDomainName(GetRndDomainID()));
            Domain target = new Domain();
            source.Status = EntityStatus.Disabled;
            Assert.NotEqual(source.Status, target.Status);
            target.ApplyChanges(source);
            Assert.Equal(source.Status, target.Status);
        }

    }
}