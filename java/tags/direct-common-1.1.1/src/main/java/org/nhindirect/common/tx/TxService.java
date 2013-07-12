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

package org.nhindirect.common.tx;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.tx.model.Tx;

/**
 * Interface for message monitoring/tracking.  The monitoring system watches for notification messages (DSN and MDN) to determine if a failure
 * notification needs to be send if no notifications are received within a give time period.
 * <p>
 * The monitoring system executes basic processing business logic such as parsing a message into a set of attributes, location relevant information that needs
 * to be transformed into monitoring details, and correlating messages together based on linkable headers such
 * as In-Reply-To.
 * @author Greg Meyer
 * @since 1.1
 */
public interface TxService 
{
	/**
	 * Tracks a message in a MimeMessage format
	 * @param msg The message to track
	 * @throws ServiceException
	 */
	public void trackMessage(MimeMessage msg) throws ServiceException;
	
	/**
	 * Tracks a message as a set of Internet headers
	 * @param headers The message to track
	 * @throws ServiceException
	 */
	public void trackMessage(InternetHeaders headers) throws ServiceException;
	
	
	/**
	 * Tracks a message as a pre parsed Tx object
	 * @param tx The message to track
	 * @throws ServiceException
	 */
	public void trackMessage(Tx tx) throws ServiceException;
	
	/**
	 * Indicates if a notification message should be suppressed from being delivered to the original message edge client
	 * based on existing notifications being received, if the original message is subject to the timely and reliable guidance,
	 * and other policies based on a specific HISP implementation.
	 * @param msg The notification message.  If the message is not a notification message, false will be returned.
	 * @return True if the notification message should be suppressed and not delivered to original edge client.
	 */
	public boolean suppressNotification(MimeMessage msg) throws ServiceException;
	
	/**
	 * Indicates if a notification message should be suppressed from being delivered to the original message edge client
	 * based on existing notifications being received, if the original message is subject to the timely and reliable guidance,
	 * and other policies based on a specific HISP implementation.
	 * @param notificationMessage Pre-parsed Tx object of the notification message.
	 * @return True if the notification message should be suppressed and not delivered to original edge client.
	 */
	public boolean suppressNotification(Tx notificationMessage) throws ServiceException;
}
