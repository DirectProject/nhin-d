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

using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using FluentAssertions;
using Health.Direct.Config.Store.Entity;
using Health.Direct.Policy.Extensions;
using Xunit;
using Xunit.Abstractions;

namespace Health.Direct.Config.Store.Tests
{
    [Collection("ManagerFacts")]
    public class CertPolicyManagerFacts : ConfigStoreTestBase
    {
        private readonly ITestOutputHelper _testOutputHelper;
        private readonly DirectDbContext _dbContext;
        private readonly CertPolicyManager _certPolicyManager;
        private readonly CertPolicyGroupManager _certPolicyGroupManager;

        public CertPolicyManagerFacts(ITestOutputHelper testOutputHelper)
        {
            _testOutputHelper = testOutputHelper;
            _dbContext = CreateConfigDatabase();
            _certPolicyManager = new CertPolicyManager(_dbContext);
            _certPolicyGroupManager = new CertPolicyGroupManager(_dbContext);
        }

        // private new static CertPolicyManager CreateManager()
        // {
        //     return new CertPolicyManager(CreateConfigStore(), new CertPolicyParseValidator());
        // }
        //
        // private static CertPolicyGroupManager CreatePolicyGroupManager()
        // {
        //     return new CertPolicyGroupManager(CreateConfigStore());
        // }

        /// <summary>
        /// A test for GetEnumerator
        /// </summary>
        [Fact]
        public async Task GetEnumeratorTest()
        {
            await InitCertPolicyRecords(_dbContext);
            Assert.Equal(9, await _certPolicyManager.Count());
        }

        /// <summary>
        /// A test for GetByAgentName by name
        /// GetByAgentName policy by name
        /// </summary>
        [Fact]
        public async Task GetPolicyByName()
        {
            await InitCertPolicyRecords(_dbContext);
            var policy = await _certPolicyManager.Get("Policy1");
            policy.Name.Should().BeEquivalentTo("Policy1");
        }

        [Fact]
        public async Task GetIncomingAndOutgoingCertPolicyByOwnerTest()
        {
            await InitCertPolicyRecords(_dbContext);
            await InitCertPolicyGroupRecords(_dbContext);

            var policyGroup1 = await _certPolicyGroupManager.Get("PolicyGroup1");
            policyGroup1.CertPolicies?.Count.Should().Be(0);
            var policyGroup2 = await _certPolicyGroupManager.Get("PolicyGroup2");
            policyGroup1.CertPolicies?.Count.Should().Be(0);
            policyGroup2.CertPolicies?.Count.Should().Be(0);

            //
            // Map cert policy group to domains
            //
            await _certPolicyGroupManager.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            await _certPolicyGroupManager.AssociateToOwner(policyGroup2.Name, "domain2.test.com");

            //
            // Map cert policy group to policy
            //
            await _certPolicyGroupManager.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);
            _dbContext.ChangeTracker.Clear();
            await _certPolicyGroupManager.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.TRUST, true, false);

            var policies = await _certPolicyManager.GetIncomingByOwner("domain1.test.com");
            policies.Count.Should().Be(2);
            _dbContext.ChangeTracker.Clear();
            policies = await _certPolicyManager.GetOutgoingByOwner("domain1.test.com");
            policies.Count.Should().Be(1);
            _dbContext.ChangeTracker.Clear();

            policies = await _certPolicyManager.GetIncomingByOwner("domain2.test.com");
            policies.Count.Should().Be(0);
            _dbContext.ChangeTracker.Clear();

