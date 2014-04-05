package org.nhindirect.stagent.trust;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.trust.TrustChainValidator_getIntermediateCertsByAIATest.TrustChainValidatorWrapper;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class TrustChainValidator_resolveIssuersTest extends TestCase
{
	public void testResolveIssuers_AIAExists_validateResolved() throws Exception
	{
	
		final TrustChainValidatorWrapper validator = new TrustChainValidatorWrapper()
		{
			protected X509Certificate downloadCertFromAIA(String url) throws NHINDException
			{
				try
				{
					retrievedURL = url;
					return TestUtils.loadCertificate("CernerDirect Cert Professional Community CA.der");
				}
				catch (Exception e){throw new NHINDException(e);}
			}
		};
				
    	final Collection<X509Certificate> resolvedIssuers = new ArrayList<X509Certificate>();
    	final Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
    	final TrustChainValidatorWrapper spyValidator = spy(validator);
		
		
		spyValidator.resolveIssuers(TestUtils.loadCertificate("demo.sandboxcernerdirect.com.der"), resolvedIssuers, 0, anchors);
		
		assertEquals(1, resolvedIssuers.size());
		assertEquals(TestUtils.loadCertificate("CernerDirect Cert Professional Community CA.der"), resolvedIssuers.iterator().next());
		
		verify(spyValidator, times(2)).downloadCertFromAIA((String)any());
	}
	
	public void testResolveIssuers_AIAExists_resolveToRoot_validateResolved() throws Exception
	{
	
		final TrustChainValidatorWrapper validator = new TrustChainValidatorWrapper()
		{
			protected X509Certificate downloadCertFromAIA(String url) throws NHINDException
			{
				try
				{
					if (url.contains("sandbox"))
						return TestUtils.loadCertificate("CernerDirect Cert Professional Community CA.der");
					else
						return TestUtils.loadCertificate("CernerRoot.der");
				}
				catch (Exception e){throw new NHINDException(e);}
			}
		};
				
    	final Collection<X509Certificate> resolvedIssuers = new ArrayList<X509Certificate>();
    	final Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
    	final TrustChainValidatorWrapper spyValidator = spy(validator);
		
		
		spyValidator.resolveIssuers(TestUtils.loadCertificate("demo.sandboxcernerdirect.com.der"), resolvedIssuers, 0, anchors);
		
		assertEquals(2, resolvedIssuers.size());
		Iterator<X509Certificate> iter = resolvedIssuers.iterator();
		assertEquals(TestUtils.loadCertificate("CernerDirect Cert Professional Community CA.der"), iter.next());
		assertEquals(TestUtils.loadCertificate("CernerRoot.der"), iter.next());
		
		verify(spyValidator, times(2)).downloadCertFromAIA((String)any());
	}
	
	public void testResolveIssuers_noAIAExists_notAvailViaResolver_validateNotResolved() throws Exception
	{
	
		final TrustChainValidatorWrapper validator = new TrustChainValidatorWrapper()
		{
			protected X509Certificate downloadCertFromAIA(String url) throws NHINDException
			{
				throw new NHINDException();
			}
		};
		
		validator.setCertificateResolver(new ArrayList<CertificateResolver>());
				
    	final Collection<X509Certificate> resolvedIssuers = new ArrayList<X509Certificate>();
    	final Collection<X509Certificate> anchors = new ArrayList<X509Certificate>();
    	final TrustChainValidatorWrapper spyValidator = spy(validator);
		
		
		spyValidator.resolveIssuers(TestUtils.loadCertificate("altNameOnly.der"), resolvedIssuers, 0, anchors);
		
		assertEquals(0, resolvedIssuers.size());
		
		verify(spyValidator, times(0)).downloadCertFromAIA((String)any());
	}
}
