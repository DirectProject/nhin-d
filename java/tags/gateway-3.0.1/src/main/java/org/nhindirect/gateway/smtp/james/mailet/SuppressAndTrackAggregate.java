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

package org.nhindirect.gateway.smtp.james.mailet;

import javax.mail.MessagingException;

import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.base.GenericMailet;

/**
 * Aggregate of the {@link TrackIncomingNotification} and {@link NotificationSuppressor} mailets.  To properly handle notification suppression, notifications cannot
 * be tracked before determine that suppression state.  However, suppressed messages still should (must for MDN processed) be tracked.  This creates a catch 22 or
 * chicken and egg condition.  The notification suppressor will eat messages if it is executed first and not allow some notifications to be tracked.  If the tracker is first,
 * then messages that should not be suppressed may end up being suppressed.
 * <p>
 * The class aggregates the two concepts so that they happen pseudo atomically.
 * @author Greg Meyer
 * @since 2.0
 */
public class SuppressAndTrackAggregate extends GenericMailet
{
	protected NotificationSuppressor suppessor;
	
	protected TrackIncomingNotification tracker;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(MailetConfig newConfig) throws MessagingException 
	{
		super.init(newConfig);
		
		suppessor = new NotificationSuppressor();
		tracker = new TrackIncomingNotification();
		
		suppessor.init(newConfig);
		tracker.init(newConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void service(Mail mail) throws MessagingException 
	{
		// execute the suppressor first, then track it
		suppessor.service(mail);
		
		tracker.service(mail);
		
	}
}
