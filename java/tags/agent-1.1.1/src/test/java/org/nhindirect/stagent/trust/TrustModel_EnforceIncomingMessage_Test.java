package org.nhindirect.stagent.trust;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import junit.framework.TestCase;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.mail.smime.CMSProcessableBodyPartInbound;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.nhindirect.stagent.AgentError;
import org.nhindirect.stagent.AgentException;
import org.nhindirect.stagent.CryptoExtensions;
import org.nhindirect.stagent.DefaultMessageSignatureImpl;
import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.NHINDAddress;
import org.nhindirect.stagent.NHINDAddressCollection;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.trust.TrustModel;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.SecondaryMimeMessage;
import org.nhindirect.stagent.utils.TestUtils;

/**
 * Generated test case.
 * @author junit_generate
 */
public class TrustModel_EnforceIncomingMessage_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			TrustModel impl = createTrustModel();
			impl.enforce(createMessage());
			doAssertions();
		}

		protected TrustModel createTrustModel() throws Exception {
			return new TrustModel() {
				@Override
				protected void findSenderSignatures(IncomingMessage message) {
					findSenderSignaturesCalls++;
					findSenderSignatures_Internal(message);
				}
				
				@Override
				protected DefaultMessageSignatureImpl findTrustedSignature(
						IncomingMessage message,
						Collection<X509Certificate> anchors) {
					findTrustedSignatureCalls++;
					return findTrustedSignature_Internal(message, anchors);
				}
			};
		}

		protected int findSenderSignaturesCalls = 0;

		protected void findSenderSignatures_Internal(IncomingMessage message) {
		}
		
		protected DefaultMessageSignatureImpl theFindTrustedSignature;
		protected int findTrustedSignatureCalls = 0;

		protected DefaultMessageSignatureImpl findTrustedSignature_Internal(
				IncomingMessage message, Collection<X509Certificate> anchors) {
			theFindTrustedSignature = null;
			return theFindTrustedSignature;
		}
		
		protected IncomingMessage theCreateMessage;

		protected IncomingMessage createMessage() throws Exception {
			MimeMessage mimeMsg = new SecondaryMimeMessage();
			mimeMsg.setText("");
			Message msg = new Message(mimeMsg);
			NHINDAddressCollection recipients = new NHINDAddressCollection();
			recipients.add(new NHINDAddress(""));
			NHINDAddress sender = new NHINDAddress("");
			theCreateMessage = new IncomingMessage(msg, recipients, sender) {

				@Override 
				public boolean hasSignatures(){
					  hasSignaturesCalls++;
					  return hasSignatures_Internal();
				}
				
				@Override 
				public boolean hasSenderSignatures(){
					  hasSenderSignaturesCalls++;
					  return hasSenderSignatures_Internal();
				}

				@Override 
				public NHINDAddressCollection getDomainRecipients(){
					  getDomainRecipientsCalls++;
					  return getDomainRecipients_Internal();
				}
			};
			return theCreateMessage;
		}
		
		protected boolean theHasSignatures;
		protected int hasSignaturesCalls=0;
		protected boolean hasSignatures_Internal(){
		  theHasSignatures=false;
		  return theHasSignatures;
		}
		
		protected boolean theHasSenderSignatures;
		protected int hasSenderSignaturesCalls=0;
		protected boolean hasSenderSignatures_Internal(){
		  theHasSenderSignatures=false;
		  return theHasSenderSignatures;
		}
		
		protected NHINDAddressCollection theGetDomainRecipients;
		protected int getDomainRecipientsCalls=0;
		protected NHINDAddressCollection getDomainRecipients_Internal(){
		  theGetDomainRecipients=new NHINDAddressCollection();
		  return theGetDomainRecipients;
		}

		protected void doAssertions() throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testIncomingMessageIsNull_ThrowsIllegalArgumentException() throws Exception {
		new TestPlan() {
			protected IncomingMessage createMessage() throws Exception {
				theCreateMessage = null;
				return theCreateMessage;
			}

			protected void doAssertions() throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertNull(theCreateMessage);
				assertTrue(exception instanceof IllegalArgumentException);
			}			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageDoesNotHaveSignatures_ThrowsAgentException() throws Exception {
		new TestPlan() {
			
			protected boolean hasSignatures_Internal(){
				  theHasSignatures=false;
				  return theHasSignatures;
			}

			protected void doAssertions() throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertEquals(1, hasSignaturesCalls);
				assertTrue(exception instanceof AgentException);
				AgentException agenException = (AgentException) exception;
				assertEquals(AgentError.UntrustedMessage, agenException.getError());
			}			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToFindSenderSignatures() throws Exception {
		new TestPlan() {
			
			protected boolean hasSignatures_Internal(){
				  theHasSignatures=true;
				  return theHasSignatures;
			}
			
			protected boolean hasSenderSignatures_Internal(){
				  theHasSenderSignatures=true;
				  return theHasSenderSignatures;
			}
			
			protected void findSenderSignatures_Internal(IncomingMessage message) {
				assertEquals(theCreateMessage, message);
			}

			protected void doAssertions() throws Exception {
				assertEquals(1, findSenderSignaturesCalls);
			}		
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageDoesNotHaveSenderSignatures_ThrowsAgentException() throws Exception {
		new TestPlan() {
			
			protected boolean hasSignatures_Internal(){
				  theHasSignatures=true;
				  return theHasSignatures;
			}
			
			protected boolean hasSenderSignatures_Internal(){
				  theHasSenderSignatures=false;
				  return theHasSenderSignatures;
			}

			protected void doAssertions() throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertEquals(1, hasSignaturesCalls);
				assertTrue(exception instanceof AgentException);
				AgentException agenException = (AgentException) exception;
				assertEquals(AgentError.MissingSenderSignature, agenException.getError());
			}			
			
		}.perform();
	}
	
	class MessageHasSenderSignatures extends TestPlan {
		protected boolean hasSignatures_Internal(){
			  theHasSignatures=true;
			  return theHasSignatures;
		}
		
		protected boolean hasSenderSignatures_Internal(){
			  theHasSenderSignatures=true;
			  return theHasSenderSignatures;
		}
		
		protected NHINDAddress recip;
		
		protected NHINDAddressCollection getDomainRecipients_Internal(){
			  theGetDomainRecipients=new NHINDAddressCollection();
			  recip = new NHINDAddress("") {

				@Override 
				public Collection<X509Certificate> getTrustAnchors(){
					  getTrustAnchorsCalls++;
					  return getTrustAnchors_Internal();
				}
				  
			  };
			  theGetDomainRecipients.add(recip);
			  return theGetDomainRecipients;
		}
		
		protected Collection<X509Certificate> theGetTrustAnchors;
		protected int getTrustAnchorsCalls=0;
		protected Collection<X509Certificate> getTrustAnchors_Internal(){
		  theGetTrustAnchors=new ArrayList();
		  return theGetTrustAnchors;
		}
		
		protected X509CertificateEx internalCert;
		
		protected SignerInformation createSignerInformation() throws Exception {
			internalCert = TestUtils.getInternalCert("user1");
			String testMessage = TestUtils
					.readResource("MultipartMimeMessage.txt");

			MimeMessage entity = EntitySerializer.Default
					.deserialize(testMessage);
			Message message = new Message(entity);

			MimeEntity entityToSig = message.extractEntityForSignature(true);

			byte[] messageBytes = EntitySerializer.Default
					.serializeToBytes(entityToSig); // Serialize message out as
													// ASCII encoded...

			MimeBodyPart partToSign = null;

			try {
				partToSign = new MimeBodyPart(new ByteArrayInputStream(
						messageBytes));
			} catch (Exception e) {
			}

			SMIMESignedGenerator gen = new SMIMESignedGenerator();

			ASN1EncodableVector signedAttrs = new ASN1EncodableVector();
			SMIMECapabilityVector caps = new SMIMECapabilityVector();

			caps.addCapability(SMIMECapability.dES_EDE3_CBC);
			caps.addCapability(SMIMECapability.rC2_CBC, 128);
			caps.addCapability(SMIMECapability.dES_CBC);
			caps.addCapability(new DERObjectIdentifier("1.2.840.113549.1.7.1"));
			caps.addCapability(PKCSObjectIdentifiers.x509Certificate);
			signedAttrs.add(new SMIMECapabilitiesAttribute(caps));

			List certList = new ArrayList();

			gen.addSigner(internalCert.getPrivateKey(), internalCert,
					SMIMESignedGenerator.DIGEST_SHA1, new AttributeTable(
							signedAttrs), null);
			certList.add(internalCert);

			MimeMultipart retVal = null;

			CertStore certsAndcrls = CertStore.getInstance("Collection",
					new CollectionCertStoreParameters(certList), CryptoExtensions.getJCEProviderName());
			gen.addCertificatesAndCRLs(certsAndcrls);

			retVal = gen.generate(partToSign, CryptoExtensions.getJCEProviderName());

			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			retVal.writeTo(oStream);
			oStream.flush();
			byte[] serialzedBytes = oStream.toByteArray();

			ByteArrayDataSource dataSource = new ByteArrayDataSource(
					serialzedBytes, retVal.getContentType());

			MimeMultipart verifyMM = new MimeMultipart(dataSource);

			CMSSignedData signeddata = new CMSSignedData(
					new CMSProcessableBodyPartInbound(partToSign), verifyMM
							.getBodyPart(1).getInputStream());
			SignerInformationStore signers = signeddata.getSignerInfos();
			Collection c = signers.getSigners();
			Iterator it = c.iterator();
			while (it.hasNext()) {
				SignerInformation signer = (SignerInformation) it.next();
				return signer;
			}
			return null;
		}
		
		protected DefaultMessageSignatureImpl findTrustedSignature_Internal(
				IncomingMessage message, Collection<X509Certificate> anchors) {
			try {
				theFindTrustedSignature = new DefaultMessageSignatureImpl(createSignerInformation(), false, internalCert) {

					@Override 
					public boolean isThumbprintVerified(){
						  isThumbprintVerifiedCalls++;
						  return isThumbprintVerified_Internal();
					}
				};
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
			return theFindTrustedSignature;
		}
		
		protected boolean theIsThumbprintVerified;
		protected int isThumbprintVerifiedCalls=0;
		protected boolean isThumbprintVerified_Internal(){
		  theIsThumbprintVerified=false;
		  return theIsThumbprintVerified;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRecipientsTrustedSignatureIsNull_SetsTrustEnforcementStatusAsFailed() throws Exception {
		new MessageHasSenderSignatures() {
			
			protected DefaultMessageSignatureImpl findTrustedSignature_Internal(
					IncomingMessage message, Collection<X509Certificate> anchors) {
				theFindTrustedSignature = null;
				return theFindTrustedSignature;
			}

			protected void doAssertions() throws Exception {
				assertEquals(1, findTrustedSignatureCalls);
				assertEquals(TrustEnforcementStatus.Failed, recip.getStatus());
			}		
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testThumbprintIsNotVerified_SetsTrustEnforcementStatusAsSuccess_ThumbprintMismatch() throws Exception {
		new MessageHasSenderSignatures() {
			
			protected boolean isThumbprintVerified_Internal(){
				  theIsThumbprintVerified=false;
				  return theIsThumbprintVerified;
			}
			

			protected void doAssertions() throws Exception {
				assertEquals(1, findTrustedSignatureCalls);
				assertEquals(TrustEnforcementStatus.Success_ThumbprintMismatch, recip.getStatus());
			}		
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testThumbprintIsVerified_SetsTrustEnforcementStatusAsSuccess() throws Exception {
		new MessageHasSenderSignatures() {
			
			protected boolean isThumbprintVerified_Internal(){
				  theIsThumbprintVerified=true;
				  return theIsThumbprintVerified;
			}

			protected void doAssertions() throws Exception {
				assertEquals(1, findTrustedSignatureCalls);
				assertEquals(TrustEnforcementStatus.Success, recip.getStatus());
			}		
			
		}.perform();
	}
}