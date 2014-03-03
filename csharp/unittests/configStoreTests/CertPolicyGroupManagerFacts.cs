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
            InitCertPolicyRecords();
            InitCertPolicyGroupRecords();

            CertPolicyGroupManager groupMgr = CreateManager();
            CertPolicyGroup policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicies.Count.Should().Be(0);
            
            CertPolicyGroupDomainMap domainMap = new CertPolicyGroupDomainMap();
            domainMap.CertPolicyGroup = policyGroup;

            policyGroup.CertPolicyGroupDomainMap.Add(domainMap);
            groupMgr.AddAssociation(policyGroup);

            policyGroup = groupMgr.Get("PolicyGroup1");
            policyGroup.CertPolicyGroupDomainMap.Count.Should().Be(1);
        }

        /// <summary>
        /// Get Policy Groups by domain
        /// </summary>
        [Fact]
        public void GetPolicyGroupsByDomain()
        {

        }
        /*
         *  Add Policy x
         *  Update Policy 
         *  Delete Policy
         *  Dissassociate PolicyGroup 
         *  GetPolcies
         *    ById
         *    
         *  GetPolcyGroups
         *    ByName
         *    ById
         *    IncludeMap
         *    
         *  Associate Policy Group to domain
         *  Dissassociate Policy Group from domain
         *  Update Policy Group
         */



    }
}
