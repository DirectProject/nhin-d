package org.nhindirect.gateway.smtp.james.mailet;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.MailetConfig;
import org.nhindirect.gateway.smtp.dsn.impl.AbstractDSNCreator;
import org.nhindirect.gateway.smtp.dsn.impl.RejectedRecipientDSNCreator;
import org.nhindirect.gateway.smtp.dsn.module.DSNCreatorProviderModule;
import org.nhindirect.gateway.smtp.dsn.provider.RejectedRecipientDSNCreatorProvider;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;

import com.google.inject.Module;

import junit.framework.TestCase;

public class NHINDSecurityAndTrustMailet_createDSNCreatorTest extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{		
		
		protected MailetConfig getMailetConfig() throws Exception
		{
			String configfile = TestUtils.getTestConfigFile(getConfigFileName());
			Map<String,String> params = new HashMap<String, String>();
			
			params.put(SecurityAndTrustMailetOptions.CONFIG_URL_PARAM, "file://" + configfile);
			
			return new MockMailetConfig(params, "NHINDSecurityAndTrustMailet");	
		}
		
		@Override
		protected void performInner() throws Exception
		{
			NHINDSecurityAndTrustMailet theMailet = new NHINDSecurityAndTrustMailet()
			{
				@Override
				protected Collection<Module> getInitModules()
				{
					return getTestInitModules();
				}

			};

			MailetConfig config = getMailetConfig();
			
			
			theMailet.init(config);
			doAssertions(theMailet);
		}
		
		protected Collection<Module> getTestInitModules()
		{
			return null;
		}
		
		protected String getConfigFileName()
		{
			return "ValidConfig.xml";
		}
		
		protected void doAssertions(NHINDSecurityAndTrustMailet agent) throws Exception
		{
		}			
	}
	
	public void testCreateDSNCreator_nullInitModuels_assertDSNCreator() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet mailet) throws Exception
			{
				assertNotNull(mailet.dsnCreator);
				assertTrue(mailet.dsnCreator instanceof RejectedRecipientDSNCreator);
				RejectedRecipientDSNCreator creator = (RejectedRecipientDSNCreator)mailet.dsnCreator;
				
				
				Field field = AbstractDSNCreator.class.getDeclaredField("mailet");
				field.setAccessible(true);
				Object mailetField = field.get(creator);
				assertNotNull(mailetField);
			}				
		}.perform();
	}
	
	public void testCreateDSNCreator_noCreatorInitModuels_assertDSNCreator() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected Collection<Module> getTestInitModules()
			{
				return Arrays.asList(mock(Module.class));
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet mailet) throws Exception
			{
				assertNotNull(mailet.dsnCreator);
				assertTrue(mailet.dsnCreator instanceof RejectedRecipientDSNCreator);
				RejectedRecipientDSNCreator creator = (RejectedRecipientDSNCreator)mailet.dsnCreator;
				
				Field field = AbstractDSNCreator.class.getDeclaredField("mailet");
				field.setAccessible(true);
				Object mailetField = field.get(creator);
				assertNotNull(mailetField);
			}				
		}.perform();
	}
	
	public void testCreateDSNCreator_existingInitModules_assertDSNCreator() throws Exception 
	{
		new TestPlan() 
		{
			@Override
			protected Collection<Module> getTestInitModules()
			{
				final RejectedRecipientDSNCreatorProvider provider = new RejectedRecipientDSNCreatorProvider(null);
				final DSNCreatorProviderModule module = DSNCreatorProviderModule.create(provider);
				return Arrays.asList((Module)module);
			}
			
			@Override
			protected void doAssertions(NHINDSecurityAndTrustMailet mailet) throws Exception
			{
				assertNotNull(mailet.dsnCreator);
				assertTrue(mailet.dsnCreator instanceof RejectedRecipientDSNCreator);
				RejectedRecipientDSNCreator creator = (RejectedRecipientDSNCreator)mailet.dsnCreator;

				Field field = AbstractDSNCreator.class.getDeclaredField("mailet");
				field.setAccessible(true);
				Object mailetField = field.get(creator);
				assertNull(mailetField);
			}				
		}.perform();
	}
}
