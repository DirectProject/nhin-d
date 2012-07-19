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

package org.nhindirect.gateway.smtp.dsn.impl;

public class FailedDeliveryDSNCreatorOptions 
{
	public static final String DEFAULT_PREFIX = "Undeliverable: ";
	
	public static final String DEFAULT_MTA_NAME = "Local Delivery Service";
	
	public static final String DEFAULT_POSTMASTER = "postmaster";
	
	public static final String DEFAULT_FAILED_RECIP_TITLE = "Failed Recipients:";
	
	public static final String DEFAULT_ERROR_MESSAGE_TITLE = "Error Message:";
	
	public static final String DEFAULT_ERROR_MESSAGE = "The Direct address that you tried to reach could not be located or does not exist. Try double-checking the recipient's email address for typos or unnecessary spaces. Learn more by visiting our help.";

	public static final String DEFAULT_HEADER = "We were permanently unable to deliver your message to the following recipients.  Please contact your system administrator with further questions.";
	
	public static final String DEFAULT_FOOTER = "";
	
	
	public static final String DSN_FAILED_PREFIX = "DeliveryFailureMessageDSNPrefix";
	
	public static final String DSN_MTA_NAME = "DeliveryFailureMessageDSNMTAName";
	
	public static final String DSN_POSTMASTER = "DeliveryFailureMessageDSNPostmaster";
	
	public static final String DSN_FAILED_RECIP_TITLE = "DeliveryFailureMessageDSNFailedRecipTitle";
	
	public static final String DSN_FAILED_ERROR_MESSAGE = "DeliveryFailureMessageDSNErrorMessage";
	
	public static final String DSN_FAILED_HEADER = "DeliveryFailureMessageDSNHeader";
	
	public static final String DSN_FAILED_FOOTER = "DeliveryFailureMessageDSNFooter";
}
