package org.nhindirect.gateway.smtp.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.commons.fileupload.util.Streams;
import org.nhindirect.gateway.smtp.DomainPostmaster;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentError;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.SmtpAgentSettings;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.stagent.module.TrustAnchorModule;
import org.nhindirect.stagent.trust.provider.UniformTrustAnchorResolverProvider;
import org.w3c.dom.Element;

import com.google.inject.Injector;

public class XMLSmtpAgentConfig_BuildDomains_Test extends TestCase
{
	abstract class TestPlan extends BaseTestPlan 
	{
		@Override
		protected void performInner() throws Exception 
		{
			SmtpAgentConfig config = createSmtpAgentConfig();
			Injector injector = config.getAgentInjector();
			SmtpAgent agent = injector.getInstance(SmtpAgent.class);
			doAssertions(agent);
		}	
	
		protected void doAssertions(SmtpAgent agent) throws Exception
		{
		}
		
		protected SmtpAgentConfig createSmtpAgentConfig() {
		    SmtpAgentConfig config = new XMLSmtpAgentConfig(TestUtils.getTestConfigFile(getConfigFileName()), null){
                @Override
                protected void buildTrustAnchorResolver(Element anchorStoreNode, Map<String, Collection<String>> incomingAnchorHolder,
                       Map<String, Collection<String>> outgoingAnchorHolder) {
                    try {
                        this.certAnchorModule = TrustAnchorModule.create(new UniformTrustAnchorResolverProvider(createX509Certificates()));
                    } 
                    catch (UnsupportedEncodingException e) {}
                    catch (CertificateException e) {}
                    catch (IOException e) {}
               }
		    };
		    return config;
		}
		
		protected Collection<X509Certificate> createX509Certificates() throws CertificateException, IOException{
		    InputStream certificate = this.getClass().getResourceAsStream( "/x509Certificate.txt" );
		    Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
		    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		    String certificateString = Streams.asString(certificate).replaceAll("\r", "");		    
		    InputStream is = new ByteArrayInputStream(certificateString.getBytes("ASCII"));
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);            
            certs.add(cert);
		    return certs;
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
