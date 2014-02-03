package org.nhind.config.rest.impl.requests;

import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractDeleteRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;

public class DeleteCertificatesByIdsRequest extends AbstractDeleteRequest<Certificate, Certificate>
{
	private final Collection<Long> ids;

    public DeleteCertificatesByIdsRequest(HttpClient httpClient, String certServerUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager,  Collection<Long> ids) 
    {
        super(httpClient, certServerUrl, jsonMapper, securityManager, null);
        
        if (ids == null || ids.isEmpty())
        	throw new IllegalArgumentException("Ids name cannot be null or empty");
        
        this.ids = ids;
    }
 
    @Override
    protected String getRequestUri() throws ServiceException
    {
    	final StringBuilder builder = new StringBuilder();
    	
    	int cnt = 0;
    	for (Long id : ids)
    	{
    		builder.append(id);
    		if (cnt < ids.size())
    			builder.append(",");
    			
    		++cnt;
    	}
    	
    	return serviceUrl + "certificate/ids/" + uriEscape(builder.toString());
    }
}