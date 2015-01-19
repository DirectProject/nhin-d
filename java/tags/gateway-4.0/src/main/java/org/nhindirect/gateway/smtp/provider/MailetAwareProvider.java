package org.nhindirect.gateway.smtp.provider;

import org.apache.mailet.Mailet;

public interface MailetAwareProvider 
{
	public void setMailet(Mailet mailet);
}
