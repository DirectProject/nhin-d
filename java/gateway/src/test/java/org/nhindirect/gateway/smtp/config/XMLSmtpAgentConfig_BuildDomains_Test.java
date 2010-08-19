package org.nhindirect.gateway.smtp.config;

import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;

import org.nhindirect.gateway.smtp.DomainPostmaster;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentError;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.SmtpAgentSettings;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.gateway.testutils.BaseTestPlan;

import com.google.inject.Injector;

import junit.framework.TestCase;

public class XMLSmtpAgentConfig_BuildDomains_Test extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void performInner() throws Exception 
		{
			SmtpAgentConfig config = new XMLSmtpAgentConfig(TestUtils.getTestConfigFile(getConfigFileName()), null);
			Injector injector = config.getAgentInjector();
			SmtpAgent agent = injector.getInstance(SmtpAgent.class);
			doAssertions(agent);
		}	
	
		protected void doAssertions(SmtpAgent agent) throws Exception
		{
		}
		
		protected abstract String getConfigFileName();
				
	}
	
	public void testValidDomainConfiguration_AssertDomainsAndPostmasters() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "ValidConfig.xml";
			}		
			
			@Override
			protected void doAssertions(SmtpAgent agent) throws Exception
			{
				// check postmasters
				SmtpAgentSettings settings = agent.getSmtpAgentSettings();
				assertNotNull(settings);
				
				assertNotNull(settings.getDomainPostmasters());
				assertEquals(2, settings.getDomainPostmasters().size());
				
				// make sure we hit both domains in the configuration
				boolean cernerConfigured = false;
				boolean secureHealthconfigured = false;
				for (Entry<String, DomainPostmaster> entry : settings.getDomainPostmasters().entrySet())
				{
					assertEquals(entry.getKey(), entry.getValue().getDomain().toUpperCase(Locale.getDefault()));
					if (entry.getKey().equalsIgnoreCase("cerner.com") && 
							entry.getValue().getPostmaster().getAddress().equalsIgnoreCase("postmaster@cerner.com"))
						cernerConfigured = true;
					else if (entry.getKey().equalsIgnoreCase("securehealthemail.com") && 
							entry.getValue().getPostmaster().getAddress().equalsIgnoreCase("postmaster@securehealthemail.com"))
						secureHealthconfigured = true; 
				}
				assertTrue(cernerConfigured);
				assertTrue(secureHealthconfigured);
				
				// check domains on the main agent
				Collection<String>  domains = agent.getAgent().getDomains();
				assertNotNull(domains);
				assertEquals(2, domains.size());
				
				cernerConfigured = false;
				secureHealthconfigured = false;
				for (String domain : domains)
				{
					if (domain.equalsIgnoreCase("cerner.com"))
						cernerConfigured = true;
					else if (domain.equalsIgnoreCase("securehealthemail.com"))
						secureHealthconfigured = true; 
				}
				
				assertTrue(cernerConfigured);
				assertTrue(secureHealthconfigured);
			}
		}.perform();
	}	
	
	public void testMissingDomains_AssertMissingDomainsException() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "MissingDomains.xml";
			}					
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(ex.getError(), SmtpAgentError.MissingDomains);
			}			
		}.perform();
	}
	
	
	public void testEmptyDomains_AssertMissingDomainsException() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "EmptyDomains.xml";
			}		
			
			
			/// C
			protected void doAssertions(SmtpAgent agent) throws Exception
			{
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(ex.getError(), SmtpAgentError.MissingDomains);
			}			
		}.perform();
	}		
	
	public void testEmptyDomainName_AssertMissingDomainNameException() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "EmptyDomainNames.xml";
			}					
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(ex.getError(), SmtpAgentError.MissingDomainName);
			}			
		}.perform();
	}		
	
	public void testMissingDomainName_AssertMissingDomainNameException() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "MissingDomainNames.xml";
			}					
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(ex.getError(), SmtpAgentError.MissingDomainName);
			}			
		}.perform();
	}		
	
	public void testMissingPostmasterName_AssertMissingPostmasterException() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "MissingPostmaster.xml";
			}					
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(ex.getError(), SmtpAgentError.MissingPostmaster);
			}			
		}.perform();
	}		
	
	public void testEmptyPostmasterName_AssertMissingPostmasterException() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "EmptyPostmaster.xml";
			}					
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(ex.getError(), SmtpAgentError.MissingPostmaster);
			}			
		}.perform();
	}			
}
