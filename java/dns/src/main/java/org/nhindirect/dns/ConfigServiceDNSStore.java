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
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.config.Certificate;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.DnsRecord;
import org.nhindirect.dns.annotation.ConfigServiceURL;
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
	
	protected static final String DEFAULT_JCE_PROVIDER_STRING = "BC";
	protected static final String JCE_PROVIDER_STRING_SYS_PARAM = "org.nhindirect.dns.JCEProviderName";	
	
	protected static final Log LOGGER = LogFactory.getFactory().getInstance(ConfigServiceDNSStore.class);
	
	protected Map<String, Record> soaRecords = null;
	
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
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Message get(Message request) throws DNSException
	{
		LOGGER.trace("get(Message) Entered");
		
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
    	
    	RRset lookupRecords = null;
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
        			lookupRecords = processGenericRecordRequest(name.toString(), type);
        		}
        		catch (Exception e)
        		{
        			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "DNS service proxy call failed: " + e.getMessage(), e);
        		}
        		break;
        	}
        	case Type.CERT:
        	{
        		lookupRecords = processCERTRecordRequest(name.toString());
        		break;
        	}
        	case Type.ANY:
        	{
        		RRset genRecs = processGenericRecordRequest(name.toString(), Type.ANY);
        		RRset certRecs = processCERTRecordRequest(name.toString());

        		if (genRecs == null || certRecs == null)
        		{
        			if (genRecs != null)
        				lookupRecords = genRecs; // certRecs is null, just return genRecs
        			else if (certRecs != null)
        				lookupRecords = certRecs; // genRecs is null, just return certRecs
        			// default case is just leave lookupRecords null
        		}
        		else
        		{
        			// we know both are non null, merge
        			lookupRecords = genRecs;
        			Iterator<Record> iter = certRecs.rrs();
        			while (iter.hasNext())
        				lookupRecords.addRR(iter.next());
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
        	
        Message response = new Message(request.getHeader().getID());
        response.getHeader().setFlag(Flags.QR);
    	if (request.getHeader().getFlag(Flags.RD))
    		response.getHeader().setFlag(Flags.RD);
    	response.addRecord(queryRecord, Section.QUESTION);
    	
    	
		Iterator<Record> iter = lookupRecords.rrs();
		while (iter.hasNext())
			response.addRecord(iter.next(), Section.ANSWER);
    	
    	// we are authoritative only
    	response.getHeader().setFlag(Flags.AA);
    	// look for an SOA record
    	Record soaRecord = checkForSoaRecord(name.toString());
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
	
	/**
	 * Processes all DNS CERT requests.
	 * @param name The record name.  In many cases this a email address.
	 * @return Returns a set of record responses to the request.
	 * @throws DNSException
	 */
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
	
				X509Certificate xCert = dataToCert(cert.getData());
								
				int keyTag = 0;
				if (xCert.getPublicKey() instanceof RSAKey)
				{
					RSAKey key = (RSAKey)xCert.getPublicKey();
					byte[] modulus = key.getModulus().toByteArray();
					
					keyTag = (modulus[modulus.length - 2] << 8) & 0xFF00;
					
					keyTag |= modulus[modulus.length - 1] & 0xFF;				
				}
				
				CERTRecord rec = new CERTRecord(Name.fromString(name), DClass.IN, 86400L, CERTRecord.PKIX, keyTag, 
						5 /*public key alg, RFC 4034*/, xCert.getEncoded());
				
				retVal.addRR(rec);
			}
		}		
		catch (Exception e)
		{
			throw new DNSException(DNSError.newError(Rcode.SERVFAIL), "Failure while parsing CERT record data: " + e.getMessage(), e);
		}
		
		return retVal;
	}
	
	/*
	 * It's possible we could be getting data from a p12 file which contains the private key.  This methods
	 * ensures that both p12 and X509 formats are decoded properly and only public certificates are returned.
	 */
	protected X509Certificate dataToCert(byte[] data) throws DNSException 
    {
    	ByteArrayInputStream bais = null;
    	X509Certificate retVal = null;
        try 
        {
            bais = new ByteArrayInputStream(data);
            
            // lets try this a as a PKCS12 data stream first
            try
            {
            	KeyStore localKeyStore = KeyStore.getInstance("PKCS12", getJCEProviderName());
            	
            	localKeyStore.load(bais, "".toCharArray());
            	Enumeration<String> aliases = localKeyStore.aliases();


        		// we are really expecting only one alias 
        		if (aliases.hasMoreElements())        			
        		{
        			String alias = aliases.nextElement();
        			retVal = (X509Certificate)localKeyStore.getCertificate(alias);
        		}
            }
            catch (Exception e)
            {
            	// must not be a PKCS12 stream, go on to next step
            }
            finally
            {
				if (bais != null)
				{
					try
					{
						bais.close();
						bais = null;
					}
					catch (IOException e) {/* no-op */}
				}
            }
            
            if (retVal == null)            	
            {
            	//try X509 certificate factory next       
                bais = new ByteArrayInputStream(data);

                retVal = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
            }
        } 
        catch (Exception e) 
        {
            throw new DNSException("Data cannot be converted to a valid X.509 Certificate");
        }
		finally
		{
			if (bais != null)
			{
				try
				{
					bais.close();
				}
				catch (IOException e) {/* no-op */}
			}
		}     
        
        return retVal;
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
}
