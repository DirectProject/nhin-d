package org.nhindirect.stagent.cert.impl.provider;

import java.util.Hashtable;

import javax.naming.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.stagent.NHINDException;
import org.nhindirect.stagent.cert.CertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.CertificateStore;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.LdapCertUtilImpl;
import org.nhindirect.stagent.cert.impl.LdapEnvironment;
import org.nhindirect.stagent.cert.impl.LdapStoreConfiguration;

import com.google.inject.Provider;

public class LdapCertificateStoreProvider implements Provider<CertificateResolver>
{
	private static final Log LOGGER = LogFactory.getFactory().getInstance(LdapCertificateStoreProvider.class);
	private static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String LDAP_TIMEOUT = "com.sun.jndi.ldap.read.timeout";
	private LdapStoreConfiguration ldapConfiguration;
	private CertificateStore bootstrapStore;
	private CertStoreCachePolicy policy;
	
	public LdapCertificateStoreProvider(LdapStoreConfiguration ldapConfiguration,  
			CertificateStore bootstrapStore, CertStoreCachePolicy policy) {
		this.ldapConfiguration = ldapConfiguration;
		this.bootstrapStore = bootstrapStore;
		this.policy = policy;
	}

	public CertificateResolver get() {
		final Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
		String[] ldapURLs = ldapConfiguration.getLdapURLs();
		String ldapProviderUrl=null;
		for (String ldapURL : ldapURLs) {
			if (ldapProviderUrl==null) {
				ldapProviderUrl = ldapURL +" ";
			}
			else {
				ldapProviderUrl += ldapURL +" ";
			}
		}
		env.put(Context.PROVIDER_URL, ldapProviderUrl);
		if(ldapConfiguration.getLdapConnectionTimeOut()!=null) {
			try
			{
				int connectionTimeOut = Integer.parseInt(ldapConfiguration.getLdapConnectionTimeOut());
				if(connectionTimeOut<1) {
					LOGGER.error("Connection timeout must be a positive integer");
					throw new NHINDException("Invalid value for the LDAP connection timeout");
				}
			}
			catch (NumberFormatException nfe)
			{
				LOGGER.error("Connection timeout string is not a valid number.");
				throw new NHINDException("Invalid value for the LDAP connection timeout", nfe);
			}
			env.put(LDAP_TIMEOUT, ldapConfiguration.getLdapConnectionTimeOut());
		}
		if(ldapConfiguration.getEmployLdapAuthInformation()!=null) {
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, ldapConfiguration.getEmployLdapAuthInformation().getLdapPrincipal());
			env.put(Context.SECURITY_CREDENTIALS, ldapConfiguration.getEmployLdapAuthInformation().getLdapPassword());
		}
		else {
			env.put(Context.SECURITY_AUTHENTICATION, "none");
		}
		
		LdapEnvironment ldapEnvironment = new LdapEnvironment(env, ldapConfiguration.getReturningCertAttribute(), ldapConfiguration.getLdapSearchBase(), ldapConfiguration.getLdapSearchAttribute());
		LdapCertUtilImpl ldapcertUtilImpl = new LdapCertUtilImpl(ldapEnvironment, ldapConfiguration.getLdapCertPassphrase(), ldapConfiguration.getCertificateFormat());
		return new LDAPCertificateStore(ldapcertUtilImpl, bootstrapStore, policy);
	}
}
