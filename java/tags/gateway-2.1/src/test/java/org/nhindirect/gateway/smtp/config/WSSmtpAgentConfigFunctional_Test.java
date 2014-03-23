package org.nhindirect.gateway.smtp.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import org.nhindirect.stagent.cert.impl.util.Lookup;
import org.nhindirect.stagent.cert.impl.util.LookupFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.mail.internet.InternetAddress;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapSchema;
import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.shared.ldap.ldif.Entry;
import org.nhind.config.Anchor;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.Setting;
import org.nhind.config.TrustBundle;
import org.nhindirect.gateway.smtp.DomainPostmaster;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentSettings;
import org.nhindirect.gateway.smtp.config.cert.impl.ConfigServiceCertificateStore;
import org.nhindirect.gateway.testutils.BaseTestPlan;
import org.nhindirect.gateway.testutils.TestUtils;
import org.nhindirect.ldap.PrivkeySchema;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.DNSCertificateStore;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.TrustAnchorCertificateStore;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;

import com.google.inject.Injector;

public class WSSmtpAgentConfigFunctional_Test extends AbstractServerTest 
{
	private static final String certBasePath = "src/test/resources/certs/";
	
	private ConfigurationServiceProxy proxy;	
	private Lookup mockLookup;
	
	protected String filePrefix;
	
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	/**
     * Initialize the servers- LDAP and HTTP.
     */
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception
	{
		// check for Windows... it doens't like file://<drive>... turns it into FTP
		File file = new File("./src/test/resources/bundles/testBundle.p7b");
		if (file.getAbsolutePath().contains(":/"))
			filePrefix = "file:///";
		else
			filePrefix = "file:///";
		
		CertCacheFactory.getInstance().flushAll();
		
		/*
		 * Setup the LDAP Server
		 */
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

        //
        //
        //
        // add the lookupTestPublic
        //
        //
	    pcfg = new MutablePartitionConfiguration();
	    pcfg.setName( "lookupTestPublic" );
	    pcfg.setSuffix( "cn=lookupTestPublic" );

        // Create some indices
        indexedAttrs = new HashSet<String>();
        indexedAttrs.add( "objectClass" );
        indexedAttrs.add( "cn" );
        pcfg.setIndexedAttributes( indexedAttrs );
	 
        // Create a first entry associated to the partition
        attrs = new BasicAttributes( true );

        // First, the objectClass attribute
        attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attrs.put( attr );
        
        // Associate this entry to the partition
        pcfg.setContextEntry( attrs );

        // As we can create more than one partition, we must store
        // each created partition in a Set before initialization
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
		
		// setup the mock DNS SRV adapter
		mockLookup = mock(Lookup.class);
		LookupFactory.getFactory().addOverrideImplementation(mockLookup);
		SRVRecord srvRecord = new SRVRecord(new Name("_ldap._tcp.example.com."), DClass.IN, 3600, 0, 1, port, new Name("localhost."));
		when(mockLookup.run()).thenReturn(new Record[] {srvRecord});
		
		// create the web service and proxy
		ConfigServiceRunner.startConfigService();
		
		proxy = new ConfigurationServiceProxy(ConfigServiceRunner.getConfigServiceURL());
	}
	
    /**
     * Shutdown the servers.
     */
    public void tearDown() throws Exception
    {

    	LookupFactory.getFactory().removeOverrideImplementation();
        super.tearDown();  // will automatically take down the LDAP server
        
    }
    
	abstract class TestPlan extends BaseTestPlan 
    {
        @Override
        protected void performInner() throws Exception 
        {     
            removeTestFiles();
            cleanConfig();
            addConfiguration();
                      
            SmtpAgentConfig config = createSmtpAgentConfig();
            
            Injector injector = config.getAgentInjector();
            SmtpAgent agent = injector.getInstance(SmtpAgent.class);
            doAssertions(agent);
            removeTestFiles();
        }  
        
        protected void addPublicCertificates() throws Exception
        {
        	// default uses DNS
        }
        
        protected abstract void addPrivateCertificates() throws Exception;
        
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
        	
        	// clean bundles
    		TrustBundle[] bundles = proxy.getTrustBundles(false);
    		
    		if (bundles != null)
    			for (TrustBundle bundle : bundles)
    			proxy.deleteTrustBundles(new Long[] {bundle.getId()});
    		
