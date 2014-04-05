package org.nhindirect.stagent;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import junit.framework.TestCase;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.mail.MimeError;
import org.nhindirect.stagent.mail.MimeException;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.SecondaryMimeMessage;

/**
 * Generated test case.
 * @author junit_generate
 */
public class DefaultNHINDAgent_ProcessIncoming_AsMimeMessage_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultNHINDAgent impl = createDefaultNHINDAgent();
			IncomingMessage processIncoming = impl.processIncoming(createMsg());
			doAssertions(processIncoming);
		}

		protected DefaultNHINDAgent createDefaultNHINDAgent() throws Exception {
			return new DefaultNHINDAgent("", new KeyStoreCertificateStore(),
					new KeyStoreCertificateStore(), new DefaultTrustAnchorResolver()) {
				@Override
				public IncomingMessage processIncoming(IncomingMessage message) {
					processIncomingCalls++;
					return processIncoming_Internal(message);
				}
			};
		}
		
		protected IncomingMessage theProcessIncoming;
		protected int processIncomingCalls = 0;

		protected IncomingMessage processIncoming_Internal(
				IncomingMessage message) {
			theProcessIncoming = message;
			return theProcessIncoming;
		}

		protected MimeMessage theCreateMsg;

		protected MimeMessage createMsg() throws Exception {
			theCreateMsg = new SecondaryMimeMessage();
			theCreateMsg.setText("");
			theCreateMsg.setRecipients(RecipientType.TO, "some");
			theCreateMsg.setSender(new InternetAddress());
			return theCreateMsg;
		}

		protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testNullMessageParam_ThrowsException() throws Exception {
		new TestPlan() {
			protected MimeMessage createMsg() throws Exception {
				theCreateMsg = null;
				return theCreateMsg;
			}
			
			protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertNull(theCreateMsg);
				assertTrue(exception instanceof IllegalArgumentException);
			}
			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testNonNullMessageParam_ProcessIncomingIsCalled() throws Exception {
		new TestPlan() {
			
			protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
				assertEquals(1, processIncomingCalls);
				assertEquals(theProcessIncoming, processIncoming);
			}
			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testInvalidMessage_ThrowsMimeException() throws Exception {
		new TestPlan() {
			
			protected MimeMessage createMsg() throws Exception {
				theCreateMsg = new SecondaryMimeMessage();
				theCreateMsg.setRecipients(RecipientType.TO, "some");
				theCreateMsg.setSender(new InternetAddress());
				return theCreateMsg;
			}
			
			protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertTrue(exception instanceof MimeException);
				MimeException mimeException = (MimeException) exception;
				assertEquals(MimeError.InvalidMimeEntity, mimeException.getError());
			}
			
		}.perform();
	}
}