package org.nhindirect.stagent.mail.notifications;

public class ReportingUserAgent 
{
	private String name;
    private String product;
	
    public ReportingUserAgent(String name, String product)
    {
        setName(name);
        setProduct(product);
    }
	
    public String getName() 
    {
		return name;
	}

	public void setName(String name) 
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException();
		
		this.name = name;
	}

	public String getProduct() 
	{
		return product;
	}

	public void setProduct(String product) 
	{
		if (product == null || product.isEmpty())
			throw new IllegalArgumentException();		
		
		this.product = product;
	}    
	
	@Override
	public String toString()
	{
		return name + ";" + product;		
	}
}
