package org.nhindirect.gateway.smtp.james.mailet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;

public class MockMail implements Mail 
{
	private MimeMessage mimeMessage;
	private String state = Mail.TRANSPORT;
	
	public MockMail(MimeMessage mimeMessage)
	{
		this.mimeMessage = mimeMessage;
	}
	
	public Serializable getAttribute(String arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getLastUpdated() {
		// TODO Auto-generated method stub
		return null;
	}

	public MimeMessage getMessage() throws MessagingException 
	{
		return this.mimeMessage;
	}

	public long getMessageSize() throws MessagingException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getRecipients() 
	{
		Collection<MailAddress> addrs = new ArrayList<MailAddress>();
		
		try
		{
			for (Address addr : mimeMessage.getAllRecipients())
			{
				addrs.add(new MailAddress(addrs.toString()));
			}
		}
		catch (Exception e)
		{
			
		}
		return addrs;
	}

	public String getRemoteAddr() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	public MailAddress getSender() 
	{
		MailAddress retVal = null;
		
		try
		{
		
			Address addr = mimeMessage.getSender();
			
			if (addr == null)
			{
				Address[] addrs = mimeMessage.getFrom();
				addr = addrs[0];
			}
			
			retVal = new MailAddress(addr.toString());
		}
		catch (Exception e)
		{
			
		}
		
		return retVal;
	}

	public String getState() 
	{
		return state;
	}

	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeAllAttributes() {
		// TODO Auto-generated method stub
		
	}

	public Serializable removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable setAttribute(String arg0, Serializable arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setErrorMessage(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setLastUpdated(Date arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setMessage(MimeMessage msg) 
	{
		this.mimeMessage = msg;
		
	}

	public void setName(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setRecipients(Collection arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	public void setState(String state) 
	{
		this.state = state;
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
