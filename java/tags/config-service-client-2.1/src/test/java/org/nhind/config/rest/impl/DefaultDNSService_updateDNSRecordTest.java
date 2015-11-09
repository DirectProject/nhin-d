package org.nhind.config.rest.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.DNSService;
import org.nhind.config.testbase.BaseTestPlan;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.utils.DNSUtils;
import org.nhindirect.config.resources.DNSResource;

import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.dao.DNSDao;

public class DefaultDNSService_updateDNSRecordTest 
{
	   protected DNSDao dnsDao;
	    
		static DNSService resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			protected DNSRecord addedRecord;
			
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
			
			protected DNSRecord getDNSRecordToAdd()
			{

				addedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");			
				return addedRecord;
			}
			
			protected abstract DNSRecord getRecordToUpdate();
			
			@Override
			protected void performInner() throws Exception
			{				
				
				final DNSRecord addRecord = getDNSRecordToAdd();
				
				if (addRecord != null)
				{
					try
					{
						resource.addDNSRecord(addRecord);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
				
				final DNSRecord recordToUpdate = getRecordToUpdate();
				
				try
				{
					resource.updatedDNSRecord(recordToUpdate);
				}
				catch (ServiceException e)
				{
					throw e;
				}
				
				try
				{
					final Collection<DNSRecord> getRecords = resource.getDNSRecord(recordToUpdate.getType(), recordToUpdate.getName()); 
					doAssertions(getRecords);
				}
				catch (ServiceException e)
				{
					throw e;
				}
				
			}
			
			
			protected void doAssertions(Collection<DNSRecord> records) throws Exception
			{
				
			}
		}	
		
		@Test
		public void testUpdateDNSRecord_updateExistingRecord_assertRecordUpdated() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					Collection<org.nhindirect.config.store.DNSRecord> records = dnsDao.get(addedRecord.getName(), addedRecord.getType());
					
					// should be one record
					assertEquals(1, records.size());
					
					org.nhindirect.config.store.DNSRecord record = records.iterator().next();
					record.setName("server2.com.");
					
					updatedRecord = EntityModelConversion.toModelDNSRecord(record);
					
					return updatedRecord;
				}
				
				@Override
				protected void doAssertions(Collection<DNSRecord> records) throws Exception
				{
					assertEquals(1, records.size());
					
					DNSRecord record = records.iterator().next();
					
					assertEquals("server2.com.", record.getName());
					assertTrue(Arrays.equals(updatedRecord.getData(), record.getData()));
				}
			}.perform();
		}	
		
		@Test
		public void testUpdateDNSRecord_updateExistingRecord_noDottedSuffix_assertRecordUpdated() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					Collection<org.nhindirect.config.store.DNSRecord> records = dnsDao.get(addedRecord.getName(), addedRecord.getType());
					
					// should be one record
					assertEquals(1, records.size());
					
					org.nhindirect.config.store.DNSRecord record = records.iterator().next();
					record.setName("server2.com");
					
					updatedRecord = EntityModelConversion.toModelDNSRecord(record);
					
					return updatedRecord;
				}
				
				@Override
				protected void doAssertions(Collection<DNSRecord> records) throws Exception
				{
					assertEquals(1, records.size());
					
					DNSRecord record = records.iterator().next();
					
					assertEquals("server2.com.", record.getName());
					assertTrue(Arrays.equals(updatedRecord.getData(), record.getData()));
				}
			}.perform();
		}	
		
		@Test
		public void testUpdateDNSRecord_recordDoesntExist_assertNotFound() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					updatedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");		
					updatedRecord.setId(1233);
					return updatedRecord;
				}
				
				@Override
				protected void assertException(Exception exception) throws Exception 
				{
					assertTrue(exception instanceof ServiceMethodException);
					ServiceMethodException ex = (ServiceMethodException)exception;
					assertEquals(404, ex.getResponseCode());
				}
			}.perform();
		}		
		
		@Test
		public void testUpdateDNSRecord_errorInLookup_assertServerError() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				protected DNSResource dnsService;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						dnsService = (DNSResource)ConfigServiceRunner.getSpringApplicationContext().getBean("DNSResource");

						DNSDao mockDAO = mock(DNSDao.class);
						
						doThrow(new RuntimeException()).when(mockDAO).get(eq(1233L));
						
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
				protected DNSRecord getDNSRecordToAdd()
				{
					return null;
				}
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					updatedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");		
					updatedRecord.setId(1233);
					return updatedRecord;
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
		public void testUpdateDNSRecord_errorInUpdate_assertServerError() throws Exception
		{
			new TestPlan()
			{
				protected DNSRecord updatedRecord;
				
				protected DNSResource dnsService;
				
				@Override
				protected void setupMocks()
				{
					try
					{
						super.setupMocks();
						
						dnsService = (DNSResource)ConfigServiceRunner.getSpringApplicationContext().getBean("DNSResource");

						DNSDao mockDAO = mock(DNSDao.class);
						
						when(mockDAO.get(1233L)).thenReturn(new org.nhindirect.config.store.DNSRecord());
						doThrow(new RuntimeException()).when(mockDAO).update(eq(1233L), (org.nhindirect.config.store.DNSRecord)any());
						
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
				protected DNSRecord getDNSRecordToAdd()
				{
					return null;
				}
				
				@Override
				protected DNSRecord getRecordToUpdate()
				{				
					updatedRecord = DNSUtils.createARecord("myserver.com", 3600, "10.232.12.43");		
					updatedRecord.setId(1233);
					return updatedRecord;
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
