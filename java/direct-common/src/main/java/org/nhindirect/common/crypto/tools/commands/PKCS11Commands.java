package org.nhindirect.common.crypto.tools.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509ExtensionsGenerator;
import org.bouncycastle.crypto.prng.VMPCRandomGenerator;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.WrappableKeyProtectionManager;
import org.nhindirect.common.crypto.impl.AbstractPKCS11TokenKeyStoreProtectionManager;
import org.nhindirect.common.crypto.tools.commands.printers.KeyPrinter;
import org.nhindirect.common.tooling.Command;
import org.nhindirect.common.tooling.StringArrayUtil;


public class PKCS11Commands 
{		
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
    private static final String LIST_SECRET_KEYS = "Lists secret keys in the HSM";
	
    private static final String LIST_ALL_KEYS = "Lists all keys in the HSM";
    
    private static final String ADD_RANDOM_SECRET_KEY = "Creates a new named random AES128 secret key\r\n" +
    		"\r\n  keyName" +
            "\r\n\t keyName: The unique name of the new secret key.  Place the key name in quotes (\"\") if there are spaces in the name."; 
 
    private static final String ADD_USER_SECRET_KEY = "Creates a new named AES128 secret key via user entered text\r\n" +
    		"\r\n  keyName keyText" +
            "\r\n\t keyName: The unique name of the new secret key.  Place the key name in quotes (\"\") if there are spaces in the name." +
            "\r\n\t keyText: The user entered key text.  Place the text in quotes (\"\") if there are spaces in the text.";   
    
    private static final String REMOVE_KEY = "Removes a new named key\r\n " +
    		"\r\n  keyName" +
            "\r\n\t keyName: The unique name of the secret key.  Place the key name in quotes (\"\") if there are spaces in the name.";

    private static final String CREATE_KEY_PAIR = "Creates a new public/private key pair." +
    		"\r\n  keyName [keySize] " +
            "\r\n\t keyName: The key name given to the key pair." +
            "\r\n\t keySize: Option size of the key (modulus).  If not provided, the size will default to 2048";
    
    private static final String EXPORT_PUBLIC_KEY = "Exports the public key of an RSA key pair." +
    		"\r\n  keyName [file] " +
            "\r\n\t keyName: The key name given to the key pair." +
            "\r\n\t file: Optional name of the file to export to.  By default, that key name name will be use.";
    
    private static final String EXPORT_PUB_KEY_CERTIFICATE = "Exports the certificate associated with an RSA key pair." +
    		"\r\n  keyName [file] " +
            "\r\n\t keyName: The key name given to the key pair." +
            "\r\n\t file: Optional name of the file to export to.  By default, that key name name will be use.";
    
    private static final String EXPORT_PRIVATE_KEY = "Exports the private key of an RSA key pair in wrapped format.  NOTE, some devices may not allow exporting of private keys." +
    		"\r\n  keyName wrapperKeyName [file] " +
            "\r\n\t keyName: The key name given to the key pair." +
            "\r\n\t wrapperKeyName: The key name given to secret key used to wrap the private key." +    		
            "\r\n\t file: Optional name of the file to export to.  By default, that key name name will be use.";
   
    private static final String UPDATE_PUB_KEY_CERT = "Updates the certificate associated with an RSA key pair.  The public key MUST match the existing public key.\r\n " +
    		"\r\n  keyName keyName " +
    		"\r\n\t  certFileName Full path of the certificate file in der format " +      		
    		"\r\n\t  keyName The key entry name of the RSA key pair that will be updated with the new cert ";
    
    private static final String CREATE_CSR = "Creates a certificate signing request using a stored RSA key pair.  Certificates are specific to DirectProject use cases.\r\n " +
    		"\r\n  keyName commonName subjectAltName keyUsage [additionalRDNattributes]" +
    		"\r\n\t  keyName The name of the key pair used in the CSR " +     
    		"\r\n\t  commonName The certificate common name attribute used in the subject RDN field.  Do not start with \"CN=\"; it will be assumed. " +    
    		"\r\n\t  subjectAltName The subject alternative name. " +     
    		"\r\n\t  keyUsage The key usage of the certificate.  Valid value are DigitalSignature, KeyEncipherment, and DualUse" +     
    		"\r\n\t  additionalRDNattributes One or more optional subject RDN fields.  Each of these MUST start with the field name.  "
    			+ "Example: C=US S=Missouri.  Separate each field with a space.  Use quotes \"\" if a field has a space in the field's value.";

