package org.nhindirect.gateway.smtp.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapSchema;
import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.shared.ldap.ldif.Entry;
import org.nhindirect.gateway.smtp.DomainPostmaster;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentSettings;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.ldap.PrivkeySchema;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.MutableAgent;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.trust.TrustAnchorResolver;

import com.google.inject.Injector;

/**
 * Testcase using an embedded Apache Directory Server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */

public class XMLSmtpAgentConfigFunctional_Test extends AbstractServerTest
{	
	/**
     * Initialize the server.
     */
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception
	{
		CertCacheFactory.getInstance().flushAll();
		
	    MutablePartitionConfiguration pcfg = new MutablePartitionConfiguration();
	    pcfg.setName( "lookupTest" );
	    pcfg.setSuffix( "cn=lookupTest" );

        // Create some indices
        Set<String> indexedAttrs = new HashSet<String>();
        indexedAttrs.add( "objectClass" );
        indexedAttrs.add( "cn" );
        pcfg.setIndexedAttributes( indexedAttrs );
	 
        // Create a first entry associated to the partition
        Attributes attrs = new BasicAttributes( true );

        // First, the objectClass attribute
        Attribute attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attrs.put( attr );
        
        // Associate this entry to the partition
        pcfg.setContextEntry( attrs );

        // As we can create more than one partition, we must store
        // each created partition in a Set before initialization
        Set<MutablePartitionConfiguration> pcfgs = new HashSet<MutablePartitionConfiguration>();
        pcfgs.add( pcfg );

        configuration.setContextPartitionConfigurations( pcfgs );
        
		this.configuration.setWorkingDirectory(new File("LDAP-TEST"));
		
        // add the private key schema
        ///
        Set<AbstractBootstrapSchema> schemas = configuration.getBootstrapSchemas();
        schemas.add( new PrivkeySchema() );

        configuration.setBootstrapSchemas(schemas);
		
		
		super.setUp();
		
		// import the ldif file
		InputStream stream = TestUtils.class.getResourceAsStream("/ldifs/privCertsOnly.ldif");
		
		if (stream == null)
			throw new IOException("Failed to load ldif file");
		
		importLdif(stream);
	}
	
	abstract class TestPlan extends BaseTestPlan 
    {
		@SuppressWarnings("unused")
        @Override
        protected void performInner() throws Exception 
        {     
            removeTestFiles();
            addCertificates();            
            SmtpAgentConfig config = createSmtpAgentConfig();
            Injector injector = config.getAgentInjector();
            SmtpAgent agent = injector.getInstance(SmtpAgent.class);
            
            TrustAnchorResolver trustResolver = ((MutableAgent)agent.getAgent()).getTrustAnchors();
            CertificateResolver incomingResolver = trustResolver.getIncomingAnchors();
            CertificateResolver outgoingResolver = trustResolver.getOutgoingAnchors();
            
            
            doAssertions(agent);
            removeTestFiles();
        }   
    
		@SuppressWarnings("deprecation")
        protected void doAssertions(SmtpAgent agent) throws Exception
        {
            
         // check postmasters
            SmtpAgentSettings settings = agent.getSmtpAgentSettings();
            assertNotNull(settings);
            
            assertNotNull(settings.getDomainPostmasters());
            assertEquals(createNumberOfDomains(), settings.getDomainPostmasters().size());
            
            // make sure we hit both domains in the configuration
            assertDomainPostmastersConfig(settings);
            
            // check domains on the main agent 
            assertDomainConfig(agent);            
            
            DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            TrustAnchorResolver trustAnchorResolver = nAgent.getTrustAnchors();
            CertificateResolver publicCertificateResolver = nAgent.getPublicCertResolver();
            CertificateResolver privateCertificateResolver = nAgent.getPrivateCertResolver();
            assertNotNull(trustAnchorResolver);
            assertNotNull(publicCertificateResolver);
            assertNotNull(privateCertificateResolver);               
            assertAnchors(trustAnchorResolver.getIncomingAnchors());                
        }
        
