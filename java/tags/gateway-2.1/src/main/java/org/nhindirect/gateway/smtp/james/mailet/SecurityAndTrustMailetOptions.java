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

/**
 * Security and trust mailet options.
 * @author Greg Meyer
 * @since 2.0
 */
public class SecurityAndTrustMailetOptions 
{
	/*
	 * Mailet configuration options through config.xml
	 */
	
    /**
     * Mailet configuration parameter for the Direct configuration service URL.
     * <p>
     * This parameter is configured using the <b>ConfigURL</b> element of the NHINDSecurityAndTrustMailet configuration.
     */
    public static final String CONFIG_URL_PARAM = "ConfigURL";
	
    /**
     * Mailet configuration parameter for the URL of the message monitoring service.
     * <p>
     * This parameter is configured using the <b>MessageMonitoringServiceURL</b> element of the NHINDSecurityAndTrustMailet configuration.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.gateway.smtp.james.mailet.TxServiceURL
     */
    public static final String MONITORING_SERVICE_URL_PARAM = "MessageMonitoringServiceURL";
    
    /**
     * Mailet configuration parameter that indicates if the mailet should automatically generate a DSN failure messages for outbound messages
     * that do not succeed security and trust processing.  The options are applied to both rejected messages and messages that have rejected recipients.
     * <br>
     * There are two options for this parameter: one controlling the generation of DSN messages for messages requesting reliable and timely delivery and
     * one for those message not requesting the reliable and timely delivery.  The parameter is set using a comma delimited string of options that are request to be turned on.
     * <br>
     * 1. <b>General:</b> If this option is set, the mailet will generate a DSN message for messages not requesting reliable and timely delivery.
     * <br>
     * 2. <b>ReliableAndTimely:</b> If this option is set, the mailet will generate a DSN message for messages requesting reliable and timely delivery.
     * <b>
     * A typical configuration may look like the following: <i>General,ReliableAndTimely</i>
     * <br>
     * The ReliableAndTimely option is set by default if this parameter does not exist.
     * <p>
     * This option is configured using the <b>AutoDSNFailueCreation</b> element of the NHINDSecurityAndTrustMailet configuration.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.gateway.smtp.james.mailet.AutoDSNFailueCreation
     */
    public static final String AUTO_DSN_FAILURE_CREATION_PARAM = "AutoDSNFailueCreation";
    
    /**
     * Mailet configuration option determining if MDN processed messages should be consumed by the mailet and not forwarded
     * on to the edge protocol.  Valid options are "true" or "false".  This options defaults to false if not present.
     * <p>
     * This option is configured using the <b>MessageMonitoringServiceURL</b> element of the NotificationSuppressor configuration.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.gateway.smtp.james.mailet.ConsumeMDNProcessed
     */
    public static final String CONSUME_MND_PROCESSED_PARAM = "ConsumeMDNProcessed";
     
}
