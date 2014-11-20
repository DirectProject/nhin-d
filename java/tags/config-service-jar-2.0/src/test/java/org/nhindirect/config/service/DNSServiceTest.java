package org.nhindirect.config.service;

import java.util.Arrays;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.nhindirect.config.service.impl.DNSServiceImpl;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.dao.DNSDao;
import org.nhindirect.config.store.util.DNSRecordUtils;
import org.xbill.DNS.Type;

public class DNSServiceTest extends MockObjectTestCase 
{
    private Mockery context = new JUnit3Mockery();

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public DNSServiceTest(String testName)
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
     * Test the addDNS method.
     */
    public void testAddDNS()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);

        final Collection<DNSRecord> records = Arrays.asList(DNSRecordUtils.createARecord("example.domain.com", 84000L, "10.45.84.12"));
        

        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).add(records);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.addDNS(records);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }    
    
    /**
     * Test the getDNSCount method.
     */
    public void testGetCount()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);

        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).count();
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.getDNSCount();
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }     
    
    /**
     * Test the getDNSByName method.
     */
    public void testGetDNSByName()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);
        final String name = "example.domain.com";
        
        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).get(name);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.getDNSByName(name);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }      
    
    
    /**
     * Test the getDNSByType method.
     */
    public void testGetDNSByType()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);
        final int type = Type.A;
        
        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).get(type);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.getDNSByType(type);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }   
    
    /**
     * Test the getDNSByNameAndType method.
     */
    public void testGetDNSByNameAndType()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);
        final String name = "example.domain.com";
        final int type = Type.A;
        
        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).get(name, type);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.getDNSByNameAndType(name, type);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }    
    
    
    /**
     * Test the getDNSByRecordId method.
     */
    public void testGetDNSByRecordId()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);
        final long recId = 8387;
        
        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).get(recId);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.getDNSByRecordId(recId);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }    
    
    /**
     * Test the getDNSByRecordIds method.
     */
    public void testGetDNSByRecordIds()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);
        final long[] recIds = new long[] {8387};
        
        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).get(recIds);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.getDNSByRecordIds(recIds);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }    
    
    /**
     * Test the removeDNS method.
     */
    public void testRemoveDNS()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);

        final Collection<DNSRecord> records = Arrays.asList(DNSRecordUtils.createARecord("example.domain.com", 84000L, "10.45.84.12"));
        

        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).remove(records);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.removeDNS(records);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }      
    
    /**
     * Test the removeDNSByRecordId method.
     */
    public void testRemoveDNSByRecordId()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);

        final long recId = 8387;
        

        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).remove(recId);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.removeDNSByRecordId(recId);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }     
    
    /**
     * Test the removeDNSByRecordIds method.
     */
    public void testRemoveDNSByRecordIds()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);

        final long[] recIds = new long[] {8387};
        

        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).remove(recIds);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.removeDNSByRecordIds(recIds);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }   
    
    /**
     * Test the updateDNS method.
     */
    public void testUpdateDNS()
    {
        final DNSDao dnsDao = context.mock(DNSDao.class);

        final DNSRecord record = DNSRecordUtils.createARecord("example.domain.com", 84000L, "10.45.84.12");
        final long recId = 8387;        

        context.checking(new Expectations()
        {
            {            	
           		oneOf(dnsDao).update(recId, record);
            }
        });

        DNSServiceImpl service = new DNSServiceImpl();
        service.setDao(dnsDao);

        try
        {
            service.updateDNS(recId, record);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }      
}
