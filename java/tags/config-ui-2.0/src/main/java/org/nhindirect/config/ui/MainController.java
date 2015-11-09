package org.nhindirect.config.ui;
/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.security.cert.CertificateEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.config.rest.CertPolicyService;
import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.SettingService;
import org.nhind.config.rest.TrustBundleService;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicy;
import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.Setting;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.TrustBundleAnchor;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.model.utils.CertUtils.CertContainer;
import org.nhindirect.config.store.EntityStatus;

import org.nhindirect.config.ui.flash.FlashMap.Message;
import org.nhindirect.config.ui.flash.FlashMap.MessageType;
import org.nhindirect.config.ui.form.AddressForm;
import org.nhindirect.config.ui.form.AnchorForm;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.DNSEntryForm;
import org.nhindirect.config.ui.form.PolicyForm;
import org.nhindirect.config.ui.form.BundleForm;
import org.nhindirect.config.ui.form.DNSType;
import org.nhindirect.config.ui.form.DomainForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.form.SettingsForm;
import org.nhindirect.config.ui.form.SimpleForm;
import org.nhindirect.config.ui.util.AjaxUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;



@Controller
@RequestMapping("/main")
public class MainController {
	
    private final Log log = LogFactory.getLog(getClass());

	private TrustBundleService bundleService;
	private CertificateService certService;
	private DNSService dnsService;
	private SettingService settingsService;
	private CertPolicyService policyService;
	private DomainService domainService;
	
	@Inject
	public void setTrustBundleService(TrustBundleService bundleService)
    {
        this.bundleService = bundleService;
    }
	
	@Inject
	public void setCertificateService(CertificateService certService)
    {
        this.certService = certService;
    }
	
	@Inject
	public void setDNSService(DNSService dnsService)
    {
        this.dnsService = dnsService;
    }
    
	@Inject
	public void setSettingsService(SettingService settingsService)
    {
        this.settingsService = settingsService;
    }
	
	@Inject
	public void setCertPolicyService(CertPolicyService policyService)
    {
        this.policyService = policyService;
    }
	
	@Inject
	public void setDomainService(DomainService domainService)
    {
        this.domainService = domainService;
    }
	
	public MainController() {
            if (log.isDebugEnabled()) 
            {
                log.debug("MainController initialized");
            }
	}
	
	/**
	 * Execute the search and return the results
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public ModelAndView search (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                        HttpSession session,
                                        @ModelAttribute SimpleForm simpleForm,
                                        Model model,
                                        @RequestParam(value="submitType") String actionPath,
                                        @RequestParam(value="domainName", required=false) String searchDomainName,
                                        @RequestParam(value="status", required=false) EntityStatus searchStatus)  
        {
            log.error("Hit Search Controller");
            
            if (log.isDebugEnabled()) {
                log.debug("Enter search");
            }                

            String message = "Search complete";

            ModelAndView mav = new ModelAndView();                            
                
            if (actionPath.equalsIgnoreCase("gotosettings") || actionPath.equalsIgnoreCase("settings"))
            {
                /*************************************
                 * Settings
                 * 
                 *************************************/ 

                if (log.isDebugEnabled()) 
                {
                    log.debug("trying to go to the settings page");
                }

                String action = "add";
                model.addAttribute("action", action);


                mav.setViewName("settings");    // Set view for this method

                mav.addObject("actionPath", "gotosettings");

                // Initialize default settings form 
                SettingsForm form = (SettingsForm) session.getAttribute("settingsForm");
                if (form == null) {
                        form = new SettingsForm();
                }
                model.addAttribute("settingsForm", form);

