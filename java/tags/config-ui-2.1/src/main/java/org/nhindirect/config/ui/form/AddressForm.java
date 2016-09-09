package org.nhindirect.config.ui.form;

import org.nhindirect.config.model.EntityStatus;

public class AddressForm {
    private String emailAddress;

    private Long id;

    private String displayName;
    
    private String endpoint;

    private EntityStatus aStatus;

    private String type;

    private String domainName;
    
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}
	
	public String getDomainName()
	{
		return this.domainName;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setaStatus(EntityStatus aStatus) {
		this.aStatus = aStatus;
	}

	public EntityStatus getaStatus() {
		return aStatus;
	}

}
