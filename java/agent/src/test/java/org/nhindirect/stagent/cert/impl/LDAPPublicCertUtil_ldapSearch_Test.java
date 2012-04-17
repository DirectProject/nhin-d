package org.nhindirect.stagent.cert.impl;


import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InitialDirContext;

import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.schema.bootstrap.AbstractBootstrapSchema;
import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.shared.ldap.ldif.Entry;
import org.nhindirect.stagent.cert.impl.util.Lookup;
import org.nhindirect.stagent.cert.impl.util.LookupFactory;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.TestUtils;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.SRVRecord;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LDAPPublicCertUtil_ldapSearch_Test extends AbstractServerTest 
{
	private Lookup mockLookup;

	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception
	{


		// create the LDAP server
		
	    MutablePartitionConfiguration pcfg = new MutablePartitionConfiguration();
	    pcfg.setName( "lookupTestPublic" );
	    pcfg.setSuffix( "cn=lookupTestPublic" );

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
		
        Set<AbstractBootstrapSchema> schemas = configuration.getBootstrapSchemas();

        configuration.setBootstrapSchemas(schemas);
	
		mockLookup = mock(Lookup.class);
		LookupFactory.getFactory().addOverrideImplementation(mockLookup);
        
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception
	{
		LookupFactory.getFactory().removeOverrideImplementation();
		super.tearDown();
	}
	
	
	abstract class TestPlan extends BaseTestPlan 
	{
		
		@Override
		protected void setupMocks()
		{
			try
			{	
				createLdapEntries();
				
				SRVRecord[] srvRecords = getSRVRecords();
				
				when(mockLookup.run()).thenReturn(srvRecords);
			}
			catch (Throwable t)
			{
				throw new RuntimeException(t);
			}
		}
		
		protected SRVRecord[] getSRVRecords() throws Exception
		{
			SRVRecord srvRecord = new SRVRecord(new Name("_ldap._tcp.example.com."), DClass.IN, 3600, 0, 1, port, new Name("localhost."));
			
			return new SRVRecord[] {srvRecord};
		}
		
		@Override
		protected void performInner() throws Exception
		{
			String subjectToSearch = getSubjectToSearch();
			
			LdapPublicCertUtilImpl impl = new LdapPublicCertUtilImpl();
			
			Collection<X509Certificate> certs = impl.ldapSearch(subjectToSearch);
			
			doAssertions(certs);
		}
		
		protected void createLdapEntries() throws Exception 
		{

			Entry entry = new Entry();
			entry.addAttribute("objectClass", "organizationalUnit");
			entry.addAttribute("objectClass", "top");
			entry.addAttribute("objectClass", "iNetOrgPerson");
			entry.addAttribute("mail", "gm2552@cerner.com");

			X509Certificate cert = TestUtils.loadCertificate("gm2552.der");
			entry.addAttribute("userCertificate", cert.getEncoded());
			entry.addAttribute("ou", "gm2552");
			entry.addAttribute("cn", "Greg Meyer");
			entry.addAttribute("sn", "");
			
			rootDSE.createSubcontext("ou=gm2552, cn=lookupTestPublic", entry.getAttributes());
		}	
		
		protected abstract String getSubjectToSearch() throws Exception;
		
		protected abstract void doAssertions(Collection<X509Certificate> certs) throws Exception;
	}
	
	
	/*
	public void testGetUserCertFromLDAP() throws Exception
	{
		SRVRecord srvRecord = new SRVRecord(new Name("_ldap._tcp.stuttgart.de."), DClass.IN, 3600, 0, 1, 389, new Name("directory.d-trust.de."));
		when(mockLookup.run()).thenReturn(new Record[] {srvRecord});
		
		LdapPublicCertUtilImpl publicUtil = new LdapPublicCertUtilImpl();
		Collection<X509Certificate> certs = publicUtil.ldapSearch("u10d001@stuttgart.de");
	}
	*/
	
	public void testLDAPSearch_getSingleCert_assertCertFound() throws Exception
	{
		new TestPlan()
		{
			X509Certificate checkCert = TestUtils.loadCertificate("gm2552.der");
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "gm2552@cerner.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());
				assertEquals(checkCert, certs.iterator().next());
			}

		}.perform();
	}
	
	public void testLDAPSearch_getSingleCertByDomain_assertCertFound() throws Exception
	{
		new TestPlan()
		{
			X509Certificate checkCert = TestUtils.loadCertificate("gm2552.der");
			
			@Override
			protected void createLdapEntries() throws Exception 
			{

				Entry entry = new Entry();
				entry.addAttribute("objectClass", "organizationalUnit");
				entry.addAttribute("objectClass", "top");
				entry.addAttribute("objectClass", "iNetOrgPerson");
				entry.addAttribute("mail", "cerner.com");

				X509Certificate cert = TestUtils.loadCertificate("gm2552.der");

				entry.addAttribute("userCertificate", cert.getEncoded());
				entry.addAttribute("ou", "gm2552");
				entry.addAttribute("cn", "Greg Meyer");
				entry.addAttribute("sn", "");
				
				rootDSE.createSubcontext("ou=gm2552, cn=lookupTestPublic", entry.getAttributes());
			}	
			
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "cerner.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());
				assertEquals(checkCert, certs.iterator().next());
			}

		}.perform();
	}
	
	public void testLDAPSearch_getMultipleCertsByDomain_assertCertsFound() throws Exception
	{
		new TestPlan()
		{
			X509Certificate check1Cert = TestUtils.loadCertificate("gm2552.der");
			X509Certificate check2Cert = TestUtils.loadCertificate("ryan.der");
			
			protected void createLdapEntries() throws Exception 
			{

				Entry entry = new Entry();
				entry.addAttribute("objectClass", "organizationalUnit");
				entry.addAttribute("objectClass", "top");
				entry.addAttribute("objectClass", "iNetOrgPerson");
				entry.addAttribute("mail", "cerner.com");

				entry.addAttribute("userCertificate", check1Cert.getEncoded());

				entry.putAttribute("userCertificate", check2Cert.getEncoded());
				
				entry.addAttribute("ou", "gm2552");
				entry.addAttribute("cn", "Greg Meyer");
				entry.addAttribute("sn", "");
				
				rootDSE.createSubcontext("ou=gm2552, cn=lookupTestPublic", entry.getAttributes());
			}	
			
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "cerner.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(2, certs.size());
				Iterator<X509Certificate> iter = certs.iterator();
				assertEquals(check1Cert, iter.next());
				assertEquals(check2Cert, iter.next());
			}

		}.perform();
	}
	
	
	public void testLDAPSearch_getSingleCert_assertCertNotFound() throws Exception
	{
		new TestPlan()
		{	
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "dummy.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(0, certs.size());

			}

		}.perform();
	}
	
	public void testLDAPSearch_getSingleCert_noSRVRecords_assertCertNotFound() throws Exception
	{
		new TestPlan()
		{
			protected SRVRecord[] getSRVRecords() throws Exception
			{				
				return null;
			}
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "dummy.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(0, certs.size());

			}

		}.perform();
	}
	
	public void testLDAPSearch_getSingleCert_noBaseDNs_assertCertNotFound() throws Exception
	{
		new TestPlan()
		{
			@Override
			protected void performInner() throws Exception
			{
				String subjectToSearch = getSubjectToSearch();
				
				LdapPublicCertUtilImpl impl = new LdapPublicCertUtilImpl()
				{
					protected List<String> getBaseNamingContexts(InitialDirContext ctx)
					{
						return Collections.emptyList();
					}
				};
				
				Collection<X509Certificate> certs = impl.ldapSearch(subjectToSearch);
				
				doAssertions(certs);
			}
			
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "dummy.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(0, certs.size());

			}

		}.perform();
	}
	
	public void testLDAPSearch_getSingleCert_multipleSRVRecords_assertCertFound() throws Exception
	{
		new TestPlan()
		{
			X509Certificate checkCert = TestUtils.loadCertificate("gm2552.der");
			
			protected SRVRecord[] getSRVRecords() throws Exception
			{
				SRVRecord srvRecord1 = new SRVRecord(new Name("_ldap._tcp.example.com."), DClass.IN, 3600, 0, 1, port-1, new Name("localhost."));
				SRVRecord srvRecord2 = new SRVRecord(new Name("_ldap._tcp.example.com."), DClass.IN, 3600, 0, 1, port, new Name("localhost."));

				
				return new SRVRecord[] {srvRecord1, srvRecord2};
			}
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "gm2552@cerner.com";
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				assertNotNull(certs);
				assertEquals(1, certs.size());
				assertEquals(checkCert, certs.iterator().next());
			}

		}.perform();
	}
	
	public void testLDAPSearch_exceptionInSearch_assertExeption() throws Exception
	{
		new TestPlan()
		{
			
			@Override
			protected void performInner() throws Exception
			{
				String subjectToSearch = getSubjectToSearch();
				
				LdapPublicCertUtilImpl impl = new LdapPublicCertUtilImpl()
				{
					protected List<String> getBaseNamingContexts(InitialDirContext ctx)
					{
						throw new RuntimeException();
					}
				};
				
				Collection<X509Certificate> certs = impl.ldapSearch(subjectToSearch);
				
				doAssertions(certs);
			}
			
			@Override
			protected String getSubjectToSearch() throws Exception
			{
				return "gm2552@cerner.com";
			}
			
			@Override
			protected void assertException(Exception exception) throws Exception 
			{
				// default case should not throw an exception
				assertNotNull(exception);
				//assertTrue(exception instanceof NHINDException);
			}
			
			@Override
			protected void doAssertions(Collection<X509Certificate> certs) throws Exception
			{
				
			}

		}.perform();
	}
}
