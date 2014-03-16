package org.nhindirect.stagent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.mail.internet.InternetAddress;

import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.policy.PolicyResolver;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class DefaultNHINDAgent_bindAddressesIncomingMessageTest extends TestCase
{
	protected IncomingMessage buildIncomingMessage(String fileName) throws Exception
	{
		final String testMessage = TestUtils.readResource(fileName);
		final Message incoming = new Message(new ByteArrayInputStream(testMessage.getBytes("ASCII")));
		
		return new IncomingMessage(incoming);
	}
	
	// PUBLIC CERTS ARE NO LONGER RESOLVED FOR INCOMING MESSAGES
	// THEY ARE ALWAYS EXTRACTED FROM THE CERTIFICATE, SO PULBIC RESOLUTION IS REDUNDANT
	/*
	public void testBindAddresses_noPublicCerts_assertNoBoundPublicCerts() throws Exception
	{

		
		final CertificateResolver publicCertResolver = mock(CertificateResolver.class);
		when(publicCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final CertificateResolver privateCertResolver = mock(CertificateResolver.class);
		when(privateCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("starugh-stateline.com", privateCertResolver, 
				publicCertResolver, mock(TrustAnchorResolver.class));
		
		final IncomingMessage message = buildIncomingMessage("MultipartMimeMessage.txt");
		message.setAgent(agent);
		
		agent.bindAddresses(message);
		
		assertEquals(0, message.getSender().getCertificates().size());
	}
	
	public void testBindAddresses_singlePublicCerts_notFiltered_assertSingleBoundPublicCerts() throws Exception
	{
		final X509Certificate cert = mock(X509Certificate.class);

		final CertificateResolver publicCertResolver = mock(CertificateResolver.class);
		when(publicCertResolver.getCertificates((InternetAddress)any())).thenReturn(Arrays.asList(cert));
		
		final CertificateResolver privateCertResolver = mock(CertificateResolver.class);
		when(privateCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("starugh-stateline.com", privateCertResolver, 
				publicCertResolver, mock(TrustAnchorResolver.class));
		
		final IncomingMessage message = buildIncomingMessage("MultipartMimeMessage.txt");
		message.setAgent(agent);
		
		agent.bindAddresses(message);
		
		assertEquals(1, message.getSender().getCertificates().size());
	}	
	
	public void testBindAddresses_singlePublicCerts_policyFiltered_assertNoBoundPublicCerts() throws Exception
	{
		final X509Certificate cert = mock(X509Certificate.class);

		final CertificateResolver publicCertResolver = mock(CertificateResolver.class);
		when(publicCertResolver.getCertificates((InternetAddress)any())).thenReturn(Arrays.asList(cert));
		
		final CertificateResolver privateCertResolver = mock(CertificateResolver.class);
		when(privateCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("starugh-stateline.com", privateCertResolver, 
				publicCertResolver, mock(TrustAnchorResolver.class))
		{
			@Override 
			protected Collection<X509Certificate> filterCertificatesByPolicy(InternetAddress sender, PolicyResolver resolver, 
		    		Collection<X509Certificate> certsToFilter, boolean incoming)
		    {
				return Collections.emptyList();
		    }
		};
		
		final IncomingMessage message = buildIncomingMessage("MultipartMimeMessage.txt");
		message.setAgent(agent);
		
		agent.bindAddresses(message);
		
		assertEquals(0, message.getSender().getCertificates().size());
	}
	*/
	public void testBindAddresses_noPrivateCerts_assertNoBoundPrivateCerts() throws Exception
	{		
		final CertificateResolver publicCertResolver = mock(CertificateResolver.class);
		when(publicCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final CertificateResolver privateCertResolver = mock(CertificateResolver.class);
		when(privateCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("starugh-stateline.com", privateCertResolver, 
				publicCertResolver, mock(TrustAnchorResolver.class));
		
		final IncomingMessage message = buildIncomingMessage("MultipartMimeMessage.txt");
		message.setAgent(agent);
		
		agent.bindAddresses(message);
		
		for(NHINDAddress recipient : message.getRecipients())
			assertEquals(0, recipient.getCertificates().size());
	}
	
	public void testBindAddresses_singlePrivateCerts_notFiltered_assertBoundPrivateCerts() throws Exception
	{		
		final X509Certificate cert = mock(X509Certificate.class);
		
		final CertificateResolver publicCertResolver = mock(CertificateResolver.class);
		when(publicCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final CertificateResolver privateCertResolver = mock(CertificateResolver.class);
		when(privateCertResolver.getCertificates((InternetAddress)any())).thenReturn(Arrays.asList(cert));
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("starugh-stateline.com", privateCertResolver, 
				publicCertResolver, mock(TrustAnchorResolver.class));
		
		final IncomingMessage message = buildIncomingMessage("MultipartMimeMessage.txt");
		message.setAgent(agent);
		
		agent.bindAddresses(message);
		
		for(NHINDAddress recipient : message.getRecipients())
			assertEquals(1, recipient.getCertificates().size());
	}
	
	public void testBindAddresses_singlePrivateCerts_plicyFiltered_assertNoBoundPrivateCerts() throws Exception
	{		
		final X509Certificate cert = mock(X509Certificate.class);
		
		final CertificateResolver publicCertResolver = mock(CertificateResolver.class);
		when(publicCertResolver.getCertificates((InternetAddress)any())).thenReturn(new ArrayList<X509Certificate>());
		
		final CertificateResolver privateCertResolver = mock(CertificateResolver.class);
		when(privateCertResolver.getCertificates((InternetAddress)any())).thenReturn(Arrays.asList(cert));
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("starugh-stateline.com", privateCertResolver, 
				publicCertResolver, mock(TrustAnchorResolver.class))
		{
			@Override 
			protected Collection<X509Certificate> filterCertificatesByPolicy(InternetAddress sender, PolicyResolver resolver, 
		    		Collection<X509Certificate> certsToFilter, boolean incoming)
		    {
				return Collections.emptyList();
		    }
		};
		
		final IncomingMessage message = buildIncomingMessage("MultipartMimeMessage.txt");
		message.setAgent(agent);
		
		agent.bindAddresses(message);
		
		for(NHINDAddress recipient : message.getRecipients())
			assertEquals(0, recipient.getCertificates().size());
	}
}
