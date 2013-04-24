package org.nhindirect.config.store;

import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.config.store.dao.DomainDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/configStore-test.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class DomainDaoTest  {
	
	public static final String derbyHomeLoc = "/target/data";	
	
	static
	{
		try
		{
			File baseLocation = new File("dummy.txt");
			String fullDerbyHome = baseLocation.getAbsolutePath().substring(0, baseLocation.getAbsolutePath().lastIndexOf(File.separator)) + derbyHomeLoc;
			System.setProperty("derby.system.home", fullDerbyHome);
			
		}
		catch (Exception e)
		{
			
		}
	}	
	
	private static final Log log = LogFactory.getLog(DomainDaoTest.class);
	
	@Autowired
	private DomainDao domainDao;
	
	/*
	 * MOD: Greg Meyer
	 * Do not rely on the order of the tests in the class to ensure that prerequisites are met.  Each test
	 * should be able to run independently without relying on the side affects of other tests.  If side affects
	 * are needed, the execute the dependent tests from within that executing test. 
	 */
	
	@Test
	public void testCleanDatabase() 
	{
		List<Domain> domains = domainDao.searchDomain(null, null);
		
		if (domains != null) 
			for (Domain dom : domains)
				domainDao.delete(dom.getDomainName());
						
		domains = domainDao.searchDomain(null, null);
		assertEquals(0, domains.size());
	}
	
	@Test
	public void testAddDomain() {
		
		testCleanDatabase();
		
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.ENABLED);
		domainDao.add(domain);
		assertEquals(domainDao.count(), 1);
	}
	
	@Test
	public void testGetByDomain() {
		testCleanDatabase();
		
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.ENABLED);
		domainDao.add(domain);
		
		Domain testDomain = domainDao.getDomainByName("Health.testdomain.com");
		log.info("Newly added Domain ID is: " + testDomain.getId());
		log.info("Newly added Domain Status is: " + testDomain.getStatus());
		
		assertTrue(testDomain.getDomainName().equals("health.testdomain.com"));
	}

	@Test
	public void testUpdateDomain() {
		testCleanDatabase();
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS Z");
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.ENABLED);
		domainDao.add(domain);
		
		Domain testDomain = domainDao.getDomainByName("health.testdomain.com");
		log.info("Newly added Domain ID is: " + testDomain.getId());
		log.info("Newly added Domain Status is: " + testDomain.getStatus());
		log.info("Newly added Domain Update Time is: " + 
				 fmt.format(new Date(testDomain.getUpdateTime().getTimeInMillis())));
		assertTrue(testDomain.getDomainName().equals("health.testdomain.com"));
		
		testDomain.setStatus(EntityStatus.DISABLED);
		domainDao.update(testDomain);
		
		domain = domainDao.getDomainByName("health.testdomain.com");
		log.info("Updated Domain ID is: " + domain.getId());
		log.info("Updated Status is: " + domain.getStatus());
		log.info("Updated Update Time is: " + 
				 fmt.format(new Date(domain.getUpdateTime().getTimeInMillis())));
		
		assertTrue(domain.getStatus().equals(EntityStatus.DISABLED));
	}
	
	
	@Test 
	public void testGetDomain() {
		testCleanDatabase();
		
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.NEW);
		domainDao.add(domain);
		domain = new Domain("health.newdomain.com");
		domain.setStatus(EntityStatus.NEW);
		domainDao.add(domain);
		
		
		List<String> names = new ArrayList<String>();
		names.add("health.testdomain.com");
		
		assertEquals(domainDao.getDomains(names, EntityStatus.NEW).size(), 1);
		assertEquals(domainDao.getDomains(null, EntityStatus.NEW).size(), 2);
		assertEquals(domainDao.getDomains(names, null).size(), 1);
		assertEquals(domainDao.getDomains(names, EntityStatus.ENABLED).size(), 0);
		assertEquals(domainDao.getDomains(names, EntityStatus.DISABLED).size(), 0);
		assertEquals(domainDao.getDomains(null, null).size(), 2);
		
		names.clear();
		names.add("health.baddomain.com");
		
		assertEquals(domainDao.getDomains(names, null).size(), 0);
	}
	
	@Test 
	public void testDeleteDomain() {
		testCleanDatabase();
		
		Domain domain = new Domain("health.newdomain.com");
		domain.setPostMasterEmail("postmaster@health.newdomain.com");
		domain.setStatus(EntityStatus.NEW);
		domainDao.add(domain);
		assertEquals(1, domainDao.count());
		domainDao.delete("health.testdomain.com");
		assertEquals(1, domainDao.count());
		domainDao.delete("health.newdomain.com");
		assertEquals(0, domainDao.count());
	}
	
	@Test
	public void testSearchDomain() {
		testCleanDatabase();
		
		log.debug("Enter");
		Domain domain = new Domain("health.newdomain.com");
		domain.setStatus(EntityStatus.NEW);
		domainDao.add(domain);
		
		domain = new Domain("healthy.domain.com");
		domain.setStatus(EntityStatus.NEW);
		domainDao.add(domain);
		
		String name = "heal*";
		List<Domain> result = domainDao.searchDomain(name, null);
		assertEquals(2, result.size());
		
		name = "*.com";
		result = domainDao.searchDomain(name, null);
		assertEquals(2, result.size());
		
		result = domainDao.searchDomain(null, null);
		assertEquals(2, result.size());
		
		name = "*.org";
		result = domainDao.searchDomain(name, null);
		assertEquals(0, result.size());
		log.debug("Exit");
	}

	/**
	 * As it turns out, you have to save the owning entity (Domain) before you 
	 * start adding dependent entities to it.
	 */
	@Test 
	public void testAddDomainsWithAddresses() {
		testCleanDatabase();
		
		Domain domain = new Domain("health.newdomain.com");
		domain.setStatus(EntityStatus.NEW);
		domain.getAddresses().add(new Address(domain, "test1@health.newdomain.com", "Test1"));
		domain.getAddresses().add(new Address(domain, "test2@health.newdomain.com", "Test2"));
		domain.setPostMasterEmail("postmaster@health.newdomain.com");
		domainDao.add(domain);
		
		Domain test = domainDao.getDomainByName("health.newdomain.com");
		assertEquals("postmaster@health.newdomain.com", test.getPostMasterEmail());
		assertEquals(3, test.getAddresses().size());
		
		log.info(domain.toString());
		log.info(test.toString());
		Iterator<Address> iter = test.getAddresses().iterator();
		
		while (iter.hasNext()) {
			Address testAddress = iter.next();
			log.info(testAddress.toString());
		}
	}
	
	/**
	 * Don't need to clean the db before execution.  @TransactionConfiguration defaults to 
	 * rolling back all transactions at the end of the method.   That should fail ONLY if the
	 * tests db doesn't support transactions (in which case needs to be fixed)
	 */
	@Test
	public void testDeleteDomainsWithAddresses() {
		
		Domain domain = new Domain("health.newdomain.com");
		domain.setStatus(EntityStatus.NEW);
		domain.getAddresses().add(new Address(domain, "test1@health.newdomain.com", "Test1"));
		domain.getAddresses().add(new Address(domain, "test2@health.newdomain.com", "Test2"));
		domain.setPostMasterEmail("postmaster@health.newdomain.com");
		domainDao.add(domain);
		
		Domain test = domainDao.getDomainByName("health.newdomain.com");
		assertEquals("postmaster@health.newdomain.com", test.getPostMasterEmail());
		assertEquals(3, test.getAddresses().size());
		
		domainDao.delete("health.newdomain.com");
		test = domainDao.getDomainByName("health.newdomain.com");
		assertEquals(null, test);
		
		domain = new Domain("health.domain.com");
		domain.setStatus(EntityStatus.NEW);
		domain.getAddresses().add(new Address(domain, "test1@health.domain.com", "Test1"));
		domain.getAddresses().add(new Address(domain, "test2@health.domain.com", "Test2"));
		domain.setPostMasterEmail("postmaster@health.domain.com");
		domainDao.add(domain);
		
		test = domainDao.getDomainByName("health.domain.com");
		Long id = test.getId();
	
		domainDao.delete(id);
		
		test = domainDao.getDomain(id);
		assertEquals(null, test);

	}
}
