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

package org.nhindirect.common.mail.dsn.impl;

import java.util.Enumeration;
import java.util.List;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang.StringEscapeUtils;
import org.nhindirect.common.mail.dsn.DSNFailureTextBodyPartGenerator;


public class DefaultDSNFailureTextBodyPartGenerator implements DSNFailureTextBodyPartGenerator
{

    public static final String ORIGINAL_SENDER_TAG = "%original_sender_tag%";
    public static final String HEADERS_TAG = "%headers_tag%";


    private String header;
    private String footer;
    private String rejectedRecipientsTitle;
    private String errorMessageTitle;
    private String defaultErrorMessage;
    private HumanReadableTextAssemblerFactory humanReadableTextAssemblerFactory;

    /**
     * 
     * @param header
     * @param footer
     * @param rejectedRecipientsTitle
     * @param errorMessageTitle
     * @param defaultErrorMessage
     * @param humanReadableTextAssemblerFactory
     */
    public DefaultDSNFailureTextBodyPartGenerator(String header, String footer, String rejectedRecipientsTitle,
	    String errorMessageTitle, String defaultErrorMessage,
	    HumanReadableTextAssemblerFactory humanReadableTextAssemblerFactory) 
    {
		this.header = header;
		this.footer = footer;
		this.rejectedRecipientsTitle = rejectedRecipientsTitle;
		this.errorMessageTitle = errorMessageTitle;
		this.defaultErrorMessage = defaultErrorMessage;
		this.humanReadableTextAssemblerFactory = humanReadableTextAssemblerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeBodyPart generate(Address originalSender, List<Address> failedRecipients,
	    Enumeration<Header> originalMessageHeaders) throws MessagingException 
	{

    	MimeBodyPart part = null;

	    if (originalSender == null) 
	    	throw new MessagingException("originalSender can't be null");

	    if (failedRecipients == null) 
	    	throw new MessagingException("Faile recipients can't be null");

	    final String populatedHeader = header.replace(ORIGINAL_SENDER_TAG,
		    StringEscapeUtils.escapeHtml(originalSender.toString()));
	    
	    String populatedFooter = null;
	    
	    if (originalMessageHeaders != null && originalMessageHeaders.hasMoreElements()) 
	    {
	    	populatedFooter = footer.replace(HEADERS_TAG, headersToString(originalMessageHeaders));
	    } 
	    else 
	    {
	    	populatedFooter = footer;
	    }

	    final HumanReadableTextAssembler assembler = humanReadableTextAssemblerFactory.createHumanReadableTextAssembler(populatedHeader,
	    		populatedFooter, rejectedRecipientsTitle, errorMessageTitle, defaultErrorMessage);
	    part = assembler.assemble(failedRecipients);

	    return part;
    }

    protected String headersToString(Enumeration<Header> originalMessageHeaders) 
    {
		final StringBuffer sb = new StringBuffer();
		while (originalMessageHeaders.hasMoreElements()) 
		{
		    Header h = originalMessageHeaders.nextElement();
		    sb.append(StringEscapeUtils.escapeHtml(h.getName() + ": " + h.getValue()));
		    sb.append("<br/>");
		}
		return sb.toString();
    }
}
