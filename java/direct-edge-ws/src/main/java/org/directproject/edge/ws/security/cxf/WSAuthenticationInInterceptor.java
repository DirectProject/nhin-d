package org.directproject.edge.ws.security.cxf;
/* 
 * Copyright (c) 2010, The Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSUsernameTokenPrincipal;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Adopted from code available at: http://forum.springsource.org/showthread.php?t=64492
 * 
 * This class uses spring-security resources to validate a usernametoken and store the
 * resulting user context into something that CXF can access.
 * 
 */
public class WSAuthenticationInInterceptor extends WSS4JInInterceptor implements
        InitializingBean 
{
    private static final Log _log = LogFactory.getLog(WSAuthenticationInInterceptor.class);
    
    private AuthenticationManager authenticationManager;
    
    public WSAuthenticationInInterceptor(Map<String, Object> properties)
    {
        super(properties);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception 
    {
        if (_log.isDebugEnabled()) _log.debug("Initialized Authentication Interceptor");
        super.getSecurityEngine().getWssConfig().setAllowNamespaceQualifiedPasswordTypes(true); 
    }


    public AuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }
    
    public void setAuthenticationManager(AuthenticationManager aMgr)
    {
        authenticationManager = aMgr;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    /**
     * Extract the username/password from the incoming message, 
     * validate it, and store the user context where CXF can get at it.
     */
    public void handleMessage(SoapMessage message) throws Fault 
    {
        try 
        {
            // Let the WSS4J parent do it's thing first
            super.handleMessage(message);
            
            Vector<WSHandlerResult> results = (Vector<WSHandlerResult>) message.getContextualProperty(WSHandlerConstants.RECV_RESULTS);
            
            if (results != null && !results.isEmpty()) 
            {
                for (WSHandlerResult result : results) 
                {
                    // loop through security engine results
                    for (WSSecurityEngineResult securityResult : (Vector<WSSecurityEngineResult>) result.getResults()) 
                    {
                        int action = (Integer) securityResult.get(WSSecurityEngineResult.TAG_ACTION);
                        
                        // Was this a usernametoken
                        if (action == WSConstants.UT) 
                        {
                            WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal) securityResult.get(WSSecurityEngineResult.TAG_PRINCIPAL);
                            if (principal.getPassword()==null)
                            {
                                principal.setPassword("");
                            }
                            
                            Authentication auth = new UsernamePasswordAuthenticationToken(principal.getName(), principal.getPassword());
                            auth = getAuthenticationManager().authenticate(auth);

                            if (auth.isAuthenticated()) 
                            {
                                _log.info("Authentication succeeds for request: User: " + principal.getName());
                            }
                            else
                            {
                                _log.warn("Authentication failed for request:  User: " + principal.getName());
                            }
                            
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }
        } 
        catch (RuntimeException ex) 
        {
            _log.error("Runtime Exception caught:", ex);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
        }
        
    }
}
