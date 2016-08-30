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

package org.nhindirect.monitor.processor;

import org.nhindirect.common.tx.model.Tx;

/**
 * Interface for managing the state of duplicate notification messages.
 * @author Greg Meyer
 * @since 1.0
 */
public interface DuplicateNotificationStateManager 
{	
	/**
	 * Adds a notification message to the duplication checking store.
	 * @param notificationMessage The notification message.
	 * @throws DuplicateNotificationStateManagerException
	 */
	public void addNotification(Tx notificationMessage) throws DuplicateNotificationStateManagerException;
	
	/**
	 * Determines if a notification message should be suppressed from being sent to the original sender due to a notification already being sent.
	 * @param notificationMessage The notification message.
	 * @return True if the notification message should be suppressed.
	 * @throws DuplicateNotificationStateManagerException
	 */
	public boolean suppressNotification(Tx notificationMessage) throws DuplicateNotificationStateManagerException;
	
	/**
	 * Purges the duplication store of old notification messages.
	 * @throws DuplicateNotificationStateManagerException
	 */
	public void purge() throws DuplicateNotificationStateManagerException;
}
