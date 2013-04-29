package org.nhindirect.policy;

public interface PolicyOperatorExecutor<O,R>
{
	public PolicyValue<R> execute();
}
