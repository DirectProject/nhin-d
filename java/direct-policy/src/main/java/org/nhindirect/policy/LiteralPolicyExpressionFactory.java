/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.policy;

import java.io.Serializable;

/**
 * Factory class for creating {@link LiteralPolicyExpression} instances.
 * @author Greg Meyer
 * @since 1.0
 *
 */
public class LiteralPolicyExpressionFactory 
{
	/**
	 * Creates an instance from a {@link PolicyValue} instance.
	 * @param value The value of the literal.
	 * @return A new instance of a {@link LiteralPolicyExpression} object containing the {@link PolicyValue}.
	 */
	public static <T> LiteralPolicyExpression<T> getInstance(PolicyValue<T> value)
	{
		return new LiteralPolicyExpressionImpl<T>(value);
	}
	
	/**
	 * Creates an instance from an object.  An internal {@link PolicyValue} object is automatically created.
	 * @param value The value of the literal.
	 * @return A new instance of a {@link LiteralPolicyExpression} object containing the value in a wrapped {@link PolicyValue} object.
	 */
	public static <T> LiteralPolicyExpression<T> getInstance(T value)
	{
		return new LiteralPolicyExpressionImpl<T>(PolicyValueFactory.getInstance(value));
	}
	
	/**
	 * Default implementation of the {@link LiteralPolicyExpression} interface.
	 * @author Greg Meyer
	 * @since 1.0
	 * 
	 * @param <T> The object type of the literal.
	 */
	protected static class LiteralPolicyExpressionImpl<T> implements LiteralPolicyExpression<T>, Serializable
	{
		static final long serialVersionUID = -4788934771158627147L;
		
		protected final PolicyValue<T> value;
		
		/**
		 * Constructor
		 * @param value The value of the literal.
		 */
		protected LiteralPolicyExpressionImpl(PolicyValue<T> value)
		{
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PolicyExpressionType getExpressionType() 
		{
			return PolicyExpressionType.LITERAL;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PolicyValue<T> getPolicyValue() 
		{
			return value;
		}
		
		///CLOVER:OFF
		/**
		 * {@inheritDoc}
		 */
		@Override 
		public boolean equals(Object obj)
		{
			if (obj == null)
				return false;
			
			if (obj instanceof LiteralPolicyExpression)
				return value.equals(((LiteralPolicyExpression<?>)obj).getPolicyValue());
			
			return value.equals(obj);
		}
		
		/**
		 * Returns the toString representation of the internal policy value.
		 */
		@Override
		public String toString()
		{
			return value.toString();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode()
		{
			return value.hashCode();
		}
		///CLOVER:ON
	}
}
