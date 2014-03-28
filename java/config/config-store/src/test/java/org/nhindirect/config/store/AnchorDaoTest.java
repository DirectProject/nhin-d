package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.config.store.dao.AnchorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/configStore-test.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class AnchorDaoTest
{
	private static final String derbyHomeLoc = "/target/data";	
	
	private static final String certBasePath = "src/test/resources/certs/"; 
	
	private static final String TEST_DOMAIN = "TestDomain1";
	
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
	
	@Autowired
	private AnchorDao anchorDao;
	
	private void addTestAnchors() throws Exception
	{
		Anchor anchor = new Anchor();
		anchor.setData(loadCertificateData("secureHealthEmailCACert.der"));
		anchor.setOwner(TEST_DOMAIN);
		anchor.setOutgoing(true);
		anchor.setIncoming(true);
		
		anchorDao.add(anchor);

		anchor = new Anchor();
		anchor.setData(loadCertificateData("cacert.der"));
		anchor.setOwner(TEST_DOMAIN);
		anchor.setOutgoing(true);
		anchor.setIncoming(true);		

		anchorDao.add(anchor);
		
	}
	
	private static byte[] loadCertificateData(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	@Test
	public void testCleanDatabase() throws Exception 
	{
		List<Anchor> anchors = anchorDao.listAll();
		
		if (anchors != null && anchors.size() > 0)
			for (Anchor anchor : anchors)
				anchorDao.delete(anchor.getOwner());
		
		anchors = anchorDao.listAll();
		
		assertEquals(0, anchors.size());
	}

	@Test
	public void testDeleteByIds() throws Exception 
	{
		// clean out all anchors
		testCleanDatabase();
		
		addTestAnchors();
		
		// get all anchors
		List<Anchor> anchors = anchorDao.listAll();
		assertNotNull(anchors);
		assertTrue(anchors.size() > 0);
		
		// now delete all by ids
		List<Long> idsToDelete = new ArrayList<Long>();
		for(Anchor anchorToDel : anchors)
			idsToDelete.add(anchorToDel.getId());
			
		
		// delete them
		anchorDao.delete(idsToDelete);
		
		// get all and make sure it is empty
		anchors = anchorDao.listAll();
		
		assertEquals(0, anchors.size());
		
		
	}
	
	
	@Test
	public void testAddAnchor() throws Exception
	{
		// clean out all anchors
		testCleanDatabase();
		
		addTestAnchors();
		
		// validate the anchor was created
		List<Anchor> anchors = anchorDao.listAll();
		assertNotNull(anchors);
		assertEquals(2, anchors.size());
		
		Anchor retAnchor = anchors.get(0);
		
		assertEquals(retAnchor.getOwner(), TEST_DOMAIN);
		
	}
	
	@Test
	public void testGetByOwner() throws Exception
	{
		// clean out all anchors
		testCleanDatabase();
		
		addTestAnchors();
		
		List<String> owners = new ArrayList<String>();
		owners.add(TEST_DOMAIN);
		
		List<Anchor> anchors = anchorDao.list(owners);
		assertNotNull(anchors);
		assertEquals(2, anchors.size());
		
		for (Anchor retAnchor : anchors)
			assertEquals(retAnchor.getOwner(), TEST_DOMAIN);
		
	}
	
	@Test
	public void testUpdateByIds() throws Exception
	{
		// clean out all anchors
		testCleanDatabase();
		
		addTestAnchors();

		// get all domains
		List<Anchor> anchors = anchorDao.listAll();
		
		assertEquals(2, anchors.size());
		
		// now update
		List<Long> idsToUpdate = new ArrayList<Long>();
		for (Anchor anchor : anchors)
			idsToUpdate.add(anchor.getId());
		
		anchorDao.setStatus(idsToUpdate, EntityStatus.ENABLED);
		
		// get all domains again
		anchors = anchorDao.listAll();
		
		assertEquals(2, anchors.size());
		
		for (Anchor anchor : anchors)
		{
			assertEquals(EntityStatus.ENABLED, anchor.getStatus());
			assertEquals(TEST_DOMAIN, anchor.getOwner());
		}
	}
	
	@Test
	public void testUpdateByOwner() throws Exception
	{
		// clean out all anchors
		testCleanDatabase();
		
		addTestAnchors();

		// get all domains
		
		
		List<String> owners = new ArrayList<String>();
		owners.add(TEST_DOMAIN);
		
		List<Anchor> anchors = anchorDao.list(owners);
		
		assertEquals(2, anchors.size());
		
		// now update
		List<Long> idsToUpdate = new ArrayList<Long>();
		for (Anchor anchor : anchors)
			idsToUpdate.add(anchor.getId());
		
		anchorDao.setStatus(idsToUpdate, EntityStatus.ENABLED);
		
		// get all domains again
		anchors = anchorDao.listAll();
		
		assertEquals(2, anchors.size());
		
		for (Anchor anchor : anchors)
		{
			assertEquals(EntityStatus.ENABLED, anchor.getStatus());
			assertEquals(TEST_DOMAIN, anchor.getOwner());
		}
	}

}

