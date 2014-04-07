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

package org.nhindirect.common.audit.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Audit context data entity.
 * @author Greg Meyer
 * @since 1.0
 */
@Entity
@Table(name = "auditcontext")
public class AuditContext 
{
	private long id;
	private String contextName;
	private String contextValue;	
	private AuditEvent auditEvent;
	
	public AuditContext()
	{
		
	}
	
    /**
     * Get the value of id.
     * 
     * @return the value of id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() 
    {
        return id;
    }
    
    /**
     * Set the value of id.
     * 
     * @param id
     *            The value of id.
     */
    public void setId(long id) 
    {
        this.id = id;
    } 
    
    /**
     * Get the value of contextName.
     * 
     * @return the value of contextName.
     */
    @Column(name = "contextName", unique = false)
    public String getContextName() 
    {
        return contextName;
    }    

    
    /**
     * Gets the value of contextName.
     * @param contextName Get the value of contextName.
     */
    public void setContextName(String contextName)
    {
    	this.contextName = contextName;
    }
    
    /**
     * Get the value of contextValue.
     * 
     * @return the value of contextValue.
     */
    @Column(name = "contextValue", unique = false)
    public String getContextValue() 
    {
        return contextValue;
    }    

    
    /**
     * Gets the value of contextValue.
     * @param contextValue Get the value of contextValue.
     */
    public void setContextValue(String contextValue)
    {
    	this.contextValue = contextValue;
    }    
    
    /**
     * Get the value of the audit event.
     * 
     * @return the value of audit event.
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "auditEventId")
    public AuditEvent getAuditEvent() 
    {
        return auditEvent;
    }

    /**
     * Set the value of the audit event.
     * 
     * @param trustBundle
     *            The value of the audit event.
     */
    public void setAuditEvent(AuditEvent auditEvent) 
    {
        this.auditEvent = auditEvent;

    }    
}