        protected SmtpAgentConfig createSmtpAgentConfig() throws Exception
        {
        	// get the configuration XML file as a resource
        	InputStream str = this.getClass().getClassLoader().getResourceAsStream("configFiles/" + getConfigFileName());
        	if (str == null)
        		throw new IOException("Config file configFiles/" + getConfigFileName() + " could not be loaded.");
        	
        	// replace all instances of the hard coded local LDAP server to the port of the actual server
        	String xmlConfig = IOUtils.toString(str);
        	String replacePort = "localhost:" + configuration.getLdapPort();
        	xmlConfig = xmlConfig.replaceAll("localhost:1024", replacePort);
        	
        	// write the configuration to a new file
        	File newConfigFile = new File("./tmp/" + getConfigFileName());
        	FileUtils.forceDeleteOnExit(newConfigFile);
        	FileUtils.writeStringToFile(newConfigFile, xmlConfig);
        	
        	// load the new file into the SmtpAgentConfig
        	String configFile = newConfigFile.getAbsolutePath();
        	
        	SmtpAgentConfig config = new XMLSmtpAgentConfig(configFile, null);
            return config;
        }
        
        protected abstract String getConfigFileName();
        
        protected abstract void addCertificates() throws NamingException;
        
        protected int createNumberOfDomains(){
            return 0;
        }
        
        protected int createNumberOfCerts(){
            return 0;
        }
        
        protected void assertAnchors(CertificateResolver incomingAnchors) throws AddressException{
            assertNotNull(incomingAnchors);  
            assertEquals(createNumberOfCerts(), incomingAnchors.getCertificates(new InternetAddress("gm2552@cerner.com")).size());
            assertEquals(createNumberOfCerts()-1, incomingAnchors.getCertificates(new InternetAddress("jp018858@securehealthemail.com")).size());
        }
        
        protected void assertDomainPostmastersConfig(SmtpAgentSettings settings){
            boolean cernerConfigured = false;
            boolean secureHealthconfigured = false;
            for (java.util.Map.Entry<String, DomainPostmaster> entry : settings.getDomainPostmasters().entrySet())
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
        }
        
        protected void assertDomainConfig(SmtpAgent agent){
            Collection<String>  domains = agent.getAgent().getDomains();
            assertNotNull(domains);
            boolean cernerConfigured = false;
            boolean secureHealthconfigured = false;
            assertEquals(createNumberOfDomains(), domains.size());            
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
    }
	
	abstract class MultiDomainTestPlan extends TestPlan {
	    @Override
        protected void assertAnchors(CertificateResolver incomingAnchors) throws AddressException{
            assertNotNull(incomingAnchors);  
            assertEquals(createNumberOfCerts(), incomingAnchors.getCertificates(new InternetAddress("gm2552@cerner.com")).size());
            assertEquals(createNumberOfCerts()-1, incomingAnchors.getCertificates(new InternetAddress("jp018858@securehealthemail.com")).size());
        }
	    @Override
        protected void assertDomainPostmastersConfig(SmtpAgentSettings settings){
            boolean cernerConfigured = false;
            boolean secureHealthconfigured = false;
            for (java.util.Map.Entry<String, DomainPostmaster> entry : settings.getDomainPostmasters().entrySet())
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
        }
	    @Override
        protected void assertDomainConfig(SmtpAgent agent){
            Collection<String>  domains = agent.getAgent().getDomains();
            assertNotNull(domains);
            boolean cernerConfigured = false;
            boolean secureHealthconfigured = false;
            assertEquals(createNumberOfDomains(), domains.size());            
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
	}	
	
	
	
	
	public void testValidMultidomainLdapConfigurationForX509Certificates() throws Exception 
    {
        new MultiDomainTestPlan() 
        {            
            protected String getConfigFileName()
            {
                return "ValidMultiDomainLdapConfig.xml";
            } 
            
            @Override
            protected int createNumberOfDomains(){
                return 2;
            }
            @Override
            protected int createNumberOfCerts(){
                return 2;
            }

            @Override
            protected void addCertificates() throws NamingException {
                addCertificatesToLdap(new String[]{"/certs/cacert.der"}, "gm2552@cerner.com"); 
                addCertificatesToLdap(new String[]{"/certs/cacert.der"}, "gm25@cerner.com"); 
                addCertificatesToLdap(new String[]{"/certs/cacert.der"}, "jp018858@securehealthemail.com"); 
            }
        }.perform();
    }
	
