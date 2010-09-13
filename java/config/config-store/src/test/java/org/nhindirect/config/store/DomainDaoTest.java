package org.nhindirect.config.store;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	
	private static final Log log = LogFactory.getLog(DomainDaoTest.class);
	
	@Autowired
	private DomainDao dao;
	
	@Test
	public void testAddDomain() {
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.ENABLED);
		dao.add(domain);
		assertEquals(dao.count(), 1);
	}
	
	@Test
	public void testGetByDomain() {
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.ENABLED);
		dao.add(domain);
		
		Domain testDomain = dao.getDomainByName("health.testdomain.com");
		log.info("Newly added Domain ID is: " + testDomain.getId());
		log.info("Newly added Domain Status is: " + testDomain.getStatus());
		
		assertTrue(testDomain.getDomainName().equals("health.testdomain.com"));
	}

	@Test
	public void testUpdateDomain() {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS Z");
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.ENABLED);
		dao.add(domain);
		
		Domain testDomain = dao.getDomainByName("health.testdomain.com");
		log.info("Newly added Domain ID is: " + testDomain.getId());
		log.info("Newly added Domain Status is: " + testDomain.getStatus());
		log.info("Newly added Domain Update Time is: " + 
				 fmt.format(new Date(testDomain.getUpdateTime().getTimeInMillis())));
		assertTrue(testDomain.getDomainName().equals("health.testdomain.com"));
		
		testDomain.setStatus(EntityStatus.DISABLED);
		dao.update(testDomain);
		
		domain = dao.getDomainByName("health.testdomain.com");
		log.info("Updated Domain ID is: " + domain.getId());
		log.info("Updated Status is: " + domain.getStatus());
		log.info("Updated Update Time is: " + 
				 fmt.format(new Date(domain.getUpdateTime().getTimeInMillis())));
		
		assertTrue(domain.getStatus().equals(EntityStatus.DISABLED));
	}
	
	
	@Test 
	public void testGetDomain() {
		Domain domain = new Domain("health.testdomain.com");
		domain.setStatus(EntityStatus.NEW);
		dao.add(domain);
		domain = new Domain("health.newdomain.com");
		domain.setStatus(EntityStatus.NEW);
		dao.add(domain);
		
		
		List<String> names = new ArrayList<String>();
		names.add("health.testdomain.com");
		
		assertEquals(dao.getDomains(names, EntityStatus.NEW).size(), 1);
		assertEquals(dao.getDomains(null, EntityStatus.NEW).size(), 2);
		assertEquals(dao.getDomains(names, null).size(), 1);
		assertEquals(dao.getDomains(names, EntityStatus.ENABLED).size(), 0);
		assertEquals(dao.getDomains(names, EntityStatus.DISABLED).size(), 0);
		assertEquals(dao.getDomains(null, null).size(), 2);
		
		names.clear();
		names.add("health.baddomain.com");
		
		assertEquals(dao.getDomains(names, null).size(), 0);
	}
	
	@Test 
	public void testDeleteDomain() {
		Domain domain = new Domain("health.newdomain.com");
		domain.setStatus(EntityStatus.NEW);
		dao.add(domain);
		assertEquals(1, dao.count());
		dao.delete("health.testdomain.com");
		assertEquals(1, dao.count());
		dao.delete("health.newdomain.com");
		assertEquals(0, dao.count());
	}
	
	
	

}
