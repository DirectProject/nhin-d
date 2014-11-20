package org.nhindirect.ldap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapSchema;
import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.shared.ldap.ldif.Entry;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.LdapStoreConfiguration;
import org.nhindirect.stagent.cert.impl.provider.LdapCertificateStoreProvider;

public class LDAPResearchTest extends AbstractServerTest
{
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

        configuration.setContextPartitionConfigurations( pcfgs );
        
		this.configuration.setWorkingDirectory(new File("LDAP-TEST"));
		
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
		createLdapEntries();
	}
	
	private DirContext createContext(String partition) throws Exception
	{
		int port = configuration.getLdapPort();
		
		String url = "ldap://localhost:" + port + "/" + partition;
		
		Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		
        env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        env.put( Context.SECURITY_CREDENTIALS, "secret" );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put( Context.PROVIDER_URL, url );
        
    	InitialContext initialContext = new InitialContext( env );
    
    	assertNotNull(initialContext);        	
        	
        return (DirContext)initialContext.lookup("");		
        

        
	}	
	
	public void testDummy() throws Exception
	{
		CertCacheFactory.getInstance().flushAll();
		
		DirContext dirContext = createContext("cn=lookupTest");
        	
    	
        Attributes attributes = dirContext.getAttributes( "" );
        assertNotNull( attributes );

        
        NamingEnumeration<Attribute> namingEnum = (NamingEnumeration<Attribute>)attributes.getAll();
    	while (namingEnum.hasMoreElements())
    	{
    		Attribute attr = namingEnum.nextElement();
    		System.out.println("Name: " + attr.getID() + "\r\nValue: " + attr.get() + "\r\n\r\n");
    	}
            
        Set<SearchResult> results = searchDNs( "(email=gm2552@cerner.com)", "", "ou=privKeys, ou=cerner, ou=com", 
                SearchControls.SUBTREE_SCOPE , dirContext);

        for (SearchResult result : results)
        {
        	System.out.println(result.getName());
        	
        	// get the priv cert
        	String privKey = (String)result.getAttributes().get("privKeyStore").get();
        	System.out.println("Privkey BASE64: " + privKey);
        }
        
	}
	
	public void testLdapSearch() throws Exception
	{
		CertCacheFactory.getInstance().flushAll();
		
		int port = configuration.getLdapPort();
		
		String url = "ldap://localhost:" + port + "/" + "cn=lookupTest";
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		
        env.put( Context.SECURITY_PRINCIPAL, "uid=admin,ou=system" );
        env.put( Context.SECURITY_CREDENTIALS, "secret" );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put( Context.PROVIDER_URL, url );
        
    	InitialContext initialContext = new InitialContext( env );
    
    	assertNotNull(initialContext);        	
        	
		DirContext dirContext = (DirContext)initialContext.lookup("");
        	
    	
        Attributes attributes = dirContext.getAttributes( "" );
        assertNotNull( attributes );

        
        NamingEnumeration<Attribute> namingEnum = (NamingEnumeration<Attribute>)attributes.getAll();
    	while (namingEnum.hasMoreElements())
    	{
    		Attribute attr = namingEnum.nextElement();
    		System.out.println("Name: " + attr.getID() + "\r\nValue: " + attr.get() + "\r\n\r\n");
    	}
            
        //Set<SearchResult> results = searchDNs( "(email=gm2552@cerner.com)", "", "ou=privKeys, ou=cerner, ou=com", 
        //        SearchControls.SUBTREE_SCOPE , dirContext);
    	
    	LdapStoreConfiguration ldapStoreConfiguration = new LdapStoreConfiguration(new String[]{url}, "", "email", "privKeyStore", "X509");
    	LdapCertificateStoreProvider provider = new LdapCertificateStoreProvider(ldapStoreConfiguration, null, null);
    	LDAPCertificateStore certificateResolver = (LDAPCertificateStore) provider.get();
    	Collection<X509Certificate> certs = certificateResolver.getCertificates("gm2552@cerner.com");
    	
        /*LdapEnvironment ldapEnvironment = new LdapEnvironment(env, "privKeyStore", "", "email");
		LdapCertUtilImpl ldapcertUtilImpl = new LdapCertUtilImpl(ldapEnvironment, "", "X.509");
		LDAPCertificateStore ldapCertStore = new LDAPCertificateStore(ldapcertUtilImpl, new KeyStoreCertificateStore(), null);
		
		Collection<X509Certificate> certs = ldapCertStore.getCertificates("gm2552@cerner.com");
		*/
		assertEquals(1, certs.size());
		X509Certificate cert = certs.iterator().next();
		assertFalse( cert instanceof X509CertificateEx );
		assertTrue(cert.getSubjectX500Principal().toString().contains("bob@nhind.hsgincubator.com"));
        
	}
	
	protected void createLdapEntries() throws NamingException {
		/*Attributes attrs = new BasicAttributes( "objectClass", "top", true);
		attrs.put("objectClass", "organizationalUnit");
		attrs.put("objectClass", "userPrivKey");
		attrs.put("email", "gm2552@cerner.com");
		attrs.put("privKeyStore", "1234567");
		rootDSE.createSubcontext("ou=gm2552, ou=privKeys, ou=cerner, ou=com, cn=lookupTest", attrs);
		*/
		Entry entry = new Entry();
		entry.addAttribute("objectClass", "organizationalUnit");
		entry.addAttribute("objectClass", "top");
		entry.addAttribute("objectClass", "userPrivKey");
		entry.addAttribute("email", "gm2552@cerner.com");
		
		File fl = new File("testfile");
		int idx = fl.getAbsolutePath().lastIndexOf("testfile");
		String path = fl.getAbsolutePath().substring(0, idx);
				
		byte[] buffer = new byte[(int) new File(path + "src/test/resources/certs/bob.der").length()+100];
		try {
			InputStream stream = LDAPResearchTest.class.getClassLoader().getResourceAsStream("certs/bob.der");
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
		entry.addAttribute("ou", "gm2552");
		rootDSE.createSubcontext("ou=gm2552, ou=privKeys, ou=cerner, ou=com, cn=lookupTest", entry.getAttributes());
	}
	

    private Set<SearchResult> searchDNs( String filter, String partition, String base, int scope , DirContext appRoot)  throws Exception
    {
    	if (appRoot == null)
    		appRoot = createContext( partition );

    	SearchControls controls = new SearchControls();
    	controls.setSearchScope( scope );
	    NamingEnumeration result = appRoot.search( base, filter, controls );
	
	    // collect all results
	    Set<SearchResult> entries = new HashSet<SearchResult>();
	
	    while ( result.hasMore() )
	    {
	    	SearchResult entry = ( SearchResult ) result.next();
	    	
	    	entries.add( entry);
	    }
	
	    return entries;
    }

	
}
