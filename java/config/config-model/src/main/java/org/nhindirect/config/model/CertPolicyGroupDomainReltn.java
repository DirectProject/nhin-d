package org.nhindirect.config.model;

public class CertPolicyGroupDomainReltn 
{
	private long id;
	
	private Domain domain;
	
	private CertPolicyGroup policyGroup;
	
	public CertPolicyGroupDomainReltn()
	{
		
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public Domain getDomain() 
	{
		return domain;
	}

	public void setDomain(Domain domain) 
	{
		this.domain = domain;
	}

	public CertPolicyGroup getPolicyGroup() 
	{
		return policyGroup;
	}

	public void setPolicyGroup(CertPolicyGroup policyGroup) 
	{
		this.policyGroup = policyGroup;
	}
	
	
}
