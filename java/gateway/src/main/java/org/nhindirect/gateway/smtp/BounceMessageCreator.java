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

package org.nhindirect.gateway.smtp;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.nhindirect.stagent.MessageEnvelope;
import org.nhindirect.stagent.NHINDAddressCollection;

public class BounceMessageCreator 
{
    private final BounceMessageTemplate template;
    
    public BounceMessageCreator(BounceMessageTemplate template)
    {
        if (template == null)
        {
            throw new IllegalArgumentException();
        }
        
        this.template = template;
    }

    public BounceMessageTemplate getMessageTemplate()
    {
    	return template;
    }
    
    public MimeMessage create(MessageEnvelope envelope, InternetAddress postmaster)
    {
        String body = this.buildBounceMessageBody(envelope);
        if (body == null || body.isEmpty())
        {
            return null;
        }
        
        MimeMessage bounceMessage = null;
        
        try
        {
        	bounceMessage = new MimeMessage((Session)null);
	        bounceMessage.addFrom(new InternetAddress[] {postmaster});
	        bounceMessage.addRecipient(RecipientType.TO, envelope.getSender());
	        bounceMessage.setSubject(template.getSubject());
	        bounceMessage.setHeader("Content-Transfer-Encoding", "ASCII"); 
	        bounceMessage.setText(body);       
        }
        catch (MessagingException e)
        {
        	return null;
        }
        return bounceMessage;            
    }

    private String buildBounceMessageBody(MessageEnvelope envelope)
    {
        NHINDAddressCollection rejections;
        if (envelope.hasRejectedRecipients())
        {
            rejections = envelope.getRejectedRecipients();
        }
        else
        {
            rejections = envelope.getRecipients();
        }
        
        if (rejections == null || rejections.size() == 0)
        {
            return null;
        }
        
        StringBuilder bounceMessage = new StringBuilder();
        bounceMessage.append(template.getBody());            
        bounceMessage.append(rejections.toString());

        return bounceMessage.toString();
    }        
}
