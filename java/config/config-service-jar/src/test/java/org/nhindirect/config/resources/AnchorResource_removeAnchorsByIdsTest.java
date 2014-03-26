package org.nhindirect.config.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.nhindirect.config.BaseTestPlan;
import org.nhindirect.config.ConfigServiceRunner;
import org.nhindirect.config.TestUtils;
import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.store.dao.AnchorDao;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class AnchorResource_removeAnchorsByIdsTest 
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
		
		protected abstract Collection<Long> getIdsToRemove();
		
		
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
						resource.path("/api/anchor").entity(addAnchor, MediaType.APPLICATION_JSON).put(addAnchor);
					}
					catch (UniformInterfaceException e)
					{
						throw e;
					}
				}
			}
			
			
			try
			{
				final Collection<Long> ids = getIdsToRemove();
				StringBuilder builder = new StringBuilder();
				int cnt = 0;
				for (Long id : ids)
				{
					builder.append(id);
					if (cnt < ids.size() - 1)
						builder.append(",");
					
					++cnt;
				}

				resource.path("/api/anchor/ids/" + builder.toString()).delete();

			}
			catch (UniformInterfaceException e)
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
	public void testRemoveAnchorsByIds_removeExistingAnchors_assertAnchorRemoved() throws Exception
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
			protected Collection<Long> getIdsToRemove()
			{
				final Collection<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
				
				final Collection<Long> ids = new ArrayList<Long>();
				for (org.nhindirect.config.store.Anchor anchor : anchors)
					ids.add(anchor.getId());
				
				return ids;
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final Collection<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
				assertTrue(anchors.isEmpty());
			}
		}.perform();
	}	
	
	@Test
	public void testRemoveAnchorsByIds_removeSingleAnchor_assertAnchorRemoved() throws Exception
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
			protected Collection<Long> getIdsToRemove()
			{
				final Collection<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
				
				final Collection<Long> ids = new ArrayList<Long>();

				ids.add(anchors.iterator().next().getId());
				
				return ids;
			}
			
			@Override
			protected void doAssertions() throws Exception
			{
				final Collection<org.nhindirect.config.store.Anchor> anchors = anchorDao.listAll();
				assertEquals(1, anchors.size());
			}
		}.perform();
	}	
	
	@Test
	public void testRemoveAnchorsByIds_errorInDelete_assertServerError() throws Exception
	{
		new TestPlan()
		{
			AnchorResource anchorService;
			
			@SuppressWarnings("unchecked")
			@Override
			protected void setupMocks()
			{
				try
				{
					super.setupMocks();
					
					anchorService = (AnchorResource)ConfigServiceRunner.getSpringApplicationContext().getBean("anchorResource");

					AnchorDao mockDAO = mock(AnchorDao.class);
					doThrow(new RuntimeException()).when(mockDAO).delete((List<Long>)any());
					
					anchorService.setAnchorDao(mockDAO);
				}
				catch (Throwable t)
				{
					throw new RuntimeException(t);
				}
			}
			
			@Override
			protected Collection<Anchor> getAnchorsToAdd()
			{
				return null;
			}
			
			@Override
			protected void tearDownMocks()
			{
				super.tearDownMocks();
				
				anchorService.setAnchorDao(anchorDao);
			}
			
			@Override
			protected Collection<Long> getIdsToRemove()
			{
				
				final Collection<Long> ids = new ArrayList<Long>();
				ids.add(12345L);
				
				return ids;
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
