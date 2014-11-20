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

package org.nhindirect.monitor.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

/**
 * Data access interface for storing duplicate notification state.
 * @author Greg Meyer
 * @since 1.0
 */
public interface NotificationDuplicationDAO 
{
	/**
	 * Adds a notification message that has been received by the system.
	 * @param messageId The id of the original message.
	 * @param address The address of the final recipient of the message
	 * @throws NotificationDAOException
	 */
	public void addNotification(String messageId, String address) throws NotificationDAOException;
	
	/**
	 * Gets a set of addresses that have received complete notification messages for a given message id. 
	 * @param messageId The id of the original message.
	 * @param addresses Collection of addresses to check if notification completion has occurred
	 * @return A set of addresses that have received a complete set of notification messages.
	 * @throws NotificationDAOException
	 */
	public Set<String> getReceivedAddresses(String messageId, Collection<String> addresses) throws NotificationDAOException;
	
	/**
	 * Purges notification messages that have been in the store longer that a given time.
	 * @param purgeTime The check time.  Messages older than this time will be purged.
	 * @throws NotificationDAOException
	 */
	public void purgeNotifications(Calendar purgeTime) throws NotificationDAOException;
}
