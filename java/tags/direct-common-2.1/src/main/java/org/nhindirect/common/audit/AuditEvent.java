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

package org.nhindirect.common.audit;

/**
 * A high level descriptor of an auditable event.  Attributes include a name which describes a high level "category" of the event and a type that further
 * qualifies that the event.
 * @author Greg Meyer
 * @since 1.0
 */
public class AuditEvent 
{
	private final String name;
	private final String type;
	
	/**
	 * Constructs an audit event from a name and type.
	 * @param name The generic name or "category" of the event.  Cannot be null or empty.
	 * @param type A type that further describes the category.  Cannot be null or empty.
	 */
	public AuditEvent(String name, String type)
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException();
		
		if (type == null || type.isEmpty())
			throw new IllegalArgumentException();		
		
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Gets the name of the event.
	 * @return The name of the event
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the type of the event.
	 * @return The type of the event
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override 
	public String toString()
	{
		StringBuilder builder = new StringBuilder("EventName: ");
		builder.append(name).append("\r\nEvent Type: ").append(type);
		
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof AuditEvent))
			return false;
		
		return obj.hashCode() == hashCode();
	}
}
