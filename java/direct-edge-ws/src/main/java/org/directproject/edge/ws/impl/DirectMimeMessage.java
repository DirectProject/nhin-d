package org.directproject.edge.ws.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID; 

public class DirectMimeMessage extends MimeMessage {
	private MimeMessage msg;
	private String domain = "example.com";
	
	public DirectMimeMessage(MimeMessage msg) throws MessagingException {
		super(msg);
	}
	
	public DirectMimeMessage(MimeMessage msg, String domain) throws MessagingException {
	    super(msg);
	    setDomain(domain);
	}
    public void updateMessageID() throws MessagingException {
    	String messageid = "";
    	UUID a = UUID.randomUUID();
    	messageid = a.toString()+"@"+getDomain();
    	
        setHeader("Message-ID", messageid);
    }
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getDomain() {
		return domain;
	}

}
