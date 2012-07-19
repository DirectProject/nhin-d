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


import java.util.HashMap;
import java.util.Map;

import org.apache.mailet.Mailet;
import org.nhindirect.common.mail.dsn.DSNFailureTextBodyPartGenerator;
import org.nhindirect.common.mail.dsn.DSNGenerator;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNStatus;
import org.nhindirect.common.mail.dsn.impl.DefaultDSNFailureTextBodyPartGenerator;
import org.nhindirect.common.mail.dsn.impl.HumanReadableTextAssemblerFactory;
import org.nhindirect.gateway.GatewayConfiguration;
import org.nhindirect.stagent.options.OptionsManager;

public class RejectedRecipientDSNCreator extends AbstractDSNCreator 
{

	
	static
	{		
		initJVMParams();
	}
	
	private synchronized static void initJVMParams()
	{
		/*
		 * Configuration parameters
		 */
		final Map<String, String> JVM_PARAMS = new HashMap<String, String>();
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_PREFIX, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedPrefix");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_MTA_NAME, "org.nhindirect.gateway.smtp.dsn.imp.DSNMTAName");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_POSTMASTER, "org.nhindirect.gateway.smtp.dsn.impl.DNSPostmaster");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_RECIP_TITLE, "org.nhindirect.gateway.smtp.dsn.impl.DSNFaileRecipTitle");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_ERROR_MESSAGE, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedErrorMessage");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_HEADER, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedHeader");
		JVM_PARAMS.put(RejectedRecipientDSNCreatorOptions.DSN_FAILED_FOOTER, "org.nhindirect.gateway.smtp.dsn.impl.DSNFailedFooter");
		
		OptionsManager.addInitParameters(JVM_PARAMS);
	}
	
	///CLOVER:OFF
	public RejectedRecipientDSNCreator(DSNGenerator generator, String postmasterMailbox, String reportingMta, 
			DSNFailureTextBodyPartGenerator textGenerator)
	{
		this.mailet = null;
		this.generator = generator;
		this.postmasterMailbox = postmasterMailbox;
		this.reportingMta = reportingMta;
		this.textGenerator = textGenerator;
		
		this.dsnStatus = DSNStatus.UNDEFINED_STATUS;
	}
	///CLOVER:ON
	
	public RejectedRecipientDSNCreator(Mailet mailet)
	{
		this.mailet = mailet;
		
		this.dsnStatus = DSNStatus.UNDEFINED_STATUS;
		
		generator = new DSNGenerator(GatewayConfiguration.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_PREFIX, 
				mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_PREFIX));
		
		postmasterMailbox = GatewayConfiguration.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_POSTMASTER, 
				mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_POSTMASTER);
		
		reportingMta = GatewayConfiguration.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_MTA_NAME, 
				mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_MTA_NAME);
		
		
		textGenerator = new DefaultDSNFailureTextBodyPartGenerator(
				GatewayConfiguration.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_HEADER, 
						mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_HEADER), 
						GatewayConfiguration.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_FOOTER, 
						mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_FOOTER), 
						GatewayConfiguration.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_RECIP_TITLE, 
						mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_FAILED_RECIP_TITLE), 
					RejectedRecipientDSNCreatorOptions.DEFAULT_ERROR_MESSAGE_TITLE,
					GatewayConfiguration.getConfigurationParam(RejectedRecipientDSNCreatorOptions.DSN_FAILED_ERROR_MESSAGE, 
								mailet, RejectedRecipientDSNCreatorOptions.DEFAULT_ERROR_MESSAGE),
			    HumanReadableTextAssemblerFactory.getInstance());
	}


}
