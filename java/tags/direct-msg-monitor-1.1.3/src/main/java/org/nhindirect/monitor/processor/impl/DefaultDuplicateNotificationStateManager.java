/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.monitor.processor.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.mail.MDNStandard;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxDetail;
import org.nhindirect.common.tx.model.TxDetailType;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.monitor.dao.NotificationDAOException;
import org.nhindirect.monitor.dao.NotificationDuplicationDAO;
import org.nhindirect.monitor.processor.DuplicateNotificationStateManager;
import org.nhindirect.monitor.processor.DuplicateNotificationStateManagerException;

/**
 * Default implementation of the DuplicateNotificationStateManager
 * @author Greg Meyer
 * @since 1.0
 */
public class DefaultDuplicateNotificationStateManager implements DuplicateNotificationStateManager
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultDuplicateNotificationStateManager.class);
	
	protected final static int DEFAULT_RETENTION = 7;
	
	protected int messageRetention;
	
	protected NotificationDuplicationDAO dao;
	
	/**
	 * Constructor
	 */
	public DefaultDuplicateNotificationStateManager()
	{
		messageRetention = DEFAULT_RETENTION;
	}
	
	/**
	 * Sets the time in days that notification messages should stay in the store until they are purged.
	 * @param messageRetention The time in days that notification messages should stay in the store
	 */
	public void setMessageRetention(int messageRetention)
	{
		this.messageRetention = messageRetention;
	}
	
	/**
	 * Sets the dao that will store the message state.
	 * @param dao The dao that will store the message state.
	 */
	public void setDao(NotificationDuplicationDAO dao)
	{
		this.dao = dao;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addNotification(Tx notificationMessage) throws DuplicateNotificationStateManagerException
	{
		if (dao == null)
			throw new IllegalArgumentException("Dao cannot be null");	
		
		if (notificationMessage == null)
			throw new IllegalArgumentException("Notification message cannot be null");
		
		final TxMessageType type = notificationMessage.getMsgType();
		
		if (type == TxMessageType.DSN || type == TxMessageType.MDN)
		{
			final TxDetail originalMessageIdDetail = notificationMessage.getDetail(TxDetailType.PARENT_MSG_ID);
			final TxDetail origRecips = notificationMessage.getDetail(TxDetailType.FINAL_RECIPIENTS);
			
			if (originalMessageIdDetail != null && origRecips != null)
			{
				for (String recipAddress : origRecips.getDetailValue().split(","))
				{
					try
					{
						dao.addNotification(originalMessageIdDetail.getDetailValue(), recipAddress);
					}
					catch (NotificationDAOException e)
					{
						throw new DuplicateNotificationStateManagerException(e);
					}
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean suppressNotification(Tx notificationMessage) throws DuplicateNotificationStateManagerException
	{
		boolean retVal = false;
		
		if (dao == null)
			throw new IllegalArgumentException("Dao cannot be null");	
		
		if (notificationMessage == null)
			throw new IllegalArgumentException("Notification message cannot be null");
		
		final TxMessageType type = notificationMessage.getMsgType();

		
		if (type == TxMessageType.DSN || type == TxMessageType.MDN)
		{
			final TxDetail dispositionDetail = notificationMessage.getDetail(TxDetailType.DISPOSITION);
			
			// if it's an MDN displayed, then don't suppress it and let it go through
			if (type == TxMessageType.MDN && (dispositionDetail == null || dispositionDetail.getDetailValue().contains(MDNStandard.Disposition_Displayed)))
				return retVal;
			
			final TxDetail originalMessageIdDetail = notificationMessage.getDetail(TxDetailType.PARENT_MSG_ID);
			final TxDetail origRecips = notificationMessage.getDetail(TxDetailType.FINAL_RECIPIENTS);
			
			if (originalMessageIdDetail != null && origRecips != null)
			{
				Collection<String> recips = Arrays.asList(origRecips.getDetailValue().split(","));
				try
				{
					final Set<String> alreadyReceivedNotifs = dao.getReceivedAddresses(originalMessageIdDetail.getDetailValue(), recips);
					if (!alreadyReceivedNotifs.isEmpty())
						retVal = true;
				}
				catch (NotificationDAOException e)
				{
					throw new DuplicateNotificationStateManagerException(e);
				}
				
			}
		}
		
		return retVal;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void purge()
	{
		if (dao == null)
			throw new IllegalArgumentException("Dao cannot be null");	
		
		try
		{
			final Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.add(Calendar.HOUR, -(24 * messageRetention));
			dao.purgeNotifications(cal);
		}
		catch (NotificationDAOException e)
		{
			LOGGER.warn("Message duplication state purge failed.", e);
		}
	}
}