    private static final String UNWRAP_KEY = "Checks that a private key can be unwrapped using a protected wrapper AES key\r\n " +
    		"\r\n  wrapperKeyName file " +
            "\r\n\t wrapperKeyName: The key name given to secret key used to unwrap the private key." +    		
            "\r\n\t file: The name of the file that contains the wrapped private key.";    		
    
    private static final String MESSAGE_SIGN_PROFILING = "Runs a test of signing x number of messages and reports the speed" +
    		"\r\n privateKeyName numSigs " +
            "\r\n\t wrapperKeyName: The key name given to secret key used to unwrap the private key." +    		
            "\r\n\t numSigs: The number of signatures to perform.";    	
    
    private static final String IMPORT_P12_FILE_FOR_TEMP_KEY = "Imports a p12 file and creates a temporary private key entry.\r\n " +
    		"\r\n  p12FileName keyStorePass privKeyPass " +
    		"\r\n\t  p12FileName Full path of the p12 file " +      		
    		"\r\n\t  keyStorePass Optional keystore password.  Using empty quotes if empty " +
    		"\r\n\t  privKeyPass Optional private key password.  Using empty quotes if empty ";
    
    private static final String INFINITE_READ = "Enters an infinite loop for reading secret keys\r\n";
    
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
	
	@Command(name = "ListAllKeys", usage = LIST_ALL_KEYS)
    public void listAllKeys(String[] args)
	{
		try
		{

			
			final KeyStore ks = mgr.getKS();
			
			
			
			// get all of the data from the token
			final Enumeration<String> aliases = ks.aliases();
			
			if (!aliases.hasMoreElements())
				System.out.println("No keys found");
			
			else
			{
				final Collection<KeyModel> models = new ArrayList<KeyModel>();
				
				while (aliases.hasMoreElements())
				{	
					final String alias = aliases.nextElement();
					
					
					if (ks.isKeyEntry(alias))
					{
						final Key key = ks.getKey(alias, null);
						
						
						char[] keyText = (key.getEncoded() != null) ? "*****".toCharArray() : "Not Extractable".toCharArray();
						
						final KeyModel keyModel = new KeyModel(alias, key, keyText);
						models.add(keyModel);
					}
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
	
	@Command(name = "RemoveKey", usage = REMOVE_KEY)
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
	
	@Command(name = "TestSignatureSpeed", usage = MESSAGE_SIGN_PROFILING)
    public void testSignatureSpeed(String[] args)
    {
		final String alias = StringArrayUtil.getRequiredValue(args, 0);
		final int numSigs = Integer.parseInt(StringArrayUtil.getRequiredValue(args, 1));
		
		try
		{

			final KeyStore ks = mgr.getKS();

			final PrivateKey privKey = (PrivateKey)ks.getKey(alias, "".toCharArray());
			if (privKey == null)
			{
				System.out.println("Key name " + alias + " does not contain a private key");
				return;
			}

			
			// create some random bytes
			byte[] b = new byte[2048];
			new Random().nextBytes(b);
			
			long startTime = System.currentTimeMillis();
			// now perform the operations
			for (int idx = 0; idx < numSigs; ++idx)
			{
				// generate a SHA256 hash
				final MessageDigest dig = MessageDigest.getInstance("SHA256", "BC");  
				dig.update(b);
				byte[] digest = dig.digest();
				
				// now create the signature
				Signature rsaSig = Signature.getInstance("SHA256withRSA", ks.getProvider());
				rsaSig.initSign(privKey);
				rsaSig.update(digest);
				rsaSig.sign();
				
				if (idx % 25 == 0)
				{
					System.out.println("Performed " + idx + " signatures");
				}
			}
			
			long totalTime = System.currentTimeMillis() - startTime;
			
			System.out.println("Completed " + numSigs + " signatures in " + totalTime + "ms.");
			
			// get seconds
			int secs = (int)totalTime / 1000;
			int averageSpeed =  numSigs / secs;
			
			System.out.println("Average speed " + averageSpeed + " signatures per second.");
			
		}
		catch (Exception e)
		{
			System.err.println("Failed to test key signatures: " + e.getMessage());
		}
		
    }
	
	
	@Command(name = "TestKeyUnwrap", usage = UNWRAP_KEY)
    public void testKeyUnwrap(String[] args)
    {
		final String wrapperAlias = StringArrayUtil.getRequiredValue(args, 0);
		final String file = StringArrayUtil.getRequiredValue(args, 1);
		
		try
		{
			final byte[] wrappedData = FileUtils.readFileToByteArray(new File(file));
			
			final KeyStore ks = mgr.getKS();
			
			// get the wrapper key
			final Key wrapperKey = mgr.getKey(wrapperAlias);
			if (wrapperKey == null)
			{
				System.out.println("Wrapper key with name " + wrapperKey + " does not exist.");
				return;
			}
			
			if (wrapperKey.getAlgorithm().startsWith("AES"))
			{
				final IvParameterSpec iv = new IvParameterSpec(AbstractPKCS11TokenKeyStoreProtectionManager.IV_BYTES);
				
				final Cipher unwrapCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", ks.getProvider().getName());
				unwrapCipher.init(Cipher.UNWRAP_MODE, wrapperKey, iv);
				
				final Key unwrappedKey = unwrapCipher.unwrap(wrappedData, "RSA", Cipher.PRIVATE_KEY);	
				
				System.out.println("Succesfully unwrapped private key.  Private key class: " + unwrappedKey.getClass().getName());
			}
			else
			{
				System.out.println("Wrapper key must be an AES key.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to unwrap private key: " + e.getMessage());
		}
    }
	
	@Command(name = "ExportPrivateKey", usage = EXPORT_PRIVATE_KEY)
    public void exportPrivateKey(String[] args)
	{
		final String alias = StringArrayUtil.getRequiredValue(args, 0);
		final String wrapperAlias = StringArrayUtil.getRequiredValue(args, 1);
		final String file = StringArrayUtil.getOptionalValue(args, 2, alias + "-privKey.der");
		
		try
		{
			final KeyStore ks = mgr.getKS();
			
			// get the wrapper key
			final Key wrapperKey = mgr.getKey(wrapperAlias);
			if (wrapperKey == null)
			{
				System.out.println("Wrapper key with name " + wrapperKey + " does not exist.");
				return;
			}
			
			if (!ks.containsAlias(alias))
			{
				System.out.println("Private key with name " + alias + " does not exist.");
				return;
			}
			
			final PrivateKey privKey = (PrivateKey)ks.getKey(alias, "".toCharArray());
			if (privKey == null)
			{
				System.out.println("Key name " + alias + " does not contain a private key");
				return;
			}
			
			// the algorithm used to wrap the key depends on the key type
			Cipher myWrapper = null;
			if (wrapperKey.getAlgorithm().startsWith("AES"))
			{
				myWrapper = Cipher.getInstance("AES/CBC/PKCS5Padding", ks.getProvider().getName());
				AlgorithmParameters mAlgParams = null;
				try
				{
					mAlgParams = AlgorithmParameters.getInstance("IV", ks.getProvider().getName());
					mAlgParams.init(new IvParameterSpec(AbstractPKCS11TokenKeyStoreProtectionManager.IV_BYTES));
				}
				catch (Exception e)
				{
					
				}
				if (mAlgParams == null)
					myWrapper.init(Cipher.WRAP_MODE, wrapperKey, new IvParameterSpec(AbstractPKCS11TokenKeyStoreProtectionManager.IV_BYTES));	
				else
					myWrapper.init(Cipher.WRAP_MODE, wrapperKey, mAlgParams);				
			}
			else if (wrapperKey.getAlgorithm().startsWith("RSA"))
			{
				myWrapper = Cipher.getInstance("RSA/ECB/NoPadding", ks.getProvider().getName());
	            myWrapper.init(Cipher.WRAP_MODE, wrapperKey);
			}
            
            byte[] wrappedKey = null;
            
            try
            {
            	wrappedKey = myWrapper.wrap(privKey);
            }
            catch (Exception e)
            {
            	System.out.println("Private key with name " + alias + " could not be extracted.  Your hardware may not allow exporting of private keys or "
            			+ "attributes on the key may not allow the key to be exported.  \r\nError message: " + e.getMessage());
            	
            	e.printStackTrace();
            	
            	return;
            }
            final File fl = new File(file);
            FileUtils.writeByteArrayToFile(fl, wrappedKey);
			
			System.out.println("Wrapped private key written to file " + fl.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to export private key: " + e.getMessage());
		}
	}
	
	@Command(name = "ExportPublicKey", usage = EXPORT_PUBLIC_KEY)
    public void exportPublicKey(String[] args)
	{
		final String alias = StringArrayUtil.getRequiredValue(args, 0);
		final String file = StringArrayUtil.getOptionalValue(args, 1, alias + "-publicKey.der");
		
		try
		{
			final KeyStore ks = mgr.getKS();
		
			if (!ks.containsAlias(alias))
			{
				System.out.println("Entry with key name " + alias + " does not exist.");
				return;
			}
			
			final X509Certificate cert = (X509Certificate)ks.getCertificate(alias);
			if (cert == null)
			{
				System.out.println("Key name " + alias + " does not contain a public key");
				return;
			}
			
			final File fl = new File(file);
			FileUtils.writeByteArrayToFile(fl, cert.getPublicKey().getEncoded());
			
			System.out.println("Public key written to file " + fl.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to export public key: " + e.getMessage());
		}
	}
	
	
	@Command(name = "CreateCSR", usage = CREATE_CSR)
    public void createCSR(String[] args)
	{
		
		final String alias = StringArrayUtil.getRequiredValue(args, 0);
		final String commonName = StringArrayUtil.getRequiredValue(args, 1);
		final String subjectAltName = StringArrayUtil.getRequiredValue(args, 2);
		final String keyUsage = StringArrayUtil.getRequiredValue(args, 3);

		// make sure we have a valid keyUsage
		if (!(keyUsage.compareToIgnoreCase("DigitalSignature") == 0 || keyUsage.compareToIgnoreCase("KeyEncipherment") == 0  ||
				keyUsage.compareToIgnoreCase("DualUse") == 0))
		{
			System.out.println("Invalid key usage.");
			return;
		}
		
		final Vector<String> additionalRDNFields = new Vector<String>();
		int cnt = 4;
		String rdnField;
		do
		{
			rdnField = StringArrayUtil.getOptionalValue(args, cnt++, "");
			if (!StringUtils.isEmpty(rdnField))
				additionalRDNFields.add(rdnField);
		} while(!StringUtils.isEmpty(rdnField));
		
		try
		{
			final KeyStore ks = mgr.getKS();
			
			if (!ks.containsAlias(alias))
			{
				System.out.println("Entry with key name " + alias + " does not exist.");
				return;
			}
			
			final X509Certificate storedCert = (X509Certificate)ks.getCertificate(alias);
			if (storedCert == null)
			{
				System.out.println("Key name " + alias + " does not contain a certificate that can be exported.  This key may not be an RSA key pair.");
				return;
			}
			
			final PrivateKey privKey = (PrivateKey)ks.getKey(alias, "".toCharArray());
			if (privKey == null)
			{
				System.out.println("Failed to object private key.  This key may not be an RSA key pair.");
				return;
			}
			
			// create the CSR
			
			//  create the extensions that we want
			final X509ExtensionsGenerator extsGen = new X509ExtensionsGenerator();
			
			// Key Usage
			int usage;
			if (keyUsage.compareToIgnoreCase("KeyEncipherment") == 0)
				usage = KeyUsage.keyEncipherment;
			else if (keyUsage.compareToIgnoreCase("DigitalSignature") == 0)
				usage = KeyUsage.digitalSignature;
			else
				usage = KeyUsage.keyEncipherment | KeyUsage.digitalSignature;
			
			extsGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(usage));
			
			// Subject Alt Name
	    	int nameType = subjectAltName.contains("@") ? GeneralName.rfc822Name : GeneralName.dNSName;
	    	final GeneralNames altName = new GeneralNames(new GeneralName(nameType, subjectAltName));
	    	extsGen.addExtension(X509Extensions.SubjectAlternativeName, false, altName);
			
			// Extended Key Usage
			final Vector<KeyPurposeId> purposes = new Vector<KeyPurposeId>();
			purposes.add(KeyPurposeId.id_kp_emailProtection);
			extsGen.addExtension(X509Extensions.ExtendedKeyUsage, false, new ExtendedKeyUsage(purposes));
			
			// Basic constraint
			final BasicConstraints bc = new BasicConstraints(false);
			extsGen.addExtension(X509Extensions.BasicConstraints, true, bc);
			
			// create the extension requests
			final X509Extensions exts = extsGen.generate();
			
	        final ASN1EncodableVector attributes = new ASN1EncodableVector();
	        final Attribute attribute = new Attribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
	                new DERSet(exts.toASN1Object()));
	        
	        attributes.add(attribute);
	        
	        final DERSet requestedAttributes = new DERSet(attributes);
	        
			// create the DN
			final StringBuilder dnBuilder = new StringBuilder("CN=").append(commonName);
			
			for (String field : additionalRDNFields)
				dnBuilder.append(",").append(field);
			
			final X500Principal subjectPrin = new X500Principal(dnBuilder.toString());
			
			final X509Principal xName = new X509Principal(true, subjectPrin.getName());
			
			// create the CSR
			final PKCS10CertificationRequest request = new PKCS10CertificationRequest("SHA256WITHRSA", xName, storedCert.getPublicKey(), 
			        requestedAttributes, privKey, ks.getProvider().getName());
			
			final byte[] encodedCSR = request.getEncoded();
			
			final String csrString = "-----BEGIN CERTIFICATE REQUEST-----\r\n"  + Base64.encodeBase64String(encodedCSR) 
			+ "-----END CERTIFICATE REQUEST-----";
			
			final File csrFile = new File(alias + "-CSR.pem");
			FileUtils.writeStringToFile(csrFile, csrString);
			
			System.out.println("CSR written to " + csrFile.getAbsolutePath());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to create CSR : " + e.getMessage());
		}
	}
	
	@Command(name = "CreateKeyPair", usage = CREATE_KEY_PAIR)
    public void createKeyPair(String[] args)
	{
		final String alias = StringArrayUtil.getRequiredValue(args, 0);
		final String keySize = StringArrayUtil.getOptionalValue(args, 1, "2048");
				
		try
		{
			
			
			// create a local keygen for a private key to sign the certificate
			final KeyPairGenerator localKeyGen = KeyPairGenerator.getInstance("RSA", "BC");
			
			final KeyPair localKeyPair = localKeyGen.generateKeyPair();
			
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA",mgr.getKS().getProvider().getName());
			keyGen.initialize(Integer.parseInt(keySize));
	        
	        final KeyPair keyPair = keyGen.generateKeyPair();
	        // create a self signed certificate
	        X509V3CertificateGenerator  v1CertGen = new X509V3CertificateGenerator();
	        v1CertGen.setPublicKey(keyPair.getPublic());
	        v1CertGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			end.add(Calendar.DAY_OF_MONTH, 3000); 
			
	        v1CertGen.setSerialNumber(BigInteger.valueOf(generatePositiveRandom()));
	        v1CertGen.setIssuerDN(new X509Principal("cn=test"));
	        v1CertGen.setNotBefore(start.getTime());
	        v1CertGen.setNotAfter(end.getTime());
	        v1CertGen.setSubjectDN(new X509Principal("cn=test")); // issuer and subject are the same for a CA
	        v1CertGen.setPublicKey(keyPair.getPublic()); 
	        X509Certificate newCACert = v1CertGen.generate(localKeyPair.getPrivate(), "BC");
	        
	        mgr.getKS().setKeyEntry(alias, keyPair.getPrivate(), "".toCharArray(), new X509Certificate[] {newCACert});

	        System.out.println("Key pair created and stored.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to generate key pair: " + e.getMessage());
		}
	}
	
	private static long generatePositiveRandom()
	{
		Random ranGen;
		long retVal = -1;
		byte[] seed = new byte[8];
		VMPCRandomGenerator seedGen = new VMPCRandomGenerator();
		seedGen.addSeedMaterial(new SecureRandom().nextLong());
		seedGen.nextBytes(seed);
		ranGen = new SecureRandom(seed);
		while (retVal < 1)
		{
			retVal = ranGen.nextLong(); 						
		}
		
		return retVal;
	}
	
	//@Command(name = "InfiniteRead", usage = INFINITE_READ)
	public void infiniteRead(String[] args)
	{
		final InfiniteRead read = new InfiniteRead();
		final Thread thr = new Thread(read);
	
		thr.start();	
	
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		try
		{
			reader.readLine();
			
			System.out.println("Stop read triggered.  Waiting for last read.");

			read.stopRunning();
		}
		catch (Exception e)
		{
			
		}
	}
	
	@Command(name = "ExportKeyPairCert", usage = EXPORT_PUB_KEY_CERTIFICATE)
    public void exportPublicKeyCert(String[] args)
	{
		final String alias = StringArrayUtil.getRequiredValue(args, 0);
		final String file = StringArrayUtil.getOptionalValue(args, 1, alias + ".der");
		
		try
		{
			final KeyStore ks = mgr.getKS();
		
			if (!ks.containsAlias(alias))
			{
				System.out.println("Entry with key name " + alias + " does not exist.");
				return;
			}
			
			final X509Certificate storedCert = (X509Certificate)ks.getCertificate(alias);
			if (storedCert == null)
			{
				System.out.println("Key name " + alias + " does not contain a certificate that can be exported.  This key may not be an RSA key pair.");
				return;
			}
			
			final File fl = new File(file);
			FileUtils.writeByteArrayToFile(fl, storedCert.getEncoded());
			
			System.out.println("Certificate written to file " + fl.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Failed to export certificate: " + e.getMessage());
		}		
		
	}
	
	
	@Command(name = "UpdateKeyPairCert", usage = UPDATE_PUB_KEY_CERT)
	public void updateKeyPairCert(String[] args)
	{
		final String certFileName = StringArrayUtil.getRequiredValue(args, 0);
		final String keyName = StringArrayUtil.getRequiredValue(args, 1);
		
		final File certFile = new File(certFileName);
		if (!certFile.exists())
		{
			System.out.println("Certificate file " + certFile.getAbsolutePath() + " could not be found.");
			return;
		}
		
		try
		{
			final KeyStore ks = mgr.getKS();
		
			if (!ks.containsAlias(keyName))
			{
				System.out.println("Entry with key name " + keyName + " does not exist.");
				return;
			}
			
			final X509Certificate storedCert = (X509Certificate)ks.getCertificate(keyName);
			if (storedCert == null)
			{
				System.out.println("Key name " + keyName + " does not contain a certificate that can be updated.  This key may not be an RSA key pair.");
				return;
			}
			
			// import the certificate
			final X509Certificate importCert = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(FileUtils.openInputStream(certFile));
			
			// make sure the public keys match... the is necessary because the private key associated with the public key must be a valid key pair
			if (!importCert.getPublicKey().equals(storedCert.getPublicKey()))
			{
				System.out.println("Imported public key does not match the stored public key");
				return;				
			}
			
			// update the public key
			
			final PrivateKey privKey = (PrivateKey)ks.getKey(keyName, "".toCharArray());
			ks.setKeyEntry(keyName, privKey, "".toCharArray(), new X509Certificate[] {importCert});
			
			System.out.println("Certificate updated.");
			
		}
		catch (Exception e)
		{
			System.err.println("Failed to update certificate: " + e.getMessage());
		}
	}
	
	//@Command(name = "ImportP12FileForTempKey", usage = IMPORT_P12_FILE_FOR_TEMP_KEY)
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
	
	protected class InfiniteRead implements Runnable
	{
		protected boolean isRunning = true; 
		
		public void run()
		{
			while (isRunning())
			{
				System.out.println("Infinite read... press return to exit.");
				listCerts(null);
				try
				{
					Thread.sleep(2000);
				}
				catch (Exception e)
				{
					
				}
			}
		}
		
		public synchronized boolean isRunning()
		{
			return isRunning;
		}
		
		public synchronized void stopRunning()
		{
			isRunning = false;
		}
	}
}
