package org.nhindirect.stagent;

public class NHINDAgentAccessor 
{
	
    public static void bindAddresses(NHINDAgent agent, IncomingMessage message)
    {
    	if (agent instanceof DefaultNHINDAgent)
    		((DefaultNHINDAgent)agent).bindAddresses(message);
    }
    
    public static void bindAddresses(NHINDAgent agent, OutgoingMessage message)
    {
    	if (agent instanceof DefaultNHINDAgent)
    		((DefaultNHINDAgent)agent).bindAddresses(message);
    }
}
