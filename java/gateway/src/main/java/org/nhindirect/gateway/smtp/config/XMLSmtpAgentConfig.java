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

import org.nhindirect.gateway.smtp.BounceMessageCreator;
import org.nhindirect.gateway.smtp.BounceMessageTemplate;
import org.nhindirect.gateway.smtp.DomainPostmaster;
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
import org.nhindirect.stagent.cert.impl.KeyStoreCertificateStore;
import org.nhindirect.stagent.cert.impl.provider.DNSCertStoreProvider;
import org.nhindirect.stagent.cert.impl.provider.KeyStoreCertificateStoreProvider;
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



public class XMLSmtpAgentConfig implements SmtpAgentConfig
{
	@Inject(optional=true)
	private Provider<SmtpAgent> smtpAgentProvider;

	@Inject
	private Provider<NHINDAgent> agentProvider;

	
	private Document doc;
	
	private Collection<String> domains;
	private Map<String, DomainPostmaster> domainPostmasters;
	private Module publicCertModule;
	private Module privateCertModule;
	private Module certAnchorModule;
	
	private RawMessageSettings rawSettings;
	private ProcessIncomingSettings incomingSettings;
	private ProcessOutgoingSettings outgoingSettings;
	private ProcessBadMessageSettings badSettings;
	private BounceMessageCreator outgoingBounceCreator;
	private BounceMessageCreator incomingBounceCreator;
	
	public XMLSmtpAgentConfig(String configFile, Provider<NHINDAgent> agentProvider)
	{
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
			 * Outgoing bounce message
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("outgoingbouncemessage"))
			{
				buildBounceMessage(docNode, true);
			}	
			/*
			 * Incoming bounce message
			 */
			else if (docNode.getNodeName().equalsIgnoreCase("incomingbouncemessage"))
			{
				buildBounceMessage(docNode, false);
			}				
			
			docNode = docNode.getNextSibling();
		} while (docNode != null);
		
		if (domains == null)
			throw new SmtpAgentException(SmtpAgentError.MissingDomains);
		
		SmtpAgentSettings settings = new SmtpAgentSettings(domainPostmasters, rawSettings, outgoingSettings,
				incomingSettings, badSettings);
		
		if (smtpAgentProvider == null)
			smtpAgentProvider = new DefaultSmtpAgentProvider(settings, outgoingBounceCreator, incomingBounceCreator);
		
		AgentModule agentModule;
		if (agentProvider == null)
			agentModule = AgentModule.create(domains, publicCertModule, privateCertModule, certAnchorModule);
		else
			agentModule = AgentModule.create(agentProvider);
			
		return Guice.createInjector(agentModule, SmtpAgentModule.create(smtpAgentProvider));
	}
	
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
	
	private void buildTrustAnchorResolver(Element anchorStoreNode, Map<String, Collection<String>> incomingAnchorHolder, 
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
	
	@SuppressWarnings("unchecked")
	private void buildPublicCertStore(Node publicCertNode)
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
			/*
			 * DNS resolver
			 */			
			else if(storeType.equalsIgnoreCase("dns"))
			{
				resolverProvider = new DNSCertStoreProvider(Collections.EMPTY_LIST, 
						new KeyStoreCertificateStore(new File("DNSCacheStore")), new DefaultCertStoreCachePolicy());								
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
		
		publicCertModule = new PublicCertStoreModule(resolverProvider);
	
	}
	
	private void buildPrivateCertStore(Node publicCertNode)
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
	
	private void buildBounceMessage(Node bounceNode, boolean isOutgoing)
	{
		Node childNode = bounceNode.getFirstChild();
		BounceMessageTemplate template = null;
		
		String subject = "";
		String body = "";
		
		// defaults to false if it is not present
		boolean encryptRequired = Boolean.valueOf(((Element)bounceNode).getAttribute("encrypt"));			
		
		do
		{
			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				if (childNode.getNodeName().equalsIgnoreCase("subject"))
					subject = childNode.getFirstChild().getNodeValue();
				if (childNode.getNodeName().equalsIgnoreCase("body"))
					body = childNode.getFirstChild().getNodeValue();		
			}
			childNode = childNode.getNextSibling();
		} while (childNode != null);
		
		if ((subject == null || subject.length() == 0) && (body == null || body.length() == 0))
			template = new BounceMessageTemplate.DefaultBounceMessageTemplate();
		else
			template = new BounceMessageTemplate(subject, body, encryptRequired);
		
		if (isOutgoing)
			outgoingBounceCreator = new BounceMessageCreator(template);
		else
			incomingBounceCreator = new BounceMessageCreator(template);
	}
}
