package org.nhindirect.config.resources;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.Setting;
import org.nhindirect.config.store.dao.SettingDao;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class SettingResource_getAllSettingsTest 
{
	   protected SettingDao settingDao;
	    
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			@Override
			protected void setupMocks()
			{
				try
				{
					settingDao = (SettingDao)ConfigServiceRunner.getSpringApplicationContext().getBean("settingDao");
					
					resource = 	getResource(ConfigServiceRunner.getConfigServiceURL());		
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
							resource.path("/api/setting/" + TestUtils.uriEscape(addSetting.getName()) + "/" + TestUtils.uriEscape(addSetting.getValue())).put();
						}
						catch (UniformInterfaceException e)
						{
							throw e;
						}
					}
				}
				
				try
				{
					
					final GenericType<ArrayList<Setting>> genType = new GenericType<ArrayList<Setting>>(){};
					final Collection<Setting> getSettings = resource.path("/api/setting/").get(genType);

					doAssertions(getSettings);
				}
				catch (UniformInterfaceException e)
				{
					if (e.getResponse().getStatus() == 204)
						doAssertions(new ArrayList<Setting>());
					else
						throw e;
				}
				
			}
				
			protected void doAssertions(Collection<Setting> setting) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testGetSettings_assertSettingsRetrieved() throws Exception
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
				protected void doAssertions(Collection<Setting> setting) throws Exception
				{
					assertNotNull(setting);
					assertEquals(2, setting.size());
					
					final Iterator<Setting> addedSettingsIter = this.settings.iterator();
					
					for (Setting retrievedSetting : settings)
					{	
						final Setting addedSetting = addedSettingsIter.next(); 
						
						assertEquals(addedSetting.getName(), retrievedSetting.getName());
						assertEquals(addedSetting.getValue(), retrievedSetting.getValue());
					}
					
				}
			}.perform();
		}	
		
		
		@Test
		public void testGetSettings_noSettingsInStore_assertNoSettingsRetrieved() throws Exception
		{
			new TestPlan()
			{
				
				@Override
				protected Collection<Setting> getSettingsToAdd()
				{
					return null;
				}

				
				@Override
				protected void doAssertions(Collection<Setting> setting) throws Exception
				{
					assertNotNull(setting);
					assertEquals(0, setting.size());
					
					
				}
			}.perform();
		}		
		
		@Test
		public void testGetSettings_errorInLookup_assertServiceError() throws Exception
		{
			new TestPlan()
			{
				protected SettingResource settingService;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						settingService = (SettingResource)ConfigServiceRunner.getSpringApplicationContext().getBean("settingResource");

						SettingDao mockDAO = mock(SettingDao.class);
						doThrow(new RuntimeException()).when(mockDAO).getAll();
						
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
					return null;
				}

				
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}			
}
