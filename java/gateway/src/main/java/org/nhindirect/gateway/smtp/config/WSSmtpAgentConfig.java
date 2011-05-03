package org.nhindirect.gateway.smtp.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


import org.nhind.config.Anchor;
import org.nhind.config.ConfigurationServiceProxy;
import org.nhind.config.Domain;
import org.nhind.config.Setting;
import org.nhindirect.gateway.smtp.DomainPostmaster;
import org.nhindirect.gateway.smtp.MessageProcessingSettings;
import org.nhindirect.gateway.smtp.NotificationProducer;
import org.nhindirect.gateway.smtp.NotificationSettings;
import org.nhindirect.gateway.smtp.ProcessBadMessageSettings;
import org.nhindirect.gateway.smtp.ProcessIncomingSettings;
import org.nhindirect.gateway.smtp.ProcessOutgoingSettings;
import org.nhindirect.gateway.smtp.RawMessageSettings;
import org.nhindirect.gateway.smtp.SmtpAgent;
import org.nhindirect.gateway.smtp.SmtpAgentError;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.SmtpAgentSettings;
import org.nhindirect.gateway.smtp.config.cert.impl.provider.ConfigServiceCertificateStoreProvider;
import org.nhindirect.gateway.smtp.module.SmtpAgentModule;
import org.nhindirect.gateway.smtp.provider.DefaultSmtpAgentProvider;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.DefaultCertStoreCachePolicy;
import org.nhindirect.stagent.cert.impl.EmployLdapAuthInformation;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.LdapStoreConfiguration;
import org.nhindirect.stagent.cert.impl.provider.DNSCertStoreProvider;
import org.nhindirect.stagent.cert.impl.provider.KeyStoreCertificateStoreProvider;
import org.nhindirect.stagent.cert.impl.provider.LdapCertificateStoreProvider;
import org.nhindirect.stagent.module.AgentModule;
import org.nhindirect.stagent.module.PrivateCertStoreModule;
import org.nhindirect.stagent.module.PublicCertStoreModule;
import org.nhindirect.stagent.module.TrustAnchorModule;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.nhindirect.stagent.trust.provider.MultiDomainTrustAnchorResolverProvider;
import org.nhindirect.stagent.trust.provider.UniformTrustAnchorResolverProvider;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

public class WSSmtpAgentConfig implements SmtpAgentConfig 
{
	private static final String STORE_TYPE_WS = "WS";
	private static final String STORE_TYPE_LDAP = "LDAP";
	private static final String STORE_TYPE_KEYSTORE = "keystore";
	private static final String STORE_TYPE_DNS = "DNS";
	
	private static final String ANCHOR_RES_TYPE_UNIFORM = "uniform";
	private static final String ANCHOR_RES_TYPE_MULTIDOMAIN = "multidomain";
	
	private static final String MESSAGE_SETTING_RAW = "Raw";
	private static final String MESSAGE_SETTING_INCOMING = "Incoming";
	private static final String MESSAGE_SETTING_OUTGOING = "Outgoing";
	private static final String MESSAGE_SETTING_BAD = "Bad";
	
	protected Collection<String> domains;
	protected Map<String, DomainPostmaster> domainPostmasters;


	
	@Inject(optional=true)
	private Provider<SmtpAgent> smtpAgentProvider;

	@Inject
	private Provider<NHINDAgent> agentProvider;
	
	protected Module certAnchorModule;
	protected Module publicCertModule;
	protected Module privateCertModule;
	
	private RawMessageSettings rawSettings;
	private ProcessIncomingSettings incomingSettings;
	private ProcessOutgoingSettings outgoingSettings;
	private ProcessBadMessageSettings badSettings;
	private NotificationProducer notificationProducer;	
	private Collection<Provider<CertificateResolver>> resolverProviders;
	
	private final ConfigurationServiceProxy cfService;
	
