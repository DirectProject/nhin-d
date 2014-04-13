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

import java.util.Collection;

/**
 * The auditor interface defines the auditing sub-system of the Direct Project.  Auditable events consists of event descriptor, a principal, and 
 * a collection of contextual information.  The auditing implementation may use any
 * appropriate medium and format to store audit events.  Some implementations may even consist of multiple audit implementation providers.
 * <p>
 * 
 * Auditor implementations should be thread safe and not block for relatively long periods of time to commit audit events to the underlying
 * storage medium.
 * 
 * @author Greg Meyer
 * @since 1.0
 */
public interface Auditor 
{	
	/**
	 * Writes an {@link AuditEvent} to the audit sub-system.
	 * @param principal An identifier of the entity that performed the event.  This may be an actual user performing a workflow or a system entity
	 * performing back office processing.  Cannot be null or empty.
	 * @param event The event that was performed and that will be audited.  Cannot be null.
	 */
	public void audit(String principal, AuditEvent event);
	
	/**
	 * Writes an {@link AuditEvent} to the audit sub-system with addition contextual data.
	 * @param principal An identifier of the entity that performed the event.  This may be an actual user performing a workflow or a system entity
	 * performing back office processing.  Cannot be null or empty.
	 * @param event The event that was performed and that will be audited.  Cannot be null
	 * @param contexts A collection of contexts that provide additional information for the event.
	 */
	public void audit(String principal, AuditEvent event, Collection<? extends AuditContext> contexts);

	
}
