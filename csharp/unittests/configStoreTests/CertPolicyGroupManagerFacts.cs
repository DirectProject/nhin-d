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
using System.Threading.Tasks;
using FluentAssertions;
using Health.Direct.Config.Store.Entity;
using Health.Direct.Policy.Extensions;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{

    [Collection("ManagerFacts")]

    public class CertPolicyGroupManagerFacts : ConfigStoreTestBase
    {
        private new static CertPolicyGroupManager CreateManager()
        {
            return new CertPolicyGroupManager(CreateConfigStore());
        }

        private static CertPolicyManager CreatePolicyManager()
        {
            return new CertPolicyManager(CreateConfigStore());
        }

        /// <summary>
        ///A test for Store
        ///</summary>
        [Fact]
        public void StoreTest()
        {
            CertPolicyGroupManager mgr = CreateManager();
            ConfigStore actual = mgr.Store;
            Assert.Equal(mgr.Store, actual);
        }

        /// <summary>
        /// A test for GetEnumerator
        /// </summary>
        [Fact]
        public async Task GetEnumeratorTest()
        {
            await InitCertPolicyGroupRecords();
            IEnumerable<CertPolicyGroup> mgr = CreateManager();
            Assert.Equal(3, mgr.Count());
        }

        /// <summary>
        /// A test for Get by name
        /// Get policy by name
        /// </summary>
        [Fact]
        public async Task GetPolicyGroupByName()
        {
            await InitCertPolicyGroupRecords();
            CertPolicyGroupManager mgr = CreateManager();
            CertPolicyGroup @group = await mgr.Get("PolicyGroup1");
            @group.Name.Should().BeEquivalentTo("PolicyGroup1");
        }

        // /// <summary>
        // /// A test for Get by name with policies
        // /// 
        // /// </summary>
        // [Fact]
        // public async Task GetPolicyGroupByNameWithPolicyUsage()
        // {
        //     InitCertPolicyRecords();
        //     InitCertPolicyGroupRecords();
        //
        //     CertPolicyGroupManager mgr = CreateManager();
        //     mgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);
        //     CertPolicyGroupMap[] maps = await mgr.GetWithPolicies("PolicyGroup1");
        //     maps[0].CertPolicyGroup.Name.Should().Be("PolicyGroup1");
        //     maps[0].CertPolicy.Name.Should().Be("Policy1");
        // }

        /// <summary>
        /// A test for Add PolicyGroup
        /// </summary>
        [Fact]
        public async Task AddPolicyGroup()
        {
            await InitCertPolicyGroupRecords();
            var mgr = CreateManager();

            var expectedPolicy = new CertPolicyGroup("UnitTestPolicyGroup", "UnitTest PolicyGroup Description");
            var actualCertPolicy = await mgr.Add(expectedPolicy);

            expectedPolicy.Name.Should().BeEquivalentTo("UnitTestPolicyGroup");
            expectedPolicy.CreateDate.Should().BeCloseTo(actualCertPolicy.CreateDate);
        }

        
        /// <summary>
        /// Associate policy to group sessionless based style
        /// </summary>
        [Fact]
        public async Task AddPolicyUseTest()
        {
            //arrange
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager mgr = CreateManager();

            //act
            await mgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);

            //assert 1
            CertPolicyGroup policyGroup = await mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(1);

            //act
            await mgr.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.TRUST, false, true);

            //assert 2
            policyGroup = await mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(2);
        }

        /// <summary>
        /// A test for Update Policy
        /// </summary>
        [Fact]
        public async Task UpdatePolicyGroupDescriptionTest()
        {
            await InitCertPolicyGroupRecords();
            var mgr = CreateManager();

            var newCertPolicyGroup = new CertPolicyGroup("UnitTestPolicyGroup", "UnitTest Policy Description");
            await mgr.Add(newCertPolicyGroup);
            var actualCertPolicy = await mgr.Get("UnitTestPolicyGroup");
            actualCertPolicy.Should().NotBeNull();

            actualCertPolicy.Description = "blank";
            await mgr.Update(actualCertPolicy);

            var updatedCertPolicy = await mgr.Get("UnitTestPolicyGroup");
            updatedCertPolicy.Description.ShouldBeEquivalentTo("blank");
        }

        /// <summary>
        /// Associate policy to group sessionless based style
        /// </summary>
        [Fact]
        public async Task DisassociatePolicyFromGroupTest()
        {
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager mgr = CreateManager();

            //act
            await mgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);

            CertPolicyGroup policyGroup = await mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(1);

            CertPolicyGroupMap[] map = new CertPolicyGroupMap[] { policyGroup.CertPolicyGroupMaps.First() };
            await mgr.RemovePolicy(map);

            policyGroup = await mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);
        }

        [Fact]
        public async Task AssociatePolicyGroupToDomain_Test()
        {
            await InitDomainRecords();
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = await groupMgr.Get("PolicyGroup1");
            // policyGroup.CertPolicies.Count.Should().Be(0);

            await groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            await groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);
        }

        [Fact]
        public async Task DisassociatePolicyGroupFromomain_Test()
        {
            await InitDomainRecords();
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            await groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            await groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);

            // now disassociate 
            await groupMgr.DisassociateFromDomain("domain1.test.com", policyGroup.ID);

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(1);

            await groupMgr.DisassociateFromDomain("domain2.test.com", policyGroup.ID);

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(0);
        }

        /// <summary>
        /// Disassociate all policy groups from a owner (
        /// </summary>
        [Fact]
        public async Task Disassociate_All_PolicyGroups_ByDomain_Test()
        {
            await InitDomainRecords();
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            await groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            await groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);

            //group2
            CertPolicyGroup policyGroup2 = await groupMgr.Get("PolicyGroup2");
            policyGroup2.CertPolicies.Count.Should().Be(0);
            await groupMgr.AssociateToOwner(policyGroup2.Name, "domain1.test.com");
            policyGroup2 = await groupMgr.Get("PolicyGroup2");
            policyGroup2.CertPolicyGroupDomainMaps.Count.Should().Be(1);

            await groupMgr.DisassociateFromDomain("domain1.test.com");

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(1);

            policyGroup = await groupMgr.Get("PolicyGroup2");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(0);
        }

        /// <summary>
        /// Disassociate a policy group from all owners
        /// </summary>
        [Fact]
        public async Task Disassociate_All_Domains_From_PolicyGroup_Test()
        {
            await InitDomainRecords();
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            await groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            await groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);

            await groupMgr.DisassociateFromDomains(policyGroup.ID);

            policyGroup = await groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(0);
        }

        /// <summary>
        /// Get Policy Groups by domain
        /// </summary>
        [Fact]
        public async Task GetPolicyGroupsByDomains()
        {
            await InitDomainRecords();
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup1 = await groupMgr.Get("PolicyGroup1");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            CertPolicyGroup policyGroup2 = await groupMgr.Get("PolicyGroup2");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            policyGroup2.CertPolicies.Count.Should().Be(0);

            //
            // Map cert policy group to domains
            //
            await groupMgr.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            await groupMgr.AssociateToOwner(policyGroup1.Name, "domain2.test.com");
            await groupMgr.AssociateToOwner(policyGroup1.Name, "domain3.test.com");
            await groupMgr.AssociateToOwner(policyGroup2.Name, "domain2.test.com");

            var policyGroups = await groupMgr.GetByDomains(new string[] { "domain1.test.com" });
            policyGroups.Count.Should().Be(1);
            policyGroups.First().CertPolicyGroupDomainMaps.Count.Should().Be(1);

            policyGroups = await groupMgr.GetByDomains(new string[] { "domain2.test.com" });
            policyGroups.Count.Should().Be(2);
            policyGroups.Where(cpg => cpg.Name == "PolicyGroup1").Select(cpg => cpg.CertPolicyGroupDomainMaps).ToList().Count.Should().Be(1);
            policyGroups.Where(cpg => cpg.Name == "PolicyGroup2").Select(cpg => cpg.CertPolicyGroupDomainMaps).ToList().Count.Should().Be(1);

            policyGroups = await groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com" });
            policyGroups.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);

            policyGroups = await groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com", "domain3.test.com" });
            policyGroups.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(3);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);
        }

        /// <summary>
        /// Get Policy Groups by domain and include policies
        /// </summary>
        [Fact]
        public async Task GetPolicyGroupsByDomainsWithPoliciesTest()
        {
            await InitDomainRecords();
            await InitCertPolicyRecords();
            await InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup1 = await groupMgr.Get("PolicyGroup1");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            CertPolicyGroup policyGroup2 = await groupMgr.Get("PolicyGroup2");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            policyGroup2.CertPolicies.Count.Should().Be(0);

            //
            // Map cert policy group to domains
            //
            await groupMgr.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            await groupMgr.AssociateToOwner(policyGroup1.Name, "domain2.test.com");
            await groupMgr.AssociateToOwner(policyGroup1.Name, "domain3.test.com");
            await groupMgr.AssociateToOwner(policyGroup2.Name, "domain2.test.com");

            //
            // Map cert policy group to policy
            //
            await groupMgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.PUBLIC_RESOLVER, true, true);
            await groupMgr.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.PUBLIC_RESOLVER, true, true);
            await groupMgr.AddPolicyUse("Policy2", "PolicyGroup2", CertPolicyUse.PUBLIC_RESOLVER, true, true);

            var policyGroups = await groupMgr.GetByDomains(new string[] { "domain1.test.com" });
            policyGroups.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicies.Count.Should().Be(2);
            foreach (var policy in policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicies)
            {
                Console.WriteLine(policy.Name);
                Console.WriteLine(policy.Data.ToUtf8String());
            }

            policyGroups = await groupMgr.GetByDomains(new string[] { "domain2.test.com" });
            policyGroups.Count .Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupMaps.Count.Should().Be(1);

            policyGroups = await groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com" });
            policyGroups.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupMaps.Count.Should().Be(1);

            policyGroups = await groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com", "domain3.test.com" });
            policyGroups.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(3);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupMaps.Count.Should().Be(1);
        }
    }
}
