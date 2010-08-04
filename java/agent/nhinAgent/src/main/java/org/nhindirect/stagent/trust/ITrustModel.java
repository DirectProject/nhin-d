package org.nhindirect.stagent.trust;

import org.nhindirect.stagent.IncomingMessage;
import org.nhindirect.stagent.OutgoingMessage;

/**
 * Trust models enforce policies of trust on messages.  Policy variable include trusted recipients, senders, organizations, and certificates.
 * @author Greg Meyer
 * @author Umesh Madan
 */
public interface ITrustModel 
{
	/**
	 * Enforces trust policies on an incoming message.
	 * @param message An incoming message that has been signed.
	 */
    void enforce(IncomingMessage message);
    
    /**
     * Enforces trust policies on an outgoing message.
     * @param message An outgoing message.
     */
    void enforce(OutgoingMessage message);
}
