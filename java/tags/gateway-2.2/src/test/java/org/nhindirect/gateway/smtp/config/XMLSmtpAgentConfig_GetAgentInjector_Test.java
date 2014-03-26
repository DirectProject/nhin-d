package org.nhindirect.gateway.smtp.config;

import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentError;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.stagent.cert.CertCacheFactory;

import com.google.inject.Injector;

import junit.framework.TestCase;

public class XMLSmtpAgentConfig_GetAgentInjector_Test extends TestCase 
{
	
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void performInner() throws Exception 
		{
			CertCacheFactory.getInstance().flushAll();
			
			SmtpAgentConfig config = new XMLSmtpAgentConfig(TestUtils.getTestConfigFile(getConfigFileName()), null);
			Injector injector = config.getAgentInjector();
			doAssertions(injector);
		}	
	
		protected void doAssertions(Injector injector) throws Exception
		{
			assertNotNull(injector);
			assertNotNull(injector.getInstance(SmtpAgent.class));
		}
		
		protected abstract String getConfigFileName();
				
	}
	
	public void testValidAndCompleteXMLCreatesValidInjector() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "ValidConfig.xml";
			}			
		}.perform();
	}	
	
	public void testInvalidXMLFailsInjectorCreation() throws Exception 
	{
		new TestPlan() 
		{
			
			protected String getConfigFileName()
			{
				return "InvalidXMLInstance.xml";
			}		
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				assertTrue(exception instanceof SmtpAgentException);
				SmtpAgentException ex = (SmtpAgentException)exception;
				assertEquals(ex.getError(), SmtpAgentError.InvalidConfigurationFormat);
			}
			
		}.perform();
	}		
}
