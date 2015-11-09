package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;

import org.nhindirect.config.model.DNSRecord;

import org.nhindirect.config.model.utils.DNSUtils;

import org.nhindirect.config.resources.util.EntityModelConversion;
import org.nhindirect.config.store.dao.DNSDao;


import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class DNSResource_updateDNSRecordTest 
{
	   protected DNSDao dnsDao;
	    
		static WebResource resource;
		
		abstract class TestPlan extends BaseTestPlan 
		{
			protected DNSRecord addedRecord;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					dnsDao = (DNSDao)ConfigServiceRunner.getSpringApplicationContext().getBean("dnsDao");
					
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
						resource.path("/api/dns").entity(addRecord, MediaType.APPLICATION_JSON).put();
					}
					catch (UniformInterfaceException e)
					{
						throw e;
					}
				}
				
				final DNSRecord recordToUpdate = getRecordToUpdate();
				
				try
				{
					resource.path("/api/dns").entity(recordToUpdate, MediaType.APPLICATION_JSON).post();
				}
				catch (UniformInterfaceException e)
				{
					throw e;
				}
				
				try
				{
					final GenericType<Collection<DNSRecord>> genType = new GenericType<Collection<DNSRecord>>(){};
					final Collection<DNSRecord> getRecords = resource.path("/api/dns/")
							.queryParam("name", recordToUpdate.getName()).queryParam("type", Integer.toString(recordToUpdate.getType())).get(genType);
					
					doAssertions(getRecords);
				}
				catch (UniformInterfaceException e)
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
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(404, ex.getResponse().getStatus());
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
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
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
					assertTrue(exception instanceof UniformInterfaceException);
					UniformInterfaceException ex = (UniformInterfaceException)exception;
					assertEquals(500, ex.getResponse().getStatus());
				}
			}.perform();
		}	
}
