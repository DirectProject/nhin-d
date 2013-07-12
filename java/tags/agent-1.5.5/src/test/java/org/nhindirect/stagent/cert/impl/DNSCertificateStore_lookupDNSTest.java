package org.nhindirect.stagent.cert.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nhindirect.stagent.cert.CertCacheFactory;
import org.nhindirect.stagent.utils.TestUtils;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Section;

import junit.framework.TestCase;

public class DNSCertificateStore_lookupDNSTest extends TestCase
{
	protected String filePrefix;
	
	@Override
	public void setUp()
	{
		// flush the caches
		CertCacheFactory.getInstance().flushAll();
		
		// check for Windows... it doens't like file://<drive>... turns it into FTP
		File file = new File("./src/test/resources/certs/certCheckA.der");
		if (file.getAbsolutePath().contains(":/"))
			filePrefix = "file:///";
		else
			filePrefix = "file:///";
	}
	
	protected Answer<Message> getPKIXAnswer(final byte[] certData)
	{
		final Answer<Message> retVal = new Answer<Message>()
		{
			public Message answer(InvocationOnMock invocation) throws Throwable 
			{
				final Message request = (Message)invocation.getArguments()[0];
				final Message response = new Message(request.getHeader().getID());
		        response.getHeader().setFlag(Flags.QR);
		    	if (request.getHeader().getFlag(Flags.RD))
		    		response.getHeader().setFlag(Flags.RD);
		    	response.addRecord(request.getQuestion(), Section.QUESTION);				
				
		    	if (certData != null)
		    	{
			    	
					final CERTRecord rec = new CERTRecord(request.getQuestion().getName(), DClass.IN, 86400L, CERTRecord.PKIX, 0, 
							5 /*public key alg, RFC 4034*/, certData);
					response.addRecord(rec, Section.ANSWER);
		    	}
		    	else
		    	{
		    		response.getHeader().setRcode(Rcode.NXDOMAIN);
		    	}

		    	
		    	// we are authoritative only
		    	response.getHeader().setFlag(Flags.AA);
				
		    	return response;
		    }
		};
		
		return retVal;
	}
	
	protected Answer<Message> getIPKIXAnswer(final String url)
	{
		final Answer<Message> retVal = new Answer<Message>()
		{
			public Message answer(InvocationOnMock invocation) throws Throwable 
			{
				final Message request = (Message)invocation.getArguments()[0];
				final Message response = new Message(request.getHeader().getID());
		        response.getHeader().setFlag(Flags.QR);
		    	if (request.getHeader().getFlag(Flags.RD))
		    		response.getHeader().setFlag(Flags.RD);
		    	response.addRecord(request.getQuestion(), Section.QUESTION);				
				
				final CERTRecord rec = new CERTRecord(request.getQuestion().getName(), DClass.IN, 86400L, CERTRecord.URI, 0, 
						5 /*public key alg, RFC 4034*/, url.getBytes());

				response.addRecord(rec, Section.ANSWER);
		    	
		    	// we are authoritative only
		    	response.getHeader().setFlag(Flags.AA);
				
		    	return response;
		    }
		};
		
		return retVal;
	}
	
	
	public void testLookupDNS_certInRRRecord_assertCertificate() throws Exception
	{
		final X509Certificate cert = (X509Certificate)TestUtils.loadCertificate("certCheckA.der");
		
		final ExtendedResolver resolver = mock(ExtendedResolver.class);
		when(resolver.send((Message )any())).thenAnswer(getPKIXAnswer(cert.getEncoded()));
		
		final DNSCertificateStore store = new DNSCertificateStore()
		{
			protected ExtendedResolver createExResolver(String[] servers, int retries, int timeout)
			{
				return resolver;
			}
		};
		
		Collection<X509Certificate> certs = store.lookupDNS("somedomain.com");
		
		assertNotNull(certs);
		assertEquals(1, certs.size());
		assertEquals(cert, certs.iterator().next());
		
	}
	
	public void testLookupDNS_certNotInRRRecord_assertNoCertificate() throws Exception
	{
		
		final ExtendedResolver resolver = mock(ExtendedResolver.class);
		when(resolver.send((Message )any())).thenAnswer(getPKIXAnswer(null));
		
		final DNSCertificateStore store = new DNSCertificateStore()
		{
			protected ExtendedResolver createExResolver(String[] servers, int retries, int timeout)
			{
				return resolver;
			}
		};
		
		Collection<X509Certificate> certs = store.lookupDNS("somedomain2.com");
		
		assertNotNull(certs);
		assertTrue(certs.isEmpty());
	}
	
	
	public void testLookupDNS_certInIPKIXRecord_assertCertificate() throws Exception
	{
		final Certificate cert = TestUtils.loadCertificate("gm2552.der");
		final File certFile = new File("./src/test/resources/certs/gm2552.der");
		final String url = filePrefix + certFile.getAbsolutePath();
		
		final ExtendedResolver resolver = mock(ExtendedResolver.class);
		when(resolver.send((Message )any())).thenAnswer(getIPKIXAnswer(url));
		
		final DNSCertificateStore store = new DNSCertificateStore()
		{
			protected ExtendedResolver createExResolver(String[] servers, int retries, int timeout)
			{
				return resolver;
			}
		};
		
		Collection<X509Certificate> certs = store.lookupDNS("somedomain3.com");
		
		assertNotNull(certs);
		assertEquals(1, certs.size());
		assertEquals(cert, certs.iterator().next());
		
	}
	
}
