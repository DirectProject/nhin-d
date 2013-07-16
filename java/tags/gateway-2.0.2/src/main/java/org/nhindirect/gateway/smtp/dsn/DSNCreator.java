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

package org.nhindirect.gateway.smtp.dsn;

import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.nhindirect.common.tx.model.Tx;
import org.nhindirect.stagent.NHINDAddressCollection;

/**
 * Interface definition for creating DSN failure messages based on a Tx object a list of failed recipients.
 * @author Greg Meyer
 * @since 2.0
 */
public interface DSNCreator 
{
	/**
	 * Creates a DSN messages using message information from a Tx object a list of failed recipients. 
	 * @param tx Tx object containing information on the message that failed to be send or delivered.
	 * @param failedRecipeints List of intended recipients that did not receive the original message. 
	 * @return A MimeMessages representing the full DSN message.
	 * @throws MessagingException
	 * @deprecated As of 2.0.1.  This method does not correctly determine the domain of the postmaster for incoming messages.
	 * Use {@link #createDSNFailure(Tx, NHINDAddressCollection, boolean)}.  
	 */
	public MimeMessage createDSNFailure(Tx tx, NHINDAddressCollection failedRecipeints) throws MessagingException;
	
	/**
	 * Creates a collection of DSN messages using message information from a Tx object a list of failed recipients.  A DSN message is created
	 * for each unique postmaster domain. <b><b>
	 * @param tx Tx object containing information on the message that failed to be send or delivered.
	 * @param failedRecipeints List of intended recipients that did not receive the original message. 
	 * @param useSenderDomainForPostmaster Indicates if the original sender or the recipients should be used to determine the postmaster domain.  
	 * Generally for rejected outgoing messages, the sender's domain is used, and for incoming messages the recipients' domains are used.
	 * @return A collection of MimeMessages representing the full DSN messages.
	 * @throws MessagingException
	 */
	public Collection<MimeMessage> createDSNFailure(Tx tx, NHINDAddressCollection failedRecipeints, boolean useSenderDomainForPostmaster) throws MessagingException;
}
