/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

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

import org.apache.commons.lang.StringUtils;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;

/**
 * A BasicAuth servlet filter that utilizes a BasicAuthValidator for authenticating requests.  It also provides other configuration parameters
 * for integration with other filters and security parameters.
 * @author Greg Meyer
 * @since 1.3
 */
public class BasicAuthFilter implements Filter
{
	protected static final String SESSION_PRINCIPAL_ATTRIBUTE = "NHINDAuthPrincipalAttr";
	
    protected boolean allowSessions = true;
    
    protected BasicAuthValidator validator;
    
    protected boolean forceSSL = false;
    
    /**
     * Constructor
     */
    public BasicAuthFilter()
    {
    	
    }
    
    /**
     * Constructs a filter with a BasicAuthValidator
     * @param validator The validator used to authenticate requests.
     */
    public BasicAuthFilter(BasicAuthValidator validator)
    {
    	this.validator = validator;
    }
    
    /**
     * Sets the validator for authenticating requests.
     * @param validator The validator for authenticating requests.
     */
    public void setBasicAuthValidator(BasicAuthValidator validator)
    {
    	this.validator = validator;
    }
    
    /**
     * Sets the flag for forcing requests to utilize SSL.  If this flag is false, then all non-SSL requests will be rejected.
     * @param forceSSL Boolean value for forcing requests to use SSL.
     */
    public void setForceSSL(boolean forceSSL)
    {
    	this.forceSSL = forceSSL;
    }
    
    /**
     * Sets the flag for allowing HTTP sessions to be used.  If this flag is true, subsequent HTTP requests can use an existing authenticated session instead
     * performing BasicAuth validation on every request.  If flase, every request must contain BasicAuth credentials that will be validated.
     * @param allowSessions Boolean value for allowing authenticated sessions to be utilized
     */
    public void setAllowSessions(boolean allowSessions)
    {
    	this.allowSessions = allowSessions;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException 
    {
    	
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
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
        
        final String str = httpRequest.getHeader("origin");
        final String origin = (StringUtils.isEmpty(str)) ? "*" : str;
        
    	httpResponse.setHeader("Access-Control-Allow-Origin", origin);
    	httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
    	httpResponse.setHeader("Access-Control-Allow-Methods:", "POST, PUT, DELETE, GET, OPTIONS");
        // check for the options parameters
        if (httpRequest.getMethod() != null && httpRequest.getMethod().compareToIgnoreCase("options") == 0)
        {
        	httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        	httpResponse.sendError(HttpServletResponse.SC_OK);
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

    /**
     * {@inheritDoc}
     */
    @Override
	public void destroy() 
	{
		
	}    
}
