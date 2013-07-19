/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   jrenolds68@gmail.com
 
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

package org.nhindirect.stagent.cert.impl;

import java.io.ByteArrayInputStream;
import java.net.UnknownHostException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.impl.util.Lookup;
import org.nhindirect.stagent.cert.impl.util.LookupFactory;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Type;

import com.google.inject.Inject;

/**
 * Certificate utility for looking up certificates in a public LDAP server.
 * <p>
 * Certificates are discovered using the following steps:
 * <ul>
 * <li>Discover LDAP server using DNS SRV records.  The DNS record name is in the format <i>_ldap._tcp.<domain_name><i>_</li>
 * <li>Connect to the LDAP server with no credentials (anonymous bind)</li>
 * <li>Discover the naming contexts (base DNs)</li>
 * <li>Query each naming context search on the Mail attribute returning the userSMIMECertificate attribute using 
 * subtree scope</li>
 * </ul>
 * @author jrenolds68@gmail.com
 * @since 1.2
 *
 */
public class LdapPublicCertUtilImpl implements LdapCertUtil{

	private static final Log LOGGER = LogFactory.getFactory().getInstance(LdapPublicCertUtilImpl.class);
	
	private static final String DEFAULT_LDAP_TIMEOUT = "5000";
	
	private static final String LDAP_TIMEOUT = "com.sun.jndi.ldap.read.timeout";
	private static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String LDAP_SRV_PREFIX = "_ldap._tcp.";
	private static final String CERT_ATTRIBUTE_BINARY = "userCertificate;binary";
	private static final String CERT_ATTRIBUTE = "userCertificate";
	private static final String EMAIL_ATTRIBUTE = "mail";
	private static final String BASE_DN_ATTRIBUTE = "namingContexts";
	
	private List<String> servers = new ArrayList<String>();
	
	/**
	 * Constructor
	 */
	@Inject
	public LdapPublicCertUtilImpl(){
		String[] configedServers = ResolverConfig.getCurrentConfig().servers();
		
		if (configedServers != null)
		{
			this.servers.addAll(Arrays.asList(configedServers));
		}
	}
	