            policies = await _certPolicyManager.GetIncomingByOwner("domain3.test.com");
            policies.Count.Should().Be(0);

        }

        [Fact]
        public async Task GetIncomingAndOutgoingCertPolicyByOwnerAndUsage_Test()
        {
            await InitCertPolicyRecords(_dbContext);
            await InitCertPolicyGroupRecords(_dbContext);

            
            var policyGroup1 = await _certPolicyGroupManager.Get("PolicyGroup1");
            policyGroup1.CertPolicies?.Count.Should().Be(0);
            var policyGroup2 = await _certPolicyGroupManager.Get("PolicyGroup2");
            policyGroup1.CertPolicies?.Count.Should().Be(0);
            policyGroup2.CertPolicies?.Count.Should().Be(0);

            //
            // Map cert policy group to domains
            //
            await _certPolicyGroupManager.AssociateToOwner(policyGroup1.Name, "domain1.test.com");
            await _certPolicyGroupManager.AssociateToOwner(policyGroup2.Name, "domain2.test.com");

            //
            // Map cert policy group to policy
            //
            await _certPolicyGroupManager.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.TRUST, true, true);
            _dbContext.ChangeTracker.Clear();
            await _certPolicyGroupManager.AddPolicyUse("Policy2", "PolicyGroup1", CertPolicyUse.PRIVATE_RESOLVER, true, false);
            _dbContext.ChangeTracker.Clear();
            var policies = await _certPolicyManager.GetIncomingByOwner("domain1.test.com", CertPolicyUse.TRUST);
            policies.Count.Should().Be(1);
            _dbContext.ChangeTracker.Clear();
            policies = await _certPolicyManager.GetIncomingByOwner("domain1.test.com", CertPolicyUse.PUBLIC_RESOLVER);
            policies.Count.Should().Be(0);
            _dbContext.ChangeTracker.Clear();
            policies = await _certPolicyManager.GetOutgoingByOwner("domain1.test.com", CertPolicyUse.TRUST);
            policies.Count.Should().Be(1);
            _dbContext.ChangeTracker.Clear();
            policies = await _certPolicyManager.GetOutgoingByOwner("domain1.test.com", CertPolicyUse.PRIVATE_RESOLVER);
            policies.Count.Should().Be(0);
            _dbContext.ChangeTracker.Clear();

            policies = await _certPolicyManager.GetIncomingByOwner("domain2.test.com", CertPolicyUse.PRIVATE_RESOLVER);
            policies.Count.Should().Be(0);
            _dbContext.ChangeTracker.Clear();

            policies = await _certPolicyManager.GetIncomingByOwner("domain3.test.com", CertPolicyUse.PRIVATE_RESOLVER);
            policies.Count.Should().Be(0);
        }

        /// <summary>
        /// A test for Add Policy
        /// </summary>
        [Fact]
        public async Task AddPolicy()
        {
            await InitCertPolicyRecords(_dbContext);
            

            var expectedPolicy = new CertPolicy("UnitTestPolicy", "", "1 = 1".ToBytesUtf8());
            await _certPolicyManager.Add(expectedPolicy);

            var actualCertPolicy = await _certPolicyManager.Get("UnitTestPolicy");
            expectedPolicy.Name.Should().BeEquivalentTo("UnitTestPolicy");
            expectedPolicy.CreateDate.Should().BeCloseTo(actualCertPolicy.CreateDate);
        }

        /// <summary>
        /// Remove Policy
        /// </summary>
        [Fact]
        public async Task DeletePolicyTest()
        {
            await InitCertPolicyRecords(_dbContext);
            var policy = await _certPolicyManager.Get("Policy2");
            await _certPolicyManager.Remove(policy.CertPolicyId);

            (await _certPolicyManager.Get("Policy2")).Should().BeNull();
        }

        /// <summary>
        /// Remove Multiple Policies
        /// </summary>
        [Fact]
        public async Task DeletePoliciesTest()
        {
            await InitCertPolicyRecords(_dbContext);
            
            (await _certPolicyManager.Count()).Should().Be(9);
            long[] ids = _certPolicyManager.Select(certPolicy => certPolicy.CertPolicyId).ToArray();
            await _certPolicyManager.Remove(ids);

            (await _certPolicyManager.Count()).Should().Be(0);
        }

        /// <summary>
        /// Remove Multiple Policies
        /// </summary>
        [Fact]
        public async Task Delete2PoliciesTest()
        {
            await InitCertPolicyRecords(_dbContext);
            (await _certPolicyManager.Count()).Should().Be(9);
            await _certPolicyManager.Remove(new long[] { 1, 2 });

            (await _certPolicyManager.Count()).Should().Be(7);
        }

        /// <summary>
        /// Delete policy and its associations
        /// </summary>
        [Fact]
        public async Task DeletePolicyWithAssociations()
        {
            await InitCertPolicyRecords(_dbContext);
            await InitCertPolicyGroupRecords(_dbContext);

            var policyGroup = await _certPolicyGroupManager.Get("PolicyGroup1");
            policyGroup.CertPolicies?.Count.Should().Be(0);

            (await _certPolicyManager.Get("Policy1")).Should().NotBeNull();

            await _certPolicyGroupManager.AddPolicyUse("Policy1", "PolicyGroup1", CertPolicyUse.PRIVATE_RESOLVER, true, true);

            policyGroup = await _certPolicyGroupManager.Get("PolicyGroup1");
            policyGroup.CertPolicies?.Count.Should().Be(1);

            var policy = await _certPolicyManager.Get("Policy1");
            await _certPolicyManager.Remove(policy.CertPolicyId);
            (await _certPolicyManager.Get("Policy1")).Should().BeNull();
        }

        /// <summary>
        /// A test for Update Policy
        /// </summary>
        [Fact]
        public async Task UpdatePolicyDataTest()
        {
            await InitCertPolicyRecords(_dbContext);

            var newCertPolicy = new CertPolicy("UnitTestPolicy", "UnitTest Policy Description", "1 = 1".ToBytesUtf8());
            await _certPolicyManager.Add(newCertPolicy);
            var actualCertPolicy = await _certPolicyManager.Get("UnitTestPolicy");
            actualCertPolicy.Should().NotBeNull();

            actualCertPolicy.Data = "1 != 1".ToBytesUtf8();
            await _certPolicyManager.Update(actualCertPolicy);

            var updatedCertPolicy = await _certPolicyManager.Get("UnitTestPolicy");
            updatedCertPolicy.Data.ToUtf8String().ShouldBeEquivalentTo("1 != 1");
        }

        /// <summary>
        /// A test for Update Policy
        /// </summary>
        [Fact]
        public async Task UpdatePolicyDescriptionTest()
        {
            await InitCertPolicyRecords(_dbContext);
            
            var newCertPolicy = new CertPolicy("UnitTestPolicy", "UnitTest Policy Description", "1 = 1".ToBytesUtf8());
            await _certPolicyManager.Add(newCertPolicy);
            var actualCertPolicy = await _certPolicyManager.Get("UnitTestPolicy");
            actualCertPolicy.Should().NotBeNull();

            actualCertPolicy.Description = "blank";
            await _certPolicyManager.Update(actualCertPolicy);

            var updatedCertPolicy = await _certPolicyManager.Get("UnitTestPolicy");
            updatedCertPolicy.Description.ShouldBeEquivalentTo("blank");
        }
    }
}
