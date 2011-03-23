/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
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

package org.nhindirect.gateway.smtp.config;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.nhindirect.gateway.smtp.DomainPostmaster;
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
import org.nhindirect.gateway.smtp.module.SmtpAgentModule;
import org.nhindirect.gateway.smtp.provider.DefaultSmtpAgentProvider;
import org.nhindirect.stagent.NHINDAgent;
import org.nhindirect.stagent.cert.DefaultCertStoreCachePolicy;
import org.nhindirect.stagent.cert.CertificateResolver;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;


/**
 * An implementation of the {@link SmtpAgentConfig} interface that loads configuration information from an XML file.
 * @author Greg Meyer
 *
 */
public class XMLSmtpAgentConfig implements SmtpAgentConfig
{
	@Inject(optional=true)
	private Provider<SmtpAgent> smtpAgentProvider;

	@Inject
	private Provider<NHINDAgent> agentProvider;
	protected LDAPCertificateStore ldapCertificateStore;
	
	private Document doc;
	
	protected Collection<String> domains;
	protected Map<String, DomainPostmaster> domainPostmasters;
	protected Module publicCertModule;
	protected Module privateCertModule;
	protected Module certAnchorModule;
	
	private RawMessageSettings rawSettings;
	private ProcessIncomingSettings incomingSettings;
	private ProcessOutgoingSettings outgoingSettings;
	private ProcessBadMessageSettings badSettings;
	private NotificationProducer notificationProducer;
	private Collection<Provider<CertificateResolver>> resolverProviders;
	
