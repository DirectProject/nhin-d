/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
 */

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
