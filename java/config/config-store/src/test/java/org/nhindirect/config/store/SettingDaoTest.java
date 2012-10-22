package org.nhindirect.config.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nhindirect.config.store.dao.SettingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/configStore-test.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class SettingDaoTest 
{
	private static final String derbyHomeLoc = "/target/data";	
	
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
	private SettingDao settingDao;
	
	private void addSetting(String name, String value) throws Exception
	{
		settingDao.add(name, value);
	}	
	
	@Test
	public void testCleanDatabase() throws Exception 
	{
		Collection<Setting> settings = settingDao.getAll();
		
		Collection<String> toDelete = new ArrayList<String>();
		
		if (settings != null && settings.size() > 0)
		{
			for (Setting setting : settings)
				toDelete.add(setting.getName());
			
			settingDao.delete(toDelete);
		}
		settings = settingDao.getAll();
		
		assertEquals(0, settings.size());
	}
	
	@Test 
	public void addSettings() throws Exception
	{
		testCleanDatabase();
		
		addSetting("TestName1", "TestValue1");
		addSetting("TestName2", "TestValue2");
		
		Collection<Setting> settings = settingDao.getAll();
		
		assertEquals(2, settings.size());
	}
	
	@Test 
	public void testAddDuplicateSettings_AssertException() throws Exception
	{
		testCleanDatabase();
		
		addSetting("TestName1", "TestValue1");
		
		boolean exceptionOccured = false;
		
		try
		{
			addSetting("TestName1", "TestValue2");
		}
		catch (ConfigurationStoreException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}	
	
	@Test 
	public void testGetAllSettings() throws Exception
	{
		testCleanDatabase();
		
		addSetting("TestName1", "TestValue1");
		addSetting("TestName2", "TestValue2");
		
		Collection<Setting> settings = settingDao.getAll();
		
		assertEquals(2, settings.size());
		
		testCleanDatabase();
		
		addSetting("TestName3", "TestValue3");
		addSetting("TestName4", "TestValue4");
		addSetting("TestName5", "TestValue5");
		
		settings = settingDao.getAll();
		
	}	
	
	@Test 
	public void testGetSettingsByName() throws Exception
	{
		testCleanDatabase();
		
		addSetting("TestName1", "TestValue1");
		addSetting("TestName2", "TestValue2");
		
		Collection<Setting> settings = settingDao.getByNames(Arrays.asList("TestName1"));
		
		assertEquals(1, settings.size());
		Setting setting = settings.iterator().next();
		assertEquals("TestName1",  setting.getName());
		assertEquals("TestValue1",  setting.getValue());
		
		
		settings = settingDao.getByNames(Arrays.asList("TestName2"));
		
		assertEquals(1, settings.size());
		setting = settings.iterator().next();
		assertEquals("TestName2",  setting.getName());
		assertEquals("TestValue2",  setting.getValue());
		
		
		settings = settingDao.getByNames(Arrays.asList("TestName1", "TestName2"));
		
		assertEquals(2, settings.size());

	}	
	
	
	@Test 
	public void testUpdateSetting() throws Exception
	{
		testCleanDatabase();
		
		addSetting("TestName1", "TestValue1");
		addSetting("TestName2", "TestValue2");
		
		Collection<Setting> settings = settingDao.getByNames(Arrays.asList("TestName1"));
		
		assertEquals(1, settings.size());
		Setting setting = settings.iterator().next();
		assertEquals("TestName1",  setting.getName());
		assertEquals("TestValue1",  setting.getValue());
		settingDao.update("TestName1", "TestUpdatedValue1");
		settings = settingDao.getByNames(Arrays.asList("TestName1"));
		assertEquals("TestName1",  setting.getName());
		assertEquals("TestUpdatedValue1",  setting.getValue());
		

		settingDao.update("TestName2", "TestUpdatedValue2");
		settings = settingDao.getByNames(Arrays.asList("TestName2"));
		setting = settings.iterator().next();
		assertEquals("TestName2",  setting.getName());
		assertEquals("TestUpdatedValue2",  setting.getValue());
	}	
}
