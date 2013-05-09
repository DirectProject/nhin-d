package org.nhind.config.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nhind.config.CertPolicy;
import org.nhind.config.CertPolicyGroup;
import org.nhind.config.CertPolicyGroupDomainReltn;
import org.nhind.config.CertPolicyUse;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.PolicyLexicon;


public class ClientProxyCertificatePolicyTest 
{
	private static ConfigurationServiceProxy proxy;
	
	@BeforeClass
	public static void setupClass() throws Exception
	{
		ConfigServiceRunner.startConfigService();    	
    	proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}
	
	@Before
	public void cleanUp() throws Exception
	{
		Domain[] domains = proxy.listDomains(null, 1000);
		
		int idx = 0;
		Long[] ids;
		if (domains != null)
			for (Domain domain : domains)
				proxy.removeDomainById(domain.getId());
		
		CertPolicy[] retrievedPolicies = proxy.getPolicies();
		if (retrievedPolicies != null && retrievedPolicies.length > 0)
		{
			idx = 0;
			ids = new Long[retrievedPolicies.length];
			for (CertPolicy policy : retrievedPolicies)
				ids[idx++] = policy.getId();
			
			proxy.deletePolicies(ids);
		}
		
		CertPolicyGroup[] retrievedGroups = proxy.getPolicyGroups();
		if (retrievedGroups != null && retrievedGroups.length > 0)
		{
			idx = 0;
			ids = new Long[retrievedGroups.length];
			for (CertPolicyGroup group : retrievedGroups)
				ids[idx++] = group.getId();
			
			proxy.deletePolicyGroups(ids);
		}		
	}
	
	@Test
	public void testAddGetRemovePolicyGroup() throws Exception
	{
		CertPolicy addPolicy = new CertPolicy();
		addPolicy.setLexicon(PolicyLexicon.XML);
		addPolicy.setPolicyName("Test Policy");
		addPolicy.setPolicyData(new byte[]{1,2,3});
		
		proxy.addPolicy(addPolicy);
		
		
		CertPolicy[] retrievedPolicies = proxy.getPolicies();
		assertEquals(1, retrievedPolicies.length);
		
		CertPolicyGroup addGroup = new CertPolicyGroup();
		addGroup.setPolicyGroupName("Test Group");
		
		proxy.addPolicyGroup(addGroup);
		
		CertPolicyGroup[] retrievedGroups = proxy.getPolicyGroups();
		assertEquals(1, retrievedGroups.length);
		
		proxy.addPolicyUseToGroup(retrievedGroups[0].getId(), retrievedPolicies[0].getId(), CertPolicyUse.TRUST, true, true);
		
		CertPolicyGroup retrievedGroup = proxy.getPolicyGroupById(retrievedGroups[0].getId());
		assertNotNull(retrievedGroup);
		
		assertEquals(1, retrievedGroup.getCertPolicyGroupReltn().length);
		assertEquals(addPolicy.getPolicyName(), retrievedGroup.getCertPolicyGroupReltn()[0].getCertPolicy().getPolicyName());
		

		proxy.removePolicyUseFromGroup(retrievedGroup.getCertPolicyGroupReltn()[0].getId());
		
		retrievedGroup = proxy.getPolicyGroupById(retrievedGroups[0].getId());
		assertNotNull(retrievedGroup);
		
		assertNull(retrievedGroup.getCertPolicyGroupReltn());

		proxy.deletePolicies(new Long[] {retrievedPolicies[0].getId()});
		retrievedPolicies = proxy.getPolicies();
		assertNull(retrievedPolicies);
		
		proxy.deletePolicyGroups(new Long[] {retrievedGroups[0].getId()});
		retrievedGroups = proxy.getPolicyGroups();
		assertNull(retrievedGroups);
	}
	
