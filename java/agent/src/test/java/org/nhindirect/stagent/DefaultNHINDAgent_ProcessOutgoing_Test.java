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
public class DefaultNHINDAgent_ProcessOutgoing_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultNHINDAgent impl = createNHINDAgent();
			OutgoingMessage processOutgoing = impl
					.processOutgoing(createMessage());
			doAssertions(processOutgoing);
		}

		protected DefaultNHINDAgent createNHINDAgent() throws Exception {
			return new DefaultNHINDAgent("", new KeyStoreCertificateStore(),
					new KeyStoreCertificateStore(), new DefaultTrustAnchorResolver()) {
				@Override
				protected void processMessage(OutgoingMessage message) {
					processMessageCalls++;
					processMessage_Internal(message);
				}
			};
		}

		protected int processMessageCalls = 0;

		protected void processMessage_Internal(OutgoingMessage message) {
		}

		protected OutgoingMessage theCreateMessage;

		protected OutgoingMessage createMessage() throws Exception {
			theCreateMessage = new OutgoingMessage("");
			return theCreateMessage;
		}

		protected void doAssertions(OutgoingMessage processOutgoing)
				throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testNullMessageParam_ThrowsException() throws Exception {
		new TestPlan() {
			protected OutgoingMessage createMessage() throws Exception {
				theCreateMessage = null;
				return theCreateMessage;
			}

			protected void doAssertions(OutgoingMessage processOutgoing)
				throws Exception {
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
	
	class MessageParamIsNotNull extends TestPlan {
		@Override
		protected OutgoingMessage createMessage() throws Exception {
			MimeMessage mimeMsg = new SecondaryMimeMessage();
			mimeMsg.setText("");
			Message msg = new Message(mimeMsg);
			NHINDAddressCollection recipients = new NHINDAddressCollection();
			recipients.add(new NHINDAddress(""));
			NHINDAddress sender = new NHINDAddress("");
			theCreateMessage = new OutgoingMessage(msg, recipients, sender) {
				
				@Override 
				protected void validate(){
					  validateCalls++;
					  validate_Internal();
					}
				
			};
			return theCreateMessage;
		}
		
		protected int validateCalls=0;
		protected void validate_Internal(){
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testAgentIsSetInMessage() throws Exception {
		new MessageParamIsNotNull() {
			
			protected void doAssertions(OutgoingMessage processOutgoing)
					throws Exception {
				assertNotNull(processOutgoing.getAgent());
			}

		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageIsValidated() throws Exception {
		new MessageParamIsNotNull() {
			
			protected void doAssertions(OutgoingMessage processOutgoing)
					throws Exception {
				assertEquals(1, validateCalls);
			}

		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToProcessMessageMethod() throws Exception {
		new MessageParamIsNotNull() {
			
			@Override
			protected void processMessage_Internal(OutgoingMessage message){
				assertEquals(theCreateMessage, message);
			}
			
			protected void doAssertions(OutgoingMessage processOutgoing)
					throws Exception {
				assertEquals(1, processMessageCalls);
			}

		}.perform();
	}
	
	class NHINDAgentEventListenerIsNotNull extends MessageParamIsNotNull {
		@Override
		protected DefaultNHINDAgent createNHINDAgent() throws Exception {
			DefaultNHINDAgent nhindAgent = new DefaultNHINDAgent("", new KeyStoreCertificateStore(),
					new KeyStoreCertificateStore(), new DefaultTrustAnchorResolver()) {

						@Override 
						protected void processMessage(OutgoingMessage message){
							  processMessageCalls++;
							  processMessage_Internal(message);
							}
				
			};
			nhindAgent.setEventListener(new NHINDAgentEventListenerAdapter() {

				@Override 
				public void errorOutgoing(OutgoingMessage msg,Exception e){
					  errorOutgoingCalls++;
					  errorOutgoing_Internal(msg,e);
					}

				@Override 
				public void postProcessOutgoing(OutgoingMessage msg) throws NHINDException {
					  postProcessOutgoingCalls++;
					  postProcessOutgoing_Internal(msg);
					}

				@Override 
				public void preProcessOutgoing(OutgoingMessage msg) throws NHINDException {
					  preProcessOutgoingCalls++;
					  preProcessOutgoing_Internal(msg);
					}
				
			});
			return nhindAgent;
		}
		
		protected int errorOutgoingCalls=0;
		protected void errorOutgoing_Internal(OutgoingMessage msg,Exception e){
		}
		
		protected int postProcessOutgoingCalls=0;
		protected void postProcessOutgoing_Internal(OutgoingMessage msg) throws NHINDException {
		}
		
		protected int preProcessOutgoingCalls=0;
		protected void preProcessOutgoing_Internal(OutgoingMessage msg) throws NHINDException {
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToPreProcessOutgoingMethod() throws Exception {
		new NHINDAgentEventListenerIsNotNull() {
			
			@Override
			protected void preProcessOutgoing_Internal(OutgoingMessage msg) throws NHINDException {
				assertEquals(theCreateMessage, msg);
			}
			
			protected void doAssertions(OutgoingMessage processOutgoing)
					throws Exception {
				assertEquals(1, preProcessOutgoingCalls);
			}

		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToPostProcessOutgoingMethod() throws Exception {
		new NHINDAgentEventListenerIsNotNull() {
			
			@Override
			protected void postProcessOutgoing_Internal(OutgoingMessage msg) throws NHINDException {
				assertEquals(theCreateMessage, msg);
			}
			
			protected void doAssertions(OutgoingMessage processOutgoing)
					throws Exception {
				assertEquals(1, postProcessOutgoingCalls);
			}

		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testPostProcessOutgoingMethodThrowsException_ErrorOutgoingIsCalled() throws Exception {
		new NHINDAgentEventListenerIsNotNull() {
			
			protected NHINDException nhindException;
			
			@Override
			protected void postProcessOutgoing_Internal(OutgoingMessage msg) throws NHINDException {
				nhindException = new NHINDException();
				throw nhindException;
			}
			
			@Override
			protected void errorOutgoing_Internal(OutgoingMessage msg,Exception e){
				assertEquals(theCreateMessage, msg);
				assertEquals(nhindException, e);
			}
			
			protected void doAssertions(OutgoingMessage processOutgoing)
					throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertEquals(1, errorOutgoingCalls);
			}

		}.perform();
	}
}