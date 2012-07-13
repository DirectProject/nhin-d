package org.nhindirect.common.mail.dsn;

import java.util.Enumeration;
import java.util.List;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public interface DSNFailureTextBodyPartGenerator 
{
    public MimeBodyPart generate(Address originalSender, List<Address> failedRecipients,
    	    Enumeration<Header> originalMessageHeaders) throws MessagingException;
}
