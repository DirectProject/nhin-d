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
