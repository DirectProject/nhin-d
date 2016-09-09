package org.nhindirect.config.manager;

import java.util.Arrays;

import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.EntityStatus;
import org.nhindirect.config.manager.printers.DomainPrinter;
import org.nhindirect.dns.tools.utils.Command;
import org.nhindirect.dns.tools.utils.StringArrayUtil;

public class DomainCommands
{
    private static final String LIST_DOMAINS_USAGE = "Lists domains in the system";
	
    private static final String ADD_DOMAIN_USAGE = "Adds a domain to the system." +
    		"\r\n  domainName postmasterEmail " +
            "\r\n\t domainName: The name of the new domain." +
            "\r\n\t postmasterEmail: The email address of the postmaster of the domain.";
    
    private static final String DELETE_DOMAIN_USAGE = "Deletes a domain from the system." +
    		"\r\n  id " +
            "\r\n\t id: The id of the domain.";
    
	protected ConfigurationServiceProxy proxy;
	
	protected final DomainPrinter domainPrinter;
	
	DomainCommands(ConfigurationServiceProxy proxy)
	{
		this.proxy = proxy;	
		
		this.domainPrinter = new DomainPrinter();
	}  
	
	@Command(name = "ListDomains", usage = LIST_DOMAINS_USAGE)
    public void listDomains(String[] args)
    {
		// get them all
		try
		{
			final Domain[] domains = proxy.listDomains(null, 100000);
			
			if (domains == null || domains.length == 0)
			{
				System.out.println("No domains have been created.");
				return;
			}
			
			domainPrinter.printRecords(Arrays.asList(domains));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to retrieve domains: " + e.getMessage());
		}

    }
	
	@Command(name = "AddDomain", usage = ADD_DOMAIN_USAGE)
    public void addDomain(String[] args)
    {
		final String domainName = StringArrayUtil.getRequiredValue(args, 0);
		final String postmasterEmail = StringArrayUtil.getRequiredValue(args, 1);
		
		try
		{
			// make sure this domain name doesn't already exist
			final Domain[] exDomain = proxy.listDomains(domainName, 1);
			
			if (exDomain != null && exDomain.length > 0)
			{
				System.out.println("The domain " + domainName + " already exists in the system");
				return;
			}
			
			final Domain newDomain = new Domain();
			newDomain.setDomainName(domainName);
			newDomain.setPostMasterEmail(postmasterEmail);
			newDomain.setStatus(EntityStatus.ENABLED);
			
			proxy.addDomain(newDomain);
			
			System.out.println("Domain " + domainName + " successfully added.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to add new domain: " + e.getMessage());
		}
    }
	
	@Command(name = "DeleteDomain", usage = DELETE_DOMAIN_USAGE)
    public void deleteDomain(String[] args)
    {
		final String strId = StringArrayUtil.getRequiredValue(args, 0);
		long id = Long.parseLong(strId); 
		
		try
		{
			// make sure this domain actually exists
			final Domain exDomain = proxy.getDomain(id);
			
			if (exDomain == null)
			{
				System.out.println("The domain with the id " + id + " does not exists in the system");
				return;
			}
			
			proxy.removeDomainById(id);
			
			System.out.println("Domain " + exDomain.getDomainName() + " successfully removed.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to add new domain: " + e.getMessage());
		}
    }
}
