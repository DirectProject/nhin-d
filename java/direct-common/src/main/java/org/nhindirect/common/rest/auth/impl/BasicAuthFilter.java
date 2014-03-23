package org.nhindirect.common.rest.auth.impl;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;


public class BasicAuthFilter implements Filter
{
	protected static final String SESSION_PRINCIPAL_ATTRIBUTE = "NHINDAuthPrincipalAttr";
	
    protected boolean allowSessions = true;
    
    protected BasicAuthValidator validator;
    
    protected boolean forceSSL = false;
    
    public BasicAuthFilter()
    {
    	
    }
    
    public BasicAuthFilter(BasicAuthValidator validator)
    {
    	this.validator = validator;
    }
    
    public void setBasicAuthValidator(BasicAuthValidator validator)
    {
    	this.validator = validator;
    }
    
    public void setForceSSL(boolean forceSSL)
    {
    	this.forceSSL = forceSSL;
    }
    
    public void setAllowSessions(boolean allowSessions)
    {
    	this.allowSessions = allowSessions;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException 
    {
    	
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // make sure the connection is secure unless configured differently
        if(forceSSL && !request.isSecure()) 
        {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        if (isPrincipal(httpRequest))
        {
        	// a previous authentication in the chain has occurred
        	// let the chain continue
            chain.doFilter(request, response);
            return;
        }
        
        // check to see if a sessions has already been established with this server (sessions may not be allowed depending on configuration)
        if(allowSessions) 
        {
            final HttpSession session = httpRequest.getSession(true);
            final Principal sessionPrin = (Principal) session.getAttribute(SESSION_PRINCIPAL_ATTRIBUTE);
            if(sessionPrin != null) 
            {
                // move along with the request
                final HttpServletRequest wrappedRequest = isPrincipal(httpRequest) ? httpRequest : new PrincipalOverrideRequestWrapper(httpRequest, sessionPrin);
                chain.doFilter(wrappedRequest, response);
                return;
            }
        }
        
        // now time to do the auth
        // get the auth header
        final String authHeader = httpRequest.getHeader("Authorization");
        
        if(authHeader != null && authHeader.toUpperCase(Locale.getDefault()).startsWith("BASIC")) 
        {

        	Principal princ;
        	try
        	{
        		princ = validator.authenticate(authHeader);
        	}
        	catch (BasicAuthException e)
        	{
        		// failure, invalid credential or unknown user
                final String scheme = httpRequest.isSecure() ? "https://" : "http://";
                final String realm = scheme + httpRequest.getLocalName();
                httpResponse.setHeader("WWW-Authenticate", "BASIC " + realm);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
        	}
        	
            // create a new principle and add it the request if one does not already exist
            if(allowSessions) 
            {
                final HttpSession session = httpRequest.getSession(true);
                session.setAttribute(SESSION_PRINCIPAL_ATTRIBUTE, princ);
            }

            final HttpServletRequest wrappedRequest = isPrincipal(httpRequest) ? httpRequest : new PrincipalOverrideRequestWrapper(httpRequest, princ);
            chain.doFilter(wrappedRequest, httpResponse);

            return;
        }
        
        // else reject the request since it's a not a request we handle
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
    
    /*
     * Checks if a principal already exists in the request. This may have occured from a previous authentication
     * in the security chain.
     */
    protected boolean isPrincipal(final HttpServletRequest httpRequest) 
    {
        return httpRequest.getUserPrincipal() != null;
    }    
    
    /**
     * Borrowed from oAuth projects. Wraps the request with a new principal object.
     * 
     */
    protected static class PrincipalOverrideRequestWrapper extends HttpServletRequestWrapper 
    {

        private final Principal principal;

        public PrincipalOverrideRequestWrapper(final HttpServletRequest request, final Principal principal) 
        {
            super(request);
            this.principal = principal;
        }

        @Override
        public String getRemoteUser() 
        {
            return principal == null ? null : principal.getName();
        }

        @Override
        public Principal getUserPrincipal() 
        {
            return principal;
        }
    }

	@Override
	public void destroy() 
	{
		
	}    
}
