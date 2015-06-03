package org.nhindirect.stagent.cert.tools;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.Type;

public class RawRecursiveDNSCertQuery 
{
	protected static List<String> dnsServers = new ArrayList<String>();
	
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
            printUsage();
            System.exit(-1);			
		}
		
		String emailAddress = "";
		String[] servers = null;	
		
		// Check parameters
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];

            // Options
            if (!arg.startsWith("-"))
            {
                System.err.println("Error: Unexpected argument [" + arg + "]\n");
                printUsage();
                System.exit(-1);
            }
            else if (arg.equalsIgnoreCase("-add"))
            {
                if (i == args.length - 1 || args[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing email address");
                    System.exit(-1);
                }
                
                emailAddress = args[++i];
                
            }
            else if (arg.equals("-server"))
            {
                if (i == args.length - 1 || args[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing DNS server list");
                    System.exit(-1);
                }
                servers = args[++i].split(",");
            }
            else if (arg.equals("-help"))
            {
                printUsage();
                System.exit(-1);
            }            
            else
            {
                System.err.println("Error: Unknown argument " + arg + "\n");
                printUsage();
                System.exit(-1);
            }
        }		
			
        if (emailAddress == null || emailAddress.isEmpty())
        {
        	System.err.println("You must provide an email address.");
        	printUsage();
        } 
        
        setServers(servers);
        
        int attemptCount = 0;
        
        while (true)
        {
        	
        	try
        	{
	        	final String lookupName = emailAddress.replace('@', '.');
	        	// run this forever
	        	final Lookup lu = new Lookup(new Name(lookupName), Type.CERT);
	        	lu.setResolver(createExResolver(dnsServers.toArray(new String[dnsServers.size()]), 0, 6));
	        	lu.setSearchPath((String[])null);
	        	
	        	// clear the cache
	        	lu.setCache(new Cache(DClass.IN));
	        	
	        	long startTime = System.currentTimeMillis();
	        	
	        	final Record[] retRecords = lu.run();
	        	
	        	long endTime = System.currentTimeMillis();
	        	
	        	if (retRecords == null || retRecords.length == 0)
	        	{
	        		System.out.println("----- Found no certificates -------");
	        		System.out.println("\tDNS search took " + (endTime - startTime) + "ms\r\n");
	        		System.out.println("Failed after " + attemptCount + " successful resolution attempts.");
	        		System.exit(0);
	        	}
	        	else
	        	{
	        		System.out.println("Found " + retRecords.length + " certificates");
	        	}
	        	System.out.println("\tDNS search took " + (endTime - startTime) + "ms\r\n");
	        	
	        	Thread.sleep(1000);
	        	
        	}
        	catch (Exception e)
        	{
        		e.printStackTrace();
        	}
        	++attemptCount;	
        }
        
        
	}
	
	public static void setServers(String[] servers)
	{
		if (servers == null || servers.length == 0)
		{
			String[] configedServers = ResolverConfig.getCurrentConfig().servers();
			
			if (configedServers != null)
			{
				dnsServers.addAll(Arrays.asList(configedServers));
			}		
		}		
		else
		{
			dnsServers.clear();
			dnsServers.addAll(Arrays.asList(servers));
		}
	}
	
	protected static ExtendedResolver createExResolver(String[] servers, int retries, int timeout)
	{
		ExtendedResolver retVal = null;
		
		// support for IP addresses instead of names
        for (int i = 0; servers != null && i < servers.length; i++) 
        {
            servers[i] = servers[i].replaceFirst("\\.$", "");
        }
		
		try
		{
			retVal = new ExtendedResolver(servers);
			retVal.setRetries(retries);
			retVal.setTimeout(timeout);
			retVal.setTCP(true);
		}
		catch (UnknownHostException e) {/* no-op */}
		return retVal;
	}
	
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java DNSCertDumper (options)...\n\n");
        use.append("options:\n");
        use.append("-add address		Email address of org/domain to retrieve certs for.\n");
        use.append("\n");
        use.append("-server     		Comma delimited list of DNS servers used for lookup.\n");
        use.append("			Default: Local machine's configured DNS server(s)\n\n");            
        use.append("-out  Out File		Optional output file name for the cert.\n");
        use.append("			Default: <email address>(<cert num>).der\n\n");    
        

        System.err.println(use);        
    }	
}
