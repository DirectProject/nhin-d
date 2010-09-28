package org.nhindirect.stagent;

import junit.framework.TestCase;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;
import org.nhindirect.stagent.utils.BaseTestPlan;

/**
 * Generated test case.
 * @author junit_generate
 */
public class DefaultNHINDAgent_ProcessIncoming_RawStringAndAddresses_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultNHINDAgent impl = createDefaultNHINDAgent();
			IncomingMessage processIncoming = impl.processIncoming(
					createMessageText(), createRecipients(), createSender());
			doAssertions(processIncoming);
		}

		protected DefaultNHINDAgent createDefaultNHINDAgent() throws Exception {
			return new DefaultNHINDAgent("", new KeyStoreCertificateStore(),
					new KeyStoreCertificateStore(), new DefaultTrustAnchorResolver()) {
				@Override
				protected void checkEnvelopeAddresses(
						NHINDAddressCollection recipients, NHINDAddress sender) {
					checkEnvelopeAddressesCalls++;
					checkEnvelopeAddresses_Internal(recipients, sender);
				}
				
				@Override
				public IncomingMessage processIncoming(IncomingMessage message) {
					processIncomingCalls++;
					return processIncoming_Internal(message);
				}
			};
		}

		protected int checkEnvelopeAddressesCalls = 0;

		protected void checkEnvelopeAddresses_Internal(
				NHINDAddressCollection recipients, NHINDAddress sender) {
		}
		
		protected IncomingMessage theProcessIncoming;
		protected int processIncomingCalls = 0;

		protected IncomingMessage processIncoming_Internal(
				IncomingMessage message) {
			theProcessIncoming = message;
			return theProcessIncoming;
		}

		protected String theCreateMessageText;

		protected String createMessageText() throws Exception {
			theCreateMessageText = "createMessageText";
			return theCreateMessageText;
		}

		protected NHINDAddressCollection theCreateRecipients;

		protected NHINDAddressCollection createRecipients() throws Exception {
			theCreateRecipients = new NHINDAddressCollection();
			theCreateRecipients.add(new NHINDAddress(""));
			return theCreateRecipients;
		}

		protected NHINDAddress theCreateSender;

		protected NHINDAddress createSender() throws Exception {
			theCreateSender = new NHINDAddress("");
			return theCreateSender;
		}

		protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectRecipientsParamIsPassedToCheckEnvelopeAddresses() throws Exception {
		new TestPlan() {
			protected void checkEnvelopeAddresses_Internal(
					NHINDAddressCollection recipients, NHINDAddress sender) {
				assertEquals(theCreateRecipients, recipients);
			}
			
			protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
				assertEquals(1, checkEnvelopeAddressesCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectSenderParamIsPassedToCheckEnvelopeAddresses() throws Exception {
		new TestPlan() {
			protected void checkEnvelopeAddresses_Internal(
					NHINDAddressCollection recipients, NHINDAddress sender) {
				assertEquals(theCreateSender, sender);
			}
			
			protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
				assertEquals(1, checkEnvelopeAddressesCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testProcessIncomingMethodIsCalled() throws Exception {
		new TestPlan() {
			
			protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
				assertEquals(1, processIncomingCalls);
				assertEquals(theProcessIncoming, processIncoming);
			}
		}.perform();
	}
}