package org.nhindirect.stagent;

import org.nhindirect.stagent.mail.Message;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class IncomingMessageTestModule extends AbstractModule {

	private final Message message;
	private final NHINDAddressCollection recipients;
	private final NHINDAddress sender;
	
	public IncomingMessageTestModule(Message message, NHINDAddressCollection recipients, NHINDAddress sender) {
		this.message = message;
		this.recipients = recipients;
		this.sender = sender;
	}
	
	@Override
	protected void configure() {
		this.bind(Message.class).annotatedWith(Names.named("Message")).toInstance(message);
		this.bind(NHINDAddressCollection.class).annotatedWith(Names.named("Recipients")).toInstance(recipients);
		this.bind(NHINDAddress.class).annotatedWith(Names.named("Sender")).toInstance(sender);
	}

}
