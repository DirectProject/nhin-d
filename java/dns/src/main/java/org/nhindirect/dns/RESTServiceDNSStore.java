package org.nhindirect.dns;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.impl.DefaultCertPolicyService;
import org.nhind.config.rest.impl.DefaultCertificateService;
import org.nhind.config.rest.impl.DefaultDNSService;
import org.nhind.config.rest.CertPolicyService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.exceptions.CertificateConversionException;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.dns.annotation.ConfigServiceURL;
import org.nhindirect.policy.PolicyFilterFactory;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.nhindirect.policy.x509.SignatureAlgorithmIdentifier;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import com.google.inject.Inject;

public class RESTServiceDNSStore extends AbstractDNSStore
{
	protected final CertificateService certService;
	protected final CertPolicyService certPolicyService;
	protected final DNSService dnsService;
	
	@Inject
	public RESTServiceDNSStore(@ConfigServiceURL String serviceURL, HttpClient httpClient, ServiceSecurityManager securityManager)
	{
		certService = new DefaultCertificateService(serviceURL, httpClient, securityManager);		
		certPolicyService = new DefaultCertPolicyService(serviceURL, httpClient, securityManager);	
		dnsService = new DefaultDNSService(serviceURL, httpClient, securityManager);	
		
		try
		{
			configCertPolicy();
		}
		catch (DNSException e)
		{
			throw new IllegalStateException(e);
		}
		
	}
	
	/**
	 * Checks to see if a certificate policy has been configured.
	 */
	protected void configCertPolicy() throws DNSException
	{
		// check to see if there is a certificate policy set
		final String polName = System.getProperty(DNS_CERT_POLICY_NAME_VAR);
		if (!StringUtils.isEmpty(polName))
		{
			InputStream inStream = null;
			LOGGER.info("Certificate policy name " + polName + " has been configured.");
			try
			{
				// get the policy by name
				final org.nhindirect.config.model.CertPolicy policy = certPolicyService.getPolicyByName(polName);
				if (policy == null)
				{
					LOGGER.warn("Certificate policy " + polName + " could not be found in the system.  Falling back to no policy.");
					return;
				}
				
				// now compile the policy into an expression
				final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(policy.getLexicon());
				inStream = new ByteArrayInputStream(policy.getPolicyData());
				this.polExpression = parser.parse(inStream);
				
				// now create the filter
				this.polFilter = PolicyFilterFactory.getInstance();
				
			}
			catch (Exception e)
			{
				// it's OK if can't find the certificate policy that was configured, we'll just log a warning
				// it's also OK if we can't download or parse the policy, but we need to log the error
				LOGGER.warn("Error loading and compling certificate policy " + polName + ".  Will fallback to no policy filter.", e);
			}
			finally
			{
				IOUtils.closeQuietly(inStream);
			}
		}
		else
			LOGGER.info("No certificate policy has been configured.");
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RRset processGenericRecordRequest(String name, int type) throws DNSException
	{		
		Collection<DNSRecord> records;
		
		try
		{
			records = dnsService.getDNSRecord(type, name);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for DNS records failed: " + e.getMessage(), e);
		}
		
		if (records == null || records.size() == 0)
			return null;
		
		RRset retVal = new RRset();	
		try
		{
			for (DNSRecord record : records)						
			{
				Record rec = Record.newRecord(Name.fromString(record.getName()), record.getType(), 
						record.getDclass(), record.getTtl(), record.getData());
				
				retVal.addRR(rec);
			}
		}		
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "Failure while parsing generic record data: " + e.getMessage(), e);
		}
		
