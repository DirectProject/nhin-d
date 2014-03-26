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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;
import org.nhindirect.gateway.smtp.GatewayState;
import org.nhindirect.stagent.cert.CertCacheFactory;

/**
 * Mailet used to refresh the state of the security and trust mailet.  Specifically it forces the {@link GatewayState} to stop and restart the 
 * settings update manager.  This mailet should be used along with an appropriate matcher such as the RecipeintIs matcher to specify a specific
 * address that will cause the gateway to refresh its state.
 * <br>
 * Messages that match on this mailet will be ghosted and not processed any further in the mailet processing chain. 
 * <br>
 * It is assumed that the gateway to be refreshed is in the same class loader as this mailet.
 * @author Greg Meyer
 * @since 1.4
 */
public class RefreshSecurityAndTrustStateMailet extends GenericMailet 
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(RefreshSecurityAndTrustStateMailet.class);	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void service(Mail mail) throws MessagingException 
	{ 	
		LOGGER.info("Gateway state refresh requested through RefreshSecurityAndTrustStateMailet.  " +
				"Attempting to stop and restart the settings update manager.");
		
		GatewayState gwState = GatewayState.getInstance();
		try
		{
			if (gwState.isAgentSettingManagerRunning())
				gwState.stopAgentSettingsManager();
					
			gwState.startAgentSettingsManager();
			
			// flush the caches
			CertCacheFactory.getInstance().flushAll();
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to restart the settings update manager.", e);
		}
		finally
		{
			mail.setState(Mail.GHOST);
		}
			
	}
		
}
