package org.nhindirect.policy.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class XStreamFactory 
{
	public static XStream getXStreamInstance()
	{
		final XStream xStreamWithFieldIgnore = new XStream()
		{
	        protected MapperWrapper wrapMapper(final MapperWrapper next) 
	        {
	            return new MapperWrapper(next) 
	            {
	                public boolean shouldSerializeMember(@SuppressWarnings("rawtypes") final Class definedIn, final String fieldName) 
	                {
	                    if (definedIn == Object.class)
	                        return false;
	                    return super.shouldSerializeMember(definedIn, fieldName);
	                }
	            };
	        }
		};
		
		xStreamWithFieldIgnore.setMode(XStream.NO_REFERENCES);
		
		return xStreamWithFieldIgnore;
	}
}
