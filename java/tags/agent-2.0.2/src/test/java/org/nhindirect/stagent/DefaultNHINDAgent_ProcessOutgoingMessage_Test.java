package org.nhindirect.stagent;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;
import org.nhindirect.stagent.DefaultNHINDAgent;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cryptography.SMIMECryptographerImpl;
import org.nhindirect.stagent.mail.MailStandard;
import org.nhindirect.stagent.mail.Message;
import org.nhindirect.stagent.trust.DefaultTrustAnchorResolver;
import org.nhindirect.stagent.trust.TrustEnforcementStatus;
import org.nhindirect.stagent.trust.TrustModel;
import org.nhindirect.stagent.utils.BaseTestPlan;
import org.nhindirect.stagent.utils.SecondaryMimeMessage;

/**
 * Generated test case.
 * @author junit_generate
 */
public class DefaultNHINDAgent_ProcessOutgoingMessage_Test extends TestCase {
	abstract class TestPlan extends BaseTestPlan {
		@Override
		protected void performInner() throws Exception {
			DefaultNHINDAgent impl = createNHINDAgent();
			impl.processMessage(createMessage());
			doAssertions();
		}

		protected DefaultNHINDAgent createNHINDAgent() throws Exception {
			return new DefaultNHINDAgent("", new KeyStoreCertificateStore(),
					new KeyStoreCertificateStore(), new DefaultTrustAnchorResolver(), createTrustModel(), new SMIMECryptographerImpl()) {
				@Override
				protected void bindAddresses(OutgoingMessage message) {
					bindAddressesCalls++;
					bindAddresses_Internal(message);
				}
				
				@Override
				protected Message wrapMessage(Message message) {
					wrapMessageCalls++;
					return wrapMessage_Internal(message);
				}
				
				@Override 
				protected void signAndEncryptMessage(OutgoingMessage message){
					  signAndEncryptMessageCalls++;
					  signAndEncryptMessage_Internal(message);
					}
			};
		}
		
		protected Message theWrapMessage;
		protected int wrapMessageCalls = 0;

		protected Message wrapMessage_Internal(Message message) {
			theWrapMessage = message;
			return theWrapMessage;
		}

		protected int bindAddressesCalls = 0;

		protected void bindAddresses_Internal(OutgoingMessage message) {
		}
		
		protected int signAndEncryptMessageCalls=0;
		protected void signAndEncryptMessage_Internal(OutgoingMessage message){
		}

		protected OutgoingMessage theCreateMessage;

		protected OutgoingMessage createMessage() throws Exception {
			MimeMessage mimeMsg = new SecondaryMimeMessage();
			mimeMsg.setText("");
			Message msg = new Message(mimeMsg);
			NHINDAddressCollection recipients = new NHINDAddressCollection();
			recipients.add(new NHINDAddress(""));
			NHINDAddress sender = new NHINDAddress("");
			theCreateMessage = new OutgoingMessage(msg, recipients, sender) {

				@Override 
				public boolean hasRecipients(){
					  hasRecipientsCalls++;
					  return hasRecipients_Internal();
					}
				
				@Override 
				protected void categorizeRecipients(TrustEnforcementStatus minTrustStatus){
					  categorizeRecipientsCalls++;
					  categorizeRecipients_Internal(minTrustStatus);
					}

				@Override 
				protected void updateRoutingHeaders(){
					  updateRoutingHeadersCalls++;
					  updateRoutingHeaders_Internal();
					}
				
			};
			return theCreateMessage;
		}
		
		protected int updateRoutingHeadersCalls=0;
		protected void updateRoutingHeaders_Internal(){
		}

		protected TrustModel theCreateTrustModel;
		protected int enforceCalls = 0;

		protected void enforce_Internal(OutgoingMessage message) {
		}
		
		protected int categorizeRecipientsCalls=0;
		protected void categorizeRecipients_Internal(TrustEnforcementStatus minTrustStatus){
		}
		
