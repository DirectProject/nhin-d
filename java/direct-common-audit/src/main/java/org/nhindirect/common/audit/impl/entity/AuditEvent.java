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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Audit event data entity.
 * @author Greg Meyer
 * @since 1.0
 */
@Entity
@Table(name = "auditevent")
public class AuditEvent 
{
	private long id;
	private String UUID;
	private String principal;
	private String eventName;
	private String eventType;	
    private Calendar eventTime;  
    private Collection<AuditContext> auditContexts;   
    
	
	public AuditEvent()
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
     * Get the value of UUID.
     * 
     * @return the value of UUID.
     */
    @Column(name = "uuid", unique = false)
    public String getUUID() 
    {
        return UUID;
    }    

    
    /**
     * Gets the value of UUID.
     * @param UUID Get the value of UUID.
     */
    public void setUUID(String UUID)
    {
    	this.UUID = UUID;
    }
    
    /**
     * Get the value of principal.
     * 
     * @return the value of principal.
     */
    @Column(name = "principal")
    public String getPrincipal() 
    {
        return principal;
    }    

    
    /**
     * Gets the value of principal.
     * @param principal Get the value of principal.
     */
    public void setPrincipal(String principal)
    {
    	this.principal = principal;
    }
    
    
    /**
     * Get the value of eventName.
     * 
     * @return the value of eventName.
     */
    @Column(name = "eventName", unique = false)
    public String getEventName() 
    {
        return eventName;
    }    

    
    /**
     * Gets the value of eventName.
     * @param eventName Get the value of eventName.
     */
    public void setEventName(String eventName)
    {
    	this.eventName = eventName;
    }
    
    /**
     * Get the value of eventType.
     * 
     * @return the value of eventType.
     */
    @Column(name = "eventType", unique = false)
    public String getEventType() 
    {
        return eventType;
    }    

    
    /**
     * Gets the value of eventType.
     * @param eventType Get the value of eventType.
     */
    public void setEventType(String eventType)
    {
    	this.eventType = eventType;
    } 
    
    /**
     * Get the value of createTime.
     * 
     * @return the value of createTime.
     */
    @Column(name = "eventTime", nullable = false)    
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getEventTime() 
    {
        return eventTime;
    }

    /**
     * Set the value of eventTime.
     * 
     * @param eventTime
     *            The value of eventTime.
     */
    public void setEventTime(Calendar eventTime) 
    {
    	this.eventTime = eventTime;
    }    

    /**
     * Get a collection of AuditContexts.
     * 
     * @return a collection of AuditContexts.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "auditEvent")
    public Collection<AuditContext> getAuditContexts() 
    {
        if (auditContexts == null) 
        {
        	auditContexts = new ArrayList<AuditContext>();
        }
        return auditContexts;
    }

    /**
     * Set the collection of AuditContexts.
     * 
     * @param contexts
     *            the value of AuditContexts
     */
    public void setAuditContexts(Collection<AuditContext> contexts)
    {
        this.auditContexts = contexts;
    }
}
