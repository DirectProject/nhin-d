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

package org.nhindirect.common.mail.dsn;

import javax.mail.internet.InternetHeaders;

import com.sun.mail.dsn.DeliveryStatus;

/**
 * Headers and utilities for delivery status notifications (DSN)
 * @author Greg Meyer
 * @since 1.1
 */
public class DSNStandard 
{
	/**
	 * Media type constants
	 * @author Greg Meyer
	 * @since 1.1
	 */
	public static class MediaType 
	{
	    /**
	     * MIME header standard DSN delivery status
	     */
	    public static final String DSNDeliveryStatus = "message/delivery-status";   
		
	    /**
	     *  Report parameter value indication a DSN message.
	     */
	    public static final String ReportTypeValueDelivery = "delivery-status";
	    
	    /**
	     * MIME parameter header for an report. 
	     */
	    public static final String ReportType = "report-type";
	}
	
	/**
	 * DNS standard headers
	 * @author Greg Meyer
	 * @since 1.1
	 */
	public static class Headers
	{
	    /**
	     *  Final recipient
	     */    
	    public static final String FinalRecipient = "Final-Recipient";
	    
	    /**
	     * DSN Action
	     */
	    public static final String Action = "Action";
	    
	    /**
	     * DSN Status
	     */
	    public static final String Status = "Status";
	    
	    /**
	     * Name for the reporting MTA
	     */
	    public static final String ReportingMTA = "Reporting-MTA";
	    
	    /**
	     * Not part of the DSN standard, but a custom header for the original message id
	     */
	    public static final String OriginalMessageID = "X-Original-Message-ID";
	}
	
	/**
	 * Enumeration of values for DSN action field
	 * @author Greg Meyer
	 * @since 1.1
	 */
	public enum DSNAction 
	{
	  /**
	   * indicates that the message could not be delivered to the recipient. The
	   * Reporting MTA has abandoned any attempts to deliver the message to this
	   * recipient. No further notifications should be expected.
	   * 
	   */
	  FAILED 
	  {
	    @Override
	    public String toString() 
	    {
	      return "failed";
	    }
	  },

	  /**
	   * 
	   indicates that the Reporting MTA has so far been unable to deliver or relay
	   * the message, but it will continue to attempt to do so. Additional
	   * notification messages may be issued as the message is further delayed or
	   * successfully delivered, or if delivery attempts are later abandoned.
	   */
	  DELAYED 
	  {
	    @Override
	    public String toString() 
	    {
	      return "delayed";
	    }
	  },
	  /**
	   * 
	   indicates that the message was successfully delivered to the recipient
	   * address specified by the sender, which includes "delivery" to a mailing
	   * list exploder. It does not indicate that the message has been read. This is
	   * a terminal state and no further DSN for this recipient should be expected.
	   */
	  DELIVERED 
	  {
	    @Override
	    public String toString() 
	    {
	      return "delivered";
	    }
	  },
	  /**
	   * 
	   indicates that the message has been relayed or gatewayed into an
	   * environment that does not accept responsibility for generating DSNs upon
	   * successful delivery. This action-value SHOULD NOT be used unless the sender
	   * has requested notification of successful delivery for this recipient.
	   */
	  RELAYED 
	  {
	    @Override
	    public String toString() 
	    {
	      return "relayed";
	    }
	  },
	  /**
	   * 
	   indicates that the message has been successfully delivered to the recipient
	   * address as specified by the sender, and forwarded by the Reporting-MTA
	   * beyond that destination to multiple additional recipient addresses. An
	   * action-value of "expanded" differs from "delivered" in that "expanded" is
	   * not a terminal state. Further "failed" and/or "delayed" notifications may
	   * be provided.
	   */
	  EXPANDED 
	  {
	    @Override
	    public String toString() 
	    {
	      return "expanded";
	    }
	  }
	}
    
	/**
	 * Enumeration of valid value for MtaNameType fields
	 * @author Greg Meyer
	 * @since 1.1
	 */
	public enum MtaNameType 
	{
	  /**
	   * 
	   For an SMTP server on an Internet host, the MTA name is the domain name of
	   * that host, and the "dns" MTA- name-type is used.
	   */
	  DNS 
	  {
	    @Override
	    public String toString() 
	    {
	       return "dns";
	    }
	  }
	}	
	
	/**
	 * Constants and getters for RFC 3463 Enhanced Mail System Status Codes
	 * <p>
	 * 5/18/2011 bf3174 - nothing has been changed with this class.  Copying
	 * the class here to avoid a dependency on apache james
	 * </p>
	 */
	public static class DSNStatus 
	{
	    // status code classes
	    /**
	     * Success
	     */
	    public static final int SUCCESS = 2;

