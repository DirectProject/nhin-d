package org.nhind.config.rest.providers;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

@Provider
@Consumes({MediaType.APPLICATION_JSON, "text/json"})
@Produces({MediaType.APPLICATION_JSON, "text/json"})
@Component
public class ConfigJSONProvider extends JacksonJsonProvider 
{
	/**
	 * {@inheritDoc}
	 */
	public ConfigJSONProvider()
	{        
		super(getDefaultObjectMapper());
	}
	
	/**
	 * {@inheritDoc}
	 */
	///CLOVER:OFF
	public ConfigJSONProvider(ObjectMapper mapper) 
    {
        super(mapper);
    }
	///CLOVER:ON
	
	/*
	 * Create the custom, default object mapper.
	 */
	private static ObjectMapper getDefaultObjectMapper()
	{
		ObjectMapper mappy = new ObjectMapper();
	    mappy.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
	    
	    return mappy;
	}
}