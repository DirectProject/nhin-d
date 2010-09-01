package org.nhindirect.ldap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapSchema;
import org.apache.directory.server.unit.AbstractServerTest;

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