		protected boolean theHasRecipients;
		protected int hasRecipientsCalls=0;
		protected boolean hasRecipients_Internal(){
		  theHasRecipients=true;
		  return theHasRecipients;
		}

		protected TrustModel createTrustModel() {
			theCreateTrustModel = new TrustModel() {
				@Override
				public void enforce(OutgoingMessage message) {
					enforceCalls++;
					enforce_Internal(message);
				}
			};
			return theCreateTrustModel;
		}

		protected void doAssertions() throws Exception {
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageIsWrapped_WrapMessageIsNotCalled() throws Exception {
		new TestPlan() {
			protected OutgoingMessage createMessage() throws Exception {
				MimeMessage mimeMsg = new SecondaryMimeMessage();
				mimeMsg.setText("");
				Message msg = new Message(mimeMsg) {

					@Override
					public String getContentType() throws MessagingException {
						return MailStandard.MediaType.WrappedMessage;
					}
					
				};
				NHINDAddressCollection recipients = new NHINDAddressCollection();
				recipients.add(new NHINDAddress(""));
				NHINDAddress sender = new NHINDAddress("");
				theCreateMessage = new OutgoingMessage(msg, recipients, sender) {

					@Override 
					protected void categorizeRecipients(TrustEnforcementStatus minTrustStatus){
						  categorizeRecipientsCalls++;
						  categorizeRecipients_Internal(minTrustStatus);
						}
					
				};
				return theCreateMessage;
			}
			
			protected void doAssertions() throws Exception {
				assertEquals(0, wrapMessageCalls);
			}
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToBindAddressMethod() throws Exception {
		new TestPlan() {
			
			protected void bindAddresses_Internal(OutgoingMessage message) {
				assertEquals(theCreateMessage, message);
			}
			
			protected void doAssertions() throws Exception {
				assertEquals(1, bindAddressesCalls);
			}
			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageDoesntHaveRecipients_ThrowsException() throws Exception {
		new TestPlan() {
			
			protected boolean hasRecipients_Internal(){
				  theHasRecipients=false;
				  return theHasRecipients;
				}
			
			protected void doAssertions() throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertTrue(exception instanceof AgentException);
				AgentException agentException = (AgentException) exception;
				assertTrue(agentException.getError().equals(AgentError.MissingTo));
			}
			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToEnforce() throws Exception {
		new TestPlan() {
			
			protected void enforce_Internal(OutgoingMessage message) {
				assertEquals(theCreateMessage, message);
			}
			
			protected void doAssertions() throws Exception {
				assertEquals(1, enforceCalls);
			}			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testMessageDoesntHaveTrustedRecipients_ThrowsException() throws Exception {
		new TestPlan() {
			
			protected boolean hasRecipients_Internal(){
				if(hasRecipientsCalls==2) {
				  theHasRecipients=false;
				}
				else {
					theHasRecipients = true;
				}
				  return theHasRecipients;
				}
			
			protected void doAssertions() throws Exception {
				fail();
			}

			@Override
			protected void assertException(Exception exception)
					throws Exception {
				assertEquals(2, hasRecipientsCalls);
				assertTrue(exception instanceof AgentException);
				AgentException agentException = (AgentException) exception;
				assertTrue(agentException.getError().equals(AgentError.NoTrustedRecipients));
			}
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testCorrectMessageParamIsPassedToSignAndEncryptMessage() throws Exception {
		new TestPlan() {
			
			protected void signAndEncryptMessage_Internal(OutgoingMessage message){
				assertEquals(theCreateMessage, message);
			}
			
			protected void doAssertions() throws Exception {
				assertEquals(1, signAndEncryptMessageCalls);
			}			
			
		}.perform();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testUpdateRoutingHeadersIsCalled() throws Exception {
		new TestPlan() {
			
			protected void doAssertions() throws Exception {
				assertEquals(1, updateRoutingHeadersCalls);
			}			
			
		}.perform();
	}
}