/* 
 Copyright (c) 2014, Direct Project
 All rights reserved.

 Authors:
    Joe Shook     Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using FluentAssertions;
using Health.Direct.Policy.Extensions;
using Xunit;
using Xunit.Extensions;

namespace Health.Direct.Config.Store.Tests
{
    public class CertPolicyTestFixture : ConfigStoreTestBase, IDisposable
    {
        public CertPolicyTestFixture()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();
        }

        public void Dispose()
        {
            // Do "global" teardown here; Only called once.
        }
    }

    public class CertPolicyManagerFacts : ConfigStoreTestBase, IUseFixture<CertPolicyTestFixture>
    {
        private static new CertPolicyManager CreateManager()
        {
            return new CertPolicyManager(CreateConfigStore(), new CertPolicyParseValidator());
        }

        private static CertPolicyGroupManager CreatePolicyGroupManager()
        {
            return new CertPolicyGroupManager(CreateConfigStore());
        }

        public void SetFixture(CertPolicyTestFixture data)
        {
           
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
           
            CertPolicyManager mgr = CreateManager();
            CertPolicy policy = mgr.Get("Policy1");
            policy.Name.Should().BeEquivalentTo("Policy1");
        }


        [Fact, AutoRollback]
        public void GetIncomingAndOutgoingCertPolicieByOwnerTest()
        {
            CertPolicyGroupManager groupMgr = CreatePolicyGroupManager();
            CertPolicyGroup policyGroup1 = groupMgr.Get("PolicyGroup1");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            CertPolicyGroup policyGroup2 = groupMgr.Get("PolicyGroup2");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            policyGroup2.CertPolicies.Count.Should().Be(0);

            


            //
            // Map cert policy group to domains
            //
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup2.Name, "domain2.test.com");
            //
            // Map cert policy group to policy
            //
            groupMgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);
            groupMgr.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.TRUST, true, false);
            

            CertPolicyManager policyMgr = CreateManager();
            CertPolicy[] policies = policyMgr.GetIncomingByOwner("domain1.test.com");
            policies.Length.Should().Be(2);
            policies = policyMgr.GetOutgoingByOwner("domain1.test.com");
            policies.Length.Should().Be(1);

            policies = policyMgr.GetIncomingByOwner("domain2.test.com");
            policies.Length.Should().Be(0);

            policies = policyMgr.GetIncomingByOwner("domain3.test.com");
            policies.Length.Should().Be(0);
            
        }


        [Fact, AutoRollback]
        public void GetIncomingAndOutgoingCertPolicieByOwnerAndUsage_Test()
        {
            CertPolicyGroupManager groupMgr = CreatePolicyGroupManager();
            CertPolicyGroup policyGroup1 = groupMgr.Get("PolicyGroup1");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            CertPolicyGroup policyGroup2 = groupMgr.Get("PolicyGroup2");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            policyGroup2.CertPolicies.Count.Should().Be(0);




            //
            // Map cert policy group to domains
            //
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup2.Name, "domain2.test.com");
            //
            // Map cert policy group to policy
            //
            groupMgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);
            groupMgr.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.PRIVATE_RESOLVER, true, false);


            CertPolicyManager policyMgr = CreateManager();
            CertPolicy[] policies = policyMgr.GetIncomingByOwner("domain1.test.com", CertPolicyUse.TRUST);
            policies.Length.Should().Be(1);
            policies = policyMgr.GetIncomingByOwner("domain1.test.com", CertPolicyUse.PUBLIC_RESOLVER);
            policies.Length.Should().Be(0);
            policies = policyMgr.GetOutgoingByOwner("domain1.test.com", CertPolicyUse.TRUST);
            policies.Length.Should().Be(1);
            policies = policyMgr.GetOutgoingByOwner("domain1.test.com", CertPolicyUse.PRIVATE_RESOLVER);
            policies.Length.Should().Be(0);

            policies = policyMgr.GetIncomingByOwner("domain2.test.com", CertPolicyUse.PRIVATE_RESOLVER);
            policies.Length.Should().Be(0);

            policies = policyMgr.GetIncomingByOwner("domain3.test.com", CertPolicyUse.PRIVATE_RESOLVER);
            policies.Length.Should().Be(0);

        }

        /// <summary>
        /// A test for Add Policy
        /// </summary>
        [Fact, AutoRollback]
        public void AddPolicy()
        {
            CertPolicyManager mgr = CreateManager();

            CertPolicy expectedPolicy = new CertPolicy("UnitTestPolicy", "", "1 = 1".ToBytesUtf8());
            mgr.Add(expectedPolicy);
            

            CertPolicy actualCertPolicy = mgr.Get("UnitTestPolicy");
            expectedPolicy.Name.Should().BeEquivalentTo("UnitTestPolicy");
            expectedPolicy.CreateDate.Should().BeCloseTo(actualCertPolicy.CreateDate);
        }

        

        
        /// <summary>
        /// Remove Policy
        /// </summary>
        [Fact, AutoRollback]
        public void DeletePolicyTest()
        {
           

            CertPolicyManager mgr = CreateManager();
            CertPolicy policy = mgr.Get("Policy2");
            mgr.Remove(policy.ID);

            mgr.Get("Policy2").Should().BeNull();
        }

        /// <summary>
        /// Remove Multiple Policies
        /// </summary>
        [Fact, AutoRollback]
        public void DeletePoliciesTest()
        {
            CertPolicyManager mgr = CreateManager();
            mgr.Count().Should().Be(9);
            long[] ids = mgr.Select(certPolicy => certPolicy.ID).ToArray();
            mgr.Remove(ids);

            mgr.Count().Should().Be(0);
        }

        /// <summary>
        /// Remove Multiple Policies
        /// </summary>
        [Fact, AutoRollback]
        public void Delete2PoliciesTest()
        {
           

            CertPolicyManager mgr = CreateManager();
            mgr.Count().Should().Be(9);
            mgr.Remove(new long[]{1,2});

            mgr.Count().Should().Be(7);
        }

        /// <summary>
        /// Delete policy and its associations
        /// </summary>
        [Fact, AutoRollback]
        public void DeletePolicyWithAssociations()
        {
           


            CertPolicyGroupManager groupMgr = CreatePolicyGroupManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            CertPolicyManager policyMgr = CreateManager();
            policyMgr.Get("Policy1").Should().NotBeNull();
            
            groupMgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.PRIVATE_RESOLVER, true, true);
            

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(1);


            CertPolicyManager mgr = CreateManager();
            CertPolicy policy = mgr.Get("Policy1");
            mgr.Remove(policy.ID);
            policyMgr.Get("Policy1").Should().BeNull();

        }


        /// <summary>
        /// A test for Update Policy
        /// </summary>
        [Fact, AutoRollback]
        public void UpdatePolicyDataTest()
        {
            
            CertPolicyManager mgr = CreateManager();

            CertPolicy newCertPolicy = new CertPolicy("UnitTestPolicy", "UnitTest Policy Description", "1 = 1".ToBytesUtf8());
            mgr.Add(newCertPolicy);
            CertPolicy actualCertPolicy = mgr.Get("UnitTestPolicy");
            actualCertPolicy.Should().NotBeNull();

            actualCertPolicy.Data = "1 != 1".ToBytesUtf8();
            mgr.Update(actualCertPolicy);

            CertPolicy updatedCertPolicy = mgr.Get("UnitTestPolicy");
            updatedCertPolicy.Data.ToUtf8String().ShouldAllBeEquivalentTo("1 != 1");
        }

        /// <summary>
        /// A test for Update Policy
        /// </summary>
        [Fact, AutoRollback]
        public void UpdatePolicyDescriptionTest()
        {
            
            CertPolicyManager mgr = CreateManager();

            CertPolicy newCertPolicy = new CertPolicy("UnitTestPolicy", "UnitTest Policy Description", "1 = 1".ToBytesUtf8());
            mgr.Add(newCertPolicy);
            CertPolicy actualCertPolicy = mgr.Get("UnitTestPolicy");
            actualCertPolicy.Should().NotBeNull();

            actualCertPolicy.Description = "blank";
            mgr.Update(actualCertPolicy);

            CertPolicy updatedCertPolicy = mgr.Get("UnitTestPolicy");
            updatedCertPolicy.Description.ShouldAllBeEquivalentTo("blank");
        }
    }
}
