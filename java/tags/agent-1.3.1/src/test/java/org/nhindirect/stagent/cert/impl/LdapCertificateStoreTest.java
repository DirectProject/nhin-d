package org.nhindirect.stagent.cert.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.codec.binary.Base64;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapSchema;
import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.shared.ldap.ldif.Entry;
import org.nhindirect.ldap.LDAPResearchTest;
import org.nhindirect.ldap.PrivkeySchema;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.provider.LdapCertificateStoreProvider;
import org.nhindirect.stagent.cert.impl.provider.PublicLdapCertificateStoreProvider;
import org.nhindirect.stagent.cert.impl.util.Lookup;
import org.nhindirect.stagent.cert.impl.util.LookupFactory;
import org.nhindirect.stagent.utils.TestUtils;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;

/**
 * Testcase using an embedded Apache Directory Server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */

public class LdapCertificateStoreTest extends AbstractServerTest
{
	private Lookup mockLookup;
	
	/**
     * Initialize the server.
     */
	@Override
	public void setUp() throws Exception
	{
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

        // Create the public LDAP partition
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
		
		/*MutableAuthenticatorConfiguration authConfig = new MutableAuthenticatorConfiguration();
		this.configuration.setAuthenticatorConfigurations(arg0)
		*/
		
        // add the private key schema
        ///
        Set<AbstractBootstrapSchema> schemas = configuration.getBootstrapSchemas();
        schemas.add( new PrivkeySchema() );

        configuration.setBootstrapSchemas(schemas);
		
		
		super.setUp();
		
		// import the ldif file
		InputStream stream = LDAPResearchTest.class.getClassLoader().getResourceAsStream("ldifs/privCertsOnly.ldif");
		
		if (stream == null)
			throw new IOException("Failed to load ldif file");
		
		importLdif(stream);
		
		
		mockLookup = mock(Lookup.class);
		LookupFactory.getFactory().addOverrideImplementation(mockLookup);
		SRVRecord srvRecord = new SRVRecord(new Name("_ldap._tcp.example.com."), DClass.IN, 3600, 0, 1, port, new Name("localhost."));
		when(mockLookup.run()).thenReturn(new Record[] {srvRecord});
	}
	
