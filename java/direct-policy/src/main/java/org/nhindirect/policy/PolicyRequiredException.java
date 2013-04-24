package org.nhindirect.policy;

public class PolicyRequiredException extends PolicyProcessException
{
	static final long serialVersionUID = 492724799349319556L;
	
	   /////CLOVER:OFF
		/**
		 * {@inheritDoc}
		 */
	    public PolicyRequiredException() 
	    {
	    	super();
	    }

		/**
		 * {@inheritDoc}
		 */
	    public PolicyRequiredException(String msg) 
	    {
	        super(msg);
	    }

		/**
		 * {@inheritDoc}
		 */
	    public PolicyRequiredException(String msg, Throwable t) 
	    {
	        super(msg, t);
	    }

		/**
		 * {@inheritDoc}
		 */
	    public PolicyRequiredException(Throwable t) 
	    {
	        super(t);
	    }
	    /////CLOVER:ON
}
