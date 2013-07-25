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

package org.nhindirect.common.audit.impl;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.Auditor;
import org.nhindirect.common.audit.annotation.MultiproviderAuditors;

import com.google.inject.Inject;

/**
 * {@link Auditor} implementation that wraps multiple auditors.  Each call to audit will result (barring exceptions in the delegated
 * auditor) in the event being committed to each auditor. 
 * 
 * @author Greg Meyer
 * @since 1.0
 *
 */
public class MultiProviderAuditor implements Auditor
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(MultiProviderAuditor.class);
	
	private final Collection<? extends Auditor> auditors;

	/**
	 * Creates an auditor with multiple internal auditor instances.
	 * @param auditors The internal auditors that will be used to audit events.
	 */
	@Inject
	public MultiProviderAuditor(@MultiproviderAuditors Collection<? extends Auditor> auditors)
	{
		if (auditors == null || auditors.size() == 0)
			throw new IllegalArgumentException("Auditors collection cannot be null or empty");
		
		this.auditors = Collections.unmodifiableCollection(auditors);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void audit(String principal, AuditEvent event)
	{
		audit(principal, event, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void audit(String principal, AuditEvent event, Collection<? extends AuditContext> contexts)
	{
		if (principal == null || principal.isEmpty())
			throw new IllegalArgumentException("Principal cannot be null or empty");
		
		if (event == null)
			throw new IllegalArgumentException("Event cannot be null");				
		
		for (Auditor auditor : auditors)
		{
			try
			{
				auditor.audit(principal, event, contexts);
			}
			catch (Exception e)
			{
				LOGGER.error("Failed to audit event using auditor " + auditor.getClass().getName(), e);
			}
		}	
	}

}