	    /**
	     * Persistent Transient Failure
	     */
	    public static final int TRANSIENT = 4;

	    /**
	     * Permanent Failure
	     */
	    public static final int PERMANENT = 5;

	    // subjects and details

	    /**
	     * Other or Undefined Status
	     */
	    public static final int UNDEFINED = 0;

	    /**
	     * Other undefined status
	     */
	    public static final String UNDEFINED_STATUS = "0.0";

	    /**
	     * Addressing Status
	     */
	    public static final int ADDRESS = 1;

	    /**
	     * Other address status
	     */
	    public static final String ADDRESS_OTHER = "1.0";

	    /**
	     * Bad destination mailbox address
	     */
	    public static final String ADDRESS_MAILBOX = "1.1";

	    /**
	     * Bad destination system address
	     */
	    public static final String ADDRESS_SYSTEM = "1.2";

	    /**
	     * Bad destination mailbox address syntax
	     */
	    public static final String ADDRESS_SYNTAX = "1.3";

	    /**
	     * Destination mailbox address ambiguous
	     */
	    public static final String ADDRESS_AMBIGUOUS = "1.4";

	    /**
	     * Destination Address valid
	     */
	    public static final String ADDRESS_VALID = "1.5";

	    /**
	     * Destimation mailbox has moved, no forwarding address
	     */
	    public static final String ADDRESS_MOVED = "1.6";

	    /**
	     * Bad sender's mailbox address syntax
	     */
	    public static final String ADDRESS_SYNTAX_SENDER = "1.7";

	    /**
	     * Bad sender's system address
	     */
	    public static final String ADDRESS_SYSTEM_SENDER = "1.8";


	    /**
	     * Mailbox Status
	     */
	    public static final int MAILBOX = 2;

	    /**
	     * Other or Undefined Mailbox Status
	     */
	    public static final String MAILBOX_OTHER = "2.0";

	    /**
	     * Mailbox disabled, not accepting messages
	     */
	    public static final String MAILBOX_DISABLED = "2.1";

	    /**
	     * Mailbox full
	     */
	    public static final String MAILBOX_FULL = "2.2";

	    /**
	     * Message length exceeds administrative limit
	     */
	    public static final String MAILBOX_MSG_TOO_BIG = "2.3";

	    /**
	     * Mailing list expansion problem
	     */
	    public static final String MAILBOX_LIST_EXPANSION = "2.4";


	    /**
	     * Mail System Status
	     */
	    public static final int SYSTEM = 3;

	    /**
	     * Other or undefined mail system status
	     */
	    public static final String SYSTEM_OTHER = "3.0";

	    /**
	     * Mail system full
	     */
	    public static final String SYSTEM_FULL = "3.1";

	    /**
	     * System not accepting messages
	     */
	    public static final String SYSTEM_NOT_ACCEPTING = "3.2";

	    /**
	     * System not capable of selected features
	     */
	    public static final String SYSTEM_NOT_CAPABLE = "3.3";

	    /**
	     * Message too big for system
	     */
	    public static final String SYSTEM_MSG_TOO_BIG = "3.4";

	    /**
	     * System incorrectly configured
	     */
	    public static final String SYSTEM_CFG_ERROR = "3.5";


	    /**
	     * Network and Routing Status
	     */
	    public static final int NETWORK = 4;

	    /**
	     * Other or undefined network or routing status
	     */
	    public static final String NETWORK_OTHER = "4.0";

	    /**
	     * No answer form host
	     */
	    public static final String NETWORK_NO_ANSWER = "4.1";

	    /**
	     * Bad Connection
	     */
	    public static final String NETWORK_CONNECTION = "4.2";

	    /**
	     * Directory server failure
	     */
	    public static final String NETWORK_DIR_SERVER = "4.3";

	    /**
	     * Unable to route
	     */
	    public static final String NETWORK_ROUTE = "4.4";

	    /**
	     * Mail system congestion
	     */
	    public static final String NETWORK_CONGESTION = "4.5";

	    /**
	     * Routing loop detected
	     */
	    public static final String NETWORK_LOOP = "4.6";

	    /**
	     * Delivery time expired
	     */
	    public static final String NETWORK_EXPIRED = "4.7";


	    /**
	     * Mail Delivery Protocol Status
	     */
	    public static final int DELIVERY = 5;

	    /**
	     * Other or undefined (SMTP) protocol status
	     */
	    public static final String DELIVERY_OTHER = "5.0";

	    /**
	     * Invalid command
	     */
	    public static final String DELIVERY_INVALID_CMD = "5.1";

