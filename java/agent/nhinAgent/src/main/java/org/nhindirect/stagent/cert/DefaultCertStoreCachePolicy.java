package org.nhindirect.stagent.cert;

public class DefaultCertStoreCachePolicy implements  CertStoreCachePolicy
{
	private int maxItems;
	private int subjectTTL;

	public DefaultCertStoreCachePolicy()
	{
		maxItems = 1000;
		subjectTTL = 3600 * 24; // 1 day
	}
	
	public int getMaxItems() 
	{
		return maxItems;
	}

	public void setMaxItems(int maxItems) 
	{
		this.maxItems = maxItems;
	}

	public void setSubjectTTL(int subjectTTL) 
	{
		this.subjectTTL = subjectTTL;
	}

	public int getSubjectTTL() 
	{
		return subjectTTL;
	}

}
