/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

package org.nhindirect.stagent.mail.notifications;

/**
 * Encapsulates message disposition status.
 * @author gm2552
 * @author Umesh Madan
 */
public class Disposition extends org.apache.jsieve.mailet.mdn.Disposition
{

	private final TriggerType triggerType;
	private final SendType sendType;
	private NotificationType notification;
	private boolean error;
	
	/**
	 * Initializes an instance with the specified disposition notification type and automatic modes.
	 * @param notification The disposition notification type.
	 */
	public Disposition(NotificationType notification)
	{
		this(TriggerType.Automatic, SendType.Automatic, notification);
	}

	/**
	 * Initializes an instance with the specified disposition notification type and action and sending modes
	 * @param triggerType The action (trigger) mode type
	 * @param sendType The sending mode type
	 * @param notification The disposition notification type
	 */
	public Disposition(TriggerType triggerType, SendType sendType, NotificationType notification)
    {
        super(triggerType, sendType, notification);
		
		this.triggerType = triggerType;
        this.sendType = sendType;
        this.notification = notification;
        this.error = false;
    }

	
	/**
	 * Indicates if this disposition is an error report.
	 * @return true if this disposition is an error report.  false otherwise. 
	 */
    public boolean isError() 
    {
		return error;
	}

	/**
	 * Sets the error status indicating if this disposition is an error report.
	 * @param error true if this disposition is an error report.  false otherwise. 
	 */    
	public void setError(boolean error) 
	{
		this.error = error;
	}

	/**
	 * Gets the type of disposition indicated
	 * @return The type of disposition indicated
	 */
	public NotificationType getNotification() 
    {
		return notification;
	}

	/**
	 * Sets the type of disposition indicated.
	 * @param notification The type of disposition indicated.
	 */
	public void setNotification(NotificationType notification) 
	{
		this.notification = notification;
	}

	/**
	 * Gets the trigger action that generated this disposition (action-mode).
	 * @return The trigger action that generated this disposition
	 */
	public TriggerType getTriggerType() 
	{
		return triggerType;
	}

	/**
	 * Gets the sending type (system or user) that sent this disposition (sending-mode). 
	 * @return The sending type that sent this disposition. 
	 */
	public SendType getSendType() 
	{
		return sendType;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
    public String toString()
    {
        StringBuilder notification = new StringBuilder("Disposition: ");
        //
        // Disposition Mode
        //
        notification.append(NotificationHelper.asString(this.triggerType));
        notification.append('/');
        notification.append(NotificationHelper.asString(this.sendType));
        notification.append(';');
        //
        // Disposition Type & Modifier
        //
        notification.append(NotificationHelper.asString(this.notification));
        if (isError())
        {
            notification.append('/');
            notification.append(MDNStandard.Modifier_Error);
        }

        return notification.toString();
    }
}
