package org.nhindirect.gateway.smtp.james.mailet;

import java.util.Collection;

import org.apache.mailet.Mail;
import org.nhindirect.gateway.smtp.MessageProcessResult;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;

import com.google.inject.Module;

public class OnProcessMailet extends NHINDSecurityAndTrustMailet 
{
	private int onRejectedCount = 0;
	private int onPreProcessCount = 0;
	private int onPostProcessCount = 0;

	@Override
	protected Collection<Module> getInitModules() 
	{
		return null;
	}

	@Override
	protected void onMessageRejected(Mail mail,
			NHINDAddressCollection recipients, NHINDAddress sender, Throwable t) 
	{
		++onRejectedCount;
	}

	@Override
	protected void onPostprocessMessage(Mail mail, MessageProcessResult result) 
	{
		++onPostProcessCount;
	}

	@Override
	protected void onPreprocessMessage(Mail mail) 
	{
		++onPreProcessCount;
	}

	public int getOnRejectedCount() 
	{
		return onRejectedCount;
	}

	public int getOnPreProcessCount() 
	{
		return onPreProcessCount;
	}

	public int getOnPostProcessCount() 
	{
		return onPostProcessCount;
	}	
}
