package org.nhindirect.stagent;

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
import org.nhindirect.stagent.DefaultMessageSignatureImpl;
import org.nhindirect.stagent.cert.X509CertificateEx;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.TestUtils;

/**
 * Generated test case.
 * 
 * @author junit_generate
 */
public class DefaultMessageSignatureImpl_CheckThumbprint_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultMessageSignatureImpl impl = createMessageSignature();
			boolean checkThumbprint = impl
					.checkThumbprint(createMessageSender());
			doAssertions(checkThumbprint);
		}

		protected DefaultMessageSignatureImpl createMessageSignature() throws Exception {
			return new DefaultMessageSignatureImpl(createSignerInformation(),
					(boolean) false, theGetCertificates.iterator().next()) {
			};
		}

		protected NHINDAddress theCreateMessageSender;

		protected NHINDAddress createMessageSender() throws Exception {
			theCreateMessageSender = new NHINDAddress("") {

				@Override
				public boolean hasCertificates() {
					hasCertificatesCalls++;
					return hasCertificates_Internal();
				}

				@Override 
				public Collection<X509Certificate> getCertificates(){
					  getCertificatesCalls++;
					  return getCertificates_Internal();
					}
				
			};
			return theCreateMessageSender;
		}
		
		protected Collection<X509Certificate> theGetCertificates;
		protected int getCertificatesCalls=0;
		protected Collection<X509Certificate> getCertificates_Internal(){
		  return theGetCertificates;
		}

		protected boolean theHasCertificates;
		protected int hasCertificatesCalls = 0;

		protected boolean hasCertificates_Internal() {
			theHasCertificates = false;
			return theHasCertificates;
		}

		protected SignerInformation createSignerInformation() throws Exception {
			X509CertificateEx internalCert = TestUtils.getInternalCert("user1");
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
			
			theGetCertificates = certList;

			MimeMultipart retVal = null;

			CertStore certsAndcrls = CertStore.getInstance("Collection",
					new CollectionCertStoreParameters(certList), "BC");
			gen.addCertificatesAndCRLs(certsAndcrls);

			retVal = gen.generate(partToSign, "BC");

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

		protected void doAssertions(boolean checkThumbprint) throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageSenderDoesNotHaveCertificates_ReturnsFalse()
			throws Exception {
		new TestPlan() {

			protected boolean hasCertificates_Internal() {
				theHasCertificates = false;
				return theHasCertificates;
			}

			protected void doAssertions(boolean checkThumbprint)
					throws Exception {
				assertFalse(checkThumbprint);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testThumbprintMatches_ReturnsTrue()
			throws Exception {
		new TestPlan() {

			protected boolean hasCertificates_Internal() {
				theHasCertificates = true;
				return theHasCertificates;
			}

			protected void doAssertions(boolean checkThumbprint)
					throws Exception {
				assertTrue(checkThumbprint);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testThumbprintDoesNotMatch_ReturnsFalse()
			throws Exception {
		new TestPlan() {

			protected boolean hasCertificates_Internal() {
				theHasCertificates = true;
				return theHasCertificates;
			}
			
			protected DefaultMessageSignatureImpl createMessageSignature() throws Exception {
				return new DefaultMessageSignatureImpl(createSignerInformation(),
						(boolean) false, TestUtils.getInternalCert("bob")) {
				};
			}

			protected void doAssertions(boolean checkThumbprint)
					throws Exception {
				
				assertFalse(checkThumbprint);
			}
		}.perform();
	}
}