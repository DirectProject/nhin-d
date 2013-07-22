package org.nhindirect.stagent;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.InternetAddress;

import junit.framework.TestCase;

public class NHINDAddressTest extends TestCase 
{
	public void testSetGetPersonllAttribute() throws Exception
	{
		NHINDAddress address = new NHINDAddress("Greg Meyer <gm2552@cerner.com>");
		
		assertEquals("gm2552@cerner.com", address.getAddress().toString());
		assertEquals("Greg Meyer", address.getPersonal());
		
		List<X509Certificate> empty = Collections.emptyList();
		
		address = new NHINDAddress("Greg Meyer <gm2552@cerner.com>", empty);
		
		assertEquals("gm2552@cerner.com", address.getAddress().toString());
		assertEquals("Greg Meyer", address.getPersonal());	
		
		
		address = new NHINDAddress(new InternetAddress("Greg Meyer <gm2552@cerner.com>"));
		
		assertEquals("gm2552@cerner.com", address.getAddress().toString());
		assertEquals("Greg Meyer", address.getPersonal());			
	}
}