	private X509Certificate certFromData(byte[] data) throws SmtpAgentException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		X509Certificate cert = null;
        
        try
        {        	
        	 cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);        	
        }
        catch (CertificateException e)
        {
        	throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "Invalid certificate data: " + e.getMessage(), e);
        }
        finally
        {	
        	try {bais.close();}
        	catch (IOException e) {/*no op*/}
        }
        return cert;
	}
	
	/**
	 * Construct and configuration component with the location of the configuration file and an optional provider for creating
	 * instances of the security and trust anchor.
	 * @param configFile The full path of the XML configuration file.
	 * @param agentProvider An option provider used for creating instances of the security and trust agent.  If the provider is
	 * null, a default provider is used.
	 */
	public WSSmtpAgentConfig(URL configServiceLocation, Provider<NHINDAgent> agentProvider)
	{
		resolverProviders = new ArrayList<Provider<CertificateResolver>>();
		this.agentProvider = agentProvider;		
		
		cfService = new ConfigurationServiceProxy(configServiceLocation.toExternalForm());
	}
		
	/**
	 * {@inheritDoc}
	 */
	public Injector getAgentInjector()
	{
		return buildAgentInjector();
	}	
	
	private Injector buildAgentInjector()
	{
		// build the domain list and trust anchors
		buildDomains();
		
		// build the public cert store
		buildPublicCertStore();
		
		// build the private cert store
		buildPrivateCertStore();
		
		// build the MDN settings
		buildMDNSettings();

		// build raw message settings
		buildMessageSettings(MESSAGE_SETTING_RAW);
		
		// build incoming message settings
		buildMessageSettings(MESSAGE_SETTING_INCOMING);
		
		// build outgoing message settings
		buildMessageSettings(MESSAGE_SETTING_OUTGOING);
		
		// build bad message settings
		buildMessageSettings(MESSAGE_SETTING_BAD);
		
		SmtpAgentSettings settings = new SmtpAgentSettings(domainPostmasters, rawSettings, outgoingSettings,
				incomingSettings, badSettings, notificationProducer);
		
		if (smtpAgentProvider == null)
			smtpAgentProvider = new DefaultSmtpAgentProvider(settings);
		
		AgentModule agentModule;
		if (agentProvider == null)
			agentModule = AgentModule.create(domains, publicCertModule, privateCertModule, certAnchorModule);
		else
			agentModule = AgentModule.create(agentProvider);
			
		return Guice.createInjector(agentModule, SmtpAgentModule.create(smtpAgentProvider));		
	
	}
	
	private void buildDomains()
	{
		domains = new ArrayList<String>();
		domainPostmasters = new HashMap<String, DomainPostmaster>();
		Domain[] lookedupDomains = null;
		
		// get the domain list first
		try
		{
			int domainCount = cfService.getDomainCount();
		
			lookedupDomains = cfService.listDomains(null, domainCount);
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting domains list: " + e.getMessage(), e);
		}
		
		if (lookedupDomains != null)
		{
			for (Domain dom : lookedupDomains)
			{
				domains.add(dom.getDomainName());
				try
				{
					domainPostmasters.put(dom.getDomainName().toUpperCase(Locale.getDefault()), 
							new DomainPostmaster(dom.getDomainName(), new InternetAddress(dom.getPostMasterEmail())));
				}
				catch (AddressException e) {}								
			}			
		}
		
		if (domains.size() == 0)
			throw new SmtpAgentException(SmtpAgentError.MissingDomains);
		
		// now get the trust anchors
		buildTrustAnchorResolver();
	}
	
	public void buildTrustAnchorResolver()
	{
		Provider<TrustAnchorResolver> provider = null;
		Map<String, Collection<X509Certificate>> incomingAnchors = new HashMap<String, Collection<X509Certificate>>();
		Map<String, Collection<X509Certificate>> outgoingAnchors = new HashMap<String, Collection<X509Certificate>>();
		
		/* 
		 * first determine how anchors are stored... possibilities are LDAP, keystore, and WS
		 * 
		 */
		Setting setting = null;
		String storeType;
		String resolverType;
		try
		{
			setting = cfService.getSettingByName("AnchorStoreType");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor store type: " + e.getMessage(), e);
		}
		
		if (setting == null || setting.getValue() == null || setting.getValue().isEmpty())
			storeType = STORE_TYPE_WS; // default to WS
		else
			storeType = setting.getValue();
		
		// if the store type is anything other than WS, then we need to get the anchor names so we can look them up in the repository
		if (!storeType.equalsIgnoreCase(STORE_TYPE_WS))
		{
			getAnchorsFromNonWS(incomingAnchors, outgoingAnchors, storeType);
			
		}
		else
		{
			// hit up the web service for each domains anchor
			for (String domain : domains)
			{
				try
				{
					Anchor[] anchors = cfService.getAnchorsForOwner(domain, null);
				
					if (anchors != null && anchors.length > 0)
					{
						Collection<X509Certificate> incomingAnchorsToAdd = new ArrayList<X509Certificate>();
						Collection<X509Certificate> outgoingAnchorsToAdd = new ArrayList<X509Certificate>();
						for (Anchor anchor : anchors)
						{
							X509Certificate anchorToAdd = certFromData(anchor.getData());
							if (anchor.isIncoming())
								incomingAnchorsToAdd.add(anchorToAdd);
							if (anchor.isOutgoing())
								outgoingAnchorsToAdd.add(anchorToAdd);
						}
						incomingAnchors.put(domain, incomingAnchorsToAdd);
						outgoingAnchors.put(domain, outgoingAnchorsToAdd);
					}
				}
				catch (SmtpAgentException e)
				{
					// rethrow
					throw e;
				}
				catch (Exception e)
				{
					throw new SmtpAgentException(SmtpAgentError.InvalidTrustAnchorSettings, 
							"WebService error getting trust anchors for domain " + domain + ":" + e.getMessage(), e);
				}
			}
		}
		
		try
		{
			setting = cfService.getSettingByName("AnchorResolverType");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor resolver type: " + e.getMessage(), e);
		}		
		
		if (incomingAnchors.size() == 0)
			throw new SmtpAgentException(SmtpAgentError.InvalidTrustAnchorSettings, "No incoming trust anchors defined.");
		
		if (setting == null || setting.getValue() == null || setting.getValue().isEmpty())
			resolverType = ANCHOR_RES_TYPE_UNIFORM; // default to unifor
		else
			resolverType = setting.getValue();
		
		
		
		if (resolverType.equalsIgnoreCase(ANCHOR_RES_TYPE_UNIFORM))
		{
			// this is uniform... doesn't really matter what we use for incoming or outgoing because in theory they should be
			// the same... just get the first collection in the incoming map
			provider = new UniformTrustAnchorResolverProvider(incomingAnchors.values().iterator().next());
		}
		else if (resolverType.equalsIgnoreCase(ANCHOR_RES_TYPE_MULTIDOMAIN))
		{
			if (outgoingAnchors.size() == 0)
				throw new SmtpAgentException(SmtpAgentError.InvalidTrustAnchorSettings, "No outgoing trust anchors defined.");
			provider = new MultiDomainTrustAnchorResolverProvider(incomingAnchors, outgoingAnchors);
		}
		else
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidTrustAnchorSettings);
		}
		
		certAnchorModule = TrustAnchorModule.create(provider);		
	}
	
	private void getAnchorsFromNonWS(Map<String, Collection<X509Certificate>> incomingAnchors, 
			Map<String, Collection<X509Certificate>> outgoingAnchors, String storeType)
	{		
		
		// get the anchor aliases for each domain... better performance to do one web call
		// little more code here, but better to take hit here instead of over the wire
		ArrayList<String> incomingLookups = new ArrayList<String>();
		ArrayList<String> outgoingLookups = new ArrayList<String>();
		for (String domain : domains)
		{
			incomingLookups.add(domain + "IncomingAnchorAliases");
			outgoingLookups.add(domain + "OutgoingAnchorAliases");
		}
		
		Setting[] incomingAliasSettings;
		Setting[] outgoingAliasSettings;
		try
		{
			incomingAliasSettings = cfService.getSettingsByNames(incomingLookups.toArray(new String[incomingLookups.size()]));
			outgoingAliasSettings = cfService.getSettingsByNames(outgoingLookups.toArray(new String[outgoingLookups.size()]));
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor aliases: " + e.getMessage(), e);
		}	
		
		// get the anchors from the correct store
		if (storeType.equalsIgnoreCase(STORE_TYPE_KEYSTORE))
		{
			Setting file;
			Setting pass;
			Setting privKeyPass;
			try
			{
				file = cfService.getSettingByName("AnchorKeyStoreFile");
				pass = cfService.getSettingByName("AnchorKeyStoreFilePass");
				privKeyPass = cfService.getSettingByName("AnchorKeyStorePrivKeyPass");
			}
			catch (Exception e)
			{
				throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor key store settings: " + e.getMessage(), e);
			}
			
			KeyStoreCertificateStore store = new KeyStoreCertificateStore((file == null) ? null : file.getValue(), 
					(pass == null) ? "DefaultFilePass" : pass.getValue(), (privKeyPass == null) ? "DefaultKeyPass" : privKeyPass.getValue());
			
			// get incoming anchors
			for (Setting setting : incomingAliasSettings)				
			{
				Collection<X509Certificate> certs = new ArrayList<X509Certificate>();				
				String aliases[] = setting.getValue().split(",");
				for (String alias : aliases)
				{
					X509Certificate cert = store.getByAlias(alias);
					if (cert != null)
					{
						certs.add(cert);
					}
				}				
				incomingAnchors.put(setting.getName().substring(0, setting.getName().lastIndexOf("IncomingAnchorAliases")), certs);
			}
						
			// get outgoing anchors
			for (Setting setting : outgoingAliasSettings)				
			{
				Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
				String aliases[] = setting.getValue().split(",");
				for (String alias : aliases)
				{
					X509Certificate cert = store.getByAlias(alias);
					if (cert != null)
					{
						certs.add(cert);
					}
				}				
				outgoingAnchors.put(setting.getName().substring(0, setting.getName().lastIndexOf("OutgoingAnchorAliases")), certs);
			}			
		}	
		else if (storeType.equalsIgnoreCase(STORE_TYPE_LDAP))
		{	

			
			LDAPCertificateStore ldapCertificateStore = (LDAPCertificateStore) buildLdapCertificateStoreProvider("TrustAnchor", "LDAPTrustAnchorStore").get();
		    // get incoming anchors
            for (Setting setting : incomingAliasSettings)
            {
                Collection<X509Certificate> certs = new ArrayList<X509Certificate>();      
                String aliases[] = setting.getValue().split(",");
                for (String alias : aliases)
                {
                    //TODO what if 2nd entry has no certs? Fail?
                    //each alias could have multiple certificates
                    certs.addAll(ldapCertificateStore.getCertificates(alias));                    
                }             
                incomingAnchors.put(setting.getName().substring(0, setting.getName().lastIndexOf("IncomingAnchorAliases")), certs);
            }
                        
            // get outgoing anchors
            for (Setting setting : outgoingAliasSettings)
            {
                Collection<X509Certificate> certs = new ArrayList<X509Certificate>();      
                String aliases[] = setting.getValue().split(",");
                for (String alias : aliases)
                {
                    //TODO what if 2nd entry has no certs? Fail?
                    //each alias could have multiple certificates
                    certs.addAll(ldapCertificateStore.getCertificates(alias));                    
                }             
                outgoingAnchors.put(setting.getName().substring(0, setting.getName().lastIndexOf("OutgoingAnchorAliases")), certs);
            }       
		}
		
	}
	
	protected LdapCertificateStoreProvider buildLdapCertificateStoreProvider(String type, String cacheStoreName)
	{
	    //required
		Setting ldapURLSetting;
		Setting ldapSearchBaseSetting;
		Setting ldapSearchAttrSetting;
		Setting ldapCertAttrSetting;
		Setting ldapCertFormatSetting;
        //optional	    
	    Setting ldapUserSetting;
	    Setting ldapPasswordSetting;
	    Setting ldapConnTimeoutSetting;	   
	    Setting ldapCertPassphraseSetting;	
		try
		{
			ldapURLSetting = cfService.getSettingByName(type +  "LDAPUrl");
			ldapSearchBaseSetting = cfService.getSettingByName(type + "LDAPSearchBase");
			ldapSearchAttrSetting = cfService.getSettingByName(type + "LDAPSearchAttr");
			ldapCertAttrSetting = cfService.getSettingByName(type + "LDAPCertAttr");
			ldapCertFormatSetting = cfService.getSettingByName(type + "LDAPCertFormat");
	        //optional	    
		    ldapUserSetting = cfService.getSettingByName(type +  "LDAPUser");
		    ldapPasswordSetting =  cfService.getSettingByName(type +  "LDAPPassword");
		    ldapConnTimeoutSetting =  cfService.getSettingByName(type +  "LDAPConnTimeout");	   
		    ldapCertPassphraseSetting =  cfService.getSettingByName(type +  "LDAPCertPassphrase");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting LDAP store settings: " + e.getMessage(), e);
		}
        if (ldapURLSetting == null || ldapURLSetting.getValue() == null || ldapURLSetting.getValue().isEmpty())
        	 throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "Missing LDAP URL");
        
		String ldapSearchBase = (ldapSearchBaseSetting == null) ? null : ldapSearchBaseSetting.getValue();
		String ldapSearchAttr = (ldapSearchAttrSetting == null) ? null : ldapSearchAttrSetting.getValue();
		String ldapCertAttr = (ldapCertAttrSetting == null) ? null : ldapCertAttrSetting.getValue();
		String ldapCertFormat = (ldapCertFormatSetting == null) ? null : ldapCertFormatSetting.getValue();
        String[] ldapURL = ldapURLSetting.getValue().split(",");

        if(ldapURL[0].isEmpty() || ldapSearchBase.isEmpty() || ldapSearchAttr.isEmpty() ||
                ldapCertAttr.isEmpty() || ldapCertFormat.isEmpty())
        {
            throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "Missing required LDAP parameters.");
        }        
        	    
	    String ldapUser = (ldapUserSetting == null) ? null : ldapUserSetting.getValue();
	    String ldapPassword =  (ldapPasswordSetting == null) ? null : ldapPasswordSetting.getValue();
	    String ldapConnTimeout =  (ldapConnTimeoutSetting == null) ? null : ldapConnTimeoutSetting.getValue();	   
	    String ldapCertPassphrase =  (ldapCertPassphraseSetting == null) ? null : ldapCertPassphraseSetting.getValue();    
	    
	    
	    if(ldapCertFormat.equalsIgnoreCase("pkcs12") && ( ldapCertPassphrase == null || ldapCertPassphrase.isEmpty()))
	    {
	        throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat);
	    }
	    LdapStoreConfiguration ldapStoreConfiguration = new LdapStoreConfiguration(ldapURL, ldapSearchBase, ldapSearchAttr, ldapCertAttr, ldapCertFormat);
	    if(ldapUser != null && !ldapUser.isEmpty() && ldapPassword != null && !ldapPassword.isEmpty())
	    {
	        ldapStoreConfiguration.setEmployLdapAuthInformation(new EmployLdapAuthInformation(ldapUser, ldapPassword));
	    }
	    if(ldapConnTimeout != null && !ldapConnTimeout.isEmpty())
	    {
	        ldapStoreConfiguration.setLdapConnectionTimeOut(ldapConnTimeout);
	    }
	    if(ldapCertPassphrase != null && !ldapCertPassphrase.isEmpty())
	    {
	        ldapStoreConfiguration.setLdapCertPassphrase(ldapCertPassphrase);
	    }

	    String passphrase = (ldapCertPassphrase == null || ldapCertPassphrase.isEmpty()) ? "DefaultPassphrase" : ldapCertPassphrase;
	    
	    LdapCertificateStoreProvider ldapCertificateStoreProvider = new LdapCertificateStoreProvider(ldapStoreConfiguration,
	    		new KeyStoreCertificateStore(new File(cacheStoreName),passphrase, passphrase), new DefaultCertStoreCachePolicy());
	    return ldapCertificateStoreProvider;
	}	
	
	/*
	 * Build the certificate resolver for public certificates
	 */
	@SuppressWarnings("unchecked")
	private void buildPublicCertStore()
	{
		Provider<CertificateResolver> resolverProvider = null;
		
		Setting setting = null;
		String storeTypes;
		try
		{
			setting = cfService.getSettingByName("PublicStoreType");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting public store type: " + e.getMessage(), e);
		}		
		
		if (setting == null || setting.getValue() == null || setting.getValue().isEmpty())
			storeTypes = STORE_TYPE_DNS; // default to DNS
		else
			storeTypes = setting.getValue();
		
		/*
		 * KeyStore based resolver
		 */
		String[] types = storeTypes.split(",");
		for (String storeType : types)
		{
			if (storeType.equalsIgnoreCase(STORE_TYPE_KEYSTORE))
			{
				Setting file;
				Setting pass;
				Setting privKeyPass;
				try
				{
					file = cfService.getSettingByName("PublicStoreFile");
					pass = cfService.getSettingByName("PublicStoreFilePass");
					privKeyPass = cfService.getSettingByName("PublicStorePrivKeyPass");
				}
				catch (Exception e)
				{
					throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting public store file settings: " + e.getMessage(), e);
				}
				
				resolverProvider = new KeyStoreCertificateStoreProvider((file == null) ? "PublicStoreKeyFile" : file.getValue(), 
						(pass == null) ? "DefaultFilePass" : pass.getValue(), (privKeyPass == null) ? "DefaultKeyPass" : privKeyPass.getValue());
			}
			/*
			 * DNS resolver
			 */			
			else if(storeType.equalsIgnoreCase(STORE_TYPE_DNS))
			{
				resolverProvider = new DNSCertStoreProvider(Collections.EMPTY_LIST, 
						new KeyStoreCertificateStore(new File("DNSCacheStore"), "DefaultFilePass", "DefaultKeyPass"), new DefaultCertStoreCachePolicy());								
			}
			/*
			 * Web Services
			 */
			else if (storeType.equalsIgnoreCase(STORE_TYPE_WS))
			{
				resolverProvider = new ConfigServiceCertificateStoreProvider(cfService, 
						new KeyStoreCertificateStore(new File("WSPublicCacheStore"), "DefaultFilePass", "DefaultKeyPass"), new DefaultCertStoreCachePolicy());
			}
			/*
			 * Default to DNS with a default cache policy
			 */
			else
			{
				resolverProvider = new DNSCertStoreProvider(Collections.EMPTY_LIST, 
						new KeyStoreCertificateStore(new File("DNSCacheStore")), new DefaultCertStoreCachePolicy());			
			}
			
			resolverProviders.add(resolverProvider);
		}
		
		publicCertModule = new PublicCertStoreModule(resolverProviders);
	}	
		
	protected void buildPrivateCertStore()
	{
		Provider<CertificateResolver> resolverProvider = null;
		
		
		Setting setting = null;
		String storeType;
		try
		{
			setting = cfService.getSettingByName("PrivateStoreType");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting public store type: " + e.getMessage(), e);
		}			
		
		if (setting == null || setting.getValue() == null || setting.getValue().isEmpty())
			storeType = STORE_TYPE_WS; // default to WS
		else
			storeType = setting.getValue();		
		

		/*
		 * KeyStore based resolver
		 */
		if (storeType.equalsIgnoreCase(STORE_TYPE_KEYSTORE))
		{
			Setting file;
			Setting pass;
			Setting privKeyPass;
			try
			{
				file = cfService.getSettingByName("PrivateStoreFile");
				pass = cfService.getSettingByName("PrivateStoreFilePass");
				privKeyPass = cfService.getSettingByName("PrivateStorePrivKeyPass");
			}
			catch (Exception e)
			{
				throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting private store file settings: " + e.getMessage(), e);
			}
			
			resolverProvider = new KeyStoreCertificateStoreProvider((file == null) ? null : file.getValue(), 
					(pass == null) ? null : pass.getValue(), (privKeyPass == null) ? null : privKeyPass.getValue());			

		}
		else if(storeType.equalsIgnoreCase(STORE_TYPE_LDAP))
		{
		    resolverProvider = buildLdapCertificateStoreProvider("PrivateStore", "LDAPPrivateCertStore");
		}
		else if (storeType.equalsIgnoreCase(STORE_TYPE_WS))
		{
			resolverProvider = new ConfigServiceCertificateStoreProvider(cfService, 
					new KeyStoreCertificateStore(new File("WSPrivCacheStore"), "DefaultFilePass", "DefaultKeyPass"), new DefaultCertStoreCachePolicy());
		}
		else
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidPrivateCertStoreSettings);
		}
	
		privateCertModule = new PrivateCertStoreModule(resolverProvider);

	}	
	
	private void buildMDNSettings()
	{
		Setting autoResponseSettings;
		Setting prodNameSetting;
		Setting textSetting;
		try
		{
			autoResponseSettings = cfService.getSettingByName("MDNAutoResponse");
			prodNameSetting = cfService.getSettingByName("MDNProdName");
			textSetting = cfService.getSettingByName("MDNText");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting MDN settings: " + e.getMessage(), e);
		}
		

		boolean autoResponse = (autoResponseSettings == null) ? true : Boolean.parseBoolean(autoResponseSettings.getValue());
		String prodName = (prodNameSetting == null) ? "" : prodNameSetting.getValue();
		String text = (textSetting == null) ? "" : textSetting.getValue();				
		
		notificationProducer = new NotificationProducer(new NotificationSettings(autoResponse, prodName, text));

	}	
	
	private void buildMessageSettings(String type)
	{
		Setting folderSettings;
		try
		{
			folderSettings = cfService.getSettingByName(type + "MessageSaveFolder");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting " + type + " message settings: " + e.getMessage(), e);
		}
		
		String saveFolder = (folderSettings == null) ? null : folderSettings.getValue();

		MessageProcessingSettings settings = null;
		if (type.equalsIgnoreCase(MESSAGE_SETTING_RAW))
			settings = rawSettings = new RawMessageSettings();
		else if (type.equalsIgnoreCase(MESSAGE_SETTING_INCOMING))
			settings = incomingSettings = new ProcessIncomingSettings();			
		else if (type.equalsIgnoreCase(MESSAGE_SETTING_OUTGOING))
			settings = outgoingSettings = new ProcessOutgoingSettings();	
		else if (type.equalsIgnoreCase(MESSAGE_SETTING_BAD))
			settings = badSettings = new ProcessBadMessageSettings();
		
		if (saveFolder != null && settings != null)
			settings.setSaveMessageFolder(new File(saveFolder));
	}
}

