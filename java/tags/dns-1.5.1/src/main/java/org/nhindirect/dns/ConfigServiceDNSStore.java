/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Chris Lomonico  chris.lomonico@surescripts.com
    Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.dns;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.config.model.exceptions.CertificateConversionException;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.dns.annotation.ConfigServiceURL;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyFilter;
import org.nhindirect.policy.PolicyFilterFactory;
import org.nhindirect.policy.PolicyLexicon;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;


import com.google.inject.Inject;

/**
 * Implementation of the the {@link DNStore} interface that uses the Direct Project configuration web service to store 
 * DNS records.
 * @author Greg Meyer
 * @since 1.0
 */
public class ConfigServiceDNSStore implements DNSStore 
{
	protected static final String DNS_CERT_POLICY_NAME_VAR = "org.nhindirect.dns.CertPolicyName";
	
	protected static final String DEFAULT_JCE_PROVIDER_STRING = "BC";
	protected static final String JCE_PROVIDER_STRING_SYS_PARAM = "org.nhindirect.dns.JCEProviderName";	
	
	protected static final Log LOGGER = LogFactory.getFactory().getInstance(ConfigServiceDNSStore.class);
	
	protected Map<String, Record> soaRecords = null;
	
	protected PolicyFilter polFilter = null;
	protected PolicyExpression polExpression = null;
	
	/**
	 * Gets the configured JCE crypto provider string for crypto operations.  This is configured using the
	 * -Dorg.nhindirect.dns.JCEProviderName JVM parameters.  If the parameter is not set or is empty,
	 * then the default string "BC" (BouncyCastle provider) is returned.  By default the agent installs the BouncyCastle provider.
	 * @return The name of the JCE provider string.
	 */
	public static String getJCEProviderName()
	{
		String retVal = System.getProperty(JCE_PROVIDER_STRING_SYS_PARAM);
		
		if (retVal == null || retVal.isEmpty())
			retVal = DEFAULT_JCE_PROVIDER_STRING;
		
		return retVal;
	}
	
