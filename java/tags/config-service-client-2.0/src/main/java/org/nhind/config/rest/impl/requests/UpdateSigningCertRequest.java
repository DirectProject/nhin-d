package org.nhind.config.rest.impl.requests;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.AbstractPostRequest;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;

public class UpdateSigningCertRequest extends AbstractPostRequest<byte[], TrustBundle>
{
	private final String bundleName;
	
	static private byte[] getSafeCertData(X509Certificate cert)
	{
		try
		{
			return cert.getEncoded();
		}
		catch (CertificateEncodingException e)
		{
			throw new IllegalArgumentException("Could not get encoded data from certificate", e);
		}
	}
	
    public UpdateSigningCertRequest(HttpClient httpClient, String serviceUrl,
            ObjectMapper jsonMapper, ServiceSecurityManager securityManager, 
            String bundleName, X509Certificate cert) 
    {
    	super(httpClient, serviceUrl, jsonMapper, securityManager, 
    			(cert == null) ? null : getSafeCertData(cert));
    	
    	if (bundleName == null || bundleName.isEmpty())
    		throw new IllegalArgumentException("Bundle name cannot be null or empty");
    	
    	this.bundleName = bundleName;
    }

	@Override
	protected String getRequestUri() throws ServiceException 
	{
		return serviceUrl + "trustbundle/" + uriEscape(bundleName)  + "/signingCert";
	}


	
}
