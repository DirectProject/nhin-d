package org.nhindirect.config.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.nhindirect.config.service.impl.SettingServiceImpl;
import org.nhindirect.config.store.dao.SettingDao;

public class SettingServiceTest extends MockObjectTestCase
{

    private Mockery context = new JUnit3Mockery();

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public SettingServiceTest(String testName)
    {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /**
     * Test the addAnchors method.
     */
    public void testAddSetting()
    {
        final SettingDao settingDao = context.mock(SettingDao.class);

        final String name = UUID.randomUUID().toString();
        final String value = UUID.randomUUID().toString();

        context.checking(new Expectations()
        {
            {            	
           		oneOf(settingDao).add(name, value);
            }
        });

        SettingServiceImpl service = new SettingServiceImpl();
        service.setDao(settingDao);

        try
        {
            service.addSetting(name, value);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }    
    
    /**
     * Test the addAnchors method.
     */
    public void testGetSettingByName()
    {
        final SettingDao settingDao = context.mock(SettingDao.class);

        final String name = UUID.randomUUID().toString();

        context.checking(new Expectations()
        {
            {            	
           		oneOf(settingDao).getByNames(Arrays.asList(name));
            }
        });

        SettingServiceImpl service = new SettingServiceImpl();
        service.setDao(settingDao);

        try
        {
            service.getSettingByName(name);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }    
    
    /**
     * Test the addAnchors method.
     */
    public void testGetSettingsByNames()
    {
        final SettingDao settingDao = context.mock(SettingDao.class);

        final Collection<String> names = Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        context.checking(new Expectations()
        {
            {            	
           		oneOf(settingDao).getByNames(names);
            }
        });

        SettingServiceImpl service = new SettingServiceImpl();
        service.setDao(settingDao);

        try
        {
            service.getSettingsByNames(names);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }   
    
    /**
     * Test the addAnchors method.
     */
    public void testAllSettings()
    {
        final SettingDao settingDao = context.mock(SettingDao.class);

        context.checking(new Expectations()
        {
            {            	
           		oneOf(settingDao).getAll();
            }
        });

        SettingServiceImpl service = new SettingServiceImpl();
        service.setDao(settingDao);

        try
        {
            service.getAllSettings();
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }    
    
    /**
     * Test the addAnchors method.
     */
    public void testUpdateSetting()
    {
        final SettingDao settingDao = context.mock(SettingDao.class);

        final String name = UUID.randomUUID().toString();
        final String value = UUID.randomUUID().toString();
        
        context.checking(new Expectations()
        {
            {            	
           		oneOf(settingDao).update(name, value);
            }
        });

        SettingServiceImpl service = new SettingServiceImpl();
        service.setDao(settingDao);

        try
        {
            service.updateSetting(name, value);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }     
    
    /**
     * Test the addAnchors method.
     */
    public void testDeleteSettings()
    {
        final SettingDao settingDao = context.mock(SettingDao.class);

        final Collection<String> name = Arrays.asList(UUID.randomUUID().toString());
        
        context.checking(new Expectations()
        {
            {            	
           		oneOf(settingDao).delete(name);
            }
        });

        SettingServiceImpl service = new SettingServiceImpl();
        service.setDao(settingDao);

        try
        {
            service.deleteSetting(name);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }       
}
