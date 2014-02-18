package org.nhindirect.stagent;

import java.io.ByteArrayOutputStream;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.SecondaryMimeMessage;

/**
 * Generated test case.
 * 
 * @author junit_generate
 */
public class DefaultNHINDAgent_ProcessIncoming_AsRawString_Test extends
		TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultNHINDAgent impl = createDefaultNHINDAgent();
			IncomingMessage processIncoming = impl
					.processIncoming(createMessageText());
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

		protected String theCreateMessageText;

		protected String createMessageText() throws Exception {
			theCreateMessageText = "createMessageText";
			return theCreateMessageText;
		}

		protected void doAssertions(IncomingMessage processIncoming)
				throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageTxtIsNull_ThrowsException() throws Exception {
		new TestPlan() {
			protected String createMessageText() throws Exception {
				theCreateMessageText = null;
				return theCreateMessageText;
			}

			protected void doAssertions(IncomingMessage processIncoming)
					throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertNull(theCreateMessageText);
				assertTrue(exception instanceof IllegalArgumentException);
			}

		}.perform();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageTxtIsBlank_ThrowsException() throws Exception {
		new TestPlan() {
			protected String createMessageText() throws Exception {
				theCreateMessageText = "";
				return theCreateMessageText;
			}

			protected void doAssertions(IncomingMessage processIncoming)
					throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertEquals(0, theCreateMessageText.length());
				assertTrue(exception instanceof IllegalArgumentException);
			}

		}.perform();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageTxtIsValid_ProcessIncomingMethodIsCalled()
			throws Exception {
		new TestPlan() {
			protected String createMessageText() throws Exception {
				MimeMessage mimeMsg = new SecondaryMimeMessage();
				mimeMsg.setText("");
				mimeMsg.setRecipients(RecipientType.TO, "some");
				mimeMsg.setSender(new InternetAddress());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				mimeMsg.writeTo(bos);
				theCreateMessageText = new String(bos.toByteArray());
				return theCreateMessageText;
			}

			protected void doAssertions(IncomingMessage processIncoming)
					throws Exception {
				assertEquals(1, processIncomingCalls);
				assertNotNull(theProcessIncoming);
				assertEquals(theProcessIncoming, processIncoming);
			}

		}.perform();
	}
}