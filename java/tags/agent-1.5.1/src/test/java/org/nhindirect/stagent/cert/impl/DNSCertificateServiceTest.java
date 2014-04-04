package org.nhindirect.stagent.cert.impl;

import java.net.InetAddress;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.xbill.DNS.Options;

import junit.framework.TestCase;

public class DNSCertificateServiceTest extends TestCase
{
	public void testRemoteCertLookup() throws Exception
	{
		/*
		* only run this test if we can resolve the address nhind.hsgincubator.com....
		* we may not be able to run it if
		* a) we have no connection to the public internet (or DNS ports/protocols are blocked)
		* b) nhind.hsgincubator.com DNS servers are down
		* c) nhind.hsgincubator.com no longer exists (it was a temporary site wasn't it?)
		* d) our local machine's DNS server(s) can't resolve nhind.hsgincubator.com for some reason
		*/
		
		try
		{
			InetAddress addr = InetAddress.getByName("nhind.hsgincubator.com");
			if (addr == null || addr.getHostAddress() == null || addr.getHostAddress().length() == 0)
				return; // bail
		}
		catch (Exception e)
		{
			// can't resolve... bail
			return;
		}
		
		DNSCertificateStore service = new DNSCertificateStore();
		
		Options.set("verbose", "true");
		Collection<X509Certificate> certs =  service.getCertificates("biff@nhind.hsgincubator.com");
	
		if (certs.size() == 0)
			return; // this server may not always be there... bail if so
		
		assertEquals(1, certs.size());
		assertTrue(service.contains(certs.iterator().next()));
		
		
		certs =  service.getCertificates("nhind.hsgincubator.com");
		
		assertTrue(certs.size() > 0);
		assertTrue(service.contains(certs.iterator().next()));
		
	}
}
