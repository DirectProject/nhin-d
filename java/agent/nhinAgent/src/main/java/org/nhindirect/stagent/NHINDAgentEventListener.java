package org.nhindirect.stagent;

/**
 * Callback interface for custom processing of a message in the {@link NHINDAgent}.  Implementations of this interface can be used to execute custom logic
 * during the processing stages of a message.  
 * @author Greg Meyer
 * @author Umesh Madan
 */
public interface NHINDAgentEventListener 
{
	/**
	 * Called when an unexpected error occurs in the agent.
	 * @param e The exception thrown by the agent.
	 */
    public void error(Exception e);
    
    /**
     * Called after the message has been validated but before it is decrypted. 
     * @param msg The incoming message.
     * @throws NHINDException
     */
    public void preProcessIncoming(IncomingMessage msg) throws NHINDException;
    
    /**
     * Called after the message is decrypted and the signature is validated.
     * @param msg The incoming message.
     * @throws NHINDException
     */
    public void postProcessIncoming(IncomingMessage msg) throws NHINDException;
    
    /**
     * Called in an exception occurs during the message processing stages. 
     * @param msg The incoming message.
     * @param The exception thrown by the agent.
     */
    public void errorIncoming(IncomingMessage msg, Exception e);
    
    /**
     * Called after the message has been validated but before it is encypted and signed. 
     * @param msg The outgoing message.
     * @throws NHINDException
     */    
    public void preProcessOutgoing(OutgoingMessage msg) throws NHINDException;
    
    /**
     * Called after the message has been encypted and signed. 
     * @param msg The outgoing message.
     * @throws NHINDException
     */      
    public void postProcessOutgoing(OutgoingMessage msg) throws NHINDException;
    
    /**
     * Called in an exception occurs during the message processing stages. 
     * @param msg The incoming message.
     * @param The exception thrown by the agent.
     */    
    public void errorOutgoing(OutgoingMessage msg, Exception e);
}
