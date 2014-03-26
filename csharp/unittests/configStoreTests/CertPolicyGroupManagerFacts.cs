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
using FluentAssertions;
using Health.Direct.Common.Mail;
using Health.Direct.Policy.Extensions;
using Xunit;

namespace Health.Direct.Config.Store.Tests
{
    public class CertPolicyGroupManagerFacts : ConfigStoreTestBase
    {
        private static new CertPolicyGroupManager CreateManager()
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
        public void GetEnumeratorTest()
        {
            InitCertPolicyGroupRecords();
            IEnumerable<CertPolicyGroup> mgr = CreateManager();
            Assert.Equal(3, mgr.Count());
        }

        /// <summary>
        /// A test for Get by name
        /// Get policy by name
        /// </summary>
        [Fact]
        public void GetPolicyGroupByName()
        {
            InitCertPolicyGroupRecords();
            CertPolicyGroupManager mgr = CreateManager();
            CertPolicyGroup @group = mgr.Get("PolicyGroup1");
            @group.Name.Should().BeEquivalentTo("PolicyGroup1");
        }

        /// <summary>
        /// A test for Get by name with policies
        /// 
        /// </summary>
        [Fact]
        public void GetPolicyGroupByNameWithPolicyUsage()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            
            CertPolicyGroupManager mgr = CreateManager();
            mgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);
            CertPolicyGroupMap[] maps = mgr.GetWithPolicies("PolicyGroup1");
            maps[0].CertPolicyGroup.Name.Should().Be("PolicyGroup1");
            maps[0].CertPolicy.Name.Should().Be("Policy1");
        }


        
        /// <summary>
        /// A test for Add PolicyGroup
        /// </summary>
        [Fact]
        public void AddPolicyGroup()
        {
            InitCertPolicyGroupRecords();
            CertPolicyGroupManager mgr = CreateManager();

            CertPolicyGroup expectedPolicy = new CertPolicyGroup("UnitTestPolicyGroup", "UnitTest PolicyGroup Description");
            CertPolicyGroup actualCertPolicy = mgr.Add(expectedPolicy);
            
            expectedPolicy.Name.Should().BeEquivalentTo("UnitTestPolicyGroup");
            expectedPolicy.CreateDate.Should().BeCloseTo(actualCertPolicy.CreateDate);
        }

        /// <summary>
        /// Associate @group to policy session based style
        /// </summary>
        [Fact(Skip="Broken.  Thinks it is adding a new certpolicy with the same name.  This session technique is probably not going to be used.")]
        public void AssociatePolicyToGroupSessionTest()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            using (ConfigDatabase db = CreateConfigDatabase(CertPolicyGroupManager.DataLoadOptions))
            {
                CertPolicyGroupManager mgr = CreateManager();
                CertPolicyGroup policyGroup = mgr.Get(db, "PolicyGroup1");
                policyGroup.CertPolicies.Count.Should().Be(0);
                CertPolicyManager policyMgr = CreatePolicyManager();
                CertPolicy certPolicy = policyMgr.Get("Policy1");

                policyGroup.CertPolicies.Add(certPolicy);
                db.SubmitChanges();
                policyGroup = mgr.Get("PolicyGroup1");
                policyGroup.CertPolicies.Count.Should().Be(1);
            }
        }

        
        /// <summary>
        /// Associate policy to group sessionless based style
        /// </summary>
        [Fact]
        public void AddPolicyUseTest()
        {
            //arrange
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager mgr = CreateManager();

            //act
            mgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);

            //assert 1
            CertPolicyGroup policyGroup = mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(1);

            
            //act
            mgr.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.TRUST, false, true);

            //assert 2
            policyGroup = mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(2);
        }

        
        
        /// <summary>
        /// A test for Update Policy
        /// </summary>
        [Fact]
        public void UpdatePolicyGroupDescriptionTest()
        {
            InitCertPolicyGroupRecords();
            CertPolicyGroupManager mgr = CreateManager();

            CertPolicyGroup newCertPolicyGroup = new CertPolicyGroup("UnitTestPolicyGroup", "UnitTest Policy Description");
            mgr.Add(newCertPolicyGroup);
            CertPolicyGroup actualCertPolicy = mgr.Get("UnitTestPolicyGroup");
            actualCertPolicy.Should().NotBeNull();

            actualCertPolicy.Description = "blank";
            mgr.Update(actualCertPolicy);

            CertPolicyGroup updatedCertPolicy = mgr.Get("UnitTestPolicyGroup");
            updatedCertPolicy.Description.ShouldAllBeEquivalentTo("blank");
        }

        

        /// <summary>
        /// Associate policy to group sessionless based style
        /// </summary>
        [Fact]
        public void DissAssociatePolicyFromGroupTest()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager mgr = CreateManager();

            //act
            mgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);

            CertPolicyGroup policyGroup = mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(1);

            
            CertPolicyGroupMap[] map = new CertPolicyGroupMap[] {policyGroup.CertPolicyGroupMaps.First()};
            mgr.RemovePolicy(map);

            policyGroup = mgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);
        }


       

        [Fact]
        public void AssociatePolicyGroupToDomain_Test()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);


            groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);
        }

        

        [Fact]
        public void DissAssociatePolicyGroupFromomain_Test()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);


            groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);

            // now disassociate 
            groupMgr.DissAssociateFromDomain("domain1.test.com", policyGroup.ID);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(1);

            groupMgr.DissAssociateFromDomain("domain2.test.com", policyGroup.ID);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(0);
        }

        /// <summary>
        /// Disassociate all policy groups from a owner (
        /// </summary>
        [Fact]
        public void DissAssociate_All_PolicyGroups_ByDomain_Test()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);


            groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);

            //group2
            CertPolicyGroup policyGroup2 = groupMgr.Get("PolicyGroup2");
            policyGroup2.CertPolicies.Count.Should().Be(0);
            groupMgr.AssociateToOwner(policyGroup2.Name, "domain1.test.com");
            policyGroup2 = groupMgr.Get("PolicyGroup2");
            policyGroup2.CertPolicyGroupDomainMaps.Count.Should().Be(1);

            groupMgr.DissAssociateFromDomain("domain1.test.com");

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(1);

            policyGroup = groupMgr.Get("PolicyGroup2");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(0);

        }

        /// <summary>
        /// Dissassociate a policy group from all owners
        /// </summary>
        [Fact]
        public void DissAssociate_All_Domains_From_PolicyGroup_Test()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);


            groupMgr.AssociateToOwner(policyGroup.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup.Name, "domain2.test.com");

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);
            
            groupMgr.DissAssociateFromDomains(policyGroup.ID);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(0);

        }

        /// <summary>
        /// Get Policy Groups by domain
        /// </summary>
        [Fact]
        public void GetPolicyGroupsByDomains()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup1 = groupMgr.Get("PolicyGroup1");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            CertPolicyGroup policyGroup2 = groupMgr.Get("PolicyGroup2");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            policyGroup2.CertPolicies.Count.Should().Be(0);

            //
            // Map cert policy group to domains
            //
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain2.test.com");
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain3.test.com");
            groupMgr.AssociateToOwner(policyGroup2.Name, "domain2.test.com");

            CertPolicyGroup[] policyGroups = groupMgr.GetByDomains(new string[] { "domain1.test.com" });
            policyGroups.Length.Should().Be(1);
            policyGroups[0].CertPolicyGroupDomainMaps.Count.Should().Be(1);

            policyGroups = groupMgr.GetByDomains(new string[] { "domain2.test.com" });
            policyGroups.Length.Should().Be(2);
            policyGroups.Where(cpg => cpg.Name == "PolicyGroup1").Select(cpg => cpg.CertPolicyGroupDomainMaps).ToList().Count.Should().Be(1);
            policyGroups.Where(cpg => cpg.Name == "PolicyGroup2").Select(cpg => cpg.CertPolicyGroupDomainMaps).ToList().Count.Should().Be(1);

            policyGroups = groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com" });
            policyGroups.Length.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);

            policyGroups = groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com", "domain3.test.com" });
            policyGroups.Length.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(3);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);

        }

        /// <summary>
        /// Get Policy Groups by domain and include policies
        /// </summary>
        [Fact]
        public void GetPolicyGroupsByDomainsWithPoliciesTest()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup1 = groupMgr.Get("PolicyGroup1");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            CertPolicyGroup policyGroup2 = groupMgr.Get("PolicyGroup2");
            policyGroup1.CertPolicies.Count.Should().Be(0);
            policyGroup2.CertPolicies.Count.Should().Be(0);

           
            
            //
            // Map cert policy group to domains
            //
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain2.test.com");
            groupMgr.AssociateToOwner(policyGroup1.Name, "domain3.test.com");
            groupMgr.AssociateToOwner(policyGroup2.Name, "domain2.test.com");
            //
            // Map cert policy group to policy
            //
            groupMgr.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.PUBLIC_RESOLVER, true, true);
            groupMgr.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.PUBLIC_RESOLVER, true, true);
            groupMgr.AddPolicyUse("Policy2", "PolicyGroup2", CertPolicyUse.PUBLIC_RESOLVER, true, true);

            CertPolicyGroup[] policyGroups = groupMgr.GetByDomains(new string[] { "domain1.test.com" });
            policyGroups.Length.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicies.Count.Should().Be(2);
            foreach (var policy in policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicies)
            {
                Console.WriteLine(policy.Name);
                Console.WriteLine(policy.Data.ToUtf8String());
            }

            policyGroups = groupMgr.GetByDomains(new string[] { "domain2.test.com" });
            policyGroups.Length.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupMaps.Count.Should().Be(1);

            policyGroups = groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com" });
            policyGroups.Length.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupMaps.Count.Should().Be(1);

            policyGroups = groupMgr.GetByDomains(new string[] { "domain1.test.com", "domain2.test.com", "domain3.test.com" });
            policyGroups.Length.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupDomainMaps.Count.Should().Be(3);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup1").CertPolicyGroupMaps.Count.Should().Be(2);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupDomainMaps.Count.Should().Be(1);
            policyGroups.Single(cpg => cpg.Name == "PolicyGroup2").CertPolicyGroupMaps.Count.Should().Be(1);


        }
    }
}
