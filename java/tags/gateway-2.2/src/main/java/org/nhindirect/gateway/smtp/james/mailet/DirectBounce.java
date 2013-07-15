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
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.common.tx.model.TxMessageType;
import org.nhindirect.gateway.smtp.dsn.DSNCreator;
import org.nhindirect.gateway.smtp.dsn.provider.FailedDeliveryDSNCreatorProvider;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;

import com.google.inject.Provider;

/**
 * Bounce mailet similar to the Bounce mailet provided by Apache James.  This mailet uses generates bounce messages using the 
 * {@link FailedDeliveryDSNCreator} class to create a configurable message with more meaningful information.
 * @author Greg Meyer
 * @since 2.0
 */
public class DirectBounce extends AbstractNotificationAwareMailet
{	
	/**
	 * {@inheritDoc}
	 */
	public void init() throws MessagingException
	{
		super.init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void service(Mail mail) throws MessagingException 
	{	
		final MimeMessage msg = mail.getMessage();
		
		final NHINDAddressCollection recipients = getMailRecipients(mail);
		
		final NHINDAddress sender = getMailSender(mail);
			
		final Tx txToTrack = getTxToTrack(msg, sender, recipients);
		

		// create a DSN message
		if (txToTrack != null && txToTrack.getMsgType() == TxMessageType.IMF)
			this.sendDSN(txToTrack, recipients, false);

	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Provider<DSNCreator> getDSNProvider() 
	{
		return new FailedDeliveryDSNCreatorProvider(this);
	}
	
}
