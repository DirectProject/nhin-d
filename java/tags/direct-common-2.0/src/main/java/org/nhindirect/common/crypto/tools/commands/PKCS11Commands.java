package org.nhindirect.common.crypto.tools.commands;

import java.io.File;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.WrappableKeyProtectionManager;
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

    private static final String IMPORT_P12_FILE_FOR_TEMP_KEY = "Imports a p12 file and creates a temporary private key entry.\r\n " +
    		"\r\n  p12FileName keyStorePass privKeyPass " +
    		"\r\n\t  p12FileName Full path of the p12 file " +      		
    		"\r\n\t  keyStorePass Optional keystore password.  Using empty quotes if empty " +
    		"\r\n\t  privKeyPass Optional private key password.  Using empty quotes if empty ";
    
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
			final KeyGenerator keyGen = KeyGenerator.getInstance("AES", mgr.getKS().getProvider().getName());
			keyGen.init(128); 
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
			key = Arrays.copyOf(key, 16); // use only first 128 bitc
			
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
	
	@Command(name = "ImportP12FileForTempKey", usage = IMPORT_P12_FILE_FOR_TEMP_KEY)
	public void importPrivateKeyFile(String[] args)
	{
		
		if (!(mgr instanceof WrappableKeyProtectionManager))
		{
			System.out.println("Key store manager does not support wrapping.");
			return;
		}
		
		final WrappableKeyProtectionManager wrapMgr = (WrappableKeyProtectionManager)mgr;
		
		final String fileName = StringArrayUtil.getRequiredValue(args, 0);
		final String keyStorePass = StringArrayUtil.getOptionalValue(args, 1, "");
		final String privKeyPass = StringArrayUtil.getOptionalValue(args, 2, "");
		
		try
		{	
			final KeyStore pkcs11Store = mgr.getKS();
			
			final String providerName = pkcs11Store.getProvider().getName();
			
			System.out.println("Provider Name: " + providerName);
			
			/*
			 * 1. Create an AES128 secret key on the HSM that will be used to 
			 * encrypt and decrypt private key data.  Use the PrivKeyProtKey entry to store it
			 */
			final KeyGenerator keyGen = KeyGenerator.getInstance("AES", providerName);
			keyGen.init(128); 
			final SecretKey keyStoreSecretKey = keyGen.generateKey();
			
			/*
			 * 2. Get an existing private key that was generated and is stored in a p12 file.  
			 * For real operations, the private key may be generated on an HSM and exported in wrapped format for
			 * storage in a database.  For this test, we'll just use an existing private key in a p12 file and 
			 * wrap it on the HSM.
			 */
			final KeyStore store = KeyStore.getInstance("pkcs12");
			store.load(FileUtils.openInputStream(new File(fileName)), keyStorePass.toCharArray());
			// there should only be on entry
			final String alias = store.aliases().nextElement();
			final PrivateKey entry = (PrivateKey)store.getKey(alias, privKeyPass.toCharArray());
			
			/*
			 * 3. "Wrap" the private using secret key and AES128 encryption and write it to a file.  The encryption is done
			 * on the HSM so the secret key never leaves the HSM token.  We aren't actually "wrapping" the private key because
			 * it's not on the HSM.  Using "encrypt" instead.
			 */
			/*
			final Cipher wrapCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", providerName);
			wrapCipher.init(Cipher.WRAP_MODE, keyStoreSecretKey, iv);
			byte[] wrappedKey = wrapCipher.wrap(entry);
			*/
			byte[] wrappedKey = wrapMgr.wrapWithSecretKey(keyStoreSecretKey, entry);
			
			/*
			 * 4. Now we have a wrap key in a file.  Let's install it into the token using the 
			 * secret key on the HSM.  This should return us with a private key object, but we should
			 * not be able to get access to the actual unencrypted key data.
			 */
			byte[] encryptedKey = wrappedKey;
			/*
			final Cipher unwrapCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", providerName);
			unwrapCipher.init(Cipher.UNWRAP_MODE, keyStoreSecretKey, iv);
			@SuppressWarnings("unused")
			final PrivateKey securedPrivateKey = (PrivateKey)unwrapCipher.unwrap(encryptedKey, "RSA", Cipher.PRIVATE_KEY);
			*/
			@SuppressWarnings("unused")
			final PrivateKey securedPrivateKey = (PrivateKey)wrapMgr.unwrapWithSecretKey(keyStoreSecretKey, encryptedKey, "RSA", Cipher.PRIVATE_KEY);
			System.out.println("Successfully created an unwrapped private key");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
