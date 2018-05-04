package org.nhindirect.stagent.cryptography.activekeyops;

import java.security.KeyStore;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.mail.smime.CMSProcessableBodyPart;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.cryptography.DigestAlgorithm;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.cryptography.activekeyops.DirectSignedDataGenerator;
import org.nhindirect.stagent.cryptography.activekeyops.SplitProviderDirectSignedDataGenerator;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class SplitProviderDirectSignedDataGenerator_generateTest extends TestCase
{	
	static
	{
		CryptoExtensions.registerJCEProviders();
	}
	
	protected X509Certificate signerCert;
	protected String pkcs11ProvName;
	
	protected void setupSigningInfo(DirectSignedDataGenerator gen) throws Exception
	{
	   	final ASN1EncodableVector signedAttrs = new ASN1EncodableVector();
    	final SMIMECapabilityVector caps = new SMIMECapabilityVector();

    	caps.addCapability(SMIMECapability.dES_EDE3_CBC);
    	caps.addCapability(SMIMECapability.rC2_CBC, 128);
    	caps.addCapability(SMIMECapability.dES_CBC);
    	caps.addCapability(new DERObjectIdentifier("1.2.840.113549.1.7.1"));	    	
    	caps.addCapability(SMIMECryptographerImpl.x509CertificateObjectsIdent);
    	signedAttrs.add(new SMIMECapabilitiesAttribute(caps));  
		

		
		// setup the certificates
    	if (signerCert == null)
    		signerCert = TestUtils.getInternalCert("user1");
    	
		final List<X509Certificate>  certList = new ArrayList<X509Certificate>();

	
		
		// add certificate
		gen.addSigner(((X509CertificateEx)signerCert).getPrivateKey(), signerCert,
				DigestAlgorithm.SHA256.getOID(), SMIMECryptographerImpl.createAttributeTable(signedAttrs), null);
		certList.add(signerCert);
		
		final CertStore certsAndcrls = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), 
    			CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("CertStore", "Collection")); 
		
    	gen.addCertificatesAndCRLs(certsAndcrls);
	}
	
	@SuppressWarnings("unchecked")
	protected void validateSignature(CMSSignedData data) throws Exception
	{
    	assertNotNull(data);
		assertEquals(1, data.getSignerInfos().getSigners().size());
		for (SignerInformation sigInfo : (Collection<SignerInformation>)data.getSignerInfos().getSigners())	    		
    		assertTrue(sigInfo.verify(signerCert, CryptoExtensions.getJCEProviderName()));
	}
	
	public void testGenerate_sameDefaultSigAndDigestProvider_assertGenerated() throws Exception
	{
		final SplitProviderDirectSignedDataGenerator gen = new SplitProviderDirectSignedDataGenerator("", "");
		setupSigningInfo(gen);
		
		// create the content 
		final MimeBodyPart signedContent = new MimeBodyPart();
		signedContent.addHeader("To:", "me@you.com");
		signedContent.addHeader("From", "test.test.com");
		signedContent.setText("Some Text To Sign");
		
		final CMSProcessableBodyPart content = new CMSProcessableBodyPart(signedContent);
    	final CMSSignedData signedData = gen.generate(content);

    	validateSignature(signedData);
    	
	}
	
	public void testGenerate_differentDefaultSigAndDigestProvider_assertGenerated() throws Exception
	{		
		final SplitProviderDirectSignedDataGenerator gen = new SplitProviderDirectSignedDataGenerator("SunRsaSign", "BC");
		setupSigningInfo(gen);
		
		// create the content 
		final MimeBodyPart signedContent = new MimeBodyPart();
		signedContent.addHeader("To:", "me@you.com");
		signedContent.addHeader("From", "test.test.com");
		signedContent.setText("Some Text To Sign");
		
		final CMSProcessableBodyPart content = new CMSProcessableBodyPart(signedContent);
    	final CMSSignedData signedData = gen.generate(content);

    	validateSignature(signedData);
    	
	}
	
	
	public void testGenerate_safeNetHSMSignatureProvider_assertGenerated() throws Exception
	{
        /**
         * This test is only run if a specific SafeNet eToken Pro HSM is connected to the testing 
         * system.  This can be modified for another specific machine and/or token.
         */
		pkcs11ProvName = TestUtils.setupSafeNetToken();
		if (!StringUtils.isEmpty(pkcs11ProvName))
		{
			// get a certificate from the key store
			final KeyStore ks = KeyStore.getInstance("PKCS11");
			
			ks.load(null, "1Kingpuff".toCharArray());
			
			final Enumeration<String> aliases = ks.aliases();
			
			while (aliases.hasMoreElements())
			{
				final String alias = aliases.nextElement();
				
				
				final KeyStore.Entry entry = ks.getEntry(alias, null);
				if (entry instanceof KeyStore.PrivateKeyEntry)
				{
					KeyStore.PrivateKeyEntry ent = (KeyStore.PrivateKeyEntry)entry;
					signerCert = X509CertificateEx.fromX509Certificate((X509Certificate)ent.getCertificate(), ent.getPrivateKey());
					break;
				}
			}
			
			final SplitProviderDirectSignedDataGenerator gen = new SplitProviderDirectSignedDataGenerator(pkcs11ProvName, "BC");
			setupSigningInfo(gen);
			
			
			// create the content 
			final MimeBodyPart signedContent = new MimeBodyPart();
			signedContent.addHeader("To:", "me@you.com");
			signedContent.addHeader("From", "test.test.com");
			signedContent.setText("Some Text To Sign");
			
			final CMSProcessableBodyPart content = new CMSProcessableBodyPart(signedContent);
	    	final CMSSignedData signedData = gen.generate(content);
	
	    	validateSignature(signedData);
		}
	}

}
