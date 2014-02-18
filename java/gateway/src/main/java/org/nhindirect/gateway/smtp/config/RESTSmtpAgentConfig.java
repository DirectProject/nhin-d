package org.nhindirect.gateway.smtp.config;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


import org.apache.commons.io.IOUtils;

import org.nhind.config.module.DefaultRESTServiceModule;
import org.nhind.config.rest.AnchorService;
import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.TrustBundleService;
import org.nhindirect.common.rest.ServiceSecurityManager;
import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.CertPolicyGroupDomainReltn;
import org.nhindirect.config.model.CertPolicyGroupUse;
import org.nhindirect.config.model.CertPolicyUse;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.Setting;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.TrustBundleAnchor;
import org.nhindirect.config.model.TrustBundleDomainReltn;
import org.nhindirect.gateway.smtp.DomainPostmaster;
import org.nhindirect.gateway.smtp.MessageProcessingSettings;
import org.nhindirect.gateway.smtp.NotificationProducer;
import org.nhindirect.gateway.smtp.NotificationSettings;
import org.nhindirect.gateway.smtp.ProcessBadMessageSettings;
import org.nhindirect.gateway.smtp.ProcessIncomingSettings;
import org.nhindirect.gateway.smtp.ProcessOutgoingSettings;
import org.nhindirect.gateway.smtp.RawMessageSettings;
import org.nhindirect.gateway.smtp.SmtpAgentError;
import org.nhindirect.gateway.smtp.SmtpAgentException;
import org.nhindirect.gateway.smtp.config.cert.impl.ConfigServiceCertificateStore;
import org.nhindirect.gateway.smtp.config.cert.impl.ConfigServiceRESTCertificateStore;
import org.nhindirect.gateway.smtp.config.cert.impl.provider.ConfigServiceRESTCertificateStoreProvider;
import org.nhindirect.policy.PolicyExpression;
import org.nhindirect.policy.PolicyLexicon;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.nhindirect.policy.PolicyParseException;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.cert.CertificateResolver;
import org.nhindirect.stagent.cert.impl.DNSCertificateStore;
import org.nhindirect.stagent.cert.impl.EmployLdapAuthInformation;
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cert.impl.LDAPCertificateStore;
import org.nhindirect.stagent.cert.impl.LdapStoreConfiguration;
import org.nhindirect.stagent.cert.impl.provider.DNSCertStoreProvider;
import org.nhindirect.stagent.cert.impl.provider.KeyStoreCertificateStoreProvider;
import org.nhindirect.stagent.cert.impl.provider.LdapCertificateStoreProvider;
import org.nhindirect.stagent.cert.impl.provider.PublicLdapCertificateStoreProvider;
import org.nhindirect.stagent.module.PrivateCertStoreModule;
import org.nhindirect.stagent.module.PrivatePolicyResolverModule;
import org.nhindirect.stagent.module.PublicCertStoreModule;
import org.nhindirect.stagent.module.PublicPolicyResolverModule;
import org.nhindirect.stagent.module.TrustAnchorModule;
import org.nhindirect.stagent.module.TrustPolicyResolverModule;

import org.nhindirect.stagent.policy.impl.provider.DomainPolicyResolverProvider;
import org.nhindirect.stagent.trust.TrustAnchorResolver;
import org.nhindirect.stagent.trust.provider.MultiDomainTrustAnchorResolverProvider;
import org.nhindirect.stagent.trust.provider.UniformTrustAnchorResolverProvider;