    		bundles = proxy.getTrustBundles(true);
    		assertNull(bundles);
        }

        
        protected void addConfiguration() throws Exception
        {
        	addDomains();
        	
        	addTrustAnchors();
        	
        	addPublicCertificates();
        	
        	addPrivateCertificates();  
        	
        	addSettings();
        }
        
        protected void addSettings() throws Exception
        {
        	// just use default settings
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
        
        protected void removeFile(String filename){
            File delete = new File(filename);
            delete.delete();
        }
        
        protected SmtpAgentConfig createSmtpAgentConfig() throws Exception
        {        	
        	SmtpAgentConfig config = new WSSmtpAgentConfig(new URL(ConfigServiceRunner.getConfigServiceURL()), null);
            return config;
        }
        
        protected void doAssertions(SmtpAgent agent) throws Exception
        {
        	assertNotNull(agent);
        	
        	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
        	TrustAnchorResolver trustResolver = nAgent.getTrustAnchors();
            assertNotNull(trustResolver);             
            assertAnchors(trustResolver.getIncomingAnchors());  
            SmtpAgentSettings settings = agent.getSmtpAgentSettings();
            assertNotNull(settings);
            
        }
        
        protected void assertAnchors(CertificateResolver anchors) throws Exception
        {
        	
        }
        
        protected int createNumberOfDomains(){
            return 0;
        }
        
        protected int createNumberOfCerts(){
            return 0;
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
        protected void assertAnchors(CertificateResolver anchors) throws Exception{
	    	assertNotNull(anchors);
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
	
	public void testDefaultConfigurationNoSettings() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

        	@Override
            protected void addPublicCertificates() throws Exception
            {
    			Entry entry = new Entry();
    			entry.addAttribute("objectClass", "organizationalUnit");
    			entry.addAttribute("objectClass", "top");
    			entry.addAttribute("objectClass", "iNetOrgPerson");
    			entry.addAttribute("mail", "gm2552@cerner.com");

                ByteArrayInputStream bais = new ByteArrayInputStream(loadCertificateData("cacert.der"));

                X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);    

    			entry.addAttribute("userCertificate", cert.getEncoded());
    			entry.addAttribute("ou", "gm2552");
    			entry.addAttribute("cn", "Greg Meyer");
    			entry.addAttribute("sn", "");
    			
    			rootDSE.createSubcontext("ou=gm2552, cn=lookupTestPublic", entry.getAttributes());
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception {
            	// we can add more than one
            	addCertificatesToConfig("cacert.der", null, "gm2552@cerner.com");
            	addCertificatesToConfig("cacert.der", null, "gm2552@cerner.com"); 
            	
            	// singleton
            	addCertificatesToConfig("cacert.der", null, "test@cerner.com");
            	
            	// 3 certs at domain level
            	addCertificatesToConfig("cacert.der", null, "cerner.com");
            	addCertificatesToConfig("cacert.der", null, "cerner.com");
            	addCertificatesToConfig("cacert.der", null, "cerner.com");
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver privResl = nAgent.getPrivateCertResolver();
            	assertNotNull(privResl);
            	Collection<X509Certificate> certs = privResl.getCertificates(new InternetAddress("gm2552@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(2, certs.size());
            	assertTrue(privResl instanceof ConfigServiceCertificateStore);
            	
            	// do it again to test the cache
            	certs = privResl.getCertificates(new InternetAddress("gm2552@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(2, certs.size());

            	// test singleton
            	certs = privResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	
            	// again for cache
            	certs = privResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	
            	
            	// test unknown user so fall back to domain level
            	certs = privResl.getCertificates(new InternetAddress("bogus@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(3, certs.size());
            	
            	// again for cache
            	certs= privResl.getCertificates(new InternetAddress("bogus@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(3, certs.size());     
            	
            	// test for domain only
            	certs = privResl.getCertificates(new InternetAddress("cerner.com"));
            	assertNotNull(certs);
            	assertEquals(3, certs.size());
            	
            	// again for cache
            	certs = privResl.getCertificates(new InternetAddress("cerner.com"));
            	assertNotNull(certs);
            	assertEquals(3, certs.size());      
            	
            	// assert we have the proper ldap resolvers
            	Collection<CertificateResolver> resolvers = nAgent.getPublicCertResolvers();
            	assertNotNull(resolvers);
            	assertEquals(2, resolvers.size());
            	Iterator<CertificateResolver> iter = resolvers.iterator();
            	
 
            	CertificateResolver ldapPublicStore;
            	assertTrue(iter.next() instanceof DNSCertificateStore);
            	assertTrue((ldapPublicStore = iter.next()) instanceof LDAPCertificateStore);
            	
               	// test public ldap
            	certs = ldapPublicStore.getCertificates(new InternetAddress("gm2552@cerner.com"));
              	assertNotNull(certs);
            	assertEquals(1, certs.size());  
            }
        }.perform();
    }	
	
	public void testConfigurationPrivateLDAPStore() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("PrivateStoreType", "LDAP");
            	proxy.addSetting("PrivateStoreLDAPUrl", "ldap://localhost:" + configuration.getLdapPort());
            	proxy.addSetting("PrivateStoreLDAPSearchBase", "cn=lookupTest");
            	proxy.addSetting("PrivateStoreLDAPSearchAttr", "email");
            	proxy.addSetting("PrivateStoreLDAPCertAttr", "privKeyStore");
            	proxy.addSetting("PrivateStoreLDAPCertFormat", "X509");
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
                addCertificatesToLdap(new String[]{"/cacert.der"}, "gm2552@cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "gm25@cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "jp018858@securehealthemail.com"); 
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver privResl = nAgent.getPrivateCertResolver();
            	assertNotNull(privResl);
            	Collection<X509Certificate> certs = privResl.getCertificates(new InternetAddress("gm2552@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	assertTrue(privResl instanceof LDAPCertificateStore);
            }
        }.perform();
    }	
	
	public void testConfigurationLDAPTrustAnchors() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("AnchorStoreType", "LDAP");
            	proxy.addSetting("AnchorResolverType", "multidomain");
            	proxy.addSetting("TrustAnchorLDAPUrl", "ldap://localhost:" + configuration.getLdapPort());
            	proxy.addSetting("TrustAnchorLDAPSearchBase", "cn=lookupTest");
            	proxy.addSetting("TrustAnchorLDAPSearchAttr", "email");
            	proxy.addSetting("TrustAnchorLDAPCertAttr", "privKeyStore");
            	proxy.addSetting("TrustAnchorLDAPCertFormat", "X509");
            	
            	
            	proxy.addSetting("cerner.comIncomingAnchorAliases", "cerner.com");
            	proxy.addSetting("securehealthemail.comIncomingAnchorAliases", "securehealthemail.com");
            	
            	proxy.addSetting("cerner.comOutgoingAnchorAliases", "cerner.com");
            	proxy.addSetting("securehealthemail.comOutgoingAnchorAliases", "securehealthemail.com");
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
                addCertificatesToLdap(new String[]{"/cacert.der"}, "cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "securehealthemail.com"); 
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver privResl = nAgent.getPrivateCertResolver();
            	assertTrue(privResl instanceof ConfigServiceCertificateStore);
            	
            	
            	CertificateResolver trustResl = nAgent.getTrustAnchors().getIncomingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the incoming trust anchor for cerner.com
            	Collection<X509Certificate> anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the incoming trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());       
            	
            	
            	
            	trustResl = nAgent.getTrustAnchors().getOutgoingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the outgoing trust anchor for cerner.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the outgoing trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());                	
            	
            }
        }.perform();
    }		
	
	public void testConfigurationPrivateLDAPStoreAndDNSPublicStore() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("PrivateStoreType", "LDAP");
            	proxy.addSetting("PrivateStoreLDAPUrl", "ldap://localhost:" + configuration.getLdapPort());
            	proxy.addSetting("PrivateStoreLDAPSearchBase", "cn=lookupTest");
            	proxy.addSetting("PrivateStoreLDAPSearchAttr", "email");
            	proxy.addSetting("PrivateStoreLDAPCertAttr", "privKeyStore");
            	proxy.addSetting("PrivateStoreLDAPCertFormat", "X509");
            	proxy.addSetting("PublicStoreType", "DNS");
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
                addCertificatesToLdap(new String[]{"/cacert.der"}, "gm2552@cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "gm25@cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "jp018858@securehealthemail.com"); 
            }
            
        	@SuppressWarnings("deprecation")
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver privResl = nAgent.getPrivateCertResolver();
            	assertNotNull(privResl);
            	Collection<X509Certificate> certs = privResl.getCertificates(new InternetAddress("gm2552@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	assertTrue(privResl instanceof LDAPCertificateStore);
            	
            	
            	CertificateResolver publicResl = nAgent.getPublicCertResolver();
            	assertTrue(publicResl instanceof DNSCertificateStore);
            }
        }.perform();
    }	
	
	public void testConfigurationPrivateLDAPStoreAndMultiplePublicStore() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("PrivateStoreType", "LDAP");
            	proxy.addSetting("PrivateStoreLDAPUrl", "ldap://localhost:" + configuration.getLdapPort());
            	proxy.addSetting("PrivateStoreLDAPSearchBase", "cn=lookupTest");
            	proxy.addSetting("PrivateStoreLDAPSearchAttr", "email");
            	proxy.addSetting("PrivateStoreLDAPCertAttr", "privKeyStore");
            	proxy.addSetting("PrivateStoreLDAPCertFormat", "X509");
            	proxy.addSetting("PublicStoreType", "Keystore,DNS,WS");
            }
        	

            
            @Override
            protected void addPrivateCertificates() throws Exception
            {
                addCertificatesToLdap(new String[]{"/cacert.der"}, "gm2552@cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "gm25@cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "jp018858@securehealthemail.com"); 
                
                /*
                 * will test these with the public cert resolver
                 */
            	addCertificatesToConfig("cacert.der", null, "cerner.com");
            	addCertificatesToConfig("cacert.der", null, "cerner.com");
            	addCertificatesToConfig("cacert.der", null, "test@cerner.com");
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver privResl = nAgent.getPrivateCertResolver();
            	assertNotNull(privResl);
            	Collection<X509Certificate> certs = privResl.getCertificates(new InternetAddress("gm2552@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	assertTrue(privResl instanceof LDAPCertificateStore);
            	((LDAPCertificateStore)privResl).flush(true);
            	
            	Collection<CertificateResolver> publicResls = nAgent.getPublicCertResolvers();
            	assertNotNull(publicResls);
            	assertEquals(3, publicResls.size());
            	
            	// get the WS resolvers
            	CertificateResolver wsRes = null;
            	for (CertificateResolver res : publicResls)
            	{
            		if (res instanceof ConfigServiceCertificateStore)
            		{
            			wsRes = res;
            			((ConfigServiceCertificateStore)wsRes).flush(true);
            			break;
            		}
            	}
            	assertNotNull(wsRes);
            	
            	certs = wsRes.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	
            	certs = wsRes.getCertificates(new InternetAddress("cerner.com"));
            	assertNotNull(certs);
            	assertEquals(2, certs.size());            	
            }
        }.perform();
    }		
	
	public void testConfigurationPrivateKeyStoreFile() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("PrivateStoreType", "keystore");
            	proxy.addSetting("PrivateStoreFile", "src/test/resources/keystores/internalKeystore");
            	proxy.addSetting("PrivateStoreFilePass", "h3||0 wor|d");
            	proxy.addSetting("PrivateStorePrivKeyPass", "pKpa$$wd");
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// already in the keystore file
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver privResl = nAgent.getPrivateCertResolver();
            	assertNotNull(privResl);
            	Collection<X509Certificate> certs = privResl.getCertificates(new InternetAddress("ryan@messaging.cernerdemos.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	assertTrue(privResl instanceof KeyStoreCertificateStore);
            	
            }
        }.perform();
    }
	
