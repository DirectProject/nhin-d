package org.nhindirect.stagent.cert.impl;

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
import javax.naming.NamingException;
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
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.utils.BaseTestPlan;

public class LDAPCertificateStore_functional_test extends AbstractServerTest 
{
	
	@SuppressWarnings("unchecked")
	@Override 
	public void setUp() throws Exception
	{
		// create the LDAP server
		
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
        
		configuration.setWorkingDirectory(new File("LDAP-TEST"));
		
        // add the private key schema
        ///
        Set<AbstractBootstrapSchema> schemas = configuration.getBootstrapSchemas();
        schemas.add( new PrivkeySchema() );

        configuration.setBootstrapSchemas(schemas);
		

		super.setUp();
	}
	
	abstract class TestPlan extends BaseTestPlan 
	{
		
		protected int port;
		protected LDAPCertificateStore certStore;
		
		
		@Override
		protected void setupMocks()
		{
			try
			{				
				// import the ldif file
				InputStream stream = LDAPResearchTest.class.getClassLoader().getResourceAsStream("ldifs/privCertsOnly.ldif");
				
				if (stream == null)
					throw new IOException("Failed to load ldif file");
				
				importLdif(stream);
				createLdapEntries();
				
				port = configuration.getLdapPort();
				
				// clean the cached keystore file
				File fl = new File("NHINKeyStore");
				if (fl.exists())
					fl.delete();
				
				// create the ldap cert resolver
				Hashtable<String, String> env = new Hashtable<String, String>();
				env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				env.put(Context.PROVIDER_URL, "ldap://localhost:" + port);
				env.put(Context.SECURITY_AUTHENTICATION, "none");
				
				LdapEnvironment ldapEnvironment = new LdapEnvironment(env, "privKeyStore", "ou=cerner, ou=com, cn=lookupTest", "email");
				LdapCertUtilImpl impl = new LdapCertUtilImpl(ldapEnvironment, "", "X509");
				certStore = new LDAPCertificateStore(impl, null, null);
				
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		protected void createLdapEntries() throws NamingException 
		{

			Entry entry = new Entry();
			entry.addAttribute("objectClass", "organizationalUnit");
			entry.addAttribute("objectClass", "top");
			entry.addAttribute("objectClass", "userPrivKey");
			entry.addAttribute("email", "gm2552@cerner.com");
			
			File fl = new File("testfile");
			int idx = fl.getAbsolutePath().lastIndexOf("testfile");
			String path = fl.getAbsolutePath().substring(0, idx);
					
			byte[] buffer = new byte[(int) new File(path + "src/test/resources/certs/gm2552.der").length()+100];
			try {
				InputStream stream = LDAPResearchTest.class.getClassLoader().getResourceAsStream("certs/gm2552.der");
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
		
		@Override
		protected void performInner() throws Exception
		{
			String subjectToSearch = getSubjectToSearch();
			
			Collection<X509Certificate> certs = certStore.getCertificates(subjectToSearch);
			
			doAssertions(certs);
		}
		
		@Override
		protected void tearDownMocks()
		{

		}	
		
		protected abstract String getSubjectToSearch() throws Exception;
		
		protected abstract void doAssertions(Collection<X509Certificate> certs) throws Exception;
	}
	
	
	public void testGetWildCardSearch_assertAllCertsFound() throws Exception
	{

		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "*";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());
				
				String address = CryptoExtensions.getSubjectAddress(certs.iterator().next());
				assertEquals("gm2552@securehealthemail.com", address);
			}

		}.perform();
	}
	
	public void testGetWildCardSearch_assertNoCertsFound() throws Exception
	{

		new TestPlan()
		{
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "c*";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(0, certs.size());
			}

		}.perform();
	}
	
	public void testGetAllCertificates_assertAllCertsFound() throws Exception
	{		
		new TestPlan()
		{
			@Override
			protected void performInner() throws Exception
			{
				
				Collection<X509Certificate> certs = certStore.getAllCertificates();
				
				doAssertions(certs);
			}
		
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());
				
				String address = CryptoExtensions.getSubjectAddress(certs.iterator().next());
				assertEquals("gm2552@securehealthemail.com", address);
			}

		}.perform();
	}
	
}