	@Test
	public void testAddGetRemovePolicyGroup_deletePolicyBeforeDisassociate() throws Exception
	{
		CertPolicy addPolicy = new CertPolicy();
		addPolicy.setLexicon(PolicyLexicon.XML);
		addPolicy.setPolicyName("Test Policy");
		addPolicy.setPolicyData(new byte[]{1,2,3});
		
		proxy.addPolicy(addPolicy);
		
		
		CertPolicy[] retrievedPolicies = proxy.getPolicies();
		assertEquals(1, retrievedPolicies.length);
		
		CertPolicyGroup addGroup = new CertPolicyGroup();
		addGroup.setPolicyGroupName("Test Group");
		
		proxy.addPolicyGroup(addGroup);
		
		CertPolicyGroup[] retrievedGroups = proxy.getPolicyGroups();
		assertEquals(1, retrievedGroups.length);
		
		proxy.addPolicyUseToGroup(retrievedGroups[0].getId(), retrievedPolicies[0].getId(), CertPolicyUse.TRUST, true, true);
		
		CertPolicyGroup retrievedGroup = proxy.getPolicyGroupById(retrievedGroups[0].getId());
		assertNotNull(retrievedGroup);
		
		assertEquals(1, retrievedGroup.getCertPolicyGroupReltn().length);
		assertEquals(addPolicy.getPolicyName(), retrievedGroup.getCertPolicyGroupReltn()[0].getCertPolicy().getPolicyName());

		proxy.deletePolicies(new Long[] {retrievedPolicies[0].getId()});
		retrievedPolicies = proxy.getPolicies();
		assertNull(retrievedPolicies);
		
		retrievedGroup = proxy.getPolicyGroupById(retrievedGroups[0].getId());
		assertNotNull(retrievedGroup);
		
		assertNull(retrievedGroup.getCertPolicyGroupReltn());

		proxy.deletePolicyGroups(new Long[] {retrievedGroups[0].getId()});
		retrievedGroups = proxy.getPolicyGroups();
		assertNull(retrievedGroups);
	}
	
	@Test
	public void testAddGetRemovePolicyGroupAndDomain_deleteDomainBeforeDissasociate() throws Exception
	{
		CertPolicy addPolicy = new CertPolicy();
		addPolicy.setLexicon(PolicyLexicon.XML);
		addPolicy.setPolicyName("Test Policy");
		addPolicy.setPolicyData(new byte[]{1,2,3});
		
		proxy.addPolicy(addPolicy);
		
		
		CertPolicy[] retrievedPolicies = proxy.getPolicies();
		assertEquals(1, retrievedPolicies.length);
		
		CertPolicyGroup addGroup = new CertPolicyGroup();
		addGroup.setPolicyGroupName("Test Group");
		
		proxy.addPolicyGroup(addGroup);
		
		CertPolicyGroup[] retrievedGroups = proxy.getPolicyGroups();
		assertEquals(1, retrievedGroups.length);
		
		proxy.addPolicyUseToGroup(retrievedGroups[0].getId(), retrievedPolicies[0].getId(), CertPolicyUse.TRUST, true, true);
		
		CertPolicyGroup retrievedGroup = proxy.getPolicyGroupById(retrievedGroups[0].getId());
		assertNotNull(retrievedGroup);
		
		assertEquals(1, retrievedGroup.getCertPolicyGroupReltn().length);
		assertEquals(addPolicy.getPolicyName(), retrievedGroup.getCertPolicyGroupReltn()[0].getCertPolicy().getPolicyName());
		
		
		Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		proxy.addDomain(domain);
		Domain[] retrievedDomains = proxy.getDomains(new String[]{domain.getDomainName()}, null);
		assertEquals(1, retrievedDomains.length);
		
		proxy.associatePolicyGroupToDomain(retrievedDomains[0].getId(), retrievedGroups[0].getId());
		CertPolicyGroupDomainReltn[] domainGroups = proxy.getPolicyGroupsByDomain(retrievedDomains[0].getId());
		assertEquals(1, domainGroups.length);
		
		// also get all domain/group relts without domain id qualifier
		domainGroups = proxy.getPolicyGroupDomainReltns();
		assertEquals(1, domainGroups.length);
		

		// should delete domain without error
		proxy.removeDomainById(retrievedDomains[0].getId());

	}	
	
