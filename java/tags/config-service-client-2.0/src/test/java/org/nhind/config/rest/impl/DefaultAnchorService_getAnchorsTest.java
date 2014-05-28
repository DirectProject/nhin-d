package org.nhind.config.rest.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import org.junit.Test;
import org.nhind.config.client.ConfigServiceRunner;
import org.nhind.config.rest.AnchorService;
import org.nhind.config.testbase.BaseTestPlan;
import org.nhind.config.testbase.TestUtils;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.resources.AnchorResource;

import org.nhindirect.config.store.dao.AnchorDao;


public class DefaultAnchorService_getAnchorsTest 
{
    
    protected AnchorDao anchorDao;
    
	static AnchorService resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				anchorDao = (AnchorDao)ConfigServiceRunner.getSpringApplicationContext().getBean("anchorDao");
				
				resource = 	(AnchorService)BaseTestPlan.getService(ConfigServiceRunner.getRestAPIBaseURL(), ANCHOR_SERVICE);	

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
		
		protected abstract Collection<Anchor> getAnchorsToAdd();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<Anchor> anchorsToAdd = getAnchorsToAdd();
			
			if (anchorsToAdd != null)
			{
				for (Anchor addAnchor : anchorsToAdd)
				{
					try
					{
						resource.addAnchor(addAnchor);
					}
					catch (ServiceException e)
					{
						throw e;
					}
				}
			}
			
			try
			{
				final Collection<Anchor> getAnchors = resource.getAnchors();

				doAssertions(getAnchors);
			}
			catch (ServiceMethodException e)
			{
				if (e.getResponseCode() == 204)
					doAssertions(new ArrayList<Anchor>());
				else
					throw e;
			}
			
		}
			
		protected void doAssertions(Collection<Anchor> anchors) throws Exception
		{
			
		}
	}	
	
	
	@Test
	public void testGetAnchors_assertAnchorsRetrieved() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				try
				{
					anchors = new ArrayList<Anchor>();
					
					Anchor anchor = new Anchor();
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test2.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			
			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertEquals(2, anchors.size());
				
				final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
				
				for (Anchor retrievedAnchor : anchors)
				{
					final Anchor addedAnchor = addedAnchorsIter.next(); 
					assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
					assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.getAnchorAsX509Certificate());
					assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
					assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
					assertEquals(addedAnchor.getStatus(), retrievedAnchor.getStatus());
					assertFalse(retrievedAnchor.getThumbprint().isEmpty());
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAnchors_noAnchorsInStore_assertNoAnchorsRetrieved() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				return null;
			}

			@Override
			protected void doAssertions(Collection<Anchor> anchors) throws Exception
			{
				assertNotNull(anchors);
				assertTrue(anchors.isEmpty());
				
			}
		}.perform();
	}	
	
	@Test
	public void testGetAnchors_errorInLookup_assertServerError() throws Exception
	{
		new TestPlan()
		{
			
			protected AnchorResource anchorService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					anchorService = (AnchorResource)ConfigServiceRunner.getSpringApplicationContext().getBean("anchorResource");

					AnchorDao mockDAO = mock(AnchorDao.class);
					doThrow(new RuntimeException()).when(mockDAO).listAll();
					
					anchorService.setAnchorDao(mockDAO);
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
				
				anchorService.setAnchorDao(anchorDao);
			}
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				return null;
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