	public void testValidMultidomainLdapConfigurationForPKCS12() throws Exception 
	    {
	        new MultiDomainTestPlan() 
	        {
	            
	            protected String getConfigFileName()
	            {
	                return "ValidMultiDomainLdapConfigPKCS12.xml";
	            } 
	            
	            @Override
	            protected int createNumberOfDomains(){
	                return 2;
	            }
	            @Override
	            protected int createNumberOfCerts(){
	                return 2;
	            }

	            @Override
	            protected void addCertificates() throws NamingException {
	                addCertificatesToLdap(new String[]{"/certs/gm2552encrypted.p12"}, "gm2552@cerner.com"); 
	                addCertificatesToLdap(new String[]{"/certs/gm2552encrypted.p12"}, "gm25@cerner.com"); 
	                addCertificatesToLdap(new String[]{"/certs/gm2552encrypted.p12"}, "jp018858@securehealthemail.com"); 
	            }
	        }.perform();
	    }
	
	abstract class UniformTestPlan extends TestPlan {
	    @Override
        protected void assertAnchors(CertificateResolver incomingAnchors) throws AddressException{
            assertNotNull(incomingAnchors);  
            assertEquals(createNumberOfCerts(), incomingAnchors.getCertificates(new InternetAddress("asd")).size());       
        }
	    
        @Override
        protected void assertDomainPostmastersConfig(SmtpAgentSettings settings){         
            boolean cernerConfigured = false;
            for (java.util.Map.Entry<String, DomainPostmaster> entry : settings.getDomainPostmasters().entrySet())
            {
                assertEquals(entry.getKey(), entry.getValue().getDomain().toUpperCase(Locale.getDefault()));
                if (entry.getKey().equalsIgnoreCase("cerner.com") && 
                        entry.getValue().getPostmaster().getAddress().equalsIgnoreCase("postmaster@cerner.com")){
                    cernerConfigured = true;
                }
            }                        
               
            assertTrue(cernerConfigured);  
        }
        @Override
        protected void assertDomainConfig(SmtpAgent agent){
            Collection<String>  domains = agent.getAgent().getDomains();
            assertNotNull(domains);
            boolean cernerConfigured = false;
            cernerConfigured = false;
            for (String domain : domains)
            {
                if (domain.equalsIgnoreCase("cerner.com"))
                    cernerConfigured = true;                   
            }                
            assertTrue(cernerConfigured);    
        } 
    }   
	
	public void testValidUniformLdapConfigurationForX509Certificates() throws Exception 
    {
        new UniformTestPlan() 
        {
            
            protected String getConfigFileName()
            {
                return "ValidUniformLdapConfig.xml";
            } 
            
            @Override
            protected int createNumberOfDomains(){
                return 1;
            }
            @Override
            protected int createNumberOfCerts(){
                return 3;
            }

            @Override
            protected void addCertificates() throws NamingException {
            	
            	CertCacheFactory.getInstance().flushAll();
                addCertificatesToLdap(new String[]{"/certs/cacert.der", "/certs/bob.der"}, "gm2552@cerner.com"); 
                addCertificatesToLdap(new String[]{"/certs/cacert.der"}, "gm25@cerner.com"); 
            }
        }.perform();
    }
    
