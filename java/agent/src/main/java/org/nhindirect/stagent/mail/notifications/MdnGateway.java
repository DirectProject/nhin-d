package org.nhindirect.stagent.mail.notifications;

public class MdnGateway 
{
    private static final String DefaultGatewayType = "smtp";
    
    private String domain;
	private String type;
    public MdnGateway(String domain)
    
	{
    	this(domain, DefaultGatewayType);
	}
    
    public MdnGateway(String domain, String type)
    {
        this.domain = domain;
        this.type = type;
    }    
    
    public String getDomain() 
    {
		return domain;
	}

	public void setDomain(String domain) 
	{
		if (domain == null || domain.isEmpty())
			throw new IllegalArgumentException();
		
		this.domain = domain;
	}

	public String getType() 
	{
		return type;
	}

	public void setType(String type) 
	{
		if (type == null || type.isEmpty())
			throw new IllegalArgumentException();	
		
		this.type = type;
	}

	@Override
    public String toString()
    {
		return type + ";" + domain;
    }

}