	    /**
	     * Syntax error
	     */
	    public static final String DELIVERY_SYNTAX = "5.2";

	    /**
	     * Too many recipients
	     */
	    public static final String DELIVERY_TOO_MANY_REC = "5.3";

	    /**
	     * Invalid command arguments
	     */
	    public static final String DELIVERY_INVALID_ARG = "5.4";

	    /**
	     * Wrong protocol version
	     */
	    public static final String DELIVERY_VERSION = "5.5";


	    /**
	     * Message Content or Media Status
	     */
	    public static final int CONTENT = 6;

	    /**
	     * Other or undefined media error
	     */
	    public static final String CONTENT_OTHER = "6.0";

	    /**
	     * Media not supported
	     */
	    public static final String CONTENT_UNSUPPORTED = "6.1";

	    /**
	     * Conversion required and prohibited
	     */
	    public static final String CONTENT_CONVERSION_NOT_ALLOWED = "6.2";

	    /**
	     * Conversion required, but not supported
	     */
	    public static final String CONTENT_CONVERSION_NOT_SUPPORTED = "6.3";

	    /**
	     * Conversion with loss performed
	     */
	    public static final String CONTENT_CONVERSION_LOSS = "6.4";

	    /**
	     * Conversion failed
	     */
	    public static final String CONTENT_CONVERSION_FAILED = "6.5";


	    /**
	     * Security or Policy Status
	     */
	    public static final int SECURITY = 7;

	    /**
	     * Other or undefined security status
	     */
	    public static final String SECURITY_OTHER = "7.0";

	    /**
	     * Delivery not authorized, message refused
	     */
	    public static final String SECURITY_AUTH = "7.1";

	    /**
	     * Mailing list expansion prohibited
	     */
	    public static final String SECURITY_LIST_EXP = "7.2";

	    /**
	     * Security conversion required, but not possible
	     */
	    public static final String SECURITY_CONVERSION = "7.3";

	    /**
	     * Security features not supported
	     */
	    public static final String SECURITY_UNSUPPORTED = "7.4";

	    /**
	     * Cryptographic failure
	     */
	    public static final String SECURITY_CRYPT_FAIL = "7.5";

	    /**
	     * Cryptographic algorithm not supported
	     */
	    public static final String SECURITY_CRYPT_ALGO = "7.6";

	    /**
	     * Message integrity failure
	     */
	    public static final String SECURITY_INTEGRITY = "7.7";


	    // get methods

	    public static String getStatus(int type, String detail) {
	        return type + "." + detail;
	    }

	    public static String getStatus(int type, int subject, int detail) {
	        return type + "." + subject + "." + detail;
	    }
	}	
	
	/**
	 * Get header values from the delivery status part of a DNS message
	 * @param status The delivery status structure
	 * @param headerName The header name to get the value for.
	 * @return Return the value of the header, or an empty string if the header does not exist.
	 */
    public static String getHeaderValueFromDeliveryStatus(DeliveryStatus status, String headerName) 
    {
		String result = "";
		final int cnt = status.getRecipientDSNCount();
		for (int i = 0; i < cnt; ++i) 
		{
		    final InternetHeaders recipHeaders = status.getRecipientDSN(i);
		    if (recipHeaders != null) 
		    {
		    	final String value = recipHeaders.getHeader(headerName, ",");
		    	if (value != null && value.length() > 0) 
		    	{
		    		result = value;
		    		break;
		    	}
		    }
		}
		return result;
    }
    
    /**
     * Get the list final recipients as a comma delimited string from the delivery status part
     * of a DSN message
     * @param status The deliver status structure
     * @return List of the final recipients as a command delimited string or an empty string if 
     * no final recipients exist.
     */
    public static String getFinalRecipients(DeliveryStatus status) 
    {
		StringBuilder builder = new StringBuilder();
		final int cnt = status.getRecipientDSNCount();
		int recipientCount = 0;
		for (int i = 0; i < cnt; ++i) 
		{
		    final InternetHeaders recipHeaders = status.getRecipientDSN(i);
		    if (recipHeaders != null) 
		    {
		    	final String value = recipHeaders.getHeader(DSNStandard.Headers.FinalRecipient, ",");
		    	if (value != null && value.length() > 0) 
		    	{
		    		String recipient = value.substring(value.indexOf(";") + 1);
		    		if (recipient != null && recipient.length() > 0) 
		    		{
		    			if (recipientCount > 0) 
		    			{
		    				builder.append(",");
		    		    }
		    	     	builder.append(recipient.trim());
		    		    ++recipientCount;
		    		}
		    	}
		    }
		}
		
		return builder.toString();
    }
}
