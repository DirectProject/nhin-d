package org.nhindirect.gateway.smtp.james.mailet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;

public class MockMailetContext implements MailetContext 
{
	protected Collection<Mail> sentMessages  = new ArrayList<Mail>();
	
	
	public void bounce(Mail arg0, String arg1, MailAddress arg2)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void bounce(Mail arg0, String arg1) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<String> getMailServers(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public MailAddress getPostmaster() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<String> getSMTPHostAddresses(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLocalEmail(MailAddress arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLocalServer(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLocalUser(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void log(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	public void log(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void sendMail(Mail arg0) throws MessagingException 
	{
		sentMessages.add(arg0);
		
	}

	@SuppressWarnings("rawtypes")
	public void sendMail(MailAddress arg0, Collection arg1, MimeMessage arg2,
			String arg3) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	public void sendMail(MailAddress arg0, Collection arg1, MimeMessage arg2)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void sendMail(MimeMessage arg0) throws MessagingException {
		sentMessages.add(new MockMail(arg0));
		
	}

	public Collection<Mail> getSentMessages()
	{
		return this.sentMessages;
	}
		
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	public void storeMail(MailAddress arg0, MailAddress arg1, MimeMessage arg2)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

}
