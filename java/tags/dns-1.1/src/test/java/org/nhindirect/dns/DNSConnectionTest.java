package org.nhindirect.dns;

import java.net.Inet4Address;

import org.apache.mina.util.AvailablePortFinder;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Options;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.Type;

import junit.framework.TestCase;

public class DNSConnectionTest extends TestCase 
{
	private static String[] servers;
	private static int recType = Type.A;
	private static String lookupRec;
	private static boolean useTCP = false;
	
	private static int getRecTypeFromArg(String arg)
	{
		int retVal = Type.A;
		
		if (arg.equalsIgnoreCase("A"))
			retVal = Type.A;
		else if (arg.equalsIgnoreCase("CERT"))
			retVal = Type.CERT;
		else if (arg.equalsIgnoreCase("SOA"))
			retVal = Type.SOA;
		else if (arg.equalsIgnoreCase("MX"))
			retVal = Type.MX;
		else
			System.err.println("Warning: Unsupported record type " + arg + ".  Defaulting to type A.");

		return retVal;
	}
	
	/*
	 * Application entry point for testing dnsjava functionality on different platforms. 
	 */
	public static void main(String argv[])
	{
		if (argv.length == 0)
		{
            printUsage();
            System.exit(-1);			
		}
		
		// Check parameters
        for (int i = 0; i < argv.length; i++)
        {
            String arg = argv[i];

            // Options
            if (!arg.startsWith("-"))
            {
            	lookupRec = arg;
            }
            else if (arg.equalsIgnoreCase("-serv"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing server list.");
                    System.exit(-1);
                }                
                servers = argv[++i].split(",");                
            }
            else if (arg.equals("-type"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing record type.");
                    System.exit(-1);
                }
                
                recType = getRecTypeFromArg(argv[++i]);
            }
            else if (arg.equalsIgnoreCase("-useTCP"))
            {
                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
                {
                    System.err.println("Error: Missing TCP indicator");
                    System.exit(-1);
                }                
                useTCP = Boolean.parseBoolean(argv[++i]);              
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

        if (lookupRec == null)
        {
            System.err.println("Error: Missing record to lookup\n");
            printUsage();
            System.exit(-1);
        }
        
        try
        {
        	performLookup();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
        System.exit(0);

	}
	
	private static void performLookup() throws Exception
	{
		// turn on debug settings for the DNS client
		Options.set("verbose", "true");
		Cache ch = Lookup.getDefaultCache(DClass.IN);
		ch.clearCache();
		
		if (servers == null || servers.length == 0)
			servers = ResolverConfig.getCurrentConfig().servers();
		
		System.out.println("\r\nConfigure DNS resolvers:");
		for (String server : servers)
		{
			System.out.println("\t" + server);
		}
		
		System.out.println("\r\nLookup up record " + lookupRec);
		
		Lookup lu = new Lookup(new Name(lookupRec), recType);
		ExtendedResolver resolver = new ExtendedResolver(servers);
		resolver.setTCP(useTCP);
		lu.setResolver(resolver);
	
		
		Record[] retRecords = lu.run();
		
		if (retRecords != null && retRecords.length > 0)
			System.out.println(retRecords.length + " records found.");
		else
			System.out.println("No records found.");
		
	}
	
	/*
	 * Prints the command line usage. 
	 */
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java DNSConnectionTest (options)...<record name>\n\n");
        use.append("options:\n");
        use.append("-serv   server		List of DNS servers used for resolution\n");
        use.append("			Default: Currently configured DNS servers\n\n");
        use.append("-type   record type   Type of DNS record to lookup\n");
        use.append("			Default: A\n\n");
        use.append("-useTCP use TCP   Use TCP to connect to the DNS server\n");
        use.append("			Default: false\n\n");        

        System.err.println(use);        
    }	
	
	public void testDNSSocketConnectionTCPWithProxyStore() throws Exception
	{		
		DNSServerSettings settings = new DNSServerSettings();
		settings.setPort(AvailablePortFinder.getNextAvailable( 1024 ));
		
		DNSServer server = new DNSServer(new ProxyDNSStore(), settings);
		
		
		server.start();
		
		// give the server a couple seconds to start
		Thread.currentThread().sleep(2000);
		
		// turn on debug settings for the DNS client
		Options.set("verbose", "true");
		
		Lookup lu = new Lookup(new Name("google.com"), Type.A);
		Inet4Address.getLocalHost();
		ExtendedResolver resolver = new ExtendedResolver(new String[] {"127.0.0.1", Inet4Address.getLocalHost().getHostAddress()});
		resolver.setTCP(true);
		resolver.setPort(settings.getPort());
		lu.setResolver(resolver); // default retries is 3, limite to 2
	
		
		Record[] retRecords = lu.run();
		assertNotNull(retRecords);
		
		
		
		server.stop();
		
		Thread.currentThread().sleep(4000);
	}

	
	public void testDNSSocketConnectionUDPWithProxyStore() throws Exception
	{
		
		
		DNSServerSettings settings = new DNSServerSettings();
		settings.setPort(AvailablePortFinder.getNextAvailable( 1024 ));
		
		DNSServer server = new DNSServer(new ProxyDNSStore(), settings);
		
		
		server.start();
		
		// give the server a couple seconds to start
		Thread.currentThread().sleep(2000);
		
		// turn on debug settings for the DNS client
		Options.set("verbose", "true");
		
		Lookup lu = new Lookup(new Name("google.com"), Type.A);
		Inet4Address.getLocalHost();
		ExtendedResolver resolver = new ExtendedResolver(new String[] {"127.0.0.1", Inet4Address.getLocalHost().getHostAddress()});
		resolver.setTCP(false);
		resolver.setPort(settings.getPort());
		lu.setResolver(resolver); // default retries is 3, limite to 2
	
		
		Record[] retRecords = lu.run();
		assertNotNull(retRecords);
		
		
		server.stop();
		
		Thread.currentThread().sleep(4000);
	}
	
}
