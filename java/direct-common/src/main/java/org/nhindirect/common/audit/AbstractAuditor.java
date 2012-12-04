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

package org.nhindirect.common.audit;

import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

/**
 * Abstract base class {@link Auditor} that performs trivial sanity checks such as parameter validation.  All calls are delegated to 
 * {@link #writeEvent(String, Calendar, String, String, Collection) which is implemented by a concrete sub class to commit the event to the
 * underlying medium.  Before calling {@link #writeEvent(String, Calendar, String, String, Collection), a unique event id and time stamp is generated.
 *  
 * @author Greg Meyer
 * @since 1.0
 */
public abstract class AbstractAuditor implements Auditor
{
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
		
		writeEvent(UUID.randomUUID(), Calendar.getInstance(), principal, event, contexts);		
	}

	/**
	 * Writes the auditable event to the storage medium.
	 * @param eventId An arbitrary unique ID for the event.
	 * @param eventTimeStamp The date/time that the event record was created.
	 * @param principal An identifier of the entity that performed the event.  This may be an actual user performing a workflow or a system entity
	 * performing back office processing.  Cannot be null or empty.
	 * @param event The event that was performed and that will be audited.  Cannot be null
	 * @param contexts A collection of contexts that provide additional information for the event.
	 */
	public abstract void writeEvent(UUID eventId, Calendar eventTimeStamp, String principal, AuditEvent event, Collection<? extends AuditContext> contexts);
}
