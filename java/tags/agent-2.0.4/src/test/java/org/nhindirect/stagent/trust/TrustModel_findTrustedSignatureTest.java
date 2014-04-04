package org.nhindirect.stagent.trust;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.mail.internet.InternetAddress;

import org.bouncycastle.cms.CMSSignedData;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.DefaultMessageSignatureImpl;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.cryptography.SignedEntity;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.utils.TestUtils;

import junit.framework.TestCase;

public class TrustModel_findTrustedSignatureTest extends TestCase
{
	private X509Certificate sigUser1;
	private X509Certificate sigUser1CA;
	private X509Certificate otherCert;
	
	private IncomingMessage inMessage;
	
	private SignedEntity signedEntity;
	private SMIMECryptographerImpl cryptographer;
	
	@Override
	public void setUp() throws Exception
	{
    	CryptoExtensions.registerJCEProviders();
		
		// load sigCert A
    	sigUser1 = TestUtils.getInternalCert("user1");
		
		// load sigCert A private certificate
    	sigUser1CA = TestUtils.getInternalCACert("cacert");		

		// load other anchor
		otherCert = TestUtils.loadCertificate("gm2552.der");

		
		// load the message that will be encrypted
		String testMessage = TestUtils.readResource("MultipartMimeMessage.txt");
		cryptographer = new SMIMECryptographerImpl();
		
        inMessage = new IncomingMessage(new Message(new ByteArrayInputStream(testMessage.getBytes())));
		
		signedEntity = cryptographer.sign(inMessage.getMessage(), sigUser1);
		               
		CMSSignedData signatures = cryptographer.deserializeSignatureEnvelope(signedEntity);
		inMessage.setSignature(signatures);

        

	}
	
	public void testFindTrustedSignatureTest_singleRecipSignature_nullRecip_assertMessageSignatureNotNull() throws Exception
	{
		final TrustModel trustModel = new TrustModel();
		trustModel.findSenderSignatures(inMessage);
		
		DefaultMessageSignatureImpl impl = trustModel.findTrustedSignature(inMessage, Arrays.asList(sigUser1CA));
		
		assertNotNull(impl);
	}
	
	public void testFindTrustedSignatureTest_singleRecipSignature_nonNullRecip_assertMessageSignatureNotNull() throws Exception
	{
		final TrustModel trustModel = new TrustModel();
		trustModel.findSenderSignatures(inMessage);
		
		DefaultMessageSignatureImpl impl = trustModel.findTrustedSignature(inMessage, 
				inMessage.getRecipients().get(0), Arrays.asList(sigUser1CA));
		
		assertNotNull(impl);
	}
	
	public void testFindTrustedSignatureTest_singleRecipSignature_senderHasCert_assertMessageSignatureNotNull() throws Exception
	{
		final TrustModel trustModel = new TrustModel();
		trustModel.findSenderSignatures(inMessage);
		inMessage.getSender().setCertificates(Arrays.asList(sigUser1));
		
		DefaultMessageSignatureImpl impl = trustModel.findTrustedSignature(inMessage, 
				inMessage.getRecipients().get(0), Arrays.asList(sigUser1CA));
		
		assertNotNull(impl);
	}
	
	public void testFindTrustedSignatureTest_singleRecipSignature_senderHasNonMatchingCert_assertMessageSignatureNotNull() throws Exception
	{
		final TrustModel trustModel = new TrustModel();
		trustModel.findSenderSignatures(inMessage);
		inMessage.getSender().setCertificates(Arrays.asList(otherCert));
		
		DefaultMessageSignatureImpl impl = trustModel.findTrustedSignature(inMessage, 
				inMessage.getRecipients().get(0), Arrays.asList(sigUser1CA));
		
		assertNotNull(impl);
	}
	
	public void testFindTrustedSignatureTest_singleRecipSignature_notPolicyCompliant_assertMessageSignatureNull() throws Exception
	{
		final TrustModel trustModel = new TrustModel()
		{
			@Override
		    protected boolean isCertPolicyCompliant(InternetAddress recipient, X509Certificate cert)
			{
				return false;
			}
		};
		trustModel.findSenderSignatures(inMessage);
		
		DefaultMessageSignatureImpl impl = trustModel.findTrustedSignature(inMessage, 
				inMessage.getRecipients().get(0), Arrays.asList(sigUser1CA));
		
		assertNull(impl);
	}	
}