	/**
	 * Overrides the configured JCE crypto provider string.  If the name is empty or null, the default string "BC" (BouncyCastle provider)
	 * is used.
	 * @param name The name of the JCE provider.
	 */
	public static void setJCEProviderName(String name)
	{
		if (name == null || name.isEmpty())
			System.setProperty(JCE_PROVIDER_STRING_SYS_PARAM, DEFAULT_JCE_PROVIDER_STRING);
		else
			System.setProperty(JCE_PROVIDER_STRING_SYS_PARAM, name);
	}
	
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}	

	final ConfigurationServiceProxy proxy;
	
	/**
	 * Creates a store using the provided URL to lookup DNS records in the configuration service.
	 * @param serviceURL The URL of the configuration service.
	 */
	@Inject
	public ConfigServiceDNSStore(@ConfigServiceURL URL serviceURL)
	{
		proxy = new ConfigurationServiceProxy(serviceURL.toString());		
		
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
				final org.nhind.config.CertPolicy policy = proxy.getPolicyByName(polName);
				if (policy == null)
				{
					LOGGER.warn("Certificate policy " + polName + " could not be found in the system.  Falling back to no policy.");
					return;
				}
				
				// now compile the policy into an expression
				final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(PolicyLexicon.valueOf(policy.getLexicon().getValue()));
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
	@SuppressWarnings("unchecked")
	@Override
	public Message get(Message request) throws DNSException
	{
		LOGGER.trace("get(Message) Entered");
		/* for testing time out cases
		try
		{
			Thread.sleep(1000000);
		}
		catch (Exception e)
		{

		}
	    */
		if (request == null)
			throw new DNSException(DNSError.newError(Rcode.FORMERR));
		
		Header header = request.getHeader();
		if (header.getFlag(Flags.QR) || header.getRcode() != Rcode.NOERROR)
			throw new DNSException(DNSError.newError(Rcode.FORMERR));

		if (header.getOpcode() != Opcode.QUERY)
			throw new DNSException(DNSError.newError(Rcode.NOTIMP));	
		
        Record question = request.getQuestion();
        
        if (question == null || question.getDClass() != DClass.IN)
        {
        	throw new DNSException(DNSError.newError(Rcode.NOTIMP));
        }

        Record queryRecord = request.getQuestion();
        Name name = queryRecord.getName();
    	int type = queryRecord.getType();
        
    	if (LOGGER.isDebugEnabled())
    	{
    		StringBuilder builder = new StringBuilder("Recieved Query Request:");
    		builder.append("\r\n\tName: " + name.toString());
    		builder.append("\r\n\tType: " + type);
    		builder.append("\r\n\tDClass: " + queryRecord.getDClass());
    		LOGGER.debug(builder.toString());
    	}

    	Collection<Record> lookupRecords= null;
        switch (question.getType())
        {
        	case Type.A:
        	case Type.MX:
        	case Type.SOA:
        	case Type.SRV:
        	case Type.NS:
        	case Type.CNAME: 
        	{
        		try
        		{
        			final RRset set = processGenericRecordRequest(name.toString(), type);
        			
        			if (set != null)
        			{
	        			lookupRecords = new ArrayList<Record>();
	        			Iterator<Record> iter = set.rrs();
	        			while (iter.hasNext())
	        				lookupRecords.add(iter.next());
        			}
        			
        		}
        		catch (Exception e)
        		{
        			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call failed: " + e.getMessage(), e);
        		}
        		break;
        	}
        	case Type.CERT:
        	{
    			final RRset set = processCERTRecordRequest(name.toString());
    			
    			if (set != null)
    			{
	    			lookupRecords = new ArrayList<Record>();
	    			Iterator<Record> iter = set.rrs();
	    			while (iter.hasNext())
	    				lookupRecords.add(iter.next());
    			}
    			
        		break;
        	}
        	case Type.ANY:
        	{
        		
        		Collection<Record> genRecs = processGenericANYRecordRequest(name.toString());
        		RRset certRecs = processCERTRecordRequest(name.toString());

        		if (genRecs != null || certRecs != null)
        		{
        			lookupRecords = new ArrayList<Record>();
        			if (genRecs != null)
        				lookupRecords.addAll(genRecs);
        			
        			if (certRecs != null)
        			{
    	    			Iterator<Record> iter = certRecs.rrs();
    	    			while (iter.hasNext())
    	    				lookupRecords.add(iter.next());
        			}
        		}
 
        		break;
        	}
        	default:
        	{
        		LOGGER.debug("Query Type " + type + " not implemented");
        		throw new DNSException(DNSError.newError(Rcode.NOTIMP), "Query Type " + type + " not implemented"); 
        	}        	
        }
     
        
        if (lookupRecords == null || lookupRecords.size() == 0)
        {
        	LOGGER.debug("No records found.");
        	return null;
        }
        	
        final Message response = new Message(request.getHeader().getID());
        response.getHeader().setFlag(Flags.QR);
    	if (request.getHeader().getFlag(Flags.RD))
    		response.getHeader().setFlag(Flags.RD);
    	response.addRecord(queryRecord, Section.QUESTION);
    	
    	
		final Iterator<Record> iter = lookupRecords.iterator();
		while (iter.hasNext())
			response.addRecord(iter.next(), Section.ANSWER);
    	
    	// we are authoritative only
    	response.getHeader().setFlag(Flags.AA);
    	// look for an SOA record
    	final Record soaRecord = checkForSoaRecord(name.toString());
    	if (soaRecord != null)
    		response.addRecord(soaRecord, Section.AUTHORITY);		
		
		LOGGER.trace("get(Message) Exit");
		
    	return response;
	}
	
	
	/**
	 * Processes all DNS requests except CERT records.
	 * @param name The record name.
	 * @param type The record type.
	 * @return Returns a set of record responses to the request.
	 * @throws DNSException
	 */
	protected RRset processGenericRecordRequest(String name, int type) throws DNSException
	{		
		DnsRecord records[];
		
		try
		{
			records = proxy.getDNSByNameAndType(name, type);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for DNS records failed: " + e.getMessage(), e);
		}
		
		if (records == null || records.length == 0)
			return null;
		
		RRset retVal = new RRset();	
		try
		{
			for (DnsRecord record : records)						
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
	
	protected Collection<Record> processGenericANYRecordRequest(String name) throws DNSException
	{		
		DnsRecord records[];
		
		try
		{
			records = proxy.getDNSByNameAndType(name, Type.ANY);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for DNS records failed: " + e.getMessage(), e);
		}
		
		if (records == null || records.length == 0)
			return null;
		
		Collection<Record>  retVal = new ArrayList<Record>();	
		try
		{
			for (DnsRecord record : records)						
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
	 * Processes all DNS CERT requests.
	 * @param name The record name.  In many cases this a email address.
	 * @return Returns a set of record responses to the request.
	 * @throws DNSException
	 */
	@SuppressWarnings("unused")
	protected RRset processCERTRecordRequest(String name) throws DNSException
	{
		if (name.endsWith("."))
			name = name.substring(0, name.length() - 1);
				
		Certificate[] certs;
		
		// use the certificate configuration service
		try
		{
			certs = proxy.getCertificatesForOwner(name, null);
		}
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for certificates failed: " + e.getMessage(), e);
		}
		
		if (certs == null || certs.length == 0)
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
					certs = proxy.getCertificatesForOwner(String.copyValueOf(chars), null);
				}
				catch (Exception e)
				{
					throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call for certificates failed: " + e.getMessage(), e);
				}				
				if (certs != null && certs.length > 0)
					break;
				
				if (replaceIndex >= (name.length() - 1))
					break;
				
				previousIndex = replaceIndex + 1;
			}
		}
			
		if (certs == null || certs.length == 0)
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
					alg = 5;
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
	
    /*
     * Look for SOA records corresponding to the request
     * TODO: Add cache coherency to SOA records?
     */
	protected synchronized Record checkForSoaRecord(String questionName)
    {
		if (!questionName.endsWith("."))
			questionName += ".";
    	
    	if (soaRecords == null)
    	{
    		DnsRecord[] getRecs = null;
    		// load all SOA records...
    		try
    		{
    			getRecs = proxy.getDNSByType(Type.SOA);
    			
    			if (getRecs == null || getRecs.length == 0)
    				soaRecords = Collections.emptyMap();
    			else
    			{
	    			soaRecords = new HashMap<String, Record>();
	    			
	    			for (DnsRecord rec : getRecs)
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
	
	protected boolean isCertCompliantWithPolicy(X509Certificate cert)
	{
		// if no policy has been set, then always return true
		if (this.polFilter == null)
			return true;
		
		try
		{
			return this.polFilter.isCompliant(cert, this.polExpression);
		}
		catch (Exception e)
		{
			LOGGER.warn("Error testing certificate for policy compliance.  Default to compliant.", e);
			return true;
		}
	}
}