	/**
	 * Searches for certificates in public LDAP servers using the subject name.
	 * @param subjectName The subject's email address or domain name.
	 * @return Collection of certificates matching the LDAP query for the subject name.
	 */
	public Collection<X509Certificate> ldapSearch(String subjectName){
		final Collection<X509Certificate> retVal = new ArrayList<X509Certificate>();
		
		String domainName;
    	
		// find by host
    	int index;
    	if ((index = subjectName.indexOf("@")) > -1)
    		domainName = subjectName.substring(index + 1);
    	else
    		domainName = subjectName;
    	
		final String lookupName = LDAP_SRV_PREFIX + domainName;
		
		InitialDirContext ctx = null;
		try
		{
	        ctx =  getDirContext(lookupName);
			if (ctx != null)
			{
				// discover the naming contexts
				List<String> dNs = getBaseNamingContexts(ctx);
				
				if (!dNs.isEmpty()){
					
					for (String dn : dNs){
						
						NamingEnumeration<SearchResult> searchResult = ctx.search(
								dn, 
								EMAIL_ATTRIBUTE + "=" + subjectName,
								getDefaultSearchControls());
						
						while (searchResult != null && searchResult.hasMore()) {
							final SearchResult certEntry = searchResult.nextElement();
							if (certEntry != null) {
								final Attributes certAttributes = certEntry.getAttributes();
								if (certAttributes != null) {
									// get only the returning cert attribute (for now, ignore all other attributes)
								    Attribute certAttribute = certAttributes.get(CERT_ATTRIBUTE_BINARY);
								    
								    // some LDAP servers do not properly support binary types... try again without the 
								    // binary modifier
									if (certAttribute == null)
										certAttribute = certAttributes.get(CERT_ATTRIBUTE);
									
									
									if (certAttribute != null) {
										NamingEnumeration<? extends Object> allValues = certAttribute.getAll();
										// LDAP may contain a collection of certificates.
										while(allValues.hasMoreElements()) {
											byte[] rawCert = null;
											
											Object obj = allValues.nextElement();
										
											rawCert = (byte[]) obj;
	
											
											final CertificateFactory cf = CertificateFactory.getInstance("X.509");
											final ByteArrayInputStream inputStream = new ByteArrayInputStream(rawCert);
											try
											{
												X509Certificate addCert = (X509Certificate)cf.generateCertificate(inputStream);
												retVal.add(addCert);
											}
											finally
											{
												IOUtils.closeQuietly(inputStream);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new NHINDException(e);
		}
		finally
		{
			this.closeDirContext(ctx);
		}
		
		return retVal;
		
	}
	
	/**
	 * Creates the LDAP directory context from an SRV lookup name.
	 * @param lookupName The SRV record name used to discover the LDAP services.
	 * @return And InitialDirContext object that is connected to a discoverd LDAP service.
	 * @throws Exception
	 */
	protected InitialDirContext getDirContext(String lookupName) throws Exception
	{
	
		// try the configured servers first
		InitialDirContext ctx = null;
		
		Lookup lu = LookupFactory.getFactory().getInstance(new Name(lookupName), Type.SRV);
		lu.setResolver(createExResolver(servers.toArray(new String[servers.size()]),2, 3)); // default retries is 3, limit to 2
		
		final Record[] retRecords = lu.run();
		if (retRecords != null && retRecords.length > 0) {
			
			String ldapURL = createLDAPUrl(retRecords);
			
			final Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
			env.put(Context.PROVIDER_URL, ldapURL);
			env.put(Context.SECURITY_AUTHENTICATION, "none");
			env.put(LDAP_TIMEOUT, DEFAULT_LDAP_TIMEOUT);
			env.put("java.naming.ldap.attributes.binary", "userCertificate, usercertificate");
			
			ctx =  new InitialDirContext(env);
		}
		
		return ctx;
	}
	
	/**
	 * Creates the LDAP connection URLs from a set of SRV records.
	 * @param retRecords SRV records containing the LDAP connection information.
	 * @return List of URLs delimited by a space.
	 */
	protected String createLDAPUrl(Record[] retRecords){
		
		StringBuilder builder = new StringBuilder();
				
		for (Record rec : retRecords){
			SRVRecord srvRec = (SRVRecord)rec;
			
			if (builder.length() > 0)
				builder.append(" ");
			
			String target = srvRec.getTarget().toString();
			
			target = (target.endsWith(".")) ? target.substring(0, target.length() - 1) : target;
			
			String url = "ldap://" + target+ ":" + srvRec.getPort();
			builder.append(url);
		}
		
		return builder.toString();
	}
	
	/**
	 * Creates the resolver used by the DNS lookup.
	 * @param servers Array or DNS servers 
	 * @param retries Number of times to retry the lookup if the case of failure
	 * @param timeout Timeout in seconds to wait for a response for a DNS query
	 * @return Configured DNS resolver object
	 */
	protected ExtendedResolver createExResolver(String[] servers, int retries, int timeout){
		ExtendedResolver retVal = null;
		try{
			retVal = new ExtendedResolver(servers);
			retVal.setRetries(retries);
			retVal.setTimeout(timeout);
			retVal.setTCP(false);
		}catch (UnknownHostException e) {/* no-op */}
		
		return retVal;
		
	}
	
	/**
	 * Gets the base DNs for a connected LDAP context
	 * @param ctx The LDAP connection context.
	 * @return List of string representing the base DNs of the LDAP server.
	 */
	protected List<String> getBaseNamingContexts(InitialDirContext ctx){
		
		List<String> dNs = new ArrayList<String>();
		
		try
		{		
			SearchControls ctls = new SearchControls();
			ctls.setReturningObjFlag(true);
			ctls.setSearchScope(SearchControls.OBJECT_SCOPE);
			ctls.setReturningAttributes(new String[] {BASE_DN_ATTRIBUTE});
			
			NamingEnumeration<SearchResult> objResults = ctx.search("",  "objectclass=*", ctls);
			while (objResults != null && objResults.hasMore()){
				
				final SearchResult objEntry = objResults.nextElement();
				final Attributes objAttributes = objEntry.getAttributes();
				if (objAttributes != null) {
					final Attribute objAttribute = objAttributes.get(BASE_DN_ATTRIBUTE);
					NamingEnumeration<? extends Object> allValues = objAttribute.getAll();
	
					while(allValues.hasMoreElements()) 
						dNs.add((String) allValues.nextElement());
				}
				
				
			}
			
			if (dNs.isEmpty())
				LOGGER.warn("No base DNs could be located for LDAP context");
		}
		catch (Exception e)
		{
			// no naming contexts could be located or query error
			LOGGER.warn("ERROR looking up base DNs for LDAP context", e);
		}
		return dNs;
	}
	
	/**
	 * Gets the search controls for searching the LDAP server.  The default controls use SUBTREE_SCOPE and 
	 * return the userSMIMECertificate attribute.
	 * @return A search control object.
	 */
	protected SearchControls getDefaultSearchControls() {
		SearchControls ctls = new SearchControls();
		ctls.setReturningObjFlag(true);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		ctls.setReturningAttributes(new String[] {CERT_ATTRIBUTE, CERT_ATTRIBUTE_BINARY});
		return ctls;
	}
	
	/**
	 * Closes a connected LDAP context.
	 * @param dirContext The context to close.
	 */
	protected void closeDirContext(DirContext dirContext) {
		if (dirContext != null) {
			try {
				dirContext.close();
			} catch (NamingException e) {
				// Not fatal since we're just trying to close a connection
			}
		}

	}
}