	/**
	 * Construct and configuration component with the location of the configuration file and an optional provider for creating
	 * instances of the security and trust anchor.
	 * @param configFile The full path of the XML configuration file.
	 * @param agentProvider An option provider used for creating instances of the security and trust agent.  If the provider is
	 * null, a default provider is used.
	 */
	public XMLSmtpAgentConfig(String configFile, Provider<NHINDAgent> agentProvider)
	{
		resolverProviders = new ArrayList<Provider<CertificateResolver>>();
		this.agentProvider = agentProvider;
		
		try
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			doc = db.parse(new File(configFile));
		}
		catch (Exception e)
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat, e);
		}
	}
		
	/**
	 * {@inheritDoc}
	 */
	public Injector getAgentInjector()
	{
		return buildAgentInjector();
	}
	
	/*
	 * Initializes all of the modules needed to build an agent using this configuration
	 * This is implemented using a simple DOM document loaded from a know XML schema
	 * Later versions may use POJOs build from XML (JAXB) or other configuration methods
	 */
	private Injector buildAgentInjector()
	{		
		// simple node iteration
		Node docNode = doc.getFirstChild().getFirstChild();
		
		do
		{			
			/*
			 * Domain information
			 */
			if (docNode.getNodeName().equalsIgnoreCase("domains"))
			{
				buildDomains(docNode);
			}
			/*
			 * public cert store
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("publiccertstore"))
			{
				buildPublicCertStore(docNode);
				publicCertModule = new PublicCertStoreModule(resolverProviders);
			}	
			/*
			 * public cert store
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("publiccertstores"))
			{
				buildPublicCertStores(docNode);
				publicCertModule = new PublicCertStoreModule(resolverProviders);
			}						
			/*
			 * private cert store
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("privatecertstore"))
			{
				buildPrivateCertStore(docNode);
			}
			/*
			 * Raw messages
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("rawmessagesettings"))
			{
				buildRawMessageSettings(docNode);
			}		
			/*
			 * Incoming messages
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("incomingmessagessettings"))
			{
				buildIncomingMessageSettings(docNode);
			}	
			/*
			 * Outgoing messages
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("outgoingmessagessettings"))
			{
				buildOutgoingMessageSettings(docNode);
			}				
			/*
			 * Bad messages
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("badmessagessettings"))
			{
				buildBadMessageSettings(docNode);
			}	
			/*
			 * MDN settings
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("mdnsettings"))
			{
				buildMDNSettings(docNode);
			}
			
			docNode = docNode.getNextSibling();
		} while (docNode != null);
		
		if (domains == null)
			throw new SmtpAgentException(SmtpAgentError.MissingDomains);
		
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
	
	/*
	 * Builds the MDN settings
	 */
	private void buildMDNSettings(Node MDNNode)
	{
		if (MDNNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element settingsNode = (Element)MDNNode;
			boolean autoResponse = Boolean.parseBoolean(settingsNode.getAttribute("autoResponse"));
			String prodName = settingsNode.getAttribute("productName");
			
			String text = null;
			Node childNode = MDNNode.getFirstChild();
			do
			{
				if (childNode.getNodeType() == Node.ELEMENT_NODE)
				{
					if (childNode.getNodeName().equalsIgnoreCase("text"))
						text = childNode.getFirstChild().getNodeValue();	
				}
				childNode = childNode.getNextSibling();
			} while (childNode != null);
			
			
			
			notificationProducer = new NotificationProducer(new NotificationSettings(autoResponse, prodName, text));
		}
	}
	
	/*
	 * Builds the list of domains managed by the agent.
	 */
	private void buildDomains(Node domainsNode)
	{
		domains = new ArrayList<String>();
		domainPostmasters = new HashMap<String, DomainPostmaster>();
		// get all domains

		Node domainNode = domainsNode.getFirstChild();
		Node anchorStoreNode = null;
		Map<String, Collection<String>> incomingAnchorHolder = new HashMap<String, Collection<String>>();
		Map<String, Collection<String>> outgoingAnchorHolder = new HashMap<String, Collection<String>>();
		
		do
		{
			// get an individual domain
			String domain = "";
			String postmasterAddr = "";
			
			if (domainNode.getNodeType() == Node.ELEMENT_NODE)
			{					
				if (domainNode.getNodeName().equalsIgnoreCase("domain"))
				{
					Element domainEl = (Element)domainNode;
					domain = domainEl.getAttribute("name");
					
					if (domain == null || domain.trim().length() == 0)
						throw new SmtpAgentException(SmtpAgentError.MissingDomainName);
					
					postmasterAddr = domainEl.getAttribute("postmaster");
	
					
					if (postmasterAddr == null || postmasterAddr.trim().length() == 0)
						throw new SmtpAgentException(SmtpAgentError.MissingPostmaster);					
					
					domains.add(domain);
					try
					{
						domainPostmasters.put(domain.toUpperCase(Locale.getDefault()), new DomainPostmaster(domain, new InternetAddress(postmasterAddr)));
					}
					catch (AddressException e) {}
					
					// get the trust anchors configured for this domain
					Node anchorsNode = domainNode.getFirstChild();
					do
					{
						if (anchorsNode.getNodeType() == Node.ELEMENT_NODE)
						{
							/*
							 * Incoming trust anchors
							 */
							if (anchorsNode.getNodeName().equalsIgnoreCase("incomingtrustanchors"))
								incomingAnchorHolder.put(domain, getConfiguredTrustAnchorNames(anchorsNode));
							/*
							 * Outgoing trust anchors
							 */
							else if (anchorsNode.getNodeName().equalsIgnoreCase("outgoingtrustanchors"))
								outgoingAnchorHolder.put(domain, getConfiguredTrustAnchorNames(anchorsNode));

						}
						anchorsNode = anchorsNode.getNextSibling();
					} while (anchorsNode != null);
				}
				else if (domainNode.getNodeName().equalsIgnoreCase("anchorstore"))
				{
					// save off for later configuration
					anchorStoreNode = domainNode;
				}
			}
			domainNode = domainNode.getNextSibling();
		} while (domainNode != null);
		
		if (domains.size() == 0)
			throw new SmtpAgentException(SmtpAgentError.MissingDomains);
		
		buildTrustAnchorResolver((Element)anchorStoreNode, incomingAnchorHolder, outgoingAnchorHolder);
		
	}	
	
	/*
	 * Builds the resolver used to find trust anchors.
	 */	
	protected void buildTrustAnchorResolver(Element anchorStoreNode, Map<String, Collection<String>> incomingAnchorHolder, 
			Map<String, Collection<String>> outgoingAnchorHolder)
	{
		
		Provider<TrustAnchorResolver> provider = null;
		String storeType = anchorStoreNode.getAttribute("storeType");
		Map<String, Collection<X509Certificate>> incomingAnchors = new HashMap<String, Collection<X509Certificate>>();
		Map<String, Collection<X509Certificate>> outgoingAnchors = new HashMap<String, Collection<X509Certificate>>();
		
		/*
		 * anchors are store in a key store
		 */
		if (storeType.equalsIgnoreCase("keystore"))
		{
			KeyStoreCertificateStore store = new KeyStoreCertificateStore(anchorStoreNode.getAttribute("file"), 
					anchorStoreNode.getAttribute("filePass"), anchorStoreNode.getAttribute("privKeyPass"));
			
			// get incoming anchors
			for (Entry<String, Collection<String>> entries : incomingAnchorHolder.entrySet())
			{
				Collection<X509Certificate> certs = new ArrayList<X509Certificate>();				
				for (String alias : entries.getValue())
				{
					X509Certificate cert = store.getByAlias(alias);
					if (cert != null)
					{
						certs.add(cert);
					}
				}				
				incomingAnchors.put(entries.getKey(), certs);
			}
						
			// get outgoing anchors
			for (Entry<String, Collection<String>> entries : outgoingAnchorHolder.entrySet())
			{
				Collection<X509Certificate> certs = new ArrayList<X509Certificate>();
				
				for (String alias : entries.getValue())
				{
					X509Certificate cert = store.getByAlias(alias);
					if (cert != null)
					{
						certs.add(cert);
					}
				}				
				outgoingAnchors.put(entries.getKey(), certs);
			}			
		}	
		else if (storeType.equalsIgnoreCase("ldap")){	
		    
		    ldapCertificateStore = (LDAPCertificateStore) buildLdapCertificateStoreProvider(anchorStoreNode, "LDAPTrustAnchorStore").get();
		    // get incoming anchors
            for (Entry<String, Collection<String>> entries : incomingAnchorHolder.entrySet())
            {
                Collection<X509Certificate> certs = new ArrayList<X509Certificate>();      
                for (String alias : entries.getValue())
                {
                    //TODO what if 2nd entry has no certs? Fail?
                    //each alias could have multiple certificates
                    certs.addAll(ldapCertificateStore.getCertificates(alias));                    
                }             
                incomingAnchors.put(entries.getKey(), certs);
            }
                        
            // get outgoing anchors
            for (Entry<String, Collection<String>> entries : outgoingAnchorHolder.entrySet())
            {
                Collection<X509Certificate> certs = new ArrayList<X509Certificate>(); 
                for (String alias : entries.getValue())
                {
                    certs.addAll(ldapCertificateStore.getCertificates(alias));                    
                }          
                outgoingAnchors.put(entries.getKey(), certs);
            }           
		}
		
		// determine what module to load to inject the trust anchor resolver implementation

		
		String type = anchorStoreNode.getAttribute("type");
		
		/*
		 * Uniform trust anchor
		 */
		if (type.equalsIgnoreCase("uniform"))
		{
			// this is uniform... doesn't really matter what we use for incoming or outgoing because in theory they should be
			// the same... just get the first collection in the incoming map
			provider = new UniformTrustAnchorResolverProvider(incomingAnchors.values().iterator().next());
		}
		else if (type.equalsIgnoreCase("multidomain"))
		{
			provider = new MultiDomainTrustAnchorResolverProvider(incomingAnchors, outgoingAnchors);
		}
		else
		{
			throw new SmtpAgentException(SmtpAgentError.InvalidTrustAnchorSettings);
		}
		
		certAnchorModule = TrustAnchorModule.create(provider);
	}
	
	/*
	 * Gets the trust anchors for a specific domain.
	 */
	private Collection<String> getConfiguredTrustAnchorNames(Node anchorsNode)
	{
		Collection<String> retVal = new ArrayList<String>();
		
		Node anchorNamesNode = anchorsNode.getFirstChild();
		do
		{
			if (anchorNamesNode.getNodeType() == Node.ELEMENT_NODE && anchorNamesNode.getNodeName().equalsIgnoreCase("anchor"))
			{
				Element anchorElement = (Element)anchorNamesNode;
				retVal.add(anchorElement.getAttribute("name"));
			}
			anchorNamesNode = anchorNamesNode.getNextSibling();
		} while (anchorNamesNode != null);
		
		if (retVal.size() == 0)
			retVal = Collections.emptyList();
		
		return retVal;
	}
	
	/*
	 * Build the certificate resolvers for public certificates
	 */
	@SuppressWarnings("unchecked")
	private void buildPublicCertStores(Node publicCertsNode)
	{
		Node publicCertNode = publicCertsNode.getFirstChild();
		do
		{
			if (publicCertNode.getNodeType() == Node.ELEMENT_NODE && publicCertNode.getNodeName().equalsIgnoreCase("PublicCertStore"))
			{
				buildPublicCertStore(publicCertNode);
			}
			publicCertNode = publicCertNode.getNextSibling();
		} while (publicCertNode != null);		
	}
	
	/*
	 * Build the certificate resolver for public certificates
	 */
	@SuppressWarnings("unchecked")
	private void buildPublicCertStore(Node publicCertNode)
	{
		Provider<CertificateResolver> resolverProvider = null;
		
		// check for multiple configured certs stores
		
		
		
		if (publicCertNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element certNode = (Element)publicCertNode;
			String storeType = certNode.getAttribute("type");
			
			/*
			 * KeyStore based resolver
			 */
			if (storeType.equalsIgnoreCase("keystore"))
			{
				resolverProvider = new KeyStoreCertificateStoreProvider(certNode.getAttribute("file"), 
						certNode.getAttribute("filePass"), certNode.getAttribute("privKeyPass"));
			}
			/*
			 * DNS resolver
			 */			
			else if(storeType.equalsIgnoreCase("dns"))
			{
				resolverProvider = new DNSCertStoreProvider(Collections.EMPTY_LIST, 
						new KeyStoreCertificateStore(new File("DNSCacheStore"), "DefaultFilePass", "DefaultKeyPass"), new DefaultCertStoreCachePolicy());								
			}
			/*
			 * Default to DNS with a default cache policy
			 */
			else
			{
				resolverProvider = new DNSCertStoreProvider(Collections.EMPTY_LIST, 
						new KeyStoreCertificateStore(new File("DNSCacheStore")), new DefaultCertStoreCachePolicy());			
			}
		}
		
		resolverProviders.add(resolverProvider);
	}
	
	/*
	 * Build the certificates store that hold private certificates.
	 */	
	protected void buildPrivateCertStore(Node publicCertNode)
	{
		Provider<CertificateResolver> resolverProvider = null;
		
		if (publicCertNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element certNode = (Element)publicCertNode;
			String storeType = certNode.getAttribute("type");
			
			/*
			 * KeyStore based resolver
			 */
			if (storeType.equalsIgnoreCase("keystore"))
			{
				resolverProvider = new KeyStoreCertificateStoreProvider(certNode.getAttribute("file"), 
						certNode.getAttribute("filePass"), certNode.getAttribute("privKeyPass"));
			}
			else if(storeType.equalsIgnoreCase("ldap"))
			{
			    resolverProvider = buildLdapCertificateStoreProvider(certNode, "LDAPPrivateCertStore");
			}
			else
			{
				throw new SmtpAgentException(SmtpAgentError.InvalidPrivateCertStoreSettings);
			}
		}
		
		privateCertModule = new PrivateCertStoreModule(resolverProvider);;

	}	
	
	private void buildRawMessageSettings(Node rawMsgNode)
	{
		if (rawMsgNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element rawMsg = (Element)rawMsgNode;
			String saveFolder = rawMsg.getAttribute("saveFolder");
			RawMessageSettings settings = new RawMessageSettings();
			
			if (saveFolder != null)
				settings.setSaveMessageFolder(new File(saveFolder));
			
			rawSettings = settings;
		}
	}
	
	
	private void buildIncomingMessageSettings(Node incomingMsgNode)
	{
		if (incomingMsgNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element incomingMsg = (Element)incomingMsgNode;
			String saveFolder = incomingMsg.getAttribute("saveFolder");
			ProcessIncomingSettings settings = new ProcessIncomingSettings();
			
			if (saveFolder != null)
				settings.setSaveMessageFolder(new File(saveFolder));
			
			incomingSettings = settings;
		}
	}
	
	private void buildOutgoingMessageSettings(Node outgoingMsgNode)
	{
		if (outgoingMsgNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element outgoingMsg = (Element)outgoingMsgNode;
			String saveFolder = outgoingMsg.getAttribute("saveFolder");
			ProcessOutgoingSettings settings = new ProcessOutgoingSettings();
			
			if (saveFolder != null)
				settings.setSaveMessageFolder(new File(saveFolder));
			
			outgoingSettings = settings;
		}
	}	
	
	private void buildBadMessageSettings(Node badMsgNode)
	{
		if (badMsgNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element badMsg = (Element)badMsgNode;
			String saveFolder = badMsg.getAttribute("saveFolder");
			ProcessBadMessageSettings settings = new ProcessBadMessageSettings();
			
			if (saveFolder != null)
				settings.setSaveMessageFolder(new File(saveFolder));
			
			badSettings = settings;
		}
	}
	
	/**
	 * This will build an LdapCertificateStoreProvider to be used to grab certificates from the LDAP store.
	 * @param anchorStoreNode - The Element node in the xml file that contains anchor information
	 * @param cacheStoreName - The name of the bootstrap cacheStore used when cache and LDAP are unreachable.
	 * @return
	 */
	protected LdapCertificateStoreProvider buildLdapCertificateStoreProvider(Element anchorStoreNode, String cacheStoreName){
	    //required
	    String[] ldapURL = anchorStoreNode.getAttribute("ldapURL").split(",");
	    String ldapSearchBase = anchorStoreNode.getAttribute("ldapSearchBase");
        String ldapSearchAttr = anchorStoreNode.getAttribute("ldapSearchAttr");
        String ldapCertAttr = anchorStoreNode.getAttribute("ldapCertAttr");
        String ldapCertFormat = anchorStoreNode.getAttribute("ldapCertFormat");  
        if(ldapURL[0].isEmpty() || ldapSearchBase.isEmpty() || ldapSearchAttr.isEmpty() ||
                ldapCertAttr.isEmpty() || ldapCertFormat.isEmpty())
        {
            throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat);
        }
        //optional	    
	    String ldapUser = anchorStoreNode.getAttribute("ldapUser");
	    String ldapPassword = anchorStoreNode.getAttribute("ldapPassword");
	    String ldapConnTimeout = anchorStoreNode.getAttribute("ldapConnTimeout");	   
	    String ldapCertPassphrase = anchorStoreNode.getAttribute("ldapCertPassphrase");
	    if(ldapCertFormat.equalsIgnoreCase("pkcs12") && ldapCertPassphrase.isEmpty())
	    {
	        throw new SmtpAgentException(SmtpAgentError.InvalidConfigurationFormat);
	    }
	    LdapStoreConfiguration ldapStoreConfiguration = new LdapStoreConfiguration(ldapURL, ldapSearchBase, ldapSearchAttr, ldapCertAttr, ldapCertFormat);
	    if(!(ldapUser.isEmpty() && ldapPassword.isEmpty()))
	    {
	        ldapStoreConfiguration.setEmployLdapAuthInformation(new EmployLdapAuthInformation(ldapUser, ldapPassword));
	    }
	    if(!ldapConnTimeout.isEmpty())
	    {
	        ldapStoreConfiguration.setLdapConnectionTimeOut(ldapConnTimeout);
	    }
	    if(!ldapCertPassphrase.isEmpty())
	    {
	        ldapStoreConfiguration.setLdapCertPassphrase(ldapCertPassphrase);
	    }

	    LdapCertificateStoreProvider ldapCertificateStoreProvider = new LdapCertificateStoreProvider(ldapStoreConfiguration,new KeyStoreCertificateStore(new File(cacheStoreName),ldapCertPassphrase, ldapCertPassphrase), new DefaultCertStoreCachePolicy());
	    return ldapCertificateStoreProvider;
	}
}