	@Test
	public void testAddGetRemovePolicyGroupAndDomain_dissasociateGroupBeforeDeleteDomain() throws Exception
	{
		CertPolicy addPolicy = new CertPolicy();
		addPolicy.setLexicon(PolicyLexicon.XML);
		addPolicy.setPolicyName("Test Policy");
		addPolicy.setPolicyData(new byte[]{1,2,3});
		
		proxy.addPolicy(addPolicy);
		
		
		CertPolicy[] retrievedPolicies = proxy.getPolicies();
		assertEquals(1, retrievedPolicies.length);
		
		CertPolicyGroup addGroup = new CertPolicyGroup();
		addGroup.setPolicyGroupName("Test Group");
		
		proxy.addPolicyGroup(addGroup);
		
		CertPolicyGroup[] retrievedGroups = proxy.getPolicyGroups();
		assertEquals(1, retrievedGroups.length);
		
		proxy.addPolicyUseToGroup(retrievedGroups[0].getId(), retrievedPolicies[0].getId(), CertPolicyUse.TRUST, true, true);
		
		CertPolicyGroup retrievedGroup = proxy.getPolicyGroupById(retrievedGroups[0].getId());
		assertNotNull(retrievedGroup);
		
		assertEquals(1, retrievedGroup.getCertPolicyGroupReltn().length);
		assertEquals(addPolicy.getPolicyName(), retrievedGroup.getCertPolicyGroupReltn()[0].getCertPolicy().getPolicyName());
		
		
		Domain domain = new Domain();
		domain.setDomainName("Test Domain");
		proxy.addDomain(domain);
		Domain[] retrievedDomains = proxy.getDomains(new String[]{domain.getDomainName()}, null);
		assertEquals(1, retrievedDomains.length);
		
		proxy.associatePolicyGroupToDomain(retrievedDomains[0].getId(), retrievedGroups[0].getId());
		CertPolicyGroupDomainReltn[] domainGroups = proxy.getPolicyGroupsByDomain(retrievedDomains[0].getId());
		assertEquals(1, domainGroups.length);

		// remove policy from single domain
		proxy.disassociatePolicyGroupFromDomain(retrievedDomains[0].getId(), retrievedGroups[0].getId());
		
		domainGroups = proxy.getPolicyGroupsByDomain(retrievedDomains[0].getId());
		assertNull(domainGroups);

		// should delete domain without error
		proxy.removeDomainById(retrievedDomains[0].getId());

	}	
	
	@Test
	public void testAddGetRemovePolicyGroupAndDomain_dissasociateGroupFromAllDomains() throws Exception
	{
		CertPolicy addPolicy = new CertPolicy();
		addPolicy.setLexicon(PolicyLexicon.XML);
		addPolicy.setPolicyName("Test Policy");
		addPolicy.setPolicyData(new byte[]{1,2,3});
		
		proxy.addPolicy(addPolicy);
		
		
		CertPolicy[] retrievedPolicies = proxy.getPolicies();
		assertEquals(1, retrievedPolicies.length);
		
		CertPolicyGroup addGroup = new CertPolicyGroup();
		addGroup.setPolicyGroupName("Test Group");
		
		proxy.addPolicyGroup(addGroup);
		
		CertPolicyGroup[] retrievedGroups = proxy.getPolicyGroups();
		assertEquals(1, retrievedGroups.length);
		
		proxy.addPolicyUseToGroup(retrievedGroups[0].getId(), retrievedPolicies[0].getId(), CertPolicyUse.TRUST, true, true);
		
		CertPolicyGroup retrievedGroup = proxy.getPolicyGroupById(retrievedGroups[0].getId());
		assertNotNull(retrievedGroup);
		
		assertEquals(1, retrievedGroup.getCertPolicyGroupReltn().length);
		assertEquals(addPolicy.getPolicyName(), retrievedGroup.getCertPolicyGroupReltn()[0].getCertPolicy().getPolicyName());
		
		
		Domain domain = new Domain();
		domain.setDomainName("Test Domain1");
		proxy.addDomain(domain);
		Domain[] retrievedDomains = proxy.listDomains(null, 100);
		assertEquals(1, retrievedDomains.length);
		
		domain = new Domain();
		domain.setDomainName("Test Domain2");
		proxy.addDomain(domain);
		retrievedDomains = proxy.listDomains(null, 100);
		assertEquals(2, retrievedDomains.length);
		
		proxy.associatePolicyGroupToDomain(retrievedDomains[0].getId(), retrievedGroups[0].getId());
		CertPolicyGroupDomainReltn[] domainGroups = proxy.getPolicyGroupsByDomain(retrievedDomains[0].getId());
		assertEquals(1, domainGroups.length);

		proxy.associatePolicyGroupToDomain(retrievedDomains[1].getId(), retrievedGroups[0].getId());
		domainGroups = proxy.getPolicyGroupsByDomain(retrievedDomains[1].getId());
		assertEquals(1, domainGroups.length);
		
		// also get all domain/group relts without domain id qualifier
		domainGroups = proxy.getPolicyGroupDomainReltns();
		assertEquals(2, domainGroups.length);
		
		// remove policy from all domains domains
		proxy.disassociatePolicyGroupFromDomains(retrievedGroups[0].getId());
		
		domainGroups = proxy.getPolicyGroupsByDomain(retrievedDomains[0].getId());
		assertNull(domainGroups);

		domainGroups = proxy.getPolicyGroupsByDomain(retrievedDomains[0].getId());
		assertNull(domainGroups);
		
		// should delete domain without error
		proxy.removeDomainById(retrievedDomains[0].getId());
	
		// should delete domain without error
		proxy.removeDomainById(retrievedDomains[1].getId());

	}	
	
}
