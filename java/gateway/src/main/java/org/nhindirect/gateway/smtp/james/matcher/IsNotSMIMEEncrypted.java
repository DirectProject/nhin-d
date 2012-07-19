package org.nhindirect.gateway.smtp.james.matcher;

import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;

import org.apache.mailet.base.GenericMatcher;


public class IsNotSMIMEEncrypted extends GenericMatcher
{    
    @SuppressWarnings("rawtypes")
    public Collection match(Mail mail) throws MessagingException 
    {
        if (mail == null) 
        	return null;
        
        MimeMessage message = mail.getMessage();
        if (message == null) return null;
        
        if (!((message.isMimeType("application/x-pkcs7-mime") 
                || message.isMimeType("application/pkcs7-mime")) && (message.getContentType().indexOf("smime-type=enveloped-data") != -1)))
        {
            return mail.getRecipients();
        } 
        else 
        	return null;
    }
}
