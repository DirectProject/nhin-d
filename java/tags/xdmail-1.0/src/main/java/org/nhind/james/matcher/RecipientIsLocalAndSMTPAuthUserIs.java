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
import java.util.Collections;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;
import org.apache.mailet.base.GenericMatcher;

/**
 * Matcher for local recipients when the auth user matches a given user.
 * 
 * @author beau
 */
public class RecipientIsLocalAndSMTPAuthUserIs extends GenericMatcher
{
    /**
     * The mail attribute holding the SMTP AUTH user name, if any.
     */
    private final static String SMTP_AUTH_USER_ATTRIBUTE_NAME = "org.apache.james.SMTPAuthUser";

    private static final Log LOGGER = LogFactory.getFactory().getInstance(RecipientIsLocalAndSMTPAuthUserIs.class);

    private String user;

    /**
     * {@inheritDoc}
     */
    public void init() throws javax.mail.MessagingException
    {
        user = getCondition();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<MailAddress> match(Mail mail) throws MessagingException
    {
        LOGGER.info("Servicing RecipientIsLocalAndSMTPAuthUserIs matcher");

        String authUser = (String) mail.getAttribute(SMTP_AUTH_USER_ATTRIBUTE_NAME);

        if (authUser == null || !StringUtils.equalsIgnoreCase(user, authUser))
        {
            LOGGER.info("Auth user is not " + user + ", skipping");
            return Collections.<MailAddress> emptyList();
        }

        MailetContext mailetContext = getMailetContext();

        Collection<MailAddress> recipients = new ArrayList<MailAddress>();

        for (MailAddress recipient : (Collection<MailAddress>) mail.getRecipients())
        {
            if (mailetContext.isLocalServer(recipient.getHost()) && mailetContext.isLocalUser(recipient.getUser()))
                recipients.add(recipient);
        }

        if (recipients.isEmpty())
            LOGGER.info("Matched no recipients");
        else
            for (MailAddress addr : recipients)
                LOGGER.info("Matched recipient " + addr.toString());

        return recipients;
    }
}
