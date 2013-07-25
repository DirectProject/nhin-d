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
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.service.impl.CertificateServiceImpl;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.CertificateDao;

/**
 * Unit tests for the CertificateService class.
 * 
 * @author beau
 */
public class CertificateServiceTest extends MockObjectTestCase
{

    private Mockery context = new JUnit3Mockery();

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public CertificateServiceTest(String testName)
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
     * Test the addCertificates method.
     */
    public void testAddCertificates()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final Collection<Certificate> certificates = Arrays.asList(new Certificate());

        context.checking(new Expectations()
        {
            {
                oneOf(certificateDao).save(certificates.iterator().next());
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            service.addCertificates(certificates);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getCertificate method.
     */
    public void testGetCertificate()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final String owner = "beau";
        final String thumbprint = "thumbprint";
        CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
            	oneOf(certificateDao).load(owner, thumbprint);
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            Certificate output = service.getCertificate(owner, thumbprint, certificateOptions);
            assertEquals("Output does not match expected", null, output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getCertificates method.
     */
    public void testGetCertificates()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final Collection<Long> certificateIds = Arrays.asList(7L, 8L);
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
            	oneOf(certificateDao).list(new ArrayList<Long>(certificateIds));
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            Collection<Certificate> output = service.getCertificates(certificateIds, certificateOptions);
            assertEquals("Output does not match expected", Collections.emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getCertificatesForOwner method.
     */
    public void testGetCertificatesForOwner()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final String owner = "beau";
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
            	oneOf(certificateDao).list(owner);
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            Collection<Certificate> output = service.getCertificatesForOwner(owner, certificateOptions);
            assertEquals("Output does not match expected", Collections.emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the setCertificateStatusForOwner method.
     */
    public void testSetCertificateStatusForOwner()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final String owner = "beau";
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
            	oneOf(certificateDao).setStatus(owner, status);
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            service.setCertificateStatusForOwner(owner, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeCertificate method.
     */
    public void testRemoveCertificates()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final List<Long> certificateIds = Arrays.asList(7L, 8L);

        context.checking(new Expectations()
        {
            {
            	oneOf(certificateDao).delete(certificateIds);
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            service.removeCertificates(certificateIds);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeCertificatesForOwner method.
     */
    public void testRemoveCertificatesForOwner()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final String owner = "beau";

        context.checking(new Expectations()
        {
            {
            	oneOf(certificateDao).delete(owner);
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            service.removeCertificatesForOwner(owner);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listCertificates method.
     */
    public void testListCertificates()
    {
        final CertificateDao certificateDao = context.mock(CertificateDao.class);

        final Long certificateId = 7L;
        final int maxResults = 7;
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
            	oneOf(certificateDao).list((String)null);
            }
        });

        CertificateServiceImpl service = new CertificateServiceImpl();
        service.setDao(certificateDao);

        try
        {
            Collection<Certificate> output = service.listCertificates(certificateId, maxResults, certificateOptions);
            assertEquals("Output does not match expected", Collections.emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

}