	protected void addCertificatesToLdap(String[] filename) throws Exception {
		Entry entry = new Entry();
		entry.addAttribute("objectClass", "organizationalUnit");
		entry.addAttribute("objectClass", "top");
		entry.addAttribute("objectClass", "userPrivKey");
		entry.addAttribute("email", "gm2552@cerner.com");
		
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		String path = fl.getAbsolutePath().substring(0, idx);
		
		for(int i=0;i<filename.length;i++) {
			byte[] buffer = new byte[(int) new File(path + "src/test/resources/"+filename[i]).length()+100];
			try {
				InputStream stream = LDAPResearchTest.class.getClassLoader().getResourceAsStream(filename[i]);
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
		
		entry.addAttribute("ou", "gm2552");
		rootDSE.createSubcontext("ou=gm2552, ou=privKeys, ou=cerner, ou=com, cn=lookupTest", entry.getAttributes());
		

	}

	public void addStockPublicLDAPCertificats() throws Exception
	{
		// add public LDAP entries
		
		// add a user level cert
		Entry entry = new Entry();
		entry.addAttribute("objectClass", "organizationalUnit");
		entry.addAttribute("objectClass", "top");
		entry.addAttribute("objectClass", "iNetOrgPerson");
		entry.addAttribute("mail", "user@testdomain.com");

		X509Certificate cert = TestUtils.loadCertificate("cert-a.der");

		entry.addAttribute("userCertificate", cert.getEncoded());
		entry.addAttribute("ou", "user");
		entry.addAttribute("cn", "Test User");
		entry.addAttribute("sn", "");
		
		rootDSE.createSubcontext("ou=user, cn=lookupTestPublic", entry.getAttributes());
		
		// add a domain level cert
		entry = new Entry();
		entry.addAttribute("objectClass", "organizationalUnit");
		entry.addAttribute("objectClass", "top");
		entry.addAttribute("objectClass", "iNetOrgPerson");
		entry.addAttribute("mail", "testdomain.com");

		cert = TestUtils.loadCertificate("cert-b.der");


		entry.addAttribute("userCertificate", cert.getEncoded());
		entry.addAttribute("ou", "testdomain.com");
		entry.addAttribute("cn", "Test Domain");
		entry.addAttribute("sn", "");
		
		rootDSE.createSubcontext("ou=testdomain.com, cn=lookupTestPublic", entry.getAttributes());
		

	}
	
    public void testLdapSearch_X509Certificate() throws Exception
	{
    	addCertificatesToLdap(new String[]{"certs/bob.der"});
		int port = configuration.getLdapPort();
		
		String url = "ldap://localhost:" + port + "/" + "cn=lookupTest";
    	
    	LdapStoreConfiguration ldapStoreConfiguration = new LdapStoreConfiguration(new String[]{url}, "", "email", "privKeyStore", "X509");
    	LdapCertificateStoreProvider provider = new LdapCertificateStoreProvider(ldapStoreConfiguration, null, null);
    	LDAPCertificateStore certificateResolver = (LDAPCertificateStore) provider.get();
    	Collection<X509Certificate> certs = certificateResolver.getCertificates("gm2552@cerner.com");
		assertEquals(1, certs.size());
		X509Certificate cert = certs.iterator().next();
		assertFalse( cert instanceof X509CertificateEx );
		assertTrue(cert.getSubjectX500Principal().toString().contains("bob@nhind.hsgincubator.com"));
	}
    
    public void testLdapSearch_LdapProviderSupportsMultipleURLs() throws Exception
	{
    	addCertificatesToLdap(new String[]{"certs/bob.der"});
		int port = configuration.getLdapPort();
		
		String url = "ldap://localhost:" + port + "/" + "cn=lookupTest";
		port = port+10;
		String fakeUrl = "ldap://localhost:" + port + "/" + "cn=lookupTest";
    	
    	LdapStoreConfiguration ldapStoreConfiguration = new LdapStoreConfiguration(new String[]{fakeUrl, url}, "", "email", "privKeyStore", "X509");
    	LdapCertificateStoreProvider provider = new LdapCertificateStoreProvider(ldapStoreConfiguration, null, null);
    	LDAPCertificateStore certificateResolver = (LDAPCertificateStore) provider.get();
    	Collection<X509Certificate> certs = certificateResolver.getCertificates("gm2552@cerner.com");
		assertEquals(1, certs.size());
		X509Certificate cert = certs.iterator().next();
		assertFalse( cert instanceof X509CertificateEx );
		assertTrue(cert.getSubjectX500Principal().toString().contains("bob@nhind.hsgincubator.com"));
	}
    
    public void testLdapSearch_MultipleX509Certificates() throws Exception
	{
    	addCertificatesToLdap(new String[]{"certs/bob.der", "certs/cacert.der"});
		int port = configuration.getLdapPort();
		
		String url = "ldap://localhost:" + port + "/" + "cn=lookupTest";
    	
    	LdapStoreConfiguration ldapStoreConfiguration = new LdapStoreConfiguration(new String[]{url}, "", "email", "privKeyStore", "X509");
    	LdapCertificateStoreProvider provider = new LdapCertificateStoreProvider(ldapStoreConfiguration, null, null);
    	LDAPCertificateStore certificateResolver = (LDAPCertificateStore) provider.get();
    	certificateResolver.flush(true);
    	Collection<X509Certificate> certs = certificateResolver.getCertificates("gm2552@cerner.com");
		assertEquals(2, certs.size());
		Iterator<X509Certificate> iterator = certs.iterator();
		X509Certificate cert = iterator.next();
		assertFalse( cert instanceof X509CertificateEx );
		assertTrue(cert.getSubjectX500Principal().toString().contains("bob@nhind.hsgincubator.com"));
		cert = iterator.next();
		assertFalse( cert instanceof X509CertificateEx );
		assertTrue(cert.getSubjectX500Principal().toString().contains("gmeyer@cerner.com"));
	}
    
    public void testLdapSearch_PKCS12PrivateKey() throws Exception
	{
    	addCertificatesToLdap(new String[] {"certs/gm2552encrypted.p12"});
		int port = configuration.getLdapPort();
		String url = "ldap://localhost:" + port + "/" + "cn=lookupTest";
    	
    	LdapStoreConfiguration ldapStoreConfiguration = new LdapStoreConfiguration(new String[] {url}, "", "email", "privKeyStore", "PKCS12");
    	ldapStoreConfiguration.setLdapCertPassphrase("1kingpuff");
    	LdapCertificateStoreProvider provider = new LdapCertificateStoreProvider(ldapStoreConfiguration, null, null);
    	LDAPCertificateStore certificateResolver = (LDAPCertificateStore) provider.get();
    	certificateResolver.flush(true);
    	Collection<X509Certificate> certs = certificateResolver.getCertificates("gm2552@cerner.com");
		assertEquals(1, certs.size());
		X509Certificate cert = certs.iterator().next();
		assertTrue( cert instanceof X509CertificateEx );
		assertTrue(cert.getSubjectX500Principal().toString().contains("gm2552@securehealthemail.com"));
	}

    public void testPublicLdapSearch_userLevelCert_assertCertExists() throws Exception
	{
    	addStockPublicLDAPCertificats();
    	
		PublicLdapCertificateStoreProvider provider = new PublicLdapCertificateStoreProvider(null, null);
		CertificateResolver resolver = provider.get();
    	Collection<X509Certificate> certs = resolver.getCertificates(new InternetAddress("user@testdomain.com"));
		assertEquals(1, certs.size());
		X509Certificate cert = certs.iterator().next();
		assertTrue(cert.getSubjectX500Principal().toString().contains("moe@direct.fnhubapp01.qa.medplus.com"));
	}

    public void testPublicLdapSearch_orgLevelCert_assertCertExists() throws Exception
	{
    	addStockPublicLDAPCertificats();
    	
		PublicLdapCertificateStoreProvider provider = new PublicLdapCertificateStoreProvider(null, null);
		CertificateResolver resolver = provider.get();
    	Collection<X509Certificate> certs = resolver.getCertificates(new InternetAddress("testdomain.com"));
		assertEquals(1, certs.size());
		X509Certificate cert = certs.iterator().next();
		assertTrue(cert.getSubjectX500Principal().toString().contains("direct.fnhubapp01.qa.medplus.com"));
	}
    
    public void testPublicLdapSearch_requestUserLevelCert_fallbackToOrgLevelCert_assertCertExists() throws Exception
	{
    	addStockPublicLDAPCertificats();
    	
		PublicLdapCertificateStoreProvider provider = new PublicLdapCertificateStoreProvider(null, null);
		CertificateResolver resolver = provider.get();
    	Collection<X509Certificate> certs = resolver.getCertificates(new InternetAddress("bogus_user@testdomain.com"));
		assertEquals(1, certs.size());
		X509Certificate cert = certs.iterator().next();
		assertTrue(cert.getSubjectX500Principal().toString().contains("direct.fnhubapp01.qa.medplus.com"));
	}
    
    /**
     * Shutdown the server.
     */
    public void tearDown() throws Exception
    {
		LookupFactory.getFactory().removeOverrideImplementation();
        super.tearDown();
    }
}
