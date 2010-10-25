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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.nhindirect.config.service.impl.AddressServiceImpl;
import org.nhindirect.config.store.Address;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.AddressDao;

/**
 * Unit tests for the AddressService class.
 *
 * @author beau
 */
public class AddressServiceTest extends MockObjectTestCase
{

    private Mockery context = new JUnit3Mockery();

    /**
     * Default constructor.
     *
     * @param testName
     *            The test name.
     */
    public AddressServiceTest(String testName)
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
     * Test the addAddress method.
     */
    public void testAddAddress()
    {
        final AddressDao addressDao = context.mock(AddressDao.class);

        final Collection<Address> addresses = Arrays.asList(new Address(new Domain("healthdomain.com"),
                "beau@healthdomain.com"), new Address(new Domain("healthdomain2.com"), "beau@healthdomain2.com"));

        context.checking(new Expectations()
        {
            {
                // TODO
            }
        });

        AddressServiceImpl service = new AddressServiceImpl();
        service.setDao(addressDao);

        try
        {
            service.addAddress(addresses);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the updateAddress method.
     */
    public void testUpdateAddress()
    {
        final AddressDao addressDao = context.mock(AddressDao.class);

        final Address address = new Address();

        context.checking(new Expectations()
        {
            {
                // TODO
            }
        });

        AddressServiceImpl service = new AddressServiceImpl();
        service.setDao(addressDao);

        try
        {
            service.updateAddress(address);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAddressCount method.
     */
    public void testGetAddressCount()
    {
        final AddressDao addressDao = context.mock(AddressDao.class);

        final int count = 0;

        context.checking(new Expectations()
        {
            {
                oneOf(addressDao).count();
                will(returnValue(count));
            }
        });

        AddressServiceImpl service = new AddressServiceImpl();
        service.setDao(addressDao);

        try
        {
            int output = service.getAddressCount();
            assertEquals("Output does not match expected", count, output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAddress method.
     */
    public void testGetAddress()
    {
        final AddressDao addressDao = context.mock(AddressDao.class);

        final Collection<String> addresses = Arrays.asList("beau@healthdomain.com", "beau@helthdomain2.com");
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
                oneOf(addressDao).listAddresses(with(equal(new ArrayList<String>(addresses))), with(same(status)));
            }
        });

        AddressServiceImpl service = new AddressServiceImpl();
        service.setDao(addressDao);

        try
        {
            service.getAddress(addresses, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeAddress method.
     */
    public void testRemoveAddress()
    {
        final AddressDao addressDao = context.mock(AddressDao.class);

        final String addressName = "beau@address.com";

        context.checking(new Expectations()
        {
            {
                oneOf(addressDao).delete(addressName);
            }
        });

        AddressServiceImpl service = new AddressServiceImpl();
        service.setDao(addressDao);

        try
        {
            service.removeAddress(addressName);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listAddresss method.
     */
    public void testListAddresss()
    {
        final AddressDao addressDao = context.mock(AddressDao.class);

        final String lastAddressName = "lastAddressName.com";
        final int maxResults = 7;

        context.checking(new Expectations()
        {
            {
                // TODO
            }
        });

        AddressServiceImpl service = new AddressServiceImpl();
        service.setDao(addressDao);

        try
        {
            service.listAddresss(lastAddressName, maxResults);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

}
