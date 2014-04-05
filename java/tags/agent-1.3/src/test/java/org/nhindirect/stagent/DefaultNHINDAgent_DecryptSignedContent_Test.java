package org.nhindirect.stagent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import junit.framework.TestCase;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.mail.smime.CMSProcessableBodyPartInbound;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cryptography.Cryptographer;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.cryptography.SignedEntity;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.mail.MimeEntity;
import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeException;
import org.nhindirect.stagent.mail.MimeStandard;
import org.nhindirect.stagent.parser.EntitySerializer;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.SecondaryMimeMessage;
import org.nhindirect.stagent.utils.TestUtils;

/**
 * Generated test case.
 * @author junit_generate
 */
public class DefaultNHINDAgent_DecryptSignedContent_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultNHINDAgent impl = createDefaultNHINDAgent();
			impl.setCryptographer(createCryptographer());
			impl.decryptSignedContent(createMessage());
			doAssertions();
		}

		protected DefaultNHINDAgent createDefaultNHINDAgent() throws Exception {
			
			return new DefaultNHINDAgent("", new KeyStoreCertificateStore(),
					new KeyStoreCertificateStore(), new DefaultTrustAnchorResolver()) {
				@Override
				protected MimeEntity decryptMessage(IncomingMessage message) {
					decryptMessageCalls++;
					return decryptMessage_Internal(message);
				}
			};
		}

		protected MimeEntity theDecryptMessage;
		protected int decryptMessageCalls = 0;

		protected MimeEntity decryptMessage_Internal(IncomingMessage message) {
			try {
				String testMessage = TestUtils.readResource("MultipartMimeMessage.txt");
				
		        MimeMessage entity = EntitySerializer.Default.deserialize(testMessage);
		        Message message1 = new Message(entity) {

					@Override
					public MimeEntity extractMimeEntity() {
						MimeEntity retVal = null;
				    	
				    	try
				    	{
				    		InternetHeaders headers = new InternetHeaders();
				    		
					        if (this.headers.getAllHeaders().hasMoreElements())
					        {
					        	Enumeration hEnum = this.headers.getAllHeaders();
					        	while (hEnum.hasMoreElements())
					        	{
					        		javax.mail.Header hdr = (javax.mail.Header)hEnum.nextElement();
					        		if (MimeStandard.startsWith(hdr.getName(), MimeStandard.HeaderPrefix))
					        			headers.addHeader(hdr.getName(), hdr.getValue());
					        	}
					
					            if (!headers.getAllHeaders().hasMoreElements())
					            {                        	
					                throw new MimeException(MimeError.InvalidMimeEntity);
					            }
					            
					            retVal = new MimeEntity(headers, getContentAsBytes()) {
					            	@Override 
									public String getContentType() throws MessagingException {
										  getContentTypeCalls++;
										  return getContentType_Internal();
										}
					            };
					            
					        }
				    	}
				    	catch (MessagingException e)
				    	{
				    		throw new MimeException(MimeError.InvalidMimeEntity, e);
				    	}	
				        return retVal;
					}
		        };
		        
		        MimeEntity entityToSig = message1.extractEntityForSignature(true);
		        theDecryptMessage = entityToSig;
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
			return theDecryptMessage;
		}
		
		protected String theGetContentType;
		protected int getContentTypeCalls=0;
		protected String getContentType_Internal() throws MessagingException {
			theGetContentType = "application/pkcs7-mime; smime-type=signed-data";
		  return theGetContentType;
		}

		protected IncomingMessage theCreateMessage;

		protected IncomingMessage createMessage() throws Exception {
			MimeMessage mimeMsg = new SecondaryMimeMessage();
			mimeMsg.setText("");
			Message msg = new Message(mimeMsg);
			msg.addHeader("Subject", "subject");
			msg.addHeader("Bcc", "bcc");
			msg.addHeader("Content-ID", "content-id");
			NHINDAddressCollection recipients = new NHINDAddressCollection();
			recipients.add(new NHINDAddress(""));
			NHINDAddress sender = new NHINDAddress("");
			theCreateMessage = new IncomingMessage(msg, recipients, sender);
			return theCreateMessage;
		}

		protected Cryptographer theCreateCryptographer;
		protected int deserializeEnvelopedSignatureCalls = 0;
		protected CMSSignedData theDeserializeEnvelopedSignature;
		protected int deserializeSignatureEnvelopeCalls = 0;
		protected CMSSignedData theDeserializeSignatureEnvelope;

		protected CMSSignedData deserializeEnvelopedSignature_Internal(
				MimeEntity envelopeEntity) throws Exception {
			CMSSignedData signedData = createCMSSignedData();
			theDeserializeEnvelopedSignature = signedData;
			return theDeserializeEnvelopedSignature;
		}

		protected CMSSignedData deserializeSignatureEnvelope_Internal(
				SignedEntity entity) throws Exception {
			
			CMSSignedData signedData = createCMSSignedData();
			theDeserializeSignatureEnvelope = signedData;
			return theDeserializeSignatureEnvelope;
		}
		
		protected CMSSignedData createCMSSignedData() throws Exception {
			String testMessage = TestUtils
					.readResource("MultipartMimeMessage.txt");
			MimeMessage entity1 = EntitySerializer.Default
					.deserialize(testMessage);
			Message message = new Message(entity1);
			MimeEntity entityToSig = message.extractEntityForSignature(true);
			byte[] messageBytes = EntitySerializer.Default
					.serializeToBytes(entityToSig); // Serialize message out as
													// ASCII encoded...
			MimeBodyPart partToSign = new MimeBodyPart(new ByteArrayInputStream(
						messageBytes));
			SMIMESignedGenerator gen = new SMIMESignedGenerator();
			MimeMultipart retVal = gen.generate(partToSign, CryptoExtensions.getJCEProviderName());
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
			return signeddata;
		}

		protected Cryptographer createCryptographer() {
			theCreateCryptographer = new SMIMECryptographerImpl() {
				@Override
				public CMSSignedData deserializeEnvelopedSignature(
						MimeEntity envelopeEntity) {
					deserializeEnvelopedSignatureCalls++;
					try {
						return deserializeEnvelopedSignature_Internal(envelopeEntity);
					} catch (Exception e) {
						e.printStackTrace();
						fail();
						return null;
					}
				}

				@Override
				public CMSSignedData deserializeSignatureEnvelope(
						SignedEntity entity) {
					deserializeSignatureEnvelopeCalls++;
					try {
						return deserializeSignatureEnvelope_Internal(entity);
					} catch (Exception e) {
						e.printStackTrace();
						fail();
						return null;
					}
				}
			};
			return theCreateCryptographer;
		}

		protected void doAssertions() throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToDecryptMessage() throws Exception {
		new TestPlan() {
			protected MimeEntity decryptMessage_Internal(IncomingMessage message) {
				assertEquals(theCreateMessage, message);
				return super.decryptMessage_Internal(message);
			}
			
			protected void doAssertions() throws Exception {
				assertEquals(1, decryptMessageCalls);
			}
		}.perform();
	}
	
	class ContentTypeIsEnvelopedSignature extends TestPlan {
		@Override
		protected String getContentType_Internal() throws MessagingException {
				theGetContentType = "application/pkcs7-mime; smime-type=signed-data";
			  return theGetContentType;
		}
	}
	
	class ContentTypeIsMultipartSignature extends TestPlan {
		@Override
		protected String getContentType_Internal() throws MessagingException {
			theGetContentType = "multipart/signed;";
			return theGetContentType;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testAllNonContentHeadersAreAddedBackToDecryptedMessage() throws Exception {
		new ContentTypeIsMultipartSignature() {
			protected MimeEntity decryptMessage_Internal(IncomingMessage message) {
				assertEquals(theCreateMessage, message);
				return super.decryptMessage_Internal(message);
			}
			
			protected void doAssertions() throws Exception {
				Enumeration allHeaders = theCreateMessage.getMessage().getAllHeaders();
				int i = 0;
				for (; allHeaders.hasMoreElements();) {
					allHeaders.nextElement();
					i++;
				}
				assertEquals(3, i);
				assertEquals(1, decryptMessageCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testReturnValueFromDeserializeEnvelopedSignatureMethodIsSetAsMessageSignature() throws Exception {
		new ContentTypeIsEnvelopedSignature() {
			
			protected void doAssertions() throws Exception {
				assertEquals(theDeserializeEnvelopedSignature, theCreateMessage.getSignature());
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testReturnValueFromDeserializeSignatureEnvelopeMethodIsSetAsMessageSignature() throws Exception {
		new ContentTypeIsMultipartSignature() {
			
			protected void doAssertions() throws Exception {
				assertEquals(theDeserializeSignatureEnvelope, theCreateMessage.getSignature());
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testContentTypeIsNeitherMultipartSignedNorCmsEnvelopeMediaType_ThrowsAgentException() throws Exception {
		new TestPlan() {
			
			protected String getContentType_Internal() throws MessagingException {
			  theGetContentType = "application/pkcs7-mime;";
			  return theGetContentType;
			}
			
			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertTrue(getContentTypeCalls>0);
				assertTrue(exception instanceof AgentException);
				AgentException agentException = (AgentException) exception;
				assertEquals(AgentError.UnsignedMessage, agentException.getError());
			}

			protected void doAssertions() throws Exception {
				fail();
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testContentTypeIsInvalid_ThrowsMimeException() throws Exception {
		new TestPlan() {
			
			protected String getContentType_Internal() throws MessagingException {
			  theGetContentType = "theGetContentType";
			  return theGetContentType;
			}
			
			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertTrue(getContentTypeCalls>0);
				assertTrue(exception instanceof MimeException);
				MimeException mimeException = (MimeException) exception;
				assertEquals(MimeError.InvalidBody, mimeException.getError());
			}

			protected void doAssertions() throws Exception {
				fail();
			}
		}.perform();
	}
}