                // Retrieve list of settings for settingsResults
                List<Setting> results = null;
                if (settingsService != null) 
                {
                    try {
                            final Collection<Setting> settings = settingsService.getSettings();
                            if (settings != null) {
                            results = new ArrayList<Setting>(settings);
                        }
                            else {
                            results = new ArrayList<Setting>();
                        }
                    } catch (ServiceException e) {
                    }
                }
                model.addAttribute("simpleForm",new SimpleForm());
                model.addAttribute("settingsResults", results);			
            }	
            else if (actionPath.equalsIgnoreCase("gotocertificates") || actionPath.equalsIgnoreCase("certificates"))
            {
                /*************************************
                 * Manage Certificates
                 * 
                 *************************************/                     

                if (log.isDebugEnabled()) {
                    log.debug("trying to go to the certificates page");
                }
                    final String action = "Update";
                    model.addAttribute("action", action);

                    mav.setViewName("certificates");
                    mav.addObject("actionPath", "gotocertificates");
                    CertificateForm form = (CertificateForm) session.getAttribute("certificateForm");
                    if (form == null) {
                            form = new CertificateForm();
                    }
                    model.addAttribute("certificateForm", form);
                    // retrieve list of settings for settingsResults
                    List<Certificate> results = null;
                    if (certService != null) {
                            try {
                                    final Collection<Certificate> certs = certService.getAllCertificates();
                                    if (certs != null) {
                                    results = new ArrayList<Certificate>(certs);
                                }
                                    else {
                                    results = new ArrayList<Certificate>();
                                }
                            } catch (ServiceException e) {
                            }
                    }
                    model.addAttribute("simpleForm",new SimpleForm());
                    model.addAttribute("certificatesResults", results);
            }
            else if (actionPath.equalsIgnoreCase("newdomain") || actionPath.equalsIgnoreCase("new domain"))
            {
                /*************************************
                 * Create New Domain
                 * 
                 *************************************/ 

                if (log.isDebugEnabled()) {
                    log.debug("trying to go to the new domain page");
                }
                    final HashMap<String, String> msgs = new HashMap<String, String>();
                    mav.addObject("msgs", msgs);

                    model.addAttribute("simpleForm",new SimpleForm());

                    final AddressForm addrform = new AddressForm();
                    addrform.setId(0L);
                    model.addAttribute("addressForm",addrform);
                    // TODO: once certificates and anchors are available change code accordingly
                    final CertificateForm cform = new CertificateForm();
                    //cform.setId(0L);
                    final AnchorForm aform = new AnchorForm();
                    aform.setId(0L);

                    model.addAttribute("certificateForm",cform);
                    model.addAttribute("anchorForm",aform);
                    final String action = "Add";
                    DomainForm form = (DomainForm) session.getAttribute("domainForm");
                    if (form == null) {
                            form = new DomainForm();
                    }
                    model.addAttribute("domainForm", form);
                    model.addAttribute("action", action);

                    mav.setViewName("domain");
                    mav.addObject("actionPath", "newdomain");
                    mav.addObject("statusList", EntityStatus.getEntityStatusList());
            }		
            else if (actionPath.equalsIgnoreCase("gotodns") || actionPath.equalsIgnoreCase("DNS Entries"))
            {
                /*************************************
                 * Manage DNS 
                 * 
                 *************************************/ 

                if (log.isDebugEnabled()) 
                {
                    log.debug("Entering DNS Management page");
                }

                final HashMap<String, String> msgs = new HashMap<String, String>();
                mav.addObject("msgs", msgs);
                final String action = "Update";
                model.addAttribute("action", action);
                // get all DNSType.A.getValue() records
                // GET A RECORDS
                Collection<DNSRecord> arecords = null;
                            arecords = getDnsRecords(DNSType.A.getValue());
                            model.addAttribute("dnsARecordResults",arecords);
                // GET A4 RECORDS
                            Collection<DNSRecord> a4records = null;
                    a4records = getDnsRecords(DNSType.AAAA.getValue());
                    model.addAttribute("dnsA4RecordResults",a4records);
                // GET C RECORDS
                            Collection<DNSRecord> crecords = null;
                            crecords = getDnsRecords(DNSType.CNAME.getValue());
                    model.addAttribute("dnsCnameRecordResults",crecords);
                // GET Cert RECORDS
                            Collection<DNSRecord> certrecords = null;
                            certrecords = getDnsRecords(DNSType.CERT.getValue());
                    model.addAttribute("dnsCertRecordResults",certrecords);
                // GET MX RECORDS
                            Collection<DNSRecord> mxrecords = null;
                            mxrecords = getDnsRecords(DNSType.MX.getValue());
                    model.addAttribute("dnsMxRecordResults",mxrecords);
                // GET SRV RECORDS
                            Collection<DNSRecord> srvrecords = null;
                            srvrecords = getDnsRecords(DNSType.SRV.getValue());
                    model.addAttribute("dnsSrvRecordResults",srvrecords);

                mav.setViewName("dns");

                mav.addObject("actionPath", "gotodns");

                model.addAttribute("AdnsForm", new DNSEntryForm());
                model.addAttribute("AAdnsForm", new DNSEntryForm());
                model.addAttribute("CdnsForm", new DNSEntryForm());
                model.addAttribute("CertdnsForm", new DNSEntryForm());
                model.addAttribute("MXdnsForm", new DNSEntryForm());
                model.addAttribute("SrvdnsForm", new DNSEntryForm());

                refreshModelFromService(model);

                model.addAttribute("simpleForm",new SimpleForm());            
            }
            
            else if (actionPath.equalsIgnoreCase("ManagePolicies") || actionPath.equalsIgnoreCase("Policies"))
            {
                if (log.isDebugEnabled())  {
                    log.debug("trying to go to the Policies page");
                }


                final String action = "Update";
                model.addAttribute("action", action);

                mav.setViewName("policies");
                mav.addObject("actionPath", "gotopolicies");

                PolicyForm form = (PolicyForm) session.getAttribute("policyForm");
                if (form == null) {
                    form = new PolicyForm();
                }

                model.addAttribute("policyForm", form);
                
                                              
                
                Collection<CertPolicy> policies = null;
                
                
                try {
                    policies = policyService.getPolicies();
                    
                } catch (Exception e) {
                    System.out.println("Failed to lookup policies: " + e.getMessage());
                }
                
                //log.error(policies);
                
                
                if(policies != null) {
                    model.addAttribute("policies", policies);
                } else {                    
                    model.addAttribute("policies", "");
                }
                
                
                
                /*
                // retrieve list of settings for settingsResults
                List<Certificate> results = null;
                if (configSvc != null) {
                    // Process data for Trust Bundle View
                    try {

                        // Get Trust Bundles
                        Collection<TrustBundle> trustBundles = configSvc.getTrustBundles(true); 
                        
                        if (trustBundles == null)
                        	trustBundles = Collections.emptyList();
                        
                        Map<String, Object> bundleMap = new HashMap<String, Object>(trustBundles.size());                                                                                                            
                                    
                        Collection<TrustBundleAnchor> tbAnchors;    // Store anchors for each bundle   



                        for(TrustBundle bundle : trustBundles) 
                        {                                        
                            tbAnchors = bundle.getTrustBundleAnchors();    
                            Map<TrustBundleAnchor, String> anchorMap = new HashMap<TrustBundleAnchor, String>(tbAnchors.size());                                                                                

                            //String[] anchorDNs = new String[tbAnchors.size()];  // String array for storing anchor DNs
                            int curAnchor = 0;  // Counter as we iterate through anchor list

                            // Loop through anchors to collect some information about the certificates
                            for(TrustBundleAnchor anchor : tbAnchors) {

                                try {
                                    X509Certificate cert = anchor.toCertificate();                                            

                                    String subjectDN = cert.getSubjectDN().toString();
                                    anchorMap.put(anchor, subjectDN);

                                } catch (org.nhindirect.config.store.CertificateException ex) {                                                
                                }

                                curAnchor++;
                            }

                            bundleMap.put(bundle.getBundleName(), anchorMap);

                        }

                        model.addAttribute("bundleMap", bundleMap);  
                        
                        
                        
                        model.addAttribute("trustBundles", trustBundles);                                

                    } catch (ConfigurationServiceException e1) {
                            e1.printStackTrace();
                    }								
                }
                */ 
                
                model.addAttribute("simpleForm",new SimpleForm());            



            } 
            
            
            else if (actionPath.equalsIgnoreCase("ManageTrustBundles") || actionPath.equalsIgnoreCase("Bundles"))
            {
                if (log.isDebugEnabled())  {
                    log.debug("trying to go to the Bundles page");
                }


                final String action = "Update";
                model.addAttribute("action", action);

                mav.setViewName("bundles");
                mav.addObject("actionPath", "gotobundles");

                BundleForm form = (BundleForm) session.getAttribute("BundleForm");
                if (form == null) {
                    form = new BundleForm();
                }

                model.addAttribute("bundleForm", form);

                // retrieve list of settings for settingsResults
                if (bundleService != null) {
                    // Process data for Trust Bundle View
                    try {

                        // Get Trust Bundles
                        Collection<TrustBundle> trustBundles = bundleService.getTrustBundles(true); 
                        
                        if (trustBundles == null) {
                        	trustBundles = Collections.emptyList();
                        }
                                
                        final Map<String, Object> bundleMap = new HashMap<String, Object>(trustBundles.size());                                                                                                            
                                    
                        Collection<TrustBundleAnchor> tbAnchors;    // Store anchors for each bundle   



                        for(TrustBundle bundle : trustBundles) 
                        {                                        
                            tbAnchors = bundle.getTrustBundleAnchors();    
                            final Map<TrustBundleAnchor, String> anchorMap = new HashMap<TrustBundleAnchor, String>(tbAnchors.size());                                                                                

                            //String[] anchorDNs = new String[tbAnchors.size()];  // String array for storing anchor DNs

                            // Loop through anchors to collect some information about the certificates
                            for(TrustBundleAnchor anchor : tbAnchors) {

	                            final X509Certificate cert = anchor.getAsX509Certificate();                                          
	
	                            final String subjectDN = cert.getSubjectDN().toString();
	                            anchorMap.put(anchor, subjectDN);
                            }

                            bundleMap.put(bundle.getBundleName(), anchorMap);

                        }

                        model.addAttribute("bundleMap", bundleMap);  
                        
                        
                        
                        model.addAttribute("trustBundles", trustBundles);                                

                    } catch (ServiceException e1) {
                            e1.printStackTrace();
                    }								
                }
                
                
                model.addAttribute("simpleForm",new SimpleForm());            



            } 
            
            
            
            else
            {

                SearchDomainForm form = (SearchDomainForm) session.getAttribute("searchDomainForm");
                if (form == null) { 
                        form = new SearchDomainForm();
                }
                model.addAttribute(form);
                model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));

                final String domain = (!searchDomainName.isEmpty()) ? searchDomainName : "%";
                
                mav.addObject("searchTerm", searchDomainName);
                EntityStatus status = searchStatus;

                List<Domain> results = null;

                if (domainService != null) 
                {
                	try
                	{
	                    final Collection<Domain> domains = domainService.searchDomains(domain, 
	                    		org.nhindirect.config.model.EntityStatus.valueOf(status.toString()));
	                    
	                    if (domains != null)
	                    {
	                        results = new ArrayList<Domain>(domains);
	                    }
	                    else 
	                    {
	                        results = new ArrayList<Domain>();
	                    }
                	}
                	catch (ServiceException e1) 
                	{
                        e1.printStackTrace();
                	}		
                }
                
                if (AjaxUtils.isAjaxRequest(requestedWith)) {
                        // prepare model for rendering success message in this request
                        model.addAttribute("message", new Message(MessageType.success, message));
                        model.addAttribute("ajaxRequest", true);
                        model.addAttribute("searchResults", results);
                        return null;
                }

                mav.setViewName("main");
                mav.addObject("statusList", EntityStatus.getEntityStatusList());
                mav.addObject("searchResults", results);
            }

            if (log.isDebugEnabled()) 
            {
                log.debug("Exit");
            }
            
            return mav;
        }
	
        
	private Collection<DNSRecord> getDnsRecords(int type){
		Collection<DNSRecord> arecords = null;
        try {
			arecords = dnsService.getDNSRecord(type, "");
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return arecords;
	}

	/**
	 * Return the login page when requested
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView display(@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                HttpSession session, 
                                @ModelAttribute SimpleForm simpleForm,
                                Model model) { 		
		if (log.isDebugEnabled()) log.debug("Enter");
		
		ModelAndView mav = new ModelAndView(); 
		
		SearchDomainForm form = (SearchDomainForm) session.getAttribute("searchDomainForm");
		model.addAttribute(form != null ? form : new SearchDomainForm());
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));

		mav.setViewName("main"); 
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
                mav.addObject("searchTerm", "");
                
                // Get all domains managed by this HISP
                String domain = "%";
                

                List<Domain> results = null;

                if (domainService != null) 
                {
                    try
                    {
	                    final Collection<Domain> enabledDomains = domainService.searchDomains(domain, org.nhindirect.config.model.EntityStatus.ENABLED);
	                    final Collection<Domain> disabledDomains = domainService.searchDomains(domain, org.nhindirect.config.model.EntityStatus.DISABLED);
	                    
	                    Collection<Domain> domains = domainService.searchDomains(domain,org.nhindirect.config.model.EntityStatus.NEW);
	                    if (domains.isEmpty())
	                    	domains = new ArrayList<Domain>();
	                    
	                    
	                    if(enabledDomains != null && !enabledDomains.isEmpty()) 
	                    {
	                        log.error(enabledDomains);
	                        if(domains.isEmpty())
	                        {
	                            domains = enabledDomains;
	                        } else {
	                            domains.addAll(enabledDomains);
	                        }
	                    }
	                    
	                    if(disabledDomains != null && !disabledDomains.isEmpty())
	                    {
	                        if(domains.isEmpty())
	                        {
	                            domains = disabledDomains;
	                        } else {
	                            domains.addAll(disabledDomains);
	                        }
	                    }                    
	                    
	                    if (!domains.isEmpty())
	                    {
	                        results = new ArrayList<Domain>(domains);
	                    }
	                    else 
	                    {
	                        results = new ArrayList<Domain>();
	                    }
	                }
                    catch (ServiceException e)
                    {
                    	e.printStackTrace();
                    }
                }
                model.addAttribute("searchResults", results);

                mav.setViewName("main");
                mav.addObject("statusList", EntityStatus.getEntityStatusList());
                mav.addObject("searchResults", results);
		
		if (log.isDebugEnabled()) log.debug("Exit");
		return mav;
	}
	

	/**
	 * Set up to display a new Domain page
	 * @param requestedWith
	 * @param session
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/new", method=RequestMethod.GET)
	public ModelAndView newDomain (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
							        HttpSession session, 
							        Model model) {
		if (log.isDebugEnabled()) log.debug("Enter");
		ModelAndView mav = new ModelAndView(); 
		
		mav.setViewName("domain"); 
		model.addAttribute(new DomainForm());
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
		
		if (log.isDebugEnabled()) log.debug("Exit");
		return mav;
	}
    
    public void refreshModelFromService(Model model)
    {
        // GET A RECORDS
        Collection<DNSRecord> arecords = null;
		arecords = getDnsRecords(DNSType.A.getValue());
		
		Collection<DNSEntryForm> aform = new ArrayList<DNSEntryForm>();
		if (arecords != null)
		{			
			for (DNSRecord t : arecords)
			{
				try {
					ARecord newrec = (ARecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					DNSEntryForm tmp = new DNSEntryForm();
					tmp.setId(t.getId());
					tmp.setDest(""+newrec.getAddress());
					tmp.setTtl(newrec.getTTL());
					tmp.setName(""+newrec.getName());
					aform.add(tmp);
				} catch (TextParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		model.addAttribute("dnsARecordResults",aform);
        // GET A4 RECORDS
		Collection<DNSRecord> a4records = null;
    	a4records = getDnsRecords(DNSType.AAAA.getValue());
    	Collection<DNSEntryForm> a4form = new ArrayList<DNSEntryForm>();
    	if (a4records != null)
    	{			
			for (Iterator<DNSRecord> iter = a4records.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					AAAARecord newrec = (AAAARecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					DNSEntryForm tmp = new DNSEntryForm();
					tmp.setId(t.getId());
					tmp.setDest(""+newrec.getAddress());
					tmp.setTtl(newrec.getTTL());
					tmp.setName(""+newrec.getName());
					a4form.add(tmp);
				} catch (TextParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	model.addAttribute("dnsA4RecordResults",a4form);
        // GET CNAME RECORDS
		Collection<DNSRecord> crecords = null;
		crecords = getDnsRecords(DNSType.CNAME.getValue());
		Collection<DNSEntryForm> cform = new ArrayList<DNSEntryForm>();
		if (crecords != null)
		{
			for (Iterator<DNSRecord> iter = crecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					CNAMERecord newrec = (CNAMERecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					DNSEntryForm tmp = new DNSEntryForm();
					tmp.setId(t.getId());
					tmp.setDest(""+newrec.getTarget());
					tmp.setTtl(newrec.getTTL());
					tmp.setName(""+newrec.getName());
					cform.add(tmp);
				} catch (TextParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	model.addAttribute("dnsCnameRecordResults",cform);
        // GET MX RECORDS
		Collection<DNSRecord> mxrecords = null;
		mxrecords = getDnsRecords(DNSType.MX.getValue());
		Collection<DNSEntryForm> mxform = new ArrayList<DNSEntryForm>();
		if (mxrecords != null)
		{
			for (Iterator<DNSRecord> iter = mxrecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					MXRecord newrec = (MXRecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					DNSEntryForm tmp = new DNSEntryForm();
					tmp.setPriority(newrec.getPriority());
					tmp.setId(t.getId());
					tmp.setDest(""+newrec.getTarget());
					tmp.setTtl(newrec.getTTL());
					tmp.setName(""+newrec.getName());
					mxform.add(tmp);
				} catch (TextParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	model.addAttribute("dnsMxRecordResults",mxform);
        // GET Cert RECORDS
		Collection<DNSRecord> certrecords = null;
		certrecords = getDnsRecords(DNSType.CERT.getValue());
		// get the thumbprint and assign
		// create a new collection 
		Collection<SrvRecord> form = new ArrayList<SrvRecord>();
		CertContainer cont;
		if (certrecords != null)
		{
			for (Iterator<DNSRecord> iter = certrecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
	
				SrvRecord srv = new SrvRecord();
				srv.setCreateTime(t.getCreateTime());
				srv.setData(t.getData());
				srv.setDclass(t.getDclass());
				srv.setId(t.getId());
				srv.setName(t.getName());
				srv.setTtl(t.getTtl());
				srv.setType(t.getType());
				srv.setThumb("");
				
				
	    		try {
					CERTRecord newrec = (CERTRecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					String thumb = "";
		            byte[] certData = newrec.getCert();
					if (certData != null) {
						// get the owner from the certificate information
						// first transform into a certificate
						cont = CertUtils.toCertContainer(certData);
						if (cont != null && cont.getCert() != null) {
	
							Certificate cert2 = new Certificate();
							cert2.setData(certData);
							thumb = getThumbPrint(cont.getCert());
							srv.setThumb(thumb);
						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				form.add(srv);
			}
		}
    	model.addAttribute("dnsCertRecordResults",form);
        // GET SRV RECORDS
		Collection<DNSRecord> srvrecords = null;
		srvrecords = getDnsRecords(DNSType.SRV.getValue());
		// create a new collection 
		Collection<SrvRecord> form2 = new ArrayList<SrvRecord>();
		if (srvrecords != null)
		{
			for (Iterator<DNSRecord> iter = srvrecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				SrvRecord srv = new SrvRecord();
				try {
					SRVRecord srv4 = (SRVRecord) SRVRecord.newRecord(Name
							.fromString(t.getName()), t.getType(), t.getDclass(), t
							.getTtl(), t.getData());
	
					srv.setCreateTime(t.getCreateTime());
					srv.setData(t.getData());
					srv.setDclass(t.getDclass());
					srv.setId(t.getId());
					srv.setName(t.getName());
					String name = t.getName();
					// parse the name to get service, protocol, priority , weight,
					// port
	
					int firstpos = name.indexOf("_");
					if (firstpos == 0) {
						// then this can be parsed as a srv record
						// ("_"+SrvdnsForm.getService()+"._"+SrvdnsForm.getProtocol()+"._"+SrvdnsForm.getPriority()+"._"+SrvdnsForm.getWeight()+"._"+SrvdnsForm.getPort()+"._"+SrvdnsForm.getDest()+"."+SrvdnsForm.getName()
						int secondpos = name.indexOf("._");
						int thirdpos = name.indexOf(".", secondpos + 2);
						// from first to second is service
						String service_ = name.substring(firstpos + 1, secondpos);
						srv.setService(service_);
						// from second to third is protocol
						String protocol_ = name.substring(secondpos + 2, thirdpos);
						;
						srv.setProtocol(protocol_);
						int last2pos = name.indexOf(".", thirdpos);
						String name_ = name.substring(last2pos+1, name.length());
						srv.setName(name_);
					}
					srv.setTtl(t.getTtl());
					srv.setType(t.getType());
	
					srv.setPort(srv4.getPort());
					srv.setWeight(srv4.getWeight());
					srv.setPriority("" + srv4.getPriority());
					srv.setTarget("" + srv4.getTarget().toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				form2.add(srv);
			}
		}
    	model.addAttribute("dnsSrvRecordResults",form2);
        // GET SOA RECORDS
        Collection<DNSRecord> soarecords = null;
		soarecords = getDnsRecords(DNSType.SOA.getValue());
		Collection<DNSEntryForm> soaform = new ArrayList<DNSEntryForm>();
		if (soarecords != null)
		{
			for (Iterator<DNSRecord> iter = soarecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					SOARecord newrec = (SOARecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					DNSEntryForm tmp = new DNSEntryForm();
					tmp.setId(t.getId());
					tmp.setAdmin(""+newrec.getAdmin());
					tmp.setExpire(newrec.getExpire());
					tmp.setMinimum(newrec.getMinimum());
					tmp.setRefresh(newrec.getRefresh());
					tmp.setRetry(newrec.getRetry());
					tmp.setSerial(newrec.getSerial());
					tmp.setDest(""+newrec.getHost());
					tmp.setDomain(""+newrec.getHost());
					tmp.setTtl(newrec.getTTL());
					tmp.setName(""+newrec.getName());
					soaform.add(tmp);
				} catch (TextParseException e) {
					e.printStackTrace();
				}
			}
		}
		model.addAttribute("dnsSOARecordResults",soaform);
		
        // GET NS RECORDS
        Collection<DNSRecord> nsrecords = null;
        nsrecords = getDnsRecords(DNSType.NS.getValue());
		Collection<DNSEntryForm> nsform = new ArrayList<DNSEntryForm>();
		if (nsrecords != null)
		{
			for (Iterator<DNSRecord> iter = nsrecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					NSRecord newrec = (NSRecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					DNSEntryForm tmp = new DNSEntryForm();
					tmp.setId(t.getId());
					tmp.setDest(""+newrec.getTarget());
					tmp.setTtl(newrec.getTTL());
					tmp.setName(""+newrec.getName());
					nsform.add(tmp);
				} catch (TextParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		model.addAttribute("dnsNSRecordResults",nsform);

		// *****************
		model.addAttribute("NSdnsForm", new DNSEntryForm());
		model.addAttribute("SoadnsForm", new DNSEntryForm());
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());
		
    }


	/**
	 * Handle exceptions as gracefully as possible
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(IOException.class) 
	public String handleIOException(IOException ex, HttpServletRequest request) 
	{
		//TODO Actually do something useful
		return ClassUtils.getShortName(ex.getClass() + ":" + ex.getMessage());
	}


	public static String getThumbPrint(X509Certificate cert)
			throws NoSuchAlgorithmException, CertificateEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] der = null;
		byte[] digest = null;
		try {
			der = cert.getEncoded();
			md.update(der);
			digest = md.digest();
		} catch (java.security.cert.CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hexify(digest);

	}

	public static String hexify(byte bytes[]) {

		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		StringBuffer buf = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; ++i) {
			buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			buf.append(hexDigits[bytes[i] & 0x0f]);
		}

		return buf.toString();
	}	


}
