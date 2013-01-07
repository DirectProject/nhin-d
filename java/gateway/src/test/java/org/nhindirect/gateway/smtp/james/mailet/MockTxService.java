package org.nhindirect.gateway.smtp.james.mailet;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.TxService;
import org.nhindirect.common.tx.model.Tx;

public class MockTxService implements TxService
{
	protected Collection<Tx> txs  = new ArrayList<Tx>();

	@Override
	public void trackMessage(MimeMessage msg) throws ServiceException 
	{
		
	}

	@Override
	public void trackMessage(InternetHeaders headers) throws ServiceException 
	{
		
	}

	@Override
	public void trackMessage(Tx tx) throws ServiceException 
	{
		txs.add(tx);
	}

	@Override
	public boolean suppressNotification(MimeMessage msg) throws ServiceException 
	{
		return false;
	}

	@Override
	public boolean suppressNotification(Tx notificationMessage) throws ServiceException 
	{
		return false;
	}
	
	
}
