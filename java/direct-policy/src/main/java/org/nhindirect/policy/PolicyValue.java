package org.nhindirect.policy;

import java.io.Serializable;

public interface PolicyValue<T> extends Serializable
{
	public T getPolicyValue();
}
