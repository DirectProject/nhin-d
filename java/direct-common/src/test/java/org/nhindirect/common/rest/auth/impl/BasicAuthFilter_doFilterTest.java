package org.nhindirect.common.rest.auth.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.NHINDPrincipal;

public class BasicAuthFilter_doFilterTest 
{
	protected String buildRawCredential(String user, String password)
	{
		final String basicAuthCredFormat = user + ":" + password;
		
		return "Basic " + Base64.encodeBase64String(basicAuthCredFormat.getBytes());
	}
	
	protected BasicAuthValidator buildValidator()
	{
		final BasicAuthCredential cred = new DefaultBasicAuthCredential("gm2552", "password", "admin");
		
		final List<BasicAuthCredential> credentials = Arrays.asList(cred);
		
		final BasicAuthCredentialStore store = new BootstrapBasicAuthCredentialStore(credentials);
		
		return new HashableBasicAuthValidator(store);
	}
	
	protected BasicAuthFilter buildFilter()
	{
		final BasicAuthFilter filter = new BasicAuthFilter();
		
		filter.setAllowSessions(true);
		filter.setForceSSL(false);
		filter.setBasicAuthValidator(buildValidator());
		
		return filter;
	}
	
	@Test
	public void testDoFilter_noSessionOrPrincipal() throws Exception
	{		
		final HttpSession session = mock(HttpSession.class);
		
		final BasicAuthFilter filter = buildFilter();
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);
		when(request.getHeader("Authorization")).thenReturn(buildRawCredential("gm2552", "password"));
		when(request.getSession(true)).thenReturn(session);
		
		filter.doFilter(request, response, chain);
		
		verify(chain, times(1)).doFilter((HttpServletRequest)any(), eq(response));
		verify(session, times(1)).setAttribute(eq("NHINDAuthPrincipalAttr"), (Principal)any());
		verify(response, never()).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
	}
	
	@Test
	public void testDoFilter_sessionsNotAllowed() throws Exception
	{		
		final HttpSession session = mock(HttpSession.class);
		
		final BasicAuthFilter filter = buildFilter();
		filter.setAllowSessions(false);
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);
		when(request.getHeader("Authorization")).thenReturn(buildRawCredential("gm2552", "password"));
		when(request.getSession(true)).thenReturn(session);
		
		filter.doFilter(request, response, chain);
		
		verify(chain, times(1)).doFilter((HttpServletRequest)any(), eq(response));
		verify(session, never()).setAttribute(eq("NHINDAuthPrincipalAttr"), (Principal)any());
		verify(response, never()).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
	}	
	
	@Test
	public void testDoFilter_exsitingPrincipal() throws Exception
	{		
		final HttpSession session = mock(HttpSession.class);
		
		final BasicAuthFilter filter = buildFilter();
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);
		when(request.getUserPrincipal()).thenReturn(new NHINDPrincipal("gm2552", "admin"));
		
		filter.doFilter(request, response, chain);
		
		verify(chain, times(1)).doFilter((HttpServletRequest)any(), eq(response));
		verify(request, never()).getSession(eq(true));
		verify(session, never()).setAttribute(eq("NHINDAuthPrincipalAttr"), (Principal)any());
		verify(response, never()).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
		verify(request, never()).getHeader(eq("Authorization"));
	}
	
	@Test
	public void testDoFilter_existingSession() throws Exception
	{		
		final HttpSession session = mock(HttpSession.class);
		
		final BasicAuthFilter filter = buildFilter();
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);

		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute("NHINDAuthPrincipalAttr")).thenReturn(new NHINDPrincipal("gm2552", "admin"));
		
		filter.doFilter(request, response, chain);
		
		verify(chain, times(1)).doFilter((HttpServletRequest)any(), eq(response));
		verify(session, never()).setAttribute(eq("NHINDAuthPrincipalAttr"), (Principal)any());
		verify(response, never()).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
		verify(request, never()).getHeader(eq("Authorization"));
	}
	
	@Test
	public void testDoFilter_nonSSLConnectionDisallowed_assertForbidden() throws Exception
	{		
		final HttpSession session = mock(HttpSession.class);
		
		final BasicAuthFilter filter = buildFilter();
		filter.setForceSSL(true);
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);
		
		filter.doFilter(request, response, chain);
		
		verify(chain, never()).doFilter((HttpServletRequest)any(), eq(response));
		verify(session, never()).setAttribute(eq("NHINDAuthPrincipalAttr"), (Principal)any());
		verify(response, times(1)).sendError(eq(HttpServletResponse.SC_FORBIDDEN));
		verify(request, never()).getHeader(eq("Authorization"));
	}
	
	@Test
	public void testDoFilter_nonBasicAuthScheme_assertUnauthorized() throws Exception
	{		
		final HttpSession session = mock(HttpSession.class);
		
		final BasicAuthFilter filter = buildFilter();
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);
		when(request.getHeader("Authorization")).thenReturn("OAuth 39843");
		when(request.getSession(true)).thenReturn(session);
		
		filter.doFilter(request, response, chain);
		
		verify(chain, never()).doFilter((HttpServletRequest)any(), eq(response));
		verify(request, times(1)).getHeader(eq("Authorization"));
		verify(session, never()).setAttribute(eq("NHINDAuthPrincipalAttr"), (Principal)any());
		verify(response, times(1)).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
	}	
	
	@Test
	public void testDoFilter_invalidCredentials_assertUnauthorized() throws Exception
	{		
		final HttpSession session = mock(HttpSession.class);
		
		final BasicAuthFilter filter = buildFilter();
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);
		final FilterChain chain = mock(FilterChain.class);
		when(request.getHeader("Authorization")).thenReturn(buildRawCredential("gm2552", "password1"));
		when(request.getSession(true)).thenReturn(session);
		
		filter.doFilter(request, response, chain);
		
		verify(chain, never()).doFilter((HttpServletRequest)any(), eq(response));
		verify(request, times(1)).getHeader(eq("Authorization"));
		verify(session, never()).setAttribute(eq("NHINDAuthPrincipalAttr"), (Principal)any());
		verify(response, times(1)).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
	}	
}
