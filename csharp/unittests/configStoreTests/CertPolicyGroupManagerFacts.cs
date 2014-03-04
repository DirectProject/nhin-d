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
        [Fact]
        public void AssociatePolicyToGroupSessionTest()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            using (ConfigDatabase db = CreateConfigDatabase(CertPolicyGroupManager.DataLoadOptions))
            {
                CertPolicyGroupManager mgr = CreateManager();
                CertPolicyGroup policyGroup = mgr.Get(db, "PolicyGroup1");
                policyGroup.CertPolicies.Count.Should().Be(0);
                CertPolicyManager groupMgr = CreatePolicyManager();
                CertPolicy certPolicy = groupMgr.Get("Policy1");

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
        public void AssociatePolicyToGroupTest()
        {
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            CertPolicyManager policyMgr = CreatePolicyManager();
            CertPolicy certPolicy = policyMgr.Get("Policy1");

            policyGroup.CertPolicies.Add(certPolicy);
            groupMgr.AddAssociation(policyGroup);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(1);
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

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            CertPolicyManager policyMgr = CreatePolicyManager();
            CertPolicy certPolicy = policyMgr.Get("Policy1");

            policyGroup.CertPolicies.Add(certPolicy);
            groupMgr.AddAssociation(policyGroup);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(1);

            // now dissassociate 
            //var query = policyGroup.CertPolicies.AsEnumerable().Where(cp => cp.ID == certPolicy.ID);
            //foreach (CertPolicy cp in query.ToList())
            //{
            //    policyGroup.CertPolicies.Remove(cp);
            //}
            CertPolicyGroupMap[] map = new CertPolicyGroupMap[] {policyGroup.CertPolicyGroupMap.First()};
            groupMgr.RemovePolicy(map);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);
        }


        [Fact]
        public void AssociatePolicyGroupToDomain()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            policyGroup.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup,
                    Owner = "domain1.test.com"
                });

            policyGroup.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup,
                    Owner = "domain2.test.com"
                });

            groupMgr.AddAssociation(policyGroup);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(2);
        }

        [Fact]
        public void DissAssociatePolicyGroupToDomain()
        {
            InitDomainRecords();
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);

            CertPolicyGroupDomainMap domainMap = new CertPolicyGroupDomainMap(true);
            domainMap.CertPolicyGroup = policyGroup;
            domainMap.Owner = "domain1.test.com";

            policyGroup.CertPolicyGroupDomainMaps.Add(domainMap);
            groupMgr.AddAssociation(policyGroup);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMaps.Count.Should().Be(1);

            // now dissassociate 
            CertPolicyGroupDomainMap[] map = new CertPolicyGroupDomainMap[] { policyGroup.CertPolicyGroupDomainMaps.First() };
            groupMgr.RemoveDomain(map);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);
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

            policyGroup1.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup1,
                    Owner = "domain1.test.com"
                });

            policyGroup1.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup1,
                    Owner = "domain2.test.com"
                });

            policyGroup1.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup1,
                    Owner = "domain3.test.com"
                });

            policyGroup2.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup2,
                    Owner = "domain2.test.com"
                });


            //
            // Submit the addistions to the CertPolicyGroup object graph. 
            // This is the first time we push the changes into the database.
            //
            groupMgr.AddAssociation(policyGroup1);
            groupMgr.AddAssociation(policyGroup2);

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
            // map cert policy group to policies
            //
            CertPolicyManager policyMgr = CreatePolicyManager();
            CertPolicy certPolicy = policyMgr.Get("Policy1");
            policyGroup1.CertPolicies.Add(certPolicy);
            certPolicy = policyMgr.Get("Policy2");
            policyGroup1.CertPolicies.Add(certPolicy);

            certPolicy = policyMgr.Get("Policy3");
            policyGroup2.CertPolicies.Add(certPolicy);



            //
            // Map cert policy group to domains
            //
            policyGroup1.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup1,
                    Owner = "domain1.test.com"
                });

            policyGroup1.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup1,
                    Owner = "domain2.test.com"
                });

            policyGroup1.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup1,
                    Owner = "domain3.test.com"
                });

            policyGroup2.CertPolicyGroupDomainMaps.Add(
                new CertPolicyGroupDomainMap(true)
                {
                    CertPolicyGroup = policyGroup2,
                    Owner = "domain2.test.com"
                });

            //
            // Submit the addistions to the CertPolicyGroup object graph. 
            // This is the first time we push the changes into the database.
            //
            groupMgr.AddAssociation(policyGroup1);
            groupMgr.AddAssociation(policyGroup2);

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
    }
}
