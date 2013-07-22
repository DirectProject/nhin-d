package org.nhindirect.stagent;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.policy.PolicyResolver;
import org.nhindirect.stagent.trust.TrustAnchorResolver;

import junit.framework.TestCase;

public class DefaultNHINDAgent_filterCertificateByPolicyTest extends TestCase
{
	public void testFilterCertificateByPolicy_nullResolver_assertNoCertsFiltered() throws Exception
	{
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));
		
		Collection<X509Certificate> filteredCerts = agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), null, certs, true);
		
		assertEquals(1, filteredCerts.size());
	}
	
	public void testFilterCertificateByPolicy_noIncomingExpressions_assertNoCertsFiltered() throws Exception
	{
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(new ArrayList<PolicyExpression>());
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));

		
		Collection<X509Certificate> filteredCerts = agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), resolver, certs, true);
		
		assertEquals(1, filteredCerts.size());
	}
	
	public void testFilterCertificateByPolicy_noOutgoingExpressions_assertNoCertsFiltered() throws Exception
	{
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getOutgoingPolicy((InternetAddress)any())).thenReturn(new ArrayList<PolicyExpression>());
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));

		
		Collection<X509Certificate> filteredCerts = agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), resolver, certs, false);
		
		assertEquals(1, filteredCerts.size());
	}
	
	public void testFilterCertificateByPolicy_incomingPolicyCompliant_assertNoCertsFiltered() throws Exception
	{
		final PolicyFilter filter = mock(PolicyFilter.class);
		when(filter.isCompliant((X509Certificate)any(), (PolicyExpression)any())).thenReturn(true);
		
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));
		agent.setPolicyFilter(filter);
		
		Collection<X509Certificate> filteredCerts = agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), resolver, certs, true);
		
		assertEquals(1, filteredCerts.size());
	}
	
	public void testFilterCertificateByPolicy_outgoingPolicyCompliant_assertNoCertsFiltered() throws Exception
	{
		final PolicyFilter filter = mock(PolicyFilter.class);
		when(filter.isCompliant((X509Certificate)any(), (PolicyExpression)any())).thenReturn(true);
		
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getOutgoingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));
		agent.setPolicyFilter(filter);
		
		Collection<X509Certificate> filteredCerts = agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), resolver, certs, false);
		
		assertEquals(1, filteredCerts.size());
	}	
	
	public void testFilterCertificateByPolicy_notCompliant_assertNoCertsFiltered() throws Exception
	{
		final PolicyFilter filter = mock(PolicyFilter.class);
		when(filter.isCompliant((X509Certificate)any(), (PolicyExpression)any())).thenReturn(false);
		
		
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));
		agent.setPolicyFilter(filter);
		
		Collection<X509Certificate> filteredCerts = agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), resolver, certs, true);
		
		assertEquals(0, filteredCerts.size());
	}	
	
	public void testFilterCertificateByPolicy_requiredFieldMissing_assertNoCertsFiltered() throws Exception
	{
		final PolicyFilter filter = mock(PolicyFilter.class);
		doThrow(new PolicyRequiredException("Just Passing Through")).when(filter).isCompliant((X509Certificate)any(), (PolicyExpression)any());
		
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));
		agent.setPolicyFilter(filter);
		
		Collection<X509Certificate> filteredCerts = agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), resolver, certs, true);
		
		assertEquals(0, filteredCerts.size());
	}	
	
	public void testFilterCertificateByPolicy_badPolicyExpression_assertNoCertsFiltered() throws Exception
	{
		final PolicyFilter filter = mock(PolicyFilter.class);
		doThrow(new PolicyProcessException("Just Passing Through")).when(filter).isCompliant((X509Certificate)any(), (PolicyExpression)any());
		
		final PolicyExpression expression = mock(PolicyExpression.class);
		final X509Certificate cert = mock(X509Certificate.class);
		final Collection<X509Certificate> certs = Arrays.asList(cert);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		final DefaultNHINDAgent agent = new DefaultNHINDAgent("", mock(CertificateResolver.class), 
				mock(CertificateResolver.class), mock(TrustAnchorResolver.class));
		agent.setPolicyFilter(filter);
		
		boolean exceptionOccured = false;
		try
		{
			agent.filterCertificatesByPolicy(new InternetAddress("me@you.com"), resolver, certs, true);
		}
		catch (AgentException e)
		{
			exceptionOccured = true;
		}
		assertTrue(exceptionOccured);
	}			
}
