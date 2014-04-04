package org.nhindirect.stagent.utils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * <p>
 * Mime message that won't change the message id when it gets put in the outbox
 * folder. The message id can be set by application business logic and it won't
 * change.
 * </p>
 * 
 * 
 */
public class SecondaryMimeMessage extends MimeMessage {
	public SecondaryMimeMessage() {
		super((Session) null);
	}
	public SecondaryMimeMessage(Session session) {
		super(session);
	}

	@Override
	protected void updateMessageID() throws MessagingException {
		// Want to avoid resetting the message id when the message is sent,
		// so we'll remove the code that does so.
	}
}