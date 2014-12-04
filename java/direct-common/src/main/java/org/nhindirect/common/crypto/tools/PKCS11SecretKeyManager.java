package org.nhindirect.common.crypto.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.common.crypto.impl.BootstrappedPKCS11Credential;
import org.nhindirect.common.crypto.impl.StaticPKCS11TokenKeyStoreProtectionManager;
import org.nhindirect.common.crypto.tools.commands.PKCS11Commands;
import org.nhindirect.common.tooling.Commands;

///CLOVER:OFF
public class PKCS11SecretKeyManager
{

	private static boolean exitOnEndCommands = true;
	private static String keyStoreType = null;
	private static String providerName = null;
	private static String keyStoreSource = null;
	
	private final Commands commands;
	
	protected static String pkcs11ProviderCfg = null;
	protected static String keyStoreConfigFile = null;
	
	public static void main(String[] argv)
	{     
		String[] passArgs = null;

		
		// need to check if there is a configuration for the PKCS11
		// provider... if not, assume the JVM has already been configured for one
		if (argv.length > 0)
		{

			// Check parameters
	        for (int i = 0; i < argv.length; i++)
	        {
	            String arg = argv[i];

	            // Options
	            if (!arg.startsWith("-"))
	            {
	                System.err.println("Error: Unexpected argument [" + arg + "]\n");
	                printUsage();
	                System.exit(-1);
	            }
	            else if (arg.equalsIgnoreCase("-pkcscfg"))
	            {
	                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
	                {
	                    System.err.println("Error: Missing pkcs config file");
	                    System.exit(-1);
	                }
	                
	                pkcs11ProviderCfg = argv[++i];
	                
	            }
	            else if (arg.equals("-keyStoreCfg"))
	            {
	                if (i == argv.length - 1 || argv[i + 1].startsWith("-"))
	                {
	                    System.err.println("Error: Missing keystore config file");
	                    System.exit(-1);
	                }
	                keyStoreConfigFile = argv[++i];
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
		}
		
		if (keyStoreConfigFile != null)
		{
			try
			{
				// get additional properties
				final InputStream inStream = FileUtils.openInputStream(new File(keyStoreConfigFile));
				
				final Properties props = new Properties();
				props.load(inStream);
				
				keyStoreType = props.getProperty("keyStoreType");
				providerName = props.getProperty("keyStoreProviderName");
				keyStoreSource = props.getProperty("keyStoreSource");
			}
			catch (IOException e)
			{
				System.err.println("Error reading keystore config file to properties: " + e.getMessage());
				System.exit(-1);
			}
		}
		
		MutableKeyStoreProtectionManager mgr = null;
		// need to login
		try
		{
			mgr = tokenLogin();
		}
		catch (CryptoException e)
		{
			
			System.out.println("Failed to login to hardware token: " + e.getMessage());
			System.exit(-1);
		}
		final PKCS11SecretKeyManager mgmt = new PKCS11SecretKeyManager(mgr);
		
		boolean runCommand = false;
		
		if (mgmt != null)
		{
			runCommand = mgmt.run(passArgs);
		}

		if (exitOnEndCommands)
			System.exit(runCommand ? 0 : -1);
	}
	
	public boolean run(String[] args)
	{
        if (args != null && args.length > 0)
        {
            return commands.run(args);
        }
        
        commands.runInteractive();
        System.out.println("Shutting Down Configuration Manager Console");
        return true;		
	}
	
   /*
    * Print program usage.
    */
    private static void printUsage()
    {
        StringBuffer use = new StringBuffer();
        use.append("Usage:\n");
        use.append("java PKCS11SecretKeyManager (options)...\n\n");
        use.append("options:\n");
        use.append("-pkcscfg    PKCS11 Config File  Optional location for the PKCS11 provider configuration.  If this is not" +
        		" set, then it is assumed that the JVM has already been configured to support your PKCS11 token.\n");
        use.append("            Default: \"\"\n\n");

        System.err.println(use);        
    }
	
    public static MutableKeyStoreProtectionManager tokenLogin() throws CryptoException
    {	
    	try
    	{
    		
			 final Console cons = null;//System.console();
			 char[] passwd = null;
			 if (cons != null) 
			 {
				 passwd = cons.readPassword("[%s]", "Enter hardware token password: ");
			     java.util.Arrays.fill(passwd, ' ');
			 }
			 else
			 {
				 System.out.print("Enter hardware token password: ");
				  final BufferedReader reader = new BufferedReader(new InputStreamReader(
				            System.in));
				  passwd = reader.readLine().toCharArray();
			 }
				
			final BootstrappedPKCS11Credential cred = new BootstrappedPKCS11Credential(new String(passwd));
			final StaticPKCS11TokenKeyStoreProtectionManager loginMgr = new StaticPKCS11TokenKeyStoreProtectionManager();
			loginMgr.setCredential(cred);
			loginMgr.setKeyStoreProviderName(providerName);

			if (!StringUtils.isEmpty(keyStoreType))
				loginMgr.setKeyStoreType(keyStoreType);
			
			if (!StringUtils.isEmpty(keyStoreSource))
			{
				InputStream str = new ByteArrayInputStream(keyStoreSource.getBytes());
				loginMgr.setKeyStoreSource(str);
			}
			
			if (!StringUtils.isEmpty(pkcs11ProviderCfg))
				loginMgr.setPcks11ConfigFile(pkcs11ProviderCfg);
			
			loginMgr.initTokenStore();
			
	    	return loginMgr;
    	}
    	catch (Exception e)
    	{
    		throw new RuntimeException("Error getting password.", e);
    	}

    }
    
	public PKCS11SecretKeyManager(MutableKeyStoreProtectionManager mgr)
	{	
		commands = new Commands("PKCS11 Secret Key Management Console");
	    commands.register(new PKCS11Commands(mgr));	
	}
	public static void setExitOnEndCommands(boolean exit)
	{
		exitOnEndCommands = exit;
	}	
}
///CLOVER:ON