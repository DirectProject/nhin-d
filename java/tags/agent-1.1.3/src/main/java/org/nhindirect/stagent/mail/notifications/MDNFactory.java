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

package org.nhindirect.stagent.mail.notifications;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.jsieve.mailet.mdn.Disposition;
import org.apache.mailet.base.mail.MimeMultipartReport;

/**
 * Overridden implementation of the Apache James MDNFactory.  Adds support for additional fiedls such as error and gateway and does not automatically
 * set information for null or empty fields.
 * @author Greg Meyer
 * @since 1.1.3
 */
public class MDNFactory 
{

    /**
     * Default Constructor
     */
    private MDNFactory()
    {
        super();
    }
    
    /**
     * Answers a MimeMultipartReport containing a
     * Message Delivery Notification as specified by RFC 2298.
     * 
     * @param humanText
     * @param reporting_UA_name
     * @param reporting_UA_product
     * @param original_recipient
     * @param final_recipient
     * @param original_message_id
     * @param disposition
     * @return MimeMultipartReport
     * @throws MessagingException
     */
    static public MimeMultipartReport create(String humanText,
            String reporting_UA_name,
            String reporting_UA_product,
            String original_recipient,
            String final_recipient,
            String original_message_id,
            String error,
            MdnGateway gateway, 
            Disposition disposition) throws MessagingException
    {
        
    	if (disposition == null)
    		throw new IllegalArgumentException("Disposition can not be null.");
    	
    	// Create the message parts. According to RFC 2298, there are two
        // compulsory parts and one optional part...
        MimeMultipartReport multiPart = new MimeMultipartReport();
        multiPart.setReportType("disposition-notification");
        
        // Part 1: The 'human-readable' part
        MimeBodyPart humanPart = new MimeBodyPart();
        humanPart.setText(humanText);
        multiPart.addBodyPart(humanPart);

        // Part 2: MDN Report Part
        // 1) reporting-ua-field
        StringBuilder mdnReport = new StringBuilder(128);
        if (reporting_UA_name != null && !reporting_UA_name.isEmpty())
        {
        	mdnReport.append("Reporting-UA: ");
        	mdnReport.append((reporting_UA_name == null ? "" : reporting_UA_name));
        	mdnReport.append("; ");
        	mdnReport.append((reporting_UA_product == null ? "" : reporting_UA_product));
        	mdnReport.append("\r\n");
        }
        // 2) original-recipient-field
        if (original_recipient != null && !original_recipient.isEmpty())
        {
            mdnReport.append("Original-Recipient: ");
            mdnReport.append("rfc822; ");
            mdnReport.append(original_recipient);
            mdnReport.append("\r\n");
        }
        // 3) final-recipient-field
        if (final_recipient != null && !final_recipient.isEmpty())
        {
	        mdnReport.append("Final-Recipient: ");
	        mdnReport.append("rfc822; ");
	        mdnReport.append(final_recipient);
	        mdnReport.append("\r\n");
        }
        // 4) original-message-id-field
        if (original_message_id != null && !original_message_id.isEmpty())
        {        
	        mdnReport.append("Original-Message-ID: ");
	        mdnReport.append(original_message_id);
	        mdnReport.append("\r\n");
        }
  
        // 5) mdn-gateway-field
        if (gateway != null)
        {        
        	mdnReport.append("MDN-Gateway: ");
        	mdnReport.append(gateway.toString());
        	mdnReport.append("\r\n");
        }        
        
        // 6) error-field
        if (error != null && !error.isEmpty())
        {        
        	mdnReport.append("Error: ");
        	mdnReport.append(error);
        	mdnReport.append("\r\n");
        }          
        
        mdnReport.append(disposition.toString());
        mdnReport.append("\r\n");
        MimeBodyPart mdnPart = new MimeBodyPart();
        mdnPart.setContent(mdnReport.toString(), "message/disposition-notification");
        multiPart.addBodyPart(mdnPart);

        // Part 3: The optional third part, the original message is omitted.
        // We don't want to propogate over-sized, virus infected or
        // other undesirable mail!
        // There is the option of adding a Text/RFC822-Headers part, which
        // includes only the RFC 822 headers of the failed message. This is
        // described in RFC 1892. It would be a useful addition!        
        return multiPart;
    }

}