	public void testConfigurationPublicKeyStoreFile() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("PublicStoreType", "keystore");
            	proxy.addSetting("PublicStoreFile", "src/test/resources/keystores/internalKeystore");
            	proxy.addSetting("PublicStoreFilePass", "h3||0 wor|d");
            	proxy.addSetting("PublicStorePrivKeyPass", "pKpa$$wd");
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// already in the keystore file
            }
            
            @SuppressWarnings("deprecation")
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver pubResl = nAgent.getPublicCertResolver();
            	assertNotNull(pubResl);
            	Collection<X509Certificate> certs = pubResl.getCertificates(new InternetAddress("ryan@messaging.cernerdemos.com"));
            	assertNotNull(certs);
            	assertEquals(1, certs.size());
            	assertTrue(pubResl instanceof KeyStoreCertificateStore);
            	
            }
        }.perform();
    }	
	
	public void testConfigurationAnchorKeyStoreFile() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("AnchorStoreType", "keystore");
            	proxy.addSetting("AnchorResolverType", "multidomain");
            	proxy.addSetting("AnchorKeyStoreFile", "src/test/resources/keystores/internalKeystore");
            	proxy.addSetting("AnchorKeyStoreFilePass", "h3||0 wor|d");
            	proxy.addSetting("AnchorKeyStorePrivKeyPass", "pKpa$$wd");
            	
            	proxy.addSetting("cerner.comIncomingAnchorAliases", "cacert");
            	proxy.addSetting("securehealthemail.comIncomingAnchorAliases", "secureHealthEmailCACert");
            	
            	proxy.addSetting("cerner.comOutgoingAnchorAliases", "cacert");
            	proxy.addSetting("securehealthemail.comOutgoingAnchorAliases", "secureHealthEmailCACert");            	
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// doesn't matter
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());

            	
            	
            	CertificateResolver trustResl = nAgent.getTrustAnchors().getIncomingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the incoming trust anchor for cerner.com
            	Collection<X509Certificate> anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the incoming trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());       
            	
            	
            	
            	trustResl = nAgent.getTrustAnchors().getOutgoingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the outgoing trust anchor for cerner.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the outgoing trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());    
            	
            }
        }.perform();
    }		
	
	
	public void testMissingPostmasters() throws Exception 
    {
		new MultiDomainTestPlan() 
		{
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// doesn't matter
            }
            
			protected void addDomains() throws Exception
			{
	        	Domain dom = new Domain();
	        	dom.setDomainName("cerner.com");
	        	proxy.addDomain(dom);
	        	
	        	dom = new Domain();
	        	dom.setDomainName("securehealthemail.com");
	        	proxy.addDomain(dom);
			}	    
		
		    @Override
	        protected void assertDomainPostmastersConfig(SmtpAgentSettings settings)
		    {
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
		}.perform();
	}	

	
	public void testEmptyPostmasters() throws Exception 
    {
		new MultiDomainTestPlan() 
		{
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// doesn't matter
            }
            
			protected void addDomains() throws Exception
			{
	        	Domain dom = new Domain();
	        	dom.setDomainName("cerner.com");
	        	dom.setPostMasterEmail(" ");
	        	proxy.addDomain(dom);
	        	
	        	
	        	dom = new Domain();
	        	dom.setDomainName("securehealthemail.com");
	        	dom.setPostMasterEmail(" ");
	        	proxy.addDomain(dom);
			}	    
		
		    @Override
	        protected void assertDomainPostmastersConfig(SmtpAgentSettings settings)
		    {
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
		}.perform();
	}	
	
	public void testOutboundOnlyAnchors_keyStoreAnchor() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("AnchorStoreType", "keystore");
            	proxy.addSetting("AnchorResolverType", "multidomain");
            	proxy.addSetting("AnchorKeyStoreFile", "src/test/resources/keystores/internalKeystore");
            	proxy.addSetting("AnchorKeyStoreFilePass", "h3||0 wor|d");
            	proxy.addSetting("AnchorKeyStorePrivKeyPass", "pKpa$$wd");
            	
            	proxy.addSetting("cerner.comOutgoingAnchorAliases", "cacert");
            	proxy.addSetting("securehealthemail.comOutgoingAnchorAliases", "secureHealthEmailCACert");            	
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// doesn't matter
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());

            	
            	
            	CertificateResolver trustResl = nAgent.getTrustAnchors().getIncomingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// assert 0 incoming anchors
            	Collection<X509Certificate> anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());
            	
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());       
            	
            	
            	
            	trustResl = nAgent.getTrustAnchors().getOutgoingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the outgoing trust anchor for cerner.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the outgoing trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());    
            	
            }
        }.perform();
    }
	
	public void testOutboundOnlyAnchors_LDAPAnchor() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     

            protected void addSettings() throws Exception
            {
            	proxy.addSetting("AnchorStoreType", "LDAP");
            	proxy.addSetting("AnchorResolverType", "multidomain");
            	proxy.addSetting("TrustAnchorLDAPUrl", "ldap://localhost:" + configuration.getLdapPort());
            	proxy.addSetting("TrustAnchorLDAPSearchBase", "cn=lookupTest");
            	proxy.addSetting("TrustAnchorLDAPSearchAttr", "email");
            	proxy.addSetting("TrustAnchorLDAPCertAttr", "privKeyStore");
            	proxy.addSetting("TrustAnchorLDAPCertFormat", "X509");
            	
            	
            	proxy.addSetting("cerner.comOutgoingAnchorAliases", "cerner.com");
            	proxy.addSetting("securehealthemail.comOutgoingAnchorAliases", "securehealthemail.com");
            }
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
                addCertificatesToLdap(new String[]{"/cacert.der"}, "cerner.com"); 
                addCertificatesToLdap(new String[]{"/cacert.der"}, "securehealthemail.com"); 
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());
            	CertificateResolver privResl = nAgent.getPrivateCertResolver();
            	assertTrue(privResl instanceof ConfigServiceCertificateStore);
            	
            	
            	CertificateResolver trustResl = nAgent.getTrustAnchors().getIncomingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// assert 0 incoming anchors
            	Collection<X509Certificate> anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());
            	
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());          
            	
            	
            	
            	trustResl = nAgent.getTrustAnchors().getOutgoingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the outgoing trust anchor for cerner.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the outgoing trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());                	
            	
            }
        }.perform();
    }		
	
	public void testOutboundOnlyAnchors_WSStoreAnchor() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// doesn't matter
            }
            
            @Override
            protected void addTrustAnchors() throws Exception
            {
            	Vector<Anchor> vec = new Vector<Anchor>();
            	
            	Anchor anchor = new Anchor();
            	anchor.setData(getCertificateFileData("cacert.der"));
            	anchor.setOwner("cerner.com");
            	anchor.setIncoming(false);
            	anchor.setOutgoing(true);
            	vec.add(anchor);
            	
            	anchor = new Anchor();
            	anchor.setData(getCertificateFileData("cacert.der"));
            	anchor.setOwner("securehealthemail.com");
            	anchor.setIncoming(false);
            	anchor.setOutgoing(true);
            	vec.add(anchor);
            	
            	proxy.addAnchor(vec.toArray(new Anchor[vec.size()]));
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());

            	CertificateResolver trustResl = nAgent.getTrustAnchors().getIncomingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// assert 0 incoming anchors
            	Collection<X509Certificate> anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());
            	
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());       
            	
            	
            	
            	trustResl = nAgent.getTrustAnchors().getOutgoingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the outgoing trust anchor for cerner.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the outgoing trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());    
            	
            }
        }.perform();
    }
	
	public void testInboundOnlyAnchors_WSTrustBundleAnchors_SingleBundle() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     
        	protected Domain[] domainsTested;
        	protected TrustBundle[] bundlesTested;
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// doesn't matter
            }
            
            @Override
            protected void addTrustAnchors() throws Exception
            {
            	final File bundleFile = new File("src/test/resources/bundles/testBundle.p7b");
            	
            	final TrustBundle bundle = new TrustBundle();
            	bundle.setBundleName("TestBundle");
            	bundle.setBundleURL(filePrefix + bundleFile.getAbsolutePath());
            	bundle.setRefreshInterval(0);
            	
            	
            	proxy.addTrustBundle(bundle);
            	
            	// load the bundles
            	Thread.sleep(2000);
            	
            	bundlesTested = proxy.getTrustBundles(true);
            	
    			int domainCount = proxy.getDomainCount();
    			
    			domainsTested = proxy.listDomains(null, domainCount);
    			
    			for (Domain domain : domainsTested)
    				for (TrustBundle testBundle : bundlesTested)
    					proxy.associateTrustBundleToDomain(domain.getId(), testBundle.getId(), true, false);
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());

            	CertificateResolver trustResl = nAgent.getTrustAnchors().getOutgoingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// assert 0 incoming anchors
            	Collection<X509Certificate> anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());
            	
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());       
            	
            	
            	
            	trustResl = nAgent.getTrustAnchors().getIncomingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the outgoing trust anchor for cerner.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the outgoing trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());    
            	
            }
        }.perform();
    }
	
	public void testOutboundOnlyAnchors_WSTrustBundleAnchors_SingleBundle() throws Exception 
    {
        new MultiDomainTestPlan() 
        {                     
        	protected Domain[] domainsTested;
        	protected TrustBundle[] bundlesTested;
        	
            @Override
            protected void addPrivateCertificates() throws Exception
            {
            	// doesn't matter
            }
            
            @Override
            protected void addTrustAnchors() throws Exception
            {
            	final File bundleFile = new File("src/test/resources/bundles/testBundle.p7b");
            	
            	final TrustBundle bundle = new TrustBundle();
            	bundle.setBundleName("TestBundle");
            	bundle.setBundleURL(filePrefix + bundleFile.getAbsolutePath());
            	bundle.setRefreshInterval(0);
            	
            	
            	proxy.addTrustBundle(bundle);
            	
            	// load the bundles
            	Thread.sleep(2000);
            	
            	bundlesTested = proxy.getTrustBundles(true);
            	
    			int domainCount = proxy.getDomainCount();
    			
    			domainsTested = proxy.listDomains(null, domainCount);
    			
    			for (Domain domain : domainsTested)
    				for (TrustBundle testBundle : bundlesTested)
    					proxy.associateTrustBundleToDomain(domain.getId(), testBundle.getId(), false, true);
            }
            
            protected void doAssertions(SmtpAgent agent) throws Exception
            {
            	super.doAssertions(agent);
            	DefaultNHINDAgent nAgent = ((DefaultNHINDAgent)agent.getAgent());

            	CertificateResolver trustResl = nAgent.getTrustAnchors().getIncomingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// assert 0 incoming anchors
            	Collection<X509Certificate> anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());
            	
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(0, anchors.size());       
            	
            	
            	
            	trustResl = nAgent.getTrustAnchors().getOutgoingAnchors();
            	assertTrue(trustResl instanceof TrustAnchorCertificateStore);
            	
            	// get the outgoing trust anchor for cerner.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@cerner.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());
            	
            	// get the outgoing trust anchor for securehealthemail.com
            	anchors = trustResl.getCertificates(new InternetAddress("test@securehealthemail.com"));
            	assertNotNull(anchors);
            	assertEquals(1, anchors.size());    
            	
            }
        }.perform();
    }
	
	protected byte[] getCertificateFileData(String file) throws Exception
	{
		File fl = new File("src/test/resources/certs/" + file);
		
		return FileUtils.readFileToByteArray(fl);
	}
	
	
    protected void addCertificatesToLdap(String[] filename, String email) throws Exception {
        Entry entry = new Entry();
        entry.addAttribute("objectClass", "organizationalUnit");
        entry.addAttribute("objectClass", "top");
        entry.addAttribute("objectClass", "userPrivKey");
        entry.addAttribute("email", email);
        
        
        for(int i=0;i<filename.length;i++) {
            byte[] buffer = loadCertificateData(filename[i]);

            Base64 base64 = new Base64();
            String certificateValue =  new String(base64.encode(buffer));
            entry.addAttribute("privKeyStore", certificateValue);
        }      
        
        String ou;
        int index = email.indexOf("@");
        if (index > -1)
        	ou = email.substring(0,email.indexOf("@"));  
        else
        	ou = email;      
        entry.addAttribute("ou", ou);
        rootDSE.createSubcontext("ou="+ou+", ou=privKeys, ou=cerner, ou=com, cn=lookupTest", entry.getAttributes());
    }	
    
    protected void addCertificatesToConfig(String certFilename, String keyFileName, String email) throws Exception
    {
    	byte[] dataToAdd = null;
    	if (keyFileName == null)
    	{
    		// just load the cert
    		dataToAdd = loadCertificateData(certFilename);
    	}
    	else
    	{
    		dataToAdd = loadPkcs12FromCertAndKey(certFilename, keyFileName);
    	}
    	
    	Certificate cert = new Certificate();
    	cert.setData(dataToAdd);
    	cert.setOwner(email);
    	
    	proxy.addCertificates(new Certificate[] {cert});
    }
    
	private static byte[] loadPkcs12FromCertAndKey(String certFileName, String keyFileName) throws Exception
	{
		byte[] retVal = null;
		try
		{
			KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CryptoExtensions.getJCEProviderName());
			
			localKeyStore.load(null, null);
			
			byte[] certData = loadCertificateData(certFileName);
			byte[] keyData = loadCertificateData(keyFileName);
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream inStr = new ByteArrayInputStream(certData);
			java.security.cert.Certificate cert = cf.generateCertificate(inStr);
			inStr.close();
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( keyData );
			Key privKey = kf.generatePrivate (keysp);
			
			char[] array = "".toCharArray();
			
			localKeyStore.setKeyEntry("privCert", privKey, array,  new java.security.cert.Certificate[] {cert});
			
			ByteArrayOutputStream outStr = new ByteArrayOutputStream();
			localKeyStore.store(outStr, array);
			
			retVal = outStr.toByteArray();
			
			outStr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return retVal;
	}    
    
	private static byte[] loadCertificateData(String certFileName) throws Exception
	{
		File fl = new File(certBasePath + certFileName);
		
		return FileUtils.readFileToByteArray(fl);
	}
}

