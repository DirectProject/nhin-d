package org.nhindirect.policy;

import java.io.Serializable;

public class PolicyValueFactory
{
	public static <T> PolicyValue<T> getInstance(T value)
	{
		return new PolicyValueImpl<T>(value);
	}
	
	protected static class PolicyValueImpl<T> implements PolicyValue<T>, Serializable
	{
		static final long serialVersionUID = -7760457667066558146L;

		
		protected final T value;
		
		protected PolicyValueImpl(T value)
		{
			this.value = value;
		}
		
		public T getPolicyValue()
		{
			return value;
		}
		
		@Override
		public String toString()
		{
			return value.toString();
		}
		
		@Override 
		public boolean equals(Object obj)
		{
			if (obj == null)
				return false;
			
			if (obj instanceof PolicyValue)
				return value.equals(((PolicyValue<?>)obj).getPolicyValue());
			
			return value.equals(obj);
		}
		
		@Override
		public int hashCode()
		{
			return value.hashCode();
		}
		
	}
}
