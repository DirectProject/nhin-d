package org.nhindirect.common.crypto.tools.commands;

import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.tools.commands.printers.KeyPrinter;
import org.nhindirect.common.tooling.Command;
import org.nhindirect.common.tooling.StringArrayUtil;

public class PKCS11Commands 
{	
    private static final String LIST_SECRET_KEYS = "Lists secret keys in the HSM";
	
    private static final String ADD_RANDOM_SECRET_KEY = "Creates a new named random AES128 secret key\r\n" +
    		"\r\n  keyName" +
            "\r\n\t keyName: The unique name of the new secret key.  Place the key name in quotes (\"\") if there are spaces in the name."; 
 
    private static final String ADD_USER_SECRET_KEY = "Creates a new named AES128 secret key via user entered text\r\n" +
    		"\r\n  keyName keyText" +
            "\r\n\t keyName: The unique name of the new secret key.  Place the key name in quotes (\"\") if there are spaces in the name." +
            "\r\n\t keyText: The user entered key text.  Place the text in quotes (\"\") if there are spaces in the text.";   
    
    private static final String REMOVE_SECRET_KEY = "Removes a new named secret key\r\n " +
    		"\r\n  keyName" +
            "\r\n\t keyName: The unique name of the secret key.  Place the key name in quotes (\"\") if there are spaces in the name.";
    
    protected final KeyPrinter keyPrinter;
    
	protected final MutableKeyStoreProtectionManager mgr; 
	
	public PKCS11Commands(MutableKeyStoreProtectionManager mgr)
	{
		this.mgr = mgr;
		this.keyPrinter = new KeyPrinter();
	}
	
	@Command(name = "ListSecretKeys", usage = LIST_SECRET_KEYS)
    public void listCerts(String[] args)
	{
		try
		{
			// get all of the data from the token
			final Map<String, Key> keys = mgr.getAllKeys();
			
			if (keys.isEmpty())
				System.out.println("No keys found");
			
			else
			{
				final Collection<KeyModel> models = new ArrayList<KeyModel>();
				
				for (Entry<String, Key> entry : keys.entrySet())
				{
					char[] keyText = (entry.getValue().getEncoded() != null) ? "*****".toCharArray() : "Not Extractable".toCharArray();
						
					final KeyModel keyModel = new KeyModel(entry.getKey(), entry.getValue(), keyText);
					models.add(keyModel);
				}
				
				keyPrinter.printRecords(models);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	@Command(name = "CreateRandomSecretKey", usage = ADD_RANDOM_SECRET_KEY)
    public void addRandomSecretKey(String[] args)
	{
		final String keyName = StringArrayUtil.getRequiredValue(args, 0);
		
		// generate a new random secret key
		try
		{
			final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			final SecureRandom random = new SecureRandom(); // cryptograph. secure random 
			keyGen.init(random); 
			final SecretKey key = keyGen.generateKey();
			
			mgr.clearKey(keyName);
			mgr.setKey(keyName, key);
		}
		catch (Exception e)
		{
			System.err.println("Failed to add new random secret key: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Command(name = "CreateUserSecretKey", usage = ADD_USER_SECRET_KEY)
    public void addUserSecretKey(String[] args)
	{
		final String keyName = StringArrayUtil.getRequiredValue(args, 0);
		final String keyText = StringArrayUtil.getRequiredValue(args, 1);
		
		try
		{		
			byte[] key = keyText.getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
			
			mgr.clearKey(keyName);
			mgr.setKey(keyName, new SecretKeySpec(key, "AES"));
		}
		catch (Exception e)
		{
			System.err.println("Failed to add new random secret key: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Command(name = "RemoveSecretKey", usage = REMOVE_SECRET_KEY)
    public void removeSecretKey(String[] args)
	{
		String keyName = StringArrayUtil.getRequiredValue(args, 0);
		
		// remove secret key
		try
		{
			
			mgr.clearKey(keyName);
		}
		catch (Exception e)
		{
			System.err.println("Failed to add new random secret key: " + e.getMessage());
		}
	}
	
}
