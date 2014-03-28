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

package org.nhind.james.matcher;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMatcher;
import org.nhindirect.xd.routing.RoutingResolver;
import org.nhindirect.xd.routing.impl.RoutingResolverImpl;

/**
 * Matcher for non-XD mapped recipients.
 * 
 * @author beau
 */
public class RecipientIsNotXd extends GenericMatcher
{
    private static final Log LOGGER = LogFactory.getFactory().getInstance(RecipientIsNotXd.class);
    private RoutingResolver routingResolver;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        LOGGER.info("Initializing RecipientIsNotXd matcher");

        String condition = getCondition();
        routingResolver = new RoutingResolverImpl(condition);

        LOGGER.info("Initialized RecipientIsNotXd matcher");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<MailAddress> match(Mail mail) throws MessagingException
    {
        LOGGER.info("Attempting to match non-XD recipients");

        Collection<MailAddress> recipients = new ArrayList<MailAddress>();

        for (MailAddress addr : (Collection<MailAddress>) mail.getRecipients())
        {
            if (!routingResolver.isXdEndpoint(addr.toString()))
            {
                recipients.add(addr);
            }
        }

        if (recipients.isEmpty())
            LOGGER.info("Matched no recipients");
        else
            for (MailAddress addr : recipients)
                LOGGER.info("Matched recipient " + addr.toString());

        return recipients;
    }
}
