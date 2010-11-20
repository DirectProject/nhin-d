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
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.nhindirect.config.service.impl.AnchorServiceImpl;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.AnchorDao;

/**
 * Unit tests for the AnchorService class.
 * 
 * @author beau
 */
public class AnchorServiceTest extends MockObjectTestCase
{

    private Mockery context = new JUnit3Mockery();

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public AnchorServiceTest(String testName)
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
    public void testAddAnchors()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final Collection<Anchor> anchors = Arrays.asList(new Anchor());

        context.checking(new Expectations()
        {
            {            	
            	for (Anchor anchor : anchors)
            		oneOf(anchorDao).add(anchor);
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            service.addAnchors(anchors);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAnchor method.
     */
    public void testGetAnchor()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final String owner = "beau";
        final String thumbprint = "thumbprint";
        CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        final List<String> owners = Arrays.asList(owner);
        
        context.checking(new Expectations()
        {
            {   	
            	oneOf(anchorDao).list(owners);
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            Anchor output = service.getAnchor(owner, thumbprint, certificateOptions);
            assertEquals("Output does not match expected", null, output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAnchors method.
     */
    public void testGetAnchors()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final List<Long> anchorIds = Arrays.asList(7L, 8L);
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;


        context.checking(new Expectations()
        {
            {

            	oneOf(anchorDao).listByIds(anchorIds);
            	will(returnValue(Collections.<Long>emptyList()));
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            Collection<Anchor> output = service.getAnchors(null, certificateOptions);
            assertNotNull("Output is null when passing a null param", output);
            assertEquals("Output does not match expected return value for a null param", Collections.emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
        
        try
        {
            Collection<Anchor> output = service.getAnchors(Collections.<Long>emptyList(), certificateOptions);
            assertNotNull("Output is null when passing an empty collection", output);
            assertEquals("Output does not match expected return value for an empty collection", Collections.emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
        
        try
        {
            Collection<Anchor> output = service.getAnchors(anchorIds, certificateOptions);
            assertNotNull("Output is null when using valid params", output);
            assertEquals("Output does not match mocked return value when using valid params", Collections.<Long>emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAnchorsForOwner method.
     */
    public void testGetAnchorsForOwner()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final String owner = "beau";
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        final List<String> owners = Arrays.asList(owner);
        
        context.checking(new Expectations()
        {
            {
            	oneOf(anchorDao).list(owners);
            	will(returnValue(Collections.<Anchor>emptyList()));
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            Collection<Anchor> output = service.getAnchorsForOwner(owner, certificateOptions);
            assertNotNull("Output is null when passing valid params", output);
            assertEquals("Output does not match mocked return value when using valid params", Collections.<Anchor>emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getIncomingAnchors method.
     */
    public void testGetIncomingAnchors()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final String owner = "beau";
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        final List<String> owners = Arrays.asList(owner);
        
        context.checking(new Expectations()
        {
            {
            	oneOf(anchorDao).list(owners);
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            Collection<Anchor> output = service.getIncomingAnchors(owner, certificateOptions);
            assertEquals("Output does not match expected", Collections.emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getIncomingAnchors method.
     */
    public void testGetOutgoingAnchors()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final String owner = "beau";
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        final List<String> owners = Arrays.asList(owner);
        
        context.checking(new Expectations()
        {
            {
            	oneOf(anchorDao).list(owners);
            }
        });


        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            Collection<Anchor> output = service.getOutgoingAnchors(owner, certificateOptions);
            assertEquals("Output does not match expected", Collections.emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the setAnchorStatusForOwner method.
     */
    public void testSetAnchorStatusForOwner()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final String owner = "beau";
        final EntityStatus status = EntityStatus.ENABLED;

        final Anchor anchor = new Anchor();
        anchor.setOwner(owner);
        
        context.checking(new Expectations()
        {
            {            
            	oneOf(anchorDao).setStatus(owner, status);
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            service.setAnchorStatusForOwner(owner, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listAnchors method.
     */
    public void testListAnchors()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final Long anchorId = 7L;
        final int maxResults = 7;
        final CertificateGetOptions certificateOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
            	oneOf(anchorDao).listAll();
            	will(returnValue(Collections.<Anchor>emptyList()));
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            Collection<Anchor> output = service.listAnchors(anchorId, maxResults, certificateOptions);
            assertNotNull("Output is null when passing valid params", output);
            assertEquals("Output does not match mocked return value when using valid params", Collections.<Anchor>emptyList(), output);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeAnchor method.
     */
    public void testRemoveAnchors()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final List<Long> anchorIds = Arrays.asList(7L, 8L);

        context.checking(new Expectations()
        {
            {
            	oneOf(anchorDao).delete(anchorIds);
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            service.removeAnchors(anchorIds);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeAnchorsForOwner method.
     */
    public void testRemoveAnchorsForOwner()
    {
        final AnchorDao anchorDao = context.mock(AnchorDao.class);

        final String owner = "beau";

        context.checking(new Expectations()
        {
            {
            	oneOf(anchorDao).delete(owner);
            }
        });

        AnchorServiceImpl service = new AnchorServiceImpl();
        service.setDao(anchorDao);

        try
        {
            service.removeAnchorsForOwner(owner);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

}
