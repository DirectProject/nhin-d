/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescipts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

using System;
using FluentAssertions;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class CertPolicyGroupMapFacts : ConfigStoreTestBase
    {

        private static new CertPolicyGroupManager CreateManager()
        {
            return new CertPolicyGroupManager(CreateConfigStore());
        }

        
        /// <summary>
        /// A test for CreateDate
        /// </summary>
        [Fact]
        public void CreateDateTest()
        {
            CertPolicyGroupMap target = new CertPolicyGroupMap();
            DateTime expected = DateTime.UtcNow;
            target.CreateDate = expected;
            DateTime actual = target.CreateDate;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        /// A test for CertPolicyUse
        /// </summary>
        [Fact]
        public void CertPolicyUseTest()
        {
            CertPolicyGroupMap target = new CertPolicyGroupMap();
            CertPolicyUse expected = CertPolicyUse.Trust;
            CertPolicyUse actual = target.Use;
            Assert.Equal(expected, actual);

            target = new CertPolicyGroupMap();
            target.Use = CertPolicyUse.PrivateResolver;
            expected = CertPolicyUse.PrivateResolver;
            actual = target.Use;
            Assert.Equal(expected, actual);
        }

        /// <summary>
        /// A test for ForOutgoing
        /// </summary>
        [Fact]
        public void ForOutgoingTest()
        {
            CertPolicyGroupMap target = new CertPolicyGroupMap();
            Assert.False(target.ForOutgoing);
            target.ForOutgoing = true;
            Assert.True(target.ForOutgoing);
        }

        /// <summary>
        /// A test for ForIncoming
        /// </summary>
        [Fact]
        public void ForIncomingTest()
        {
            CertPolicyGroupMap target = new CertPolicyGroupMap();
            Assert.False(target.ForIncoming);
            target.ForIncoming = true;
            Assert.True(target.ForIncoming);
        }

        /// <summary>
        /// A test for Get by name
        /// Get policy by name
        /// </summary>
        [Fact]
        public void GetPolicyByName()
        {
            InitCertPolicyRecords();
            CertPolicyGroupManager mgr = CreateManager();
            CertPolicyGroup policy = mgr.Get("PolicyGroup1");
            policy.Name.Should().BeEquivalentTo("PolicyGroup1");
        }


        /// <summary>
        /// A test for Add Policy @group 
        /// </summary>
        [Fact]
        public void AddPolicyGroup()
        {
            InitCertPolicyGroupRecords();
            CertPolicyGroupManager mgr = CreateManager();

            CertPolicyGroup expectedPolicy = new CertPolicyGroup("UnitTestPolicyGroup", "UnitTest Policy Group Description");
            mgr.Add(expectedPolicy);


            CertPolicyGroup actualCertPolicy = mgr.Get("UnitTestPolicyGroup");
            expectedPolicy.Name.Should().BeEquivalentTo("UnitTestPolicyGroup");
            expectedPolicy.CreateDate.Should().BeCloseTo(actualCertPolicy.CreateDate);
        }

    }
}