    public void testValidUniformLdapConfigurationForPKCS12() throws Exception 
    {
        new UniformTestPlan() 
        {            
            protected String getConfigFileName()
            {
                return "ValidUniformLdapConfigPKCS12.xml";
            }       
            
            
            
            @Override
            protected int createNumberOfDomains(){
                return 1;
            }
            @Override
            protected int createNumberOfCerts(){
                return 2;
            }

            @Override
            protected void addCertificates() throws NamingException {
                addCertificatesToLdap(new String[]{"/certs/gm2552encrypted.p12"}, "gm2552@cerner.com"); 
                addCertificatesToLdap(new String[]{"/certs/gm2552encrypted.p12"}, "gm25@cerner.com"); 
            }
        }.perform();
    }
    
    public void testValidUniformLdapConfigurationForPKCS12AndMultiPublicCerts() throws Exception 
    {
        new UniformTestPlan() 
        {
            
            protected String getConfigFileName()
            {
                return "ValidUniformLdapMultiPublicResConfig.xml";
            } 
            
            @Override
            protected int createNumberOfDomains(){
                return 1;
            }
            @Override
            protected int createNumberOfCerts(){
                return 3;
            }

            @Override
            protected void addCertificates() throws NamingException {
                addCertificatesToLdap(new String[]{"/certs/cacert.der", "/certs/bob.der"}, "gm2552@cerner.com"); 
                addCertificatesToLdap(new String[]{"/certs/cacert.der"}, "gm25@cerner.com"); 
            }
            
            @Override
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
                
             // check postmasters
                SmtpAgentSettings settings = agent.getSmtpAgentSettings();
                assertNotNull(settings);
                
                assertNotNull(settings.getDomainPostmasters());
                assertEquals(createNumberOfDomains(), settings.getDomainPostmasters().size());
                
                // make sure we hit both domains in the configuration
                assertDomainPostmastersConfig(settings);
                
                // check domains on the main agent 
                assertDomainConfig(agent);            
                
                DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
                TrustAnchorResolver trustAnchorResolver = nAgent.getTrustAnchors();
                Collection<CertificateResolver> publicCertificateResolvers = nAgent.getPublicCertResolvers();
                assertNotNull(trustAnchorResolver);
                assertNotNull(publicCertificateResolvers);
                assertEquals(2, publicCertificateResolvers.size());
     
            }
            
        }.perform();
    }
    
    protected void removeTestFiles(){
        removeFile("LDAPPrivateCertStore");
        removeFile("LDAPTrustAnchorStore");
        removeFile("LdapCacheStore");
        removeFile("DNSCacheStore");
    }
    
    protected void removeFile(String filename){
        File delete = new File(filename);
        delete.delete();
    }
    
    protected void addCertificatesToLdap(String[] filename, String email) throws NamingException {
        Entry entry = new Entry();
        entry.addAttribute("objectClass", "organizationalUnit");
        entry.addAttribute("objectClass", "top");
        entry.addAttribute("objectClass", "userPrivKey");
        entry.addAttribute("email", email);
        
        File fl = new File("testfile");
        int idx = fl.getAbsolutePath().lastIndexOf("testfile");
        String path = fl.getAbsolutePath().substring(0, idx);
        
        for(int i=0;i<filename.length;i++) {
            byte[] buffer = new byte[(int) new File(path + "src/test/resources/"+filename[i]).length()+100];
            try {
                InputStream stream = TestUtils.class.getResourceAsStream(filename[i]);
                stream.read(buffer);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Base64 base64 = new Base64();
            String certificateValue =  new String(base64.encode(buffer));
            entry.addAttribute("privKeyStore", certificateValue);
        }      
        String ou = email.substring(0,email.indexOf("@"));      
        entry.addAttribute("ou", ou);
        rootDSE.createSubcontext("ou="+ou+", ou=privKeys, ou=cerner, ou=com, cn=lookupTest", entry.getAttributes());
    }


    /**
     * Shutdown the server.
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