		return retVal;
	}	
	
	@Override
	protected Collection<Record> processGenericANYRecordRequest(String name) throws DNSException
	{		
		Collection<DNSRecord> records;
		
		try
		{
			records = dnsService.getDNSRecord(Type.ANY, name);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for DNS records failed: " + e.getMessage(), e);
		}
		
		if (records == null || records.size() == 0)
			return null;
		
		Collection<Record>  retVal = new ArrayList<Record>();	
		try
		{
			for (DNSRecord record : records)						
			{
				Record rec = Record.newRecord(Name.fromString(record.getName()), record.getType(), 
						record.getDclass(), record.getTtl(), record.getData());
				
				retVal.add(rec);
			}
		}		
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "Failure while parsing generic record data: " + e.getMessage(), e);
		}
		
		return retVal;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	@Override
	protected RRset processCERTRecordRequest(String name) throws DNSException
	{
		if (name.endsWith("."))
			name = name.substring(0, name.length() - 1);
				
		Collection<Certificate> certs;
		
		// use the certificate configuration service
		try
		{
			certs = certService.getCertificatesByOwner(name);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for certificates failed: " + e.getMessage(), e);
		}
		
		if (certs == null || certs.size() == 0)
		{
			// unless the call above was for an org level cert, it will probably always fail because the
			// "name" parameter has had all instances of "@" replaced with ".".  The certificate service 
			// stores owners using "@".
			// This is horrible, but try hitting the cert service replacing each "." with "@" one by one.
			// Start at the beginning of the address because this is more than likely where the "@" character
			// will be.
			int previousIndex = 0;
			int replaceIndex = 0;
			while ((replaceIndex = name.indexOf(".", previousIndex)) > -1)
			{
				char[] chars = name.toCharArray();
				chars[replaceIndex] = '@';
				try
				{
					certs = certService.getCertificatesByOwner(String.copyValueOf(chars));
				}
				catch (Exception e)
				{
					throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for certificates failed: " + e.getMessage(), e);
				}				
				if (certs != null && certs.size() > 0)
					break;
				
				if (replaceIndex >= (name.length() - 1))
					break;
				
				previousIndex = replaceIndex + 1;
			}
		}
			
		if (certs == null || certs.size() == 0)
			return null;
		
		if (!name.endsWith("."))
			name += ".";
		
		RRset retVal = new RRset();		
		try
		{
			for (Certificate cert : certs)
			{
				int certRecordType = CERTRecord.PKIX;
				byte[] retData = null;
				
				X509Certificate xCert = null;
				try
				{
					// need to convert to cert container because this might be 
					// a certificate with wrapped private key data
					final CertUtils.CertContainer cont =  CertUtils.toCertContainer(cert.getData());
					xCert = cont.getCert();
					// check if this is a compliant certificate with the configured policy... if not, move on
					if (!isCertCompliantWithPolicy(xCert))
						continue;
					
					retData = xCert.getEncoded();
				}
				catch (CertificateConversionException e)
				{
					// probably not a Certificate... might be a URL
				}
				

				if (xCert == null)
				{
					// see if it's a URL
					try
					{
						retData = cert.getData();
						URL url = new URL(new String(retData));
						certRecordType = CERTRecord.URI;
					}
					catch (Exception e)
					{
						throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "Failure while parsing CERT record data: " + e.getMessage(), e);
					}					
				}
				
				int keyTag = 0;
				int alg = 0;
				if (xCert != null && xCert.getPublicKey() instanceof RSAKey)
				{
					RSAKey key = (RSAKey)xCert.getPublicKey();
					byte[] modulus = key.getModulus().toByteArray();
					
					keyTag = (modulus[modulus.length - 2] << 8) & 0xFF00;
					
					keyTag |= modulus[modulus.length - 1] & 0xFF;	
					if (xCert.getSigAlgOID().equalsIgnoreCase(SignatureAlgorithmIdentifier.SHA1RSA.getId())){
					    alg = 5; // RFC 4034 Appendix A.1
					} else if (xCert.getSigAlgOID().equalsIgnoreCase(SignatureAlgorithmIdentifier.SHA256RSA.getId())){
					    alg = 8; // RFC 5702 3.1
					} else if (xCert.getSigAlgOID().equalsIgnoreCase(SignatureAlgorithmIdentifier.SHA1DSA.getId())){
					    alg = 3; // RFC 4034 Appendix A.1
					} else if (xCert.getSigAlgOID().equalsIgnoreCase(SignatureAlgorithmIdentifier.MD5RSA.getId())){
					    alg = 1; // RFC 4034 Appendix A.1
					} else{
					    alg = 5;
					}
				}
				
				CERTRecord rec = new CERTRecord(Name.fromString(name), DClass.IN, 86400L, certRecordType, keyTag, 
						alg /*public key alg, RFC 4034*/, retData);
				
				retVal.addRR(rec);
			}
		}		
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "Failure while parsing CERT record data: " + e.getMessage(), e);
		}
		
		// because of policy filtering, it's possible that we could have filtered out every cert
		// resulting in an empty RR set
		return (retVal.size() == 0) ? null : retVal;
	}	
	
	@Override
	protected synchronized Record checkForSoaRecord(String questionName)
    {
		if (!questionName.endsWith("."))
			questionName += ".";
    	
    	if (soaRecords == null)
    	{
    		Collection<DNSRecord> getRecs = null;
    		// load all SOA records...
    		try
    		{
    			getRecs = dnsService.getDNSRecord(Type.SOA, "");
    			
    			if (getRecs == null || getRecs.size() == 0)
    				soaRecords = Collections.emptyMap();
    			else
    			{
	    			soaRecords = new HashMap<String, Record>();
	    			
	    			for (DNSRecord rec : getRecs)
	    			{
	    				Record newRec = Record.newRecord(Name.fromString(rec.getName()), Type.SOA, 
	    						rec.getDclass(), rec.getTtl(), rec.getData());
	    				
	    				soaRecords.put(newRec.getName().toString(), newRec);
	    			}
    			}
    		}
    		catch (Exception e)
    		{
    			LOGGER.error("Failed to load SOA records from config service.");    			
    		}
    	}
    	
    	Record retVal = null;
    	if (soaRecords.size() > 0)
    	{
    		// look for the record by question name
    		
    		retVal = soaRecords.get(questionName);
    		if (retVal == null)
    		{
	    		// start taking apart the question name . by .
	    		int index = -1;
	    		while ((index = questionName.indexOf(".")) > 0 && index < (questionName.length() - 1))
	    		{
	    			questionName = questionName.substring(index + 1);
	    			retVal = soaRecords.get(questionName);
	        		if (retVal != null)
	        			break;
	    		}
    		}    		
    	}
    	
    	return retVal;
    }	
}
