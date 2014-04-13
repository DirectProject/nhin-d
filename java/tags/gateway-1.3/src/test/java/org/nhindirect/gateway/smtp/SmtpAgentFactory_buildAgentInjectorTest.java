package org.nhindirect.gateway.smtp;

import java.net.URL;
import java.util.Arrays;

import org.nhindirect.stagent.MockNHINDAgent;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.provider.MockSmtpAgentConfigProvider;

import com.google.inject.Injector;

import junit.framework.TestCase;

public class SmtpAgentFactory_buildAgentInjectorTest extends TestCase
{
	public void testBuildAgentInjector_customConfigModule_assertInjector() throws Exception
	{
		Injector injector = SmtpAgentFactory.buildAgentInjector(new URL("http://doesntmatter"), new MockSmtpAgentConfigProvider(Arrays.asList("testdomain.com")), 
				null);
		
		assertNotNull(injector);
		
		// try to get an instance of the configuration object
		NHINDAgent agent = injector.getInstance(NHINDAgent.class);
		assertNotNull(agent);
		assertTrue(agent instanceof MockNHINDAgent);
	}
}
