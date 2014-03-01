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
using System.Collections.Generic;
using System.Linq;
using FluentAssertions;
using Health.Direct.Policy.Extensions;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class CertPolicyManagerFacts : ConfigStoreTestBase
    {
        private static new CertPolicyManager CreateManager()
        {
            return new CertPolicyManager(CreateConfigStore(), new CertPolicyParseValidator());
        }

        private static CertPolicyGroupManager CreatePolicyGroupManager()
        {
            return new CertPolicyGroupManager(CreateConfigStore());
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            CertPolicyManager mgr = CreateManager();
            ConfigStore actual = mgr.Store;
            Assert.Equal(mgr.Store, actual);
        }


        /// <summary>
        /// A test for GetEnumerator
        /// </summary>
        [Fact]
        public void GetEnumeratorTest()
        {
            InitCertPolicyRecords();
            IEnumerable<CertPolicy> mgr = CreateManager();
            Assert.Equal(9, mgr.Count());
        }

        /// <summary>
        /// A test for Get by name
        /// Get policy by name
        /// </summary>
        [Fact]
        public void GetPolicyByName()
        {
            InitCertPolicyRecords();
            CertPolicyManager mgr = CreateManager();
            CertPolicy policy = mgr.Get("Policy1");
            policy.Name.Should().BeEquivalentTo("Policy1");
        }

        /// <summary>
        /// A test for Add Policy
        /// </summary>
        [Fact]
        public void AddPolicy()
        {
            InitCertPolicyRecords();
            CertPolicyManager mgr = CreateManager();

            CertPolicy expectedPolicy = new CertPolicy("UnitTestPolicy", "UnitTest Policy Description", "1 = 1".ToBytesUtf8());
            mgr.Add(expectedPolicy);
            

            CertPolicy actualCertPolicy = mgr.Get("UnitTestPolicy");
            expectedPolicy.Name.Should().BeEquivalentTo("UnitTestPolicy");
            expectedPolicy.CreateDate.Should().BeCloseTo(actualCertPolicy.CreateDate);
        }

        /// <summary>
        /// Add group to policy session based style
        /// </summary>
        [Fact]
        public void AddGroupToPolicySessionTest()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyManager mgr = CreateManager();
            CertPolicy policy;
            using (ConfigDatabase db = CreateConfigDatabase())
            {
                policy = mgr.Get(db, "Policy1");
                policy.CertPolicyGroupMap.Count.Should().Be(0);

                CertPolicyGroup group = new CertPolicyGroup("PolicyGroup99");

                policy.CertPolicyGroups.Add(group);
                db.SubmitChanges();
            }

            policy = mgr.Get("Policy1");
            policy.CertPolicyGroupMap.Count.Should().Be(1);

        }


        /// <summary>
        /// associate group to policy session based style
        /// </summary>
        [Fact]
        public void AssociatePolicyToGroupSessionTest()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            using (ConfigDatabase db = CreateConfigDatabase())
            {
                CertPolicyManager mgr = CreateManager();
                CertPolicy policy = mgr.Get(db, "Policy1");
                CertPolicyGroupManager groupMgr = CreatePolicyGroupManager();
                CertPolicyGroup group = groupMgr.Get(db, "PolicyGroup1");

                policy.CertPolicyGroups.Add(group);
                db.SubmitChanges();
            }
        }

        /// <summary>
        /// Add group to policy sessionless based style
        /// </summary>
        [Fact]
        public void AddGroupToPolicy()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();
            
            CertPolicyManager mgr = CreateManager();
            CertPolicy policy = mgr.Get("Policy1");

            CertPolicyGroup group = new CertPolicyGroup("PolicyGroup99");

            policy.CertPolicyGroups.Add(group);
            mgr.Update(policy);
           
        }

    }
}
