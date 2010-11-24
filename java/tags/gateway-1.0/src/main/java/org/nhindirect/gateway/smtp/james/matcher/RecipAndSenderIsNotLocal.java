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

package org.nhindirect.gateway.smtp.james.matcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMatcher;
import org.nhindirect.gateway.smtp.SmtpAgentError;
import org.nhindirect.gateway.smtp.SmtpAgentException;

/**
 * Matcher for returning recipients when the sender is local and the recipient is not local.  This is useful
 * when determining if local delivery will occur.
 * @author Greg Meyer
 *
 */
public class RecipAndSenderIsNotLocal extends  GenericMatcher
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(RecipAndSenderIsNotLocal.class);	
	private Set<String> domains = new HashSet<String>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init()
	{
		LOGGER.info("Initializing RecipAndSenderIsNotLocal matcher.");
		
		String localDomains = getCondition();
		
		if (localDomains == null || localDomains.isEmpty())
			throw new SmtpAgentException(SmtpAgentError.Uninitialized, "Matcher condition must contain at least 1 local domain.");
		
		String domainsParsed[] = localDomains.split(",");
		
		StringBuilder logMessage = new StringBuilder("Local matching domains:\r\n"); 
		
		for (String domain: domainsParsed)
		{
			logMessage.append("\t" + domain + "\r\n");
			this.domains.add(domain.toUpperCase(Locale.getDefault()));
		}
		
		LOGGER.info(logMessage);
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@SuppressWarnings("unchecked")
	@Override 
    public Collection<MailAddress> match(Mail mail) throws MessagingException 
    {
		LOGGER.debug("Matching mail message from: " + mail.getSender().toString());
		
    	if (!domains.contains(mail.getSender().getDomain().toUpperCase(Locale.getDefault())))
    	{
    		// this is from a remote domain... this auto qualifies all recipients
    		LOGGER.debug("Sender is remote.  Return all recipients as matching");
    		return mail.getRecipients();
    	}
    	
    	// the sender is local, so only return recipients that are not local
    	LOGGER.debug("Sender is local.  Matching non local recipients.");
    	
        Collection<MailAddress> matching = new Vector<MailAddress>();
        for (MailAddress addr : (Collection<MailAddress>)mail.getRecipients()) 
        {
            if (!domains.contains(addr.getDomain().toUpperCase(Locale.getDefault()))) 
            {
            	LOGGER.debug("Matched recipient " + addr.toString());
                matching.add(addr);
            }
        }
        return matching;
    }
}
