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
