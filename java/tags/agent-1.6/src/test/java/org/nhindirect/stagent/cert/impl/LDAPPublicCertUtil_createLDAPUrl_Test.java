package org.nhindirect.stagent.cert.impl;

import junit.framework.TestCase;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;

public class LDAPPublicCertUtil_createLDAPUrl_Test extends TestCase
{
	public void testCreateLDAPUrl_singleSRVRecord() throws Exception
	{
		LdapPublicCertUtilImpl impl = new LdapPublicCertUtilImpl();
	
		SRVRecord rec = new SRVRecord(new Name("test.com."), DClass.IN, 3600, 0,
				  1, 339, new Name("ldap.test.com."));
		
		String url = impl.createLDAPUrl(new Record[] {rec});
		
		String[] urls = url.split(" ");
		
		assertEquals(1, urls.length);
		assertTrue(urls[0].startsWith("ldap://ldap.test.com"));
	}
	
	public void testCreateLDAPUrl_multipleSRVRecord_descendingPriority_assertPriorityOrderDesc() throws Exception
	{
		LdapPublicCertUtilImpl impl = new LdapPublicCertUtilImpl();
	
		SRVRecord rec1 = new SRVRecord(new Name("test.com."), DClass.IN, 3600, 0,
				  1, 339, new Name("ldap1.test.com."));
		
		SRVRecord rec2 = new SRVRecord(new Name("test.com."), DClass.IN, 3600, 1,
				  1, 339, new Name("ldap2.test.com."));		
		
		String url = impl.createLDAPUrl(new Record[] {rec1, rec2});
		
		String[] urls = url.split(" ");
		
		assertEquals(2, urls.length);
		assertTrue(urls[0].startsWith("ldap://ldap1.test.com"));
		assertTrue(urls[1].startsWith("ldap://ldap2.test.com"));		
	}	
	
	public void testCreateLDAPUrl_multipleSRVRecord_ascendingPriority_assertPriorityOrderDesc() throws Exception
	{
		LdapPublicCertUtilImpl impl = new LdapPublicCertUtilImpl();
	
		SRVRecord rec1 = new SRVRecord(new Name("test.com."), DClass.IN, 3600, 1,
				  1, 339, new Name("ldap1.test.com."));
		
		SRVRecord rec2 = new SRVRecord(new Name("test.com."), DClass.IN, 3600, 0,
				  1, 339, new Name("ldap2.test.com."));		
		
		String url = impl.createLDAPUrl(new Record[] {rec1, rec2});
		
		String[] urls = url.split(" ");
		
		assertEquals(2, urls.length);
		assertTrue(urls[0].startsWith("ldap://ldap2.test.com"));
		assertTrue(urls[1].startsWith("ldap://ldap1.test.com"));		
	}		
}
