package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.store.dao.AnchorDao;
import org.nhindirect.stagent.cert.Thumbprint;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class AnchorResource_addAnchorTest 
{
    protected AnchorDao anchorDao;
    
	static WebResource resource;
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void setupMocks()
		{
			try
			{
				anchorDao = (AnchorDao)ConfigServiceRunner.getSpringApplicationContext().getBean("anchorDao");
				
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

		protected abstract Collection<Anchor> getAnchorsToAdd();
		
		@Override
		protected void performInner() throws Exception
		{				
			
			final Collection<Anchor> anchorsToAdd = getAnchorsToAdd();

			for (Anchor addAnchor : anchorsToAdd)
			{
				try
				{
					resource.path("/api/anchor").entity(addAnchor, MediaType.APPLICATION_JSON).put(addAnchor);
				}
				catch (UniformInterfaceException e)
				{
					throw e;
				}
			}			

			doAssertions();
		}
			
		protected void doAssertions() throws Exception
		{
			
		}
	}	
	
	@Test
	public void testAddAnchor_assertAnchorsAdded() throws Exception
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
			protected void doAssertions() throws Exception
			{
				List<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
				
				assertNotNull(anchors);
				assertEquals(2, anchors.size());
				
				final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
				
				for (org.nhindirect.config.store.Anchor retrievedAnchor : anchors)
				{
					final Anchor addedAnchor = addedAnchorsIter.next(); 
					assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
					assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.toCertificate());
					assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
					assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
					assertFalse(retrievedAnchor.getThumbprint().isEmpty());
				}
				
			}
		}.perform();
	}	
	
	@Test
	public void testAddAnchor_thumbprintEmpty_assertAnchorsAdded() throws Exception
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
					anchor.setThumbprint("");
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test2.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					anchor.setThumbprint("");
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			
			@Override
			protected void doAssertions() throws Exception
			{
				List<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
				
				assertNotNull(anchors);
				assertEquals(2, anchors.size());
				
				final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
				
				for (org.nhindirect.config.store.Anchor retrievedAnchor : anchors)
				{
					final Anchor addedAnchor = addedAnchorsIter.next(); 
					assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
					assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.toCertificate());
					assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
					assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
					assertFalse(retrievedAnchor.getThumbprint().isEmpty());
				}
				
			}
		}.perform();
	}		
	
	@Test
	public void testAddAnchor_thumbprintPreset_assertAnchorsAdded() throws Exception
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
					anchor.setThumbprint(Thumbprint.toThumbprint(anchor.getAnchorAsX509Certificate()).toString());
								
					anchors.add(anchor);
					
					
				    anchor = new Anchor();
					anchor.setOwner("test2.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("sm1.direct.com Root CA.der").getEncoded());	
					anchor.setThumbprint(Thumbprint.toThumbprint(anchor.getAnchorAsX509Certificate()).toString());
					
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			
			@Override
			protected void doAssertions() throws Exception
			{
				List<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
				
				assertNotNull(anchors);
				assertEquals(2, anchors.size());
				
				final Iterator<Anchor> addedAnchorsIter = this.anchors.iterator();
				
				for (org.nhindirect.config.store.Anchor retrievedAnchor : anchors)
				{
					final Anchor addedAnchor = addedAnchorsIter.next(); 
					assertEquals(addedAnchor.getOwner(), retrievedAnchor.getOwner());
					assertEquals(addedAnchor.getAnchorAsX509Certificate(), retrievedAnchor.toCertificate());
					assertEquals(addedAnchor.isIncoming(), retrievedAnchor.isIncoming());
					assertEquals(addedAnchor.isOutgoing(), retrievedAnchor.isOutgoing());
					assertFalse(retrievedAnchor.getThumbprint().isEmpty());
				}
				
			}
		}.perform();
	}		
	
	@Test
	public void testAddAnchor_addAnchorTwice_assertConflict() throws Exception
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
					anchor.setOwner("test.com");
					anchor.setIncoming(true);
					anchor.setOutgoing(true);
					anchor.setStatus(EntityStatus.ENABLED);
					anchor.setCertificateData(TestUtils.loadSigner("bundleSigner.der").getEncoded());
								
					anchors.add(anchor);
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
			}

			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof UniformInterfaceException);
				UniformInterfaceException ex = (UniformInterfaceException)exception;
				assertEquals(409, ex.getResponse().getStatus());
			}
		}.perform();
	}		
	
	@Test
	public void testAddAnchor_errorInLookup_assertServierError() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
		
			protected AnchorResource anchorService;
			
			@SuppressWarnings("unchecked")
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					anchorService = (AnchorResource)ConfigServiceRunner.getSpringApplicationContext().getBean("anchorResource");

					AnchorDao mockDAO = mock(AnchorDao.class);
					doThrow(new RuntimeException()).when(mockDAO).list((List<String>)any());
					
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
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
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
	public void testAddAnchor_errorInAdd_assertServierError() throws Exception
	{
		new TestPlan()
		{
			protected Collection<Anchor> anchors;
		
			protected AnchorResource anchorService;
			
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					anchorService = (AnchorResource)ConfigServiceRunner.getSpringApplicationContext().getBean("anchorResource");

					AnchorDao mockDAO = mock(AnchorDao.class);
					doThrow(new RuntimeException()).when(mockDAO).add((org.nhindirect.config.store.Anchor)any());
					
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
					
					return anchors;
				}
				catch (Exception e)
				{
					throw new RuntimeException (e);
				}
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
