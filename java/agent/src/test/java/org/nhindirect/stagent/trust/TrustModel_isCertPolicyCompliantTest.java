package org.nhindirect.stagent.trust;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.internet.InternetAddress;

import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.policy.PolicyProcessException;
import org.nhindirect.policy.PolicyRequiredException;
import org.nhindirect.stagent.AgentException;
import org.nhindirect.stagent.policy.PolicyResolver;

import junit.framework.TestCase;

public class TrustModel_isCertPolicyCompliantTest extends TestCase
{
	public void testIsCertPolicyCompliant_noResolver_assertTrue() throws Exception
	{
		final TrustModel model = new TrustModel();
		
		model.setTrustPolicyResolver(null);
		
		final X509Certificate cert = mock(X509Certificate.class);
		
		assertTrue(model.isCertPolicyCompliant(new InternetAddress("me@test.com"), cert));
	}
	
	public void testIsCertPolicyCompliant_noPolicyFilter_assertTrue() throws Exception
	{
		final TrustModel model = new TrustModel();
		
		model.setTrustPolicyResolver(mock(PolicyResolver.class));
		model.setPolicyFilter(null);
		
		final X509Certificate cert = mock(X509Certificate.class);
		
		assertTrue(model.isCertPolicyCompliant(new InternetAddress("me@test.com"), cert));
	}
	
	public void testIsCertPolicyCompliant_noPolicyExpression_assertTrue() throws Exception
	{
		final TrustModel model = new TrustModel();
		
		final PolicyResolver resolver = mock(PolicyResolver.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(new ArrayList<PolicyExpression>());
		
		model.setTrustPolicyResolver(resolver);
		
		final X509Certificate cert = mock(X509Certificate.class);
		
		assertTrue(model.isCertPolicyCompliant(new InternetAddress("me@test.com"), cert));
	}	
	
	public void testIsCertPolicyCompliant_policyCompliant_assertTrue() throws Exception
	{
		final TrustModel model = new TrustModel();
		
		final PolicyFilter filter = mock(PolicyFilter.class);
		when(filter.isCompliant((X509Certificate)any(), (PolicyExpression)any())).thenReturn(true);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		final PolicyExpression expression = mock(PolicyExpression.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		model.setTrustPolicyResolver(resolver);
		model.setPolicyFilter(filter);
		
		final X509Certificate cert = mock(X509Certificate.class);
		
		assertTrue(model.isCertPolicyCompliant(new InternetAddress("me@test.com"), cert));
	}	
	
	public void testIsCertPolicyCompliant_policyNotCompliant_assertFalse() throws Exception
	{
		final TrustModel model = new TrustModel();
		
		final PolicyFilter filter = mock(PolicyFilter.class);
		when(filter.isCompliant((X509Certificate)any(), (PolicyExpression)any())).thenReturn(false);
		final PolicyResolver resolver = mock(PolicyResolver.class);
		final PolicyExpression expression = mock(PolicyExpression.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		model.setTrustPolicyResolver(resolver);
		model.setPolicyFilter(filter);
		
		final X509Certificate cert = mock(X509Certificate.class);
		
		assertFalse(model.isCertPolicyCompliant(new InternetAddress("me@test.com"), cert));
	}	
	
	public void testIsCertPolicyCompliant_missingRequiredField_assertFalse() throws Exception
	{
		final TrustModel model = new TrustModel();
		
		final PolicyFilter filter = mock(PolicyFilter.class);
		doThrow(new PolicyRequiredException("Just Passing Through")).when(filter).isCompliant((X509Certificate)any(), (PolicyExpression)any());
		
		final PolicyResolver resolver = mock(PolicyResolver.class);
		final PolicyExpression expression = mock(PolicyExpression.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		model.setTrustPolicyResolver(resolver);
		model.setPolicyFilter(filter);
		
		final X509Certificate cert = mock(X509Certificate.class);
		
		assertFalse(model.isCertPolicyCompliant(new InternetAddress("me@test.com"), cert));
	}	
	
	public void testIsCertPolicyCompliant_policyExpressionError_assertExecption() throws Exception
	{
		final TrustModel model = new TrustModel();
		
		final PolicyFilter filter = mock(PolicyFilter.class);
		doThrow(new PolicyProcessException("Just Passing Through")).when(filter).isCompliant((X509Certificate)any(), (PolicyExpression)any());
		
		final PolicyResolver resolver = mock(PolicyResolver.class);
		final PolicyExpression expression = mock(PolicyExpression.class);
		when(resolver.getIncomingPolicy((InternetAddress)any())).thenReturn(Arrays.asList(expression));
		
		model.setTrustPolicyResolver(resolver);
		model.setPolicyFilter(filter);
		
		final X509Certificate cert = mock(X509Certificate.class);
		
		boolean exceptionOccured = false;
		try
		{	
			model.isCertPolicyCompliant(new InternetAddress("me@test.com"), cert);
		}
		catch (AgentException e)
		{
			exceptionOccured = true;
		}
		
		assertTrue(exceptionOccured);
	}		
}
