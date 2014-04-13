package org.nhindirect.install;

public class AddDomainTest 
{
	public static void main(String[] args)
	{
		AddDomainCAAndPrivCert.main(new String[] {"http://localhost:8081/config-service/ConfigurationService", "test.com", "CA", "test.com"});
	}
}
