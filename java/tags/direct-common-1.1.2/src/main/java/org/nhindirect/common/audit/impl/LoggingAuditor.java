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

import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.common.audit.AbstractAuditor;
import org.nhindirect.common.audit.AuditContext;
import org.nhindirect.common.audit.AuditEvent;
import org.nhindirect.common.audit.Auditor;

/**
 * {@link Auditor} implementation that utilizes the Apache Commons logging framework as the storage medium for auditing events.  Audit events are distinguished
 * from other log entries with the tag <i>[DIRECT AUDIT EVENT]</i> in the log text.
 * @author Greg Meyer
 * @since 1.0
 */
public class LoggingAuditor extends AbstractAuditor 
{
	private static final String EVENT_TAG = "[DIRECT AUDIT EVENT]";
	private static final String EVENT_ID = "EVENT ID";
	private static final String EVENT_PRINCIPAL = "EVENT PRINCIPAL";	
	private static final String EVENT_NAME = "EVENT CATEGORY";
	private static final String EVENT_TYPE = "EVENT MESSAGE";
	private static final String EVENT_CTX = "EVENT CONTEXTS";	
	
	private final Log writer = LogFactory.getFactory().getInstance(LoggingAuditor.class);

	/**
	 * Default constructor
	 */
	public LoggingAuditor()
	{

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeEvent(UUID eventId, Calendar eventTimeStamp, String principal, AuditEvent event, Collection<? extends AuditContext> contexts)
	{		
		writer.info(buildEventString(eventId, principal, event, contexts));		
	}

	/*
	 * Builds the string that will be written to the logging sub system.
	 */
	private String buildEventString(UUID eventId, String principal, AuditEvent event, Collection<? extends AuditContext> contexts)
	{
		StringBuilder builder = new StringBuilder(EVENT_TAG);
		
		builder.append("\r\n\t" + EVENT_ID + ": " + eventId);
		builder.append("\r\n\t" + EVENT_PRINCIPAL + ": " + principal);
		builder.append("\r\n\t" + EVENT_NAME + ": " + event.getName());		
		builder.append("\r\n\t" + EVENT_TYPE + ": " + event.getType());

		if (contexts != null && contexts.size() > 0)
		{
			builder.append("\r\n\t" + EVENT_CTX);
			for (AuditContext context : contexts)
				builder.append("\r\n\t\t" + context.getContextName() + ":" + context.getContextValue());
		}
		
		return builder.toString();
	}
}
