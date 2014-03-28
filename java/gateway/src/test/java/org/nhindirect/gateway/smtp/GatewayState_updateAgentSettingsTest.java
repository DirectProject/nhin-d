package org.nhindirect.gateway.smtp;

import java.io.File;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Vector;

import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.nhind.config.Anchor;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.Setting;
import org.nhindirect.gateway.smtp.config.ConfigServiceRunner;
import org.nhindirect.gateway.smtp.config.SmptAgentConfigFactory;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.stagent.MutableAgent;
import org.nhindirect.stagent.trust.TrustAnchorResolver;

import com.google.inject.Injector;


import junit.framework.TestCase;

public class GatewayState_updateAgentSettingsTest extends TestCase
{
	protected byte[] getCertificateFileData(String file) throws Exception
	{
		File fl = new File("src/test/resources/certs/" + file);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	abstract class TestPlan extends BaseTestPlan 
    {
		private ConfigurationServiceProxy proxy;	
		
		protected void setupMocks() 
		{
			
			try
			{
				// create the web service and proxy
				ConfigServiceRunner.startConfigService();
				
				proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
				
				removeTestFiles();
				
				cleanConfig();
				
				addConfiguration();
			}
			catch (Throwable t)
			{
				throw new RuntimeException(t);
			}
		}
		
	       protected void cleanConfig() throws Exception
	        {
	     	        	
	        	// clean domains
	        	int domainCount = proxy.getDomainCount();
	        	Domain[] doms = proxy.listDomains(null, domainCount);
	        	if (doms != null)
	        		for (Domain dom : doms)
	        		{
	                	// clean anchors
	                	proxy.removeAnchorsForOwner(dom.getDomainName());
	 
	        			proxy.removeDomain(dom.getDomainName());
	        		}        
	        	
	        	// clean certificates
	        	Certificate[] certs = proxy.listCertificates(0, 0x8FFFF, null);
	        	if (certs != null && certs.length > 0)
	        	{
	        		long[] ids = new long[certs.length];
	        		for (int i = 0; i < certs.length; ++i)
	        			ids[i] = certs[i].getId();
	        		
	        		proxy.removeCertificates(ids) ;
	        	}
	        	
	        	// clean settings
	        	Setting[] settings = proxy.getAllSettings();
	        	if (settings != null)
	        		for (Setting setting : settings)
	        			proxy.deleteSetting(new String[] {setting.getName()});
	        }
		
        protected void addConfiguration() throws Exception
        {
        	addDomains();
        	
        	addTrustAnchors();
        	
        }
		
        protected void removeTestFiles()
        {
            removeFile("LDAPPrivateCertStore");
            removeFile("LDAPTrustAnchorStore");
            removeFile("LdapCacheStore");
            removeFile("DNSCacheStore");
            removeFile("WSPrivCacheStore");
            removeFile("PublicStoreKeyFile");
            removeFile("WSPublicCacheStore");
            removeFile("PublicLDAPCacheStore");
        }
        
        protected void addDomains() throws Exception
        {
        	Domain dom = new Domain();
        	dom.setDomainName("cerner.com");
        	dom.setPostMasterEmail("postmaster@cerner.com");
        	proxy.addDomain(dom);
        	
        	dom = new Domain();
        	dom.setDomainName("securehealthemail.com");
        	dom.setPostMasterEmail("postmaster@securehealthemail.com");
        	proxy.addDomain(dom);
        }
        
        protected void addTrustAnchors() throws Exception
        {
        	Vector<Anchor> vec = new Vector<Anchor>();
        	
        	Anchor anchor = new Anchor();
        	anchor.setData(getCertificateFileData("cacert.der"));
        	anchor.setOwner("cerner.com");
        	anchor.setIncoming(true);
        	anchor.setOutgoing(true);
        	vec.add(anchor);
        	
        	anchor = new Anchor();
        	anchor.setData(getCertificateFileData("cacert.der"));
        	anchor.setOwner("securehealthemail.com");
        	anchor.setIncoming(true);
        	anchor.setOutgoing(true);
        	vec.add(anchor);
        	
        	proxy.addAnchor(vec.toArray(new Anchor[vec.size()]));        	
        }
        
	    public void tearDown() throws Exception
	    {
        	GatewayState stateInstance = GatewayState.getInstance();
        	stateInstance.setSettingsUpdateInterval(300);
        	
        	if (stateInstance.isAgentSettingManagerRunning())
        		stateInstance.stopAgentSettingsManager();
        	
	    	removeTestFiles();
	        
	    }
	    
        protected void removeFile(String filename){
            File delete = new File(filename);
            delete.delete();
        }
        
        @Override
        protected void performInner() throws Exception 
        {     
        	GatewayState stateInstance = GatewayState.getInstance();
        	
        	if (stateInstance.isAgentSettingManagerRunning())
        		stateInstance.stopAgentSettingsManager();
                      
            SmtpAgentConfig config = createSmtpAgentConfig();
            
            Injector injector = config.getAgentInjector();
            SmtpAgent agent = injector.getInstance(SmtpAgent.class);
            
            stateInstance.setSmptAgentConfig(config);
            stateInstance.setSmtpAgent(agent);
            stateInstance.setSettingsUpdateInterval(1);  // every one second
            stateInstance.startAgentSettingsManager();
            
            doAssertionsOriginalAgentSettings(agent);

            // change the settings
        	Domain dom = new Domain();
        	dom.setDomainName("cernerdirect.com");
        	dom.setPostMasterEmail("postmaster@cernerdirect.com");
        	proxy.addDomain(dom);
        	
        	Vector<Anchor> vec = new Vector<Anchor>();
        	
        	Anchor anchor = new Anchor();
        	anchor.setData(getCertificateFileData("cacert.der"));
        	anchor.setOwner("cernerdirect.com");
        	anchor.setIncoming(true);
        	anchor.setOutgoing(true);
        	vec.add(anchor);
        	
        	
        	proxy.addAnchor(vec.toArray(new Anchor[vec.size()]));                	
            
        	// wait 5 seconds to let the service get updated
        	Thread.sleep(5000);
        	
        	
        	doAssertionsNewAgentSettings(agent);
        	
        }  
        
        protected SmtpAgentConfig createSmtpAgentConfig() throws Exception
        {        	
        	SmtpAgentConfig config =  SmptAgentConfigFactory.createSmtpAgentConfig(new URL(ConfigServiceRunner.getConfigServiceURL()), null, null);
            return config;
        }
        
        protected abstract void doAssertionsOriginalAgentSettings(SmtpAgent agent) throws Exception;
        
        protected abstract void doAssertionsNewAgentSettings(SmtpAgent agent) throws Exception;
    }
	
	public void testNewDomainListSettings() throws Exception 
    {
        new TestPlan() 
        {     
            protected void doAssertionsOriginalAgentSettings(SmtpAgent agent) throws Exception
            {
            	assertEquals(2, agent.getAgent().getDomains().size());
            	
            	MutableAgent mutalbeAgent = (MutableAgent)agent.getAgent();
            	Collection<X509Certificate> certs = 
            			mutalbeAgent.getTrustAnchors().getIncomingAnchors().getCertificates(new InternetAddress("cernerdirect.com"));
            	
            	assertEquals(0, certs.size());
            }
            
            protected void doAssertionsNewAgentSettings(SmtpAgent agent) throws Exception
            {
            	assertEquals(3, agent.getAgent().getDomains().size());
            	
            	MutableAgent mutalbeAgent = (MutableAgent)agent.getAgent();
            	TrustAnchorResolver resolver = mutalbeAgent.getTrustAnchors();
            	Collection<X509Certificate> certs = 
            			resolver.getIncomingAnchors().getCertificates(new InternetAddress("joe@cernerdirect.com"));
            	
            	assertEquals(1, certs.size());
            }
        }.perform();
    }
}
