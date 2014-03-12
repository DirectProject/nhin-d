package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.DNSService;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhind.config.testbase.TestUtils;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.utils.DNSUtils;
import org.nhindirect.config.resources.DNSResource;

import org.nhindirect.config.store.dao.DNSDao;
import org.xbill.DNS.Type;

public class DefaultDNSService_deleteDNSRecordsByIdsTest 
{
    protected DNSDao dnsDao;
    
	static DNSService resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void setupMocks()
		{
			try
			{
				dnsDao = (DNSDao)ConfigServiceRunner.getSpringApplicationContext().getBean("DNSDaoImpl");
				
				resource = 	(DNSService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), DNS_SERVICE);	

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
		
		protected abstract Collection<DNSRecord> getRecordsToAdd() throws Exception;
		
		protected abstract Collection<Long> getIdsToRemove();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<DNSRecord> recordsToAdd = getRecordsToAdd();

			if (recordsToAdd != null)
			{
				for (DNSRecord addRec : recordsToAdd)
				{
					try
					{
						resource.addDNSRecord(addRec);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}			
			}
			
			try
			{
				final Collection<Long> ids = getIdsToRemove();

				resource.deleteDNSRecordsByIds(ids);

			}
			catch (ServiceException e)
			{
				throw e;
			}
			
			
			doAssertions();
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}
	
	@Test
	public void testRemoveDNSRecordsByIds_removeExistingRecords_assertRecordsRemoved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<DNSRecord> records;
			
			@Override
			protected Collection<DNSRecord> getRecordsToAdd() throws Exception
			{
				try
				{
					records = new ArrayList<DNSRecord>();
					
					DNSRecord record = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");			
					records.add(record);
					
					
					record = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.44");						
					records.add(record);
					
					record = DNSUtils.createARecord("myserver2.com", 3600, "10.232.12.99");						
					records.add(record);
					
					record = DNSUtils.createX509CERTRecord("gm2552@securehealthemail.com", 3600, TestUtils.loadCert("gm2552.der"));					
					records.add(record);
					
					record = DNSUtils.createMXRecord("myserver.com", "10.232.12.77", 3600, 2);
					records.add(record);
					
					return records;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}
			
			@Override
			protected Collection<Long> getIdsToRemove()
			{
				final Collection<org.nhindirect.config.store.DNSRecord> recs = dnsDao.get(Type.ANY);
				
				final Collection<Long> ids = new ArrayList<Long>();
				for (org.nhindirect.config.store.DNSRecord rec : recs)
					ids.add(rec.getId());
				
				return ids;
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final Collection<org.nhindirect.config.store.DNSRecord> recs = dnsDao.get(Type.ANY);
				assertTrue(recs.isEmpty());
			}
		}.perform();
	}		
	
	@Test
	public void testRemoveDNSRecordsByIds_errorInDelete_assertServiceError() throws Exception
	{
		new TestPlan()
		{
			protected DNSResource dnsService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					dnsService = (DNSResource)ConfigServiceRunner.getSpringApplicationContext().getBean("DNSResource");

					DNSDao mockDAO = mock(DNSDao.class);
					doThrow(new RuntimeException()).when(mockDAO).remove((long[])any());
					
					dnsService.setDNSDao(mockDAO);
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
				
				dnsService.setDNSDao(dnsDao);
			}
			
			@Override
			protected Collection<DNSRecord> getRecordsToAdd() throws Exception
			{
				return null;
			}
			
			@Override
			protected Collection<Long> getIdsToRemove()
			{	
				
				return Arrays.asList(1234L);
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
