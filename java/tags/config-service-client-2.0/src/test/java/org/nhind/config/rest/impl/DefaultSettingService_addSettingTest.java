package org.nhind.config.rest.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.SettingService;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Setting;
import org.nhindirect.config.resources.SettingResource;

import org.nhindirect.config.store.dao.SettingDao;

public class DefaultSettingService_addSettingTest 
{
	   protected SettingDao settingDao;
	    
		static SettingService resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					settingDao =  (SettingDao)ConfigServiceRunner.getSpringApplicationContext().getBean("settingDao");
					
					resource = 	(SettingService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), SETTING_SERVICE);	

				}
				catch (Throwable t)
				{
					throw new RuntimeException(t);
				}
			}
			
			@Override
			protected void tearDownMocks()
			{

			}
			
			protected abstract Collection<Setting> getSettingsToAdd();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final Collection<Setting> settingsToAdd = getSettingsToAdd();
				
				if (settingsToAdd != null)
				{
					for (Setting addSetting : settingsToAdd)
					{
						try
						{
							resource.addSetting(addSetting.getName(), addSetting.getValue());
						}
						catch (ServiceException e)
						{
							throw e;
						}
					}
				}
				
				doAssertions();

				
			}
				
			protected void doAssertions() throws Exception
			{
				
			}
		}
		
		@Test
		public void testAddSetting_assertSettingAdded() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Setting> settings;
				
				@Override
				protected Collection<Setting> getSettingsToAdd()
				{

					settings = new ArrayList<Setting>();
					
					Setting setting = new Setting();					
					setting.setName("setting1");
					setting.setValue("value1");
					settings.add(setting);
					
					setting = new Setting();					
					setting.setName("setting2");
					setting.setValue("value2");
					settings.add(setting);
					
					return settings;

				}

				
				@Override
				protected void doAssertions() throws Exception
				{
					Collection<org.nhindirect.config.store.Setting> retrievedSettings = settingDao.getAll();
					
					assertNotNull(retrievedSettings);
					assertEquals(this.settings.size(), retrievedSettings.size());
					
					final Iterator<Setting> addedSettingsIter = this.settings.iterator();
					
					for (org.nhindirect.config.store.Setting retrievedSetting : retrievedSettings)
					{
						final Setting addedSetting = addedSettingsIter.next(); 
						
						assertEquals(addedSetting.getName(), retrievedSetting.getName());
						assertEquals(addedSetting.getValue(), retrievedSetting.getValue());
					}
					
				}
			}.perform();
		}		
		
		@Test
		public void testAddSetting_addDuplicate_assertConflict() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Setting> settings;
				
				@Override
				protected Collection<Setting> getSettingsToAdd()
				{

					settings = new ArrayList<Setting>();
					
					Setting setting = new Setting();					
					setting.setName("setting1");
					setting.setValue("value1");
					settings.add(setting);
					
					setting = new Setting();					
					setting.setName("setting1");
					setting.setValue("value2");
					settings.add(setting);
					
					return settings;

				}

					
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof ServiceMethodException);
					ServiceMethodException ex = (ServiceMethodException)exception;
					assertEquals(409, ex.getResponseCode());
				}
			}.perform();
		}		
		
		@Test
		public void testAddSetting_errorInLookup_assertServiceError() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Setting> settings;
				
				protected SettingResource settingService;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						settingService = (SettingResource)ConfigServiceRunner.getSpringApplicationContext().getBean("settingResource");

						SettingDao mockDAO = mock(SettingDao.class);
						doThrow(new RuntimeException()).when(mockDAO).getByNames(Arrays.asList("setting1"));
						
						settingService.setSettingDao(mockDAO);
					}
					catch (Throwable t)
					{
						throw new RuntimeException(t);
					}
				}
				
				@Override
				protected void tearDownMocks()
				{
					super.tearDownMocks();
					
					settingService.setSettingDao(settingDao);
				}	
				
				@Override
				protected Collection<Setting> getSettingsToAdd()
				{

					settings = new ArrayList<Setting>();
					
					Setting setting = new Setting();					
					setting.setName("setting1");
					setting.setValue("value1");
					settings.add(setting);
					
					
					return settings;

				}
					
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof ServiceMethodException);
					ServiceMethodException ex = (ServiceMethodException)exception;
					assertEquals(500, ex.getResponseCode());
				}
			}.perform();
		}		
		
		@Test
		public void testAddSetting_errorInAdd_assertServiceError() throws Exception
		{
			new TestPlan()
			{
				protected Collection<Setting> settings;
				
				protected SettingResource settingService;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						settingService = (SettingResource)ConfigServiceRunner.getSpringApplicationContext().getBean("settingResource");

						SettingDao mockDAO = mock(SettingDao.class);
						when(mockDAO.getByNames(Arrays.asList("setting1"))).thenReturn(new ArrayList<org.nhindirect.config.store.Setting>());
						doThrow(new RuntimeException()).when(mockDAO).add(eq("setting1"), eq("value1"));
						
						settingService.setSettingDao(mockDAO);
					}
					catch (Throwable t)
					{
						throw new RuntimeException(t);
					}
				}
				
				@Override
				protected void tearDownMocks()
				{
					super.tearDownMocks();
					
					settingService.setSettingDao(settingDao);
				}	
				
				@Override
				protected Collection<Setting> getSettingsToAdd()
				{

					settings = new ArrayList<Setting>();
					
					Setting setting = new Setting();					
					setting.setName("setting1");
					setting.setValue("value1");
					settings.add(setting);
					
					
					return settings;

				}
					
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof ServiceMethodException);
					ServiceMethodException ex = (ServiceMethodException)exception;
					assertEquals(500, ex.getResponseCode());
				}
			}.perform();
		}			
}
