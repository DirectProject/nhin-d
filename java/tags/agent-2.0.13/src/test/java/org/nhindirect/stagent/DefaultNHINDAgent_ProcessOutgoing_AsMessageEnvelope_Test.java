package org.nhindirect.stagent;

import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.SecondaryMimeMessage;

/**
 * Generated test case.
 * @author junit_generate
 */
public class DefaultNHINDAgent_ProcessOutgoing_AsMessageEnvelope_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultNHINDAgent impl = createDefaultNHINDAgent();
			OutgoingMessage processOutgoing = impl
					.processOutgoing(createEnvelope());
			doAssertions(processOutgoing);
		}

		protected DefaultNHINDAgent createDefaultNHINDAgent() throws Exception {
			return new DefaultNHINDAgent("", new KeyStoreCertificateStore(),
					new KeyStoreCertificateStore(), new DefaultTrustAnchorResolver()) {
				@Override
				protected void checkEnvelopeAddresses(MessageEnvelope envelope) {
					checkEnvelopeAddressesCalls++;
					checkEnvelopeAddresses_Internal(envelope);
				}
				
				@Override 
				public OutgoingMessage processOutgoing(OutgoingMessage message){
					  processOutgoingCalls++;
					  return processOutgoing_Internal(message);
					}
			};
		}

		protected int checkEnvelopeAddressesCalls = 0;

		protected void checkEnvelopeAddresses_Internal(MessageEnvelope envelope) {
		}

		protected MessageEnvelope theCreateEnvelope;

		protected MessageEnvelope createEnvelope() throws Exception {
			MimeMessage mimeMsg = new SecondaryMimeMessage();
			mimeMsg.setText("");
			Message msg = new Message(mimeMsg);
			NHINDAddressCollection recipients = new NHINDAddressCollection();
			recipients.add(new NHINDAddress(""));
			NHINDAddress sender = new NHINDAddress("");
			theCreateEnvelope = new DefaultMessageEnvelope(msg, recipients, sender);
			return theCreateEnvelope;
		}
		
		protected OutgoingMessage theProcessOutgoing;
		protected int processOutgoingCalls=0;
		protected OutgoingMessage processOutgoing_Internal(OutgoingMessage message){
		  theProcessOutgoing=message;
		  return theProcessOutgoing;
		}

		protected void doAssertions(OutgoingMessage processOutgoing)
				throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageEnvelopeIsNull_ThrowsException() throws Exception {
		new TestPlan() {
			protected MessageEnvelope createEnvelope() throws Exception {
				theCreateEnvelope = null;
				return theCreateEnvelope;
			}
			
			protected void doAssertions(OutgoingMessage processOutgoing)
				throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertNull(theCreateEnvelope);
				assertTrue(exception instanceof IllegalArgumentException);
			}
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageEnvelopeIsPassedToCheckEnvelopeAddressesMethod() throws Exception {
		new TestPlan() {
			protected void checkEnvelopeAddresses_Internal(MessageEnvelope envelope) {
				assertEquals(theCreateEnvelope, envelope);
			}
			
			protected void doAssertions(OutgoingMessage processOutgoing)
				throws Exception {
				assertEquals(1, checkEnvelopeAddressesCalls);
			}
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageEnvelopeIsNotNull_ProcessOutgoingMethodIsCalled() throws Exception {
		new TestPlan() {
			
			protected void doAssertions(OutgoingMessage processOutgoing)
				throws Exception {
				assertEquals(1, processOutgoingCalls);
				assertNotNull(theProcessOutgoing);
				assertEquals(theProcessOutgoing, processOutgoing);
			}
			
		}.perform();
	}
}