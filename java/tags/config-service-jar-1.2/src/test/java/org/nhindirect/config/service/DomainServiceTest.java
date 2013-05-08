/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.nhindirect.config.service.impl.DomainServiceImpl;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.DomainDao;

/**
 * Unit tests for the DomainService class.
 * 
 * @author beau
 */
public class DomainServiceTest extends MockObjectTestCase
{

    private Mockery context = new JUnit3Mockery();

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public DomainServiceTest(String testName)
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
     * Test the addDomain method.
     */
    public void testAddDomain()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final Domain domain = new Domain();

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).add(domain);
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.addDomain(domain);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the updateDomain method.
     */
    public void testUpdateDomain()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final Domain domain = new Domain();

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).update(domain);
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.updateDomain(domain);
            service.updateDomain(null);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getDomainCount method.
     */
    public void testGetDomainCount()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final int count = 7;

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).count();
                will(returnValue(count));
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            int output = service.getDomainCount();
            assertEquals("Output does not match expected", count, output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getDomains method.
     * 
     * TODO: This test needs to be improved.
     */
    @SuppressWarnings("unchecked")
    public void testGetDomains()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final Collection<String> domainNames = Arrays.asList("domain.com", "domain2.com");
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).getDomains(with(any(List.class)), with(any(EntityStatus.class)));
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.getDomains(domainNames, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeDomain method.
     */
    public void testRemoveDomain()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final String domain = "domain.com";

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).delete(domain);
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.removeDomain(domain);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the removeDomainById method.
     */
    public void testRemoveDomainById()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final long domainId = 1;

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).delete(domainId);
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.removeDomainById(domainId);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listDomains method.
     */
    public void testListDomains()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final String lastDomainName = "lastDomainName.com";
        final int maxResults = 7;
        final List<Domain> expected = Arrays.asList(new Domain());
        final List<Domain> expectedNull = null;

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).listDomains(lastDomainName, maxResults);
                will(returnValue(expected));
                oneOf(domainDao).listDomains(lastDomainName, maxResults);
                will(returnValue(expectedNull));
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.listDomains(lastDomainName, maxResults);
            service.listDomains(lastDomainName, maxResults);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the searchDomain method.
     */
    public void testSearchDomain()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final String domainName = "domain.com";
        final EntityStatus status = EntityStatus.ENABLED;
        final List<Domain> expected = Arrays.asList(new Domain());
        final List<Domain> expectedNull = null;

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).searchDomain(domainName, status);
                will(returnValue(expected));
                oneOf(domainDao).searchDomain(domainName, status);
                will(returnValue(expectedNull));
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.searchDomain(domainName, status);
            service.searchDomain(domainName, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getDomain method.
     */
    public void testGetDomain()
    {
        final DomainDao domainDao = context.mock(DomainDao.class);

        final long id = 7;

        final Domain expected = new Domain();
        final Domain expectedNull = null;

        context.checking(new Expectations()
        {
            {
                oneOf(domainDao).getDomain(id);
                will(returnValue(expected));
                oneOf(domainDao).getDomain(id);
                will(returnValue(expectedNull));
            }
        });

        DomainServiceImpl service = new DomainServiceImpl();
        service.setDao(domainDao);

        try
        {
            service.getDomain(id);
            service.getDomain(id);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
}
