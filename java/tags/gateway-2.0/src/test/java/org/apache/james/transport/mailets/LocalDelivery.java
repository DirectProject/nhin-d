package org.apache.james.transport.mailets;

import javax.mail.MessagingException;

import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;

/**
 * Mock implementation of the LocalDelivery class.  This should not collide with the actual class because the actual class cannot be downloaded
 * from the maven central repository.
 * @author Greg Meyer
 *
 */
public class LocalDelivery extends GenericMailet
{

	@Override
	public void init() throws MessagingException 
	{

		super.init();
	}

	@Override
	public void service(Mail mail) throws MessagingException 
	{
		// TODO Auto-generated method stub
		
	}
	
}
