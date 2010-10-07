/* 
 * Copyright (c) 2010, NHIN Direct Project
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

package org.nhindirect.nhindclient.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.nhindirect.DirectMessage;
import org.nhindirect.nhindclient.NHINDClient;
import org.nhindirect.routing.RoutingResolver;
import org.nhindirect.routing.impl.RoutingResolverImpl;
import org.nhindirect.xdclient.XDClient;
import org.nhindirect.xdm.XDMMailClient;

/**
 * 
 * @author vlewis
 */
public class NHINDClientImpl implements NHINDClient
{
    private RoutingResolver routingResolver = new RoutingResolverImpl();

    private String smtpHost;

    /**
     * @param smtpHost
     */
    public NHINDClientImpl(String smtpHost)
    {
        this.smtpHost = smtpHost;
    }

    /* (non-Javadoc)
     * @see org.nhindirect.nhindclient.NHINDClient#send(org.nhindirect.DirectMessage)
     */
    public String send(DirectMessage message) throws Exception
    {
        // SMTP endpoints
        if (routingResolver.hasSmtpEndpoints(message.getReceivers()))
        {
            XDMMailClient xmc = new XDMMailClient(smtpHost);

            Collection<String> endpoints = routingResolver.getSmtpEndpoints(message.getReceivers());

            try
            {
                xmc.sendMail(message.getSender(), endpoints, message.getDocuments(), message.getBody(), "xml");
            }
            catch (MessagingException e)
            {
                throw new Exception("Unable to send documents to SMTP endpoints " + endpoints, e);
            }
        }

        // XD endpoints
        if (routingResolver.hasXdEndpoints(message.getReceivers()))
        {
            for (String endpoint : routingResolver.getXdEndpoints(message.getReceivers()))
            {
                XDClient xdc = new XDClient();

                try
                {
                    xdc.send(endpoint, message.getDocuments());
                }
                catch (Exception e)
                {
                    throw new Exception("Unable to send documents to XD endpoint " + endpoint, e);
                }
            }
        }

        return "done";
    }

    /* (non-Javadoc)
     * @see org.nhindirect.nhindclient.NHINDClient#send(java.lang.String, java.lang.String, java.util.ArrayList, java.lang.String)
     */
    @Deprecated
    public String send(String endpoint, String metadata, ArrayList<String> docs, String messageId) throws Exception
    {
        // TODO not sure if the endpoint should be a param or derived from the
        // metadata
        if (StringUtils.isBlank(endpoint))
            throw new IllegalArgumentException("Endpoint must not be blank");

        if (metadata == null)
            throw new IllegalArgumentException("metadata must not be null");

        if (docs == null)
            throw new IllegalArgumentException("metadata must not be null");

        String response = null;

        if (routingResolver.isSmtpEndpoint(endpoint))
        {
            XDMMailClient xmc = new XDMMailClient(smtpHost);

            String body = "data attached";
            // TODO fix these two to use metadata
            String from = "vlewis@lewistower.com";
            String recipient = routingResolver.resolve(endpoint);
            ArrayList<String> to = new ArrayList<String>();
            to.add(recipient);

            String suffix = "xml";
            xmc.sendMail(messageId, from, to, metadata, body, docs, suffix);
            response = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
        }
        else
        {
            XDClient xdc = new XDClient();
            response = xdc.sendRequest(endpoint, metadata, docs);
        }

        return response;
    }

}