import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class RESTSmtpAgentConfig extends WSSmtpAgentConfig
{
	protected final DomainService domainService;
	protected final AnchorService anchorService;	
	protected final CertPolicyService policyService;	
	protected final SettingService settingsService;
	protected final TrustBundleService trustBundleService;
	protected final CertificateService certificateService;
	
	protected Collection<Domain> lookedupRESTServiceDomains;
	
	public RESTSmtpAgentConfig(URL configServiceLocation, Provider<NHINDAgent> agentProvider, Provider<ServiceSecurityManager> securityManager)
	{
		super(configServiceLocation, agentProvider);
		
		final Injector configInjector = Guice.createInjector(DefaultRESTServiceModule.create(configServiceLocation.toExternalForm(), securityManager));
		
		domainService = configInjector.getInstance(DomainService.class); 
		anchorService = configInjector.getInstance(AnchorService.class); 
		policyService = configInjector.getInstance(CertPolicyService.class); 
		settingsService = configInjector.getInstance(SettingService.class); 
		trustBundleService = configInjector.getInstance(TrustBundleService.class); 
		certificateService = configInjector.getInstance(CertificateService.class); 
		
		ConfigServiceRESTCertificateStore.initJVMParams();
	}
	
	@Override
	protected void buildPolicyResolvers()
	{
		final Map<String, Collection<PolicyExpression>> incomingPrivatePolicies = new HashMap<String, Collection<PolicyExpression>>();
		final Map<String, Collection<PolicyExpression>> outgoingPrivatePolicies = new HashMap<String, Collection<PolicyExpression>>();
		
		final Map<String, Collection<PolicyExpression>> incomingPublicPolicies = new HashMap<String, Collection<PolicyExpression>>();
		final Map<String, Collection<PolicyExpression>> outgoingPublicPolicies = new HashMap<String, Collection<PolicyExpression>>();
	
		final Map<String, Collection<PolicyExpression>> trustPolicies = new HashMap<String, Collection<PolicyExpression>>();
		
		Collection<CertPolicyGroupDomainReltn> domainReltns = null;
		try
		{   
			// get all of the policy group to domain relations... 
			// doing this all in one call for efficiency
			domainReltns = policyService.getPolicyGroupDomainReltns();
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting certificate policy configuration: " + e.getMessage(), e);
		}
		
		if (domainReltns != null)
		{
			for (CertPolicyGroupDomainReltn domainReltn : domainReltns)
			{
				if (domainReltn.getPolicyGroup().getPolicies() != null)
				{
					for (CertPolicyGroupUse policyReltn : domainReltn.getPolicyGroup().getPolicies())
					{						
						if (policyReltn.getPolicyUse().equals(CertPolicyUse.PRIVATE_RESOLVER))
						{
							if (policyReltn.isIncoming())
								addPolicyToMap(incomingPrivatePolicies, domainReltn.getDomain().getDomainName(), policyReltn);
							if (policyReltn.isOutgoing())
								addPolicyToMap(outgoingPrivatePolicies, domainReltn.getDomain().getDomainName(), policyReltn);
						}
						else if (policyReltn.getPolicyUse().equals(CertPolicyUse.PUBLIC_RESOLVER))
						{
							if (policyReltn.isIncoming())
								addPolicyToMap(incomingPublicPolicies, domainReltn.getDomain().getDomainName(), policyReltn);
							if (policyReltn.isOutgoing())
								addPolicyToMap(outgoingPublicPolicies, domainReltn.getDomain().getDomainName(), policyReltn);							
						}
						else if (policyReltn.getPolicyUse().equals(CertPolicyUse.TRUST))
						{
							addPolicyToMap(trustPolicies, domainReltn.getDomain().getDomainName(), policyReltn);
						}	
					}
				}
			}
		}
		publicPolicyResolverModule = PublicPolicyResolverModule.create(new DomainPolicyResolverProvider(incomingPublicPolicies, outgoingPublicPolicies));
		privatePolicyResolverModule = PrivatePolicyResolverModule.create(new DomainPolicyResolverProvider(incomingPrivatePolicies, outgoingPrivatePolicies));
		trustPolicyResolverModule = TrustPolicyResolverModule.create(new DomainPolicyResolverProvider(trustPolicies));
	}	
	
	
	public void addPolicyToMap(Map<String, Collection<PolicyExpression>> policyMap, String domainName, CertPolicyGroupUse policyReltn)
	{
		// check to see if the domain is in the map
		Collection<PolicyExpression> policyExpressionCollection = policyMap.get(domainName);
		if (policyExpressionCollection == null)
		{
			policyExpressionCollection = new ArrayList<PolicyExpression>();
			policyMap.put(domainName, policyExpressionCollection);
		}
		
		final CertPolicy policy = policyReltn.getPolicy();
		final PolicyLexicon lexicon = policy.getLexicon();
		
		final InputStream inStr = new ByteArrayInputStream(policy.getPolicyData());
		
		try
		{
			// grab a parser and compile this policy
			final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(lexicon);
			
			policyExpressionCollection.add(parser.parse(inStr));
		}
		catch (PolicyParseException ex)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "Failed parse policy into policy expression: " + ex.getMessage(), ex);
		}
		finally
		{
			IOUtils.closeQuietly(inStr);
		}
		
	}	
	
	@Override
	protected void buildDomains()
	{
		domains = new ArrayList<String>();
		domainPostmasters = new HashMap<String, DomainPostmaster>();
		
		// get the domain list first
		try
		{
			lookedupRESTServiceDomains = domainService.searchDomains("", null);
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting domains list: " + e.getMessage(), e);
		}
		
		if (lookedupRESTServiceDomains != null)
		{
			for (Domain dom : lookedupRESTServiceDomains)
			{
				domains.add(dom.getDomainName());
				try
				{
					String configuredAddress = (dom.getPostmasterAddress() == null) ? "" : dom.getPostmasterAddress().getEmailAddress();
					configuredAddress = (configuredAddress == null || configuredAddress.trim().isEmpty()) 
						? DomainPostmaster.getDefaultPostmaster(dom.getDomainName()) : configuredAddress;
					
					domainPostmasters.put(dom.getDomainName().toUpperCase(Locale.getDefault()), 
							new DomainPostmaster(dom.getDomainName(), new InternetAddress(configuredAddress)));
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
			setting = settingsService.getSetting("AnchorStoreType");
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
			// trust bundles are shared objects across domains, so just pull the entire bundle list and associate
			// the anchors in the bundles to the appropriate domains as we go... this will not always be the most efficient
			// algorithm, but it most cases it will be when there are several domains configured (in which case this
			// loading algorithm will be much more efficient)
			final Map<String, TrustBundle> bundleMap = new HashMap<String, TrustBundle>();
			try
			{
				final Collection<TrustBundle> bundles = trustBundleService.getTrustBundles(true);
				// put the bundles in a Map by name
				if (bundles != null)
					for (TrustBundle bundle : bundles)
						bundleMap.put(bundle.getBundleName(), bundle);
			}
			catch (Exception e)
			{
				throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat,  
						"WebService error getting trust bundles: " + e.getMessage(), e);
			}
			// hit up the web service for each domains anchor
			for (Domain domain : lookedupRESTServiceDomains)
			{
				try
				{

				
					final Collection<X509Certificate> incomingAnchorsToAdd = new ArrayList<X509Certificate>();
					final Collection<X509Certificate> outgoingAnchorsToAdd = new ArrayList<X509Certificate>();
					
					// get the anchors for the domain
					final Collection<Anchor> anchors = anchorService.getAnchorsForOwner(domain.getDomainName(), false, false, null);
					
					if (anchors != null)
					{
						for (Anchor anchor : anchors)
						{
							final X509Certificate anchorToAdd = certFromData(anchor.getCertificateData());
							if (anchor.isIncoming())
								incomingAnchorsToAdd.add(anchorToAdd);
							if (anchor.isOutgoing())
								outgoingAnchorsToAdd.add(anchorToAdd);
						}

					}
					
					// check to see if there is a bundle associated to this domain
					final Collection<TrustBundleDomainReltn> domainAssocs = trustBundleService.getTrustBundlesByDomain(domain.getDomainName(), false);
					if (domainAssocs != null)
					{
						for (TrustBundleDomainReltn domainAssoc : domainAssocs)
						{
							final TrustBundle bundle = bundleMap.get(domainAssoc.getTrustBundle().getBundleName());
							if (bundle != null && bundle.getTrustBundleAnchors() != null)
							{
								for (TrustBundleAnchor anchor : bundle.getTrustBundleAnchors())
								{
									final X509Certificate anchorToAdd = certFromData(anchor.getAnchorData());
									if (domainAssoc.isIncoming())
										incomingAnchorsToAdd.add(anchorToAdd);
									if (domainAssoc.isOutgoing())
										outgoingAnchorsToAdd.add(anchorToAdd);
								}
							}
						}
					}
					
					incomingAnchors.put(domain.getDomainName(), incomingAnchorsToAdd);
					outgoingAnchors.put(domain.getDomainName(), outgoingAnchorsToAdd);
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
			setting = settingsService.getSetting("AnchorResolverType");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor resolver type: " + e.getMessage(), e);
		}		
		
		if (incomingAnchors.size() == 0 && outgoingAnchors.size() == 0)
			throw new SmtpAgentException(SmtpAgentError.InvalidTrustAnchorSettings, "No trust anchors defined.");
		
		if (setting == null || setting.getValue() == null || setting.getValue().isEmpty())
		{
			// multi domain should be the default... uniform really only makes sense for dev purposes
			resolverType = ANCHOR_RES_TYPE_MULTIDOMAIN; 		
		}
		else
			resolverType = setting.getValue();
		
		
		
		if (resolverType.equalsIgnoreCase(ANCHOR_RES_TYPE_UNIFORM))
		{
			// this is uniform... doesn't really matter what we use for incoming or outgoing because in theory they should be
			// the same... just get the first collection in the incoming map
			if (incomingAnchors.size() > 0)
				provider = new UniformTrustAnchorResolverProvider(incomingAnchors.values().iterator().next());
			else 
				provider = new UniformTrustAnchorResolverProvider(outgoingAnchors.values().iterator().next());
		}
		else if (resolverType.equalsIgnoreCase(ANCHOR_RES_TYPE_MULTIDOMAIN))
		{
			provider = new MultiDomainTrustAnchorResolverProvider(incomingAnchors, outgoingAnchors);
		}
		else
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidTrustAnchorSettings);
		}
		
		certAnchorModule = TrustAnchorModule.create(provider);		
	}	
	
	@Override
	protected void getAnchorsFromNonWS(Map<String, Collection<X509Certificate>> incomingAnchors, 
			Map<String, Collection<X509Certificate>> outgoingAnchors, String storeType)
	{		
		

		ArrayList<String> incomingLookups = new ArrayList<String>();
		ArrayList<String> outgoingLookups = new ArrayList<String>();
		for (String domain : domains)
		{
			incomingLookups.add(domain + "IncomingAnchorAliases");
			outgoingLookups.add(domain + "OutgoingAnchorAliases");
		}
		
		Collection<Setting> incomingAliasSettings = new ArrayList<Setting>();
		Collection<Setting> outgoingAliasSettings = new ArrayList<Setting>();
		for (String lookup : incomingLookups)
		{
			try
			{
				Setting st = settingsService.getSetting(lookup);
				if (st != null)
					incomingAliasSettings.add(st);
			}
			catch (Exception e)
			{
				throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor aliases: " + e.getMessage(), e);
			}
		}
		
		for (String lookup : outgoingLookups)
		{
			try
			{
				Setting st = settingsService.getSetting(lookup);
				if (st != null)
					outgoingAliasSettings.add(st);
			}
			catch (Exception e)
			{
				throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor aliases: " + e.getMessage(), e);
			}
		}
		
		
		// get the anchors from the correct store
		if (storeType.equalsIgnoreCase(STORE_TYPE_KEYSTORE))
		{
			Setting file;
			Setting pass;
			Setting privKeyPass;
			try
			{
				file = settingsService.getSetting("AnchorKeyStoreFile");
				pass = settingsService.getSetting("AnchorKeyStoreFilePass");
				privKeyPass = settingsService.getSetting("AnchorKeyStorePrivKeyPass");
			}
			catch (Exception e)
			{
				throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting anchor key store settings: " + e.getMessage(), e);
			}
			
			KeyStoreCertificateStore store = new KeyStoreCertificateStore((file == null) ? null : file.getValue(), 
					(pass == null) ? "DefaultFilePass" : pass.getValue(), (privKeyPass == null) ? "DefaultKeyPass" : privKeyPass.getValue());
			
			// get incoming anchors
			if (incomingAliasSettings != null)
			{
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
			}
			
			// get outgoing anchors
			if (outgoingAliasSettings != null)
			{
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
		}	
		else if (storeType.equalsIgnoreCase(STORE_TYPE_LDAP))
		{	

			
			LDAPCertificateStore ldapCertificateStore = (LDAPCertificateStore) buildLdapCertificateStoreProvider("TrustAnchor", "LDAPTrustAnchorStore").get();
		    // get incoming anchors
            if (incomingAliasSettings != null)
            {
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
            }          
            
            // get outgoing anchors
            if (outgoingAliasSettings != null)
            {
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
		
	}
	
	@Override
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
			ldapURLSetting = settingsService.getSetting(type +  "LDAPUrl"); 
			ldapSearchBaseSetting = settingsService.getSetting(type +  "LDAPSearchBase"); 
			ldapSearchAttrSetting = settingsService.getSetting(type +  "LDAPSearchAttr"); 
			ldapCertAttrSetting = settingsService.getSetting(type +  "LDAPCertAttr"); 
			ldapCertFormatSetting = settingsService.getSetting(type +  "LDAPCertFormat"); 
	        //optional	    
		    ldapUserSetting = settingsService.getSetting(type +  "LDAPUser"); 
		    ldapPasswordSetting =  settingsService.getSetting(type +  "LDAPPassword"); 
		    ldapConnTimeoutSetting =  settingsService.getSetting(type +  "LDAPConnTimeout");   
		    ldapCertPassphraseSetting =  settingsService.getSetting(type +  "LDAPCertPassphrase"); 
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
	    
	    LdapCertificateStoreProvider ldapCertificateStoreProvider = new LdapCertificateStoreProvider(ldapStoreConfiguration,
	    		null, new LDAPCertificateStore.DefaultLDAPCachePolicy());
	    return ldapCertificateStoreProvider;
	}	
	
	@Override
	@SuppressWarnings("unchecked")
	protected void buildPublicCertStore()
	{
		Provider<CertificateResolver> resolverProvider = null;
		Collection<Provider<CertificateResolver>> resolverProviders = new ArrayList<Provider<CertificateResolver>>();
		
		Setting setting = null;
		String storeTypes;
		try
		{
			setting = settingsService.getSetting("PublicStoreType");
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, "WebService error getting public store type: " + e.getMessage(), e);
		}		
		
		if (setting == null || setting.getValue() == null || setting.getValue().isEmpty())
			storeTypes = STORE_TYPE_DNS + "," + STORE_TYPE_PUBLIC_LDAP; // default to DNS
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
					file = settingsService.getSetting("PublicStoreFile");
					pass = settingsService.getSetting("PublicStoreFilePass"); 
					privKeyPass = settingsService.getSetting("PublicStorePrivKeyPass"); 
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
						null, new DNSCertificateStore.DefaultDNSCachePolicy());								
			}
			/*
			 * Web Services
			 */
			else if (storeType.equalsIgnoreCase(STORE_TYPE_WS))
			{
				resolverProvider = new ConfigServiceRESTCertificateStoreProvider(certificateService, 
						null, new ConfigServiceCertificateStore.DefaultConfigStoreCachePolicy());

			}
			/*
			 * Public LDAP resolver
			 */
			else if (storeType.equalsIgnoreCase(STORE_TYPE_PUBLIC_LDAP))
			{
				resolverProvider = new PublicLdapCertificateStoreProvider(null, 
						new LDAPCertificateStore.DefaultLDAPCachePolicy());
			}			
			/*
			 * Default to DNS with a default cache policy
			 */
			else
			{
				resolverProvider = new DNSCertStoreProvider(Collections.EMPTY_LIST, 
						null, new DNSCertificateStore.DefaultDNSCachePolicy());			
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
			setting = settingsService.getSetting("PrivateStoreType"); 
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
				file = settingsService.getSetting("PrivateStoreFile"); 
				pass = settingsService.getSetting("PrivateStoreFilePass"); 
				privKeyPass = settingsService.getSetting("PrivateStorePrivKeyPass"); 
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
			resolverProvider = new ConfigServiceRESTCertificateStoreProvider(certificateService, 
					null, new ConfigServiceCertificateStore.DefaultConfigStoreCachePolicy());
		}
		else
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidPrivateCertStoreSettings);
		}
	
		privateCertModule = new PrivateCertStoreModule(resolverProvider);

	}		
	
	protected void buildMDNSettings()
	{
		Setting autoResponseSettings;
		Setting prodNameSetting;
		Setting textSetting;
		try
		{
			autoResponseSettings = settingsService.getSetting("MDNAutoResponse"); 
			prodNameSetting = settingsService.getSetting("MDNProdName"); 
			textSetting = settingsService.getSetting("MDNProdName"); 
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
	
	protected void buildMessageSettings(String type)
	{
		Setting folderSettings;
		try
		{
			folderSettings = settingsService.getSetting("MessageSaveFolder");
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
