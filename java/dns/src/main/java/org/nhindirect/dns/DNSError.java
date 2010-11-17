package org.nhindirect.dns;

public class DNSError<T> 
{
	public static <T> DNSError<T> newError(T error)
	{
		return new DNSError<T>(error);
	}
		
	private final T error;
	
	public DNSError(T error)
	{
		this.error = error;
	}

	public T getError() 
	{
		return error;
	}
	
	@Override
	public String toString()
	{
		return error.toString();
	}
}
