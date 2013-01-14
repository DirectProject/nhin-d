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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;
import javax.security.cert.CertificateEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.nhindirect.config.service.ConfigurationService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.DNSRecord;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.Setting;
import org.nhindirect.config.store.util.DNSRecordUtils;

import org.nhindirect.config.ui.DomainController.CertContainer;
import org.nhindirect.config.ui.form.AnchorForm;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.DNSEntryForm;
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

/**
 * Controller (MVC) for "Manage DNS Entries" pages.
 */
@Controller
@RequestMapping("/dns")
public class DNSController
{
    private final Log log = LogFactory.getLog(getClass());
    
    private ConfigurationService configSvc;
    
    @Inject
    public void setConfigurationService(ConfigurationService service)
    {
        this.configSvc = service;
    }
    
    /*
    @Inject
    private configSvc configSvc;
    
    @Inject
    private DomainService dService;
    
    @Inject
    private SettingService configSvc;
    
    @Inject
    private configSvc configSvc;
    */
    
    public DNSController()
    {
        if (log.isDebugEnabled()) log.debug("ConfigurationController initialized");
    }
    
    private Collection<DNSEntryForm> convertDNSRecords(Collection<DNSRecord> entries)
    {
        if (log.isDebugEnabled()) log.debug("Enter");
        
        Collection<DNSEntryForm> forms = new ArrayList<DNSEntryForm>();
        if (entries != null)
        {
	        for (Iterator<DNSRecord> iter = entries.iterator(); iter.hasNext();) 
	        {
	            DNSEntryForm form = new DNSEntryForm(iter.next());
	            forms.add(form);            
	        }
        }
        
        if (log.isDebugEnabled()) log.debug("Exit");
        return forms;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/navigate", method = RequestMethod.GET)
    public ModelAndView navigate (
                                @RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                HttpSession session,
                                @ModelAttribute DNSEntryForm entryForm,
                                Model model,
                                @RequestParam(value="submitType") String actionPath
                                ) 
    {
        if (log.isDebugEnabled()) log.debug("Enter: " + actionPath);
        
        ModelAndView mav = new ModelAndView();
        
        if ("gotodomains".equalsIgnoreCase(actionPath) || "domains".equalsIgnoreCase(actionPath))
        {
            SearchDomainForm form2 = (SearchDomainForm) session
                    .getAttribute("searchDomainForm");
            model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
            model.addAttribute("ajaxRequest", AjaxUtils
                    .isAjaxRequest(requestedWith));
    
            mav.setViewName("main");
            mav.addObject("statusList", EntityStatus.getEntityStatusList());
        }
        else if ("gotosettings".equalsIgnoreCase(actionPath) || "settings".equalsIgnoreCase(actionPath))
        {
            String action = "add";
            model.addAttribute("action", action);
            
            mav.setViewName("settings");
            mav.addObject("actionPath", actionPath);
            SettingsForm form = (SettingsForm) session.getAttribute("settingsForm");
            if (form == null) {
                form = new SettingsForm();
            }
            model.addAttribute("settingsForm", form);
            // retrieve list of settings for settingsResults
            List<Setting> results = null;
            if (configSvc != null) {
                try {
                	Collection<Setting> settings = configSvc.getAllSettings();
                	if (settings != null)
                		results = new ArrayList<Setting>(settings);
                	else
                		results = new ArrayList<Setting>();                		
                } catch (ConfigurationServiceException e) {
                    e.printStackTrace();
                }
            }
            model.addAttribute("simpleForm",new SimpleForm());
            model.addAttribute("settingsResults", results);
        }
        else if (actionPath.equalsIgnoreCase("gotocertificates") || actionPath.equalsIgnoreCase("certificates"))
        {           
            String action = "Update";
            model.addAttribute("action", action);
            
            mav.setViewName("certificates");
            mav.addObject("actionPath", actionPath);
            CertificateForm form = (CertificateForm) session.getAttribute("certificateForm");
            if (form == null) {
                form = new CertificateForm();
            }
            model.addAttribute("certificateForm", form);
            // retrieve list of settings for settingsResults
            List<Certificate> results = null;
            if (configSvc != null) {
                try {
                	Collection<Certificate> certs = configSvc.listCertificates(1, 10000, CertificateGetOptions.DEFAULT);
                	if (certs != null)
                		results = new ArrayList<Certificate>(certs);
                	else
                		results = new ArrayList<Certificate>();
                } catch (ConfigurationServiceException e) {
                    e.printStackTrace();
                }
            }
            model.addAttribute("simpleForm",new SimpleForm());
            model.addAttribute("certificatesResults", results);
        }
        
        if (log.isDebugEnabled()) log.debug("Exit");
        return mav;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showDNSEntries (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                        HttpSession session,
                                        @ModelAttribute DNSEntryForm entryForm,
                                        Model model,
                                        @RequestParam(value="submitType") String actionPath
                                        ) 
    {
        
        if (log.isDebugEnabled()) log.debug("Enter");
        
        ModelAndView mav = new ModelAndView("dns");
        model.addAttribute("dnsEntryForm", new DNSEntryForm());

        int serviceError = 0;
        String errorDetails = "";
       
        if (configSvc != null)
        {
            refreshModelFromService(model);
        }
        return mav; 
    }


    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addDNSRecord", method = RequestMethod.POST)
	public ModelAndView addSetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("AdnsForm") DNSEntryForm AdnsForm,
			@ModelAttribute("AAdnsForm") DNSEntryForm AAdnsForm,
			@ModelAttribute("CdnsForm") DNSEntryForm CdnsForm,
			@ModelAttribute("CertdnsForm") DNSEntryForm CertdnsForm,
			@ModelAttribute("SrvdnsForm") DNSEntryForm SrvdnsForm,
			@ModelAttribute("MXdnsForm") DNSEntryForm MXdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// A records
		if (AdnsForm != null && !AdnsForm.getName().equalsIgnoreCase("")
				&& AdnsForm.getTtl() != 0L
				&& !AdnsForm.getDest().equalsIgnoreCase("")) {

			AdnsForm.setType("A");
			DNSEntryForm.toDNSRecord(AdnsForm);
		}
		model.addAttribute("AdnsForm", new DNSEntryForm());

		// A4 records
		if (AAdnsForm != null && !AAdnsForm.getName().equalsIgnoreCase("")
				&& AAdnsForm.getTtl() != 0L
				&& !AAdnsForm.getDest().equalsIgnoreCase("")) {

			AAdnsForm.setType("AAAA");
			DNSEntryForm.toDNSRecord(AAdnsForm);
		}
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		// CNAME records
		if (CdnsForm != null && !CdnsForm.getName().equalsIgnoreCase("")
				&& CdnsForm.getTtl() != 0L
				&& !CdnsForm.getDest().equalsIgnoreCase("")) {

			CdnsForm.setType("CNAME");
			DNSEntryForm.toDNSRecord(CdnsForm);
		}
		model.addAttribute("CdnsForm", new DNSEntryForm());
		// MX records
		if (MXdnsForm != null && !MXdnsForm.getName().equalsIgnoreCase("")
				&& MXdnsForm.getTtl() != 0L
				&& !MXdnsForm.getDest().equalsIgnoreCase("")) {

			MXdnsForm.setType("MX");
			DNSEntryForm.toDNSRecord(MXdnsForm);
		}
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		// CERT records
		if (CertdnsForm != null && !CertdnsForm.getName().equalsIgnoreCase("")
				&& CertdnsForm.getTtl() != 0L
				&& !CertdnsForm.getDest().equalsIgnoreCase("")) {

			CertdnsForm.setType("CERT");
			DNSEntryForm.toDNSRecord(CertdnsForm);
		}
		// SRV records
		if (SrvdnsForm != null && !SrvdnsForm.getName().equalsIgnoreCase("")
				&& SrvdnsForm.getTtl() != 0L
				&& !SrvdnsForm.getDest().equalsIgnoreCase("")) {

			SrvdnsForm.setType("SRV");
			DNSEntryForm.toDNSRecord(SrvdnsForm);
		}
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);

		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addADNSRecord", method = RequestMethod.POST)
	public ModelAndView addSetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("AdnsForm") DNSEntryForm AdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// A records
		if (AdnsForm != null && !AdnsForm.getName().equalsIgnoreCase("")
				&& AdnsForm.getTtl() != 0L
				&& !AdnsForm.getDest().equalsIgnoreCase("")) {

            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
            records.add(DNSRecordUtils.createARecord(AdnsForm.getName(), AdnsForm.getTtl(), AdnsForm.getDest()));
            
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}
		
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);



		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addNSDNSRecord", method = RequestMethod.POST)
	public ModelAndView addNSSetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("NSdnsForm") DNSEntryForm NSdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// NS records
		if (NSdnsForm != null && !NSdnsForm.getName().equalsIgnoreCase("")
				&& NSdnsForm.getTtl() != 0L
				&& !NSdnsForm.getDest().equalsIgnoreCase("")) {

            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
            records.add(DNSEntryForm.createNSRecord(NSdnsForm.getName(), NSdnsForm.getTtl(), NSdnsForm.getDest()));
            
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}
		

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);



		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addA4DNSRecord", method = RequestMethod.POST)
	public ModelAndView addA4Setting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("AAdnsForm") DNSEntryForm AAdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// A records
		if (AAdnsForm != null && !AAdnsForm.getName().equalsIgnoreCase("")
				&& AAdnsForm.getTtl() != 0L
				&& !AAdnsForm.getDest().equalsIgnoreCase("")) {

            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
            records.add(DNSEntryForm.createA4Record(AAdnsForm.getName(), AAdnsForm.getTtl(), AAdnsForm.getDest()));
            
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);

		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addMXDNSRecord", method = RequestMethod.POST)
	public ModelAndView addMXSetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("MXdnsForm") DNSEntryForm MXdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// A records
		if (MXdnsForm != null && !MXdnsForm.getName().equalsIgnoreCase("")
				&& MXdnsForm.getTtl() != 0L
				&& !MXdnsForm.getDest().equalsIgnoreCase("")) {

            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
            records.add(DNSRecordUtils.createMXRecord(MXdnsForm.getName(), MXdnsForm.getDest(), MXdnsForm.getTtl(), MXdnsForm.getPriority()));
            
            
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);

		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addCNAMEDNSRecord", method = RequestMethod.POST)
	public ModelAndView addCNAMESetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("CdnsForm") DNSEntryForm CdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// A records
		if (CdnsForm != null && !CdnsForm.getName().equalsIgnoreCase("")
				&& CdnsForm.getTtl() != 0L
				&& !CdnsForm.getDest().equalsIgnoreCase("")) {

            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
            records.add(DNSEntryForm.toDNSRecord(CdnsForm));
            
            
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);
		
		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}
    
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addSRVDNSRecord", method = RequestMethod.POST)
	public ModelAndView addSRVSetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("SrvdnsForm") DNSEntryForm SrvdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// A records
		if (SrvdnsForm != null && !SrvdnsForm.getName().equalsIgnoreCase("")
				&& SrvdnsForm.getTtl() != 0L
				&& !SrvdnsForm.getDest().equalsIgnoreCase("")) {

            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
            records.add(DNSRecordUtils.createSRVRecord("_"+SrvdnsForm.getService()+"._"+SrvdnsForm.getProtocol()+"."+SrvdnsForm.getName(), 
            		SrvdnsForm.getDest(), 
            		SrvdnsForm.getTtl(), 
            		SrvdnsForm.getPort(),
            		SrvdnsForm.getPriority(),
            		SrvdnsForm.getWeight()));
            
            
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);
		mav.setViewName("dns");
		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addCertDNSRecord", method = RequestMethod.POST)
	public ModelAndView addCertSetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("CertdnsForm") DNSEntryForm CertdnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// CERT records
		if (CertdnsForm != null && !CertdnsForm.getName().equalsIgnoreCase("")
				&& CertdnsForm.getTtl() != 0L
				) {

			int certtype = 0;
			int keytag = 0;
			int alg = 0;
			X509Certificate tcert = null;
			byte[] certbytes = null; 
			try {
				if (!CertdnsForm.getFileData().isEmpty()) {
					byte[] bytes = CertdnsForm.getFileData().getBytes();
					
					certbytes = bytes;
					
					String theUser = "";
					if (bytes != null) {
						// get the owner from the certificate information
						// first transform into a certificate
						CertContainer cont = toCertContainer(bytes);
						if (cont != null && cont.getCert() != null) {

							Certificate cert2 = new Certificate();
							cert2.setData(bytes);
							
							tcert = cont.getCert();
							
						}
					}

				}

			} catch (ConfigurationServiceException ed) {
				if (log.isDebugEnabled())
					log.error(ed);
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.error(e.getMessage());
				e.printStackTrace();
			}
			
            Collection<DNSRecord> records = new ArrayList<DNSRecord>();

            CertdnsForm.setType("CERT");
            CertdnsForm.setCertificate(tcert);
            CertdnsForm.setCertificateData(certbytes);
            records.add(DNSEntryForm.createCertRecord(CertdnsForm));
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}
		
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);

		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/addSOADNSRecord", method = RequestMethod.POST)
	public ModelAndView addSOASetting(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			HttpSession session,
			@ModelAttribute("SoadnsForm") DNSEntryForm SoadnsForm, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter");
		// A records
		if (SoadnsForm != null && !SoadnsForm.getName().equalsIgnoreCase("")
				&& SoadnsForm.getTtl() != 0L
				) {

            Collection<DNSRecord> records = new ArrayList<DNSRecord>();
            records.add(DNSRecordUtils.createSOARecord(SoadnsForm.getName(), SoadnsForm.getTtl(), SoadnsForm.getDomain(), SoadnsForm.getAdmin(), (int)SoadnsForm.getSerial(), SoadnsForm.getRefresh(), SoadnsForm.getRetry(), SoadnsForm.getExpire(), SoadnsForm.getMinimum()));
            
            
            try {
				configSvc.addDNS(records);
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
		}

		ModelAndView mav = new ModelAndView("dns");
		refreshModelFromService(model);
		mav.setViewName("dns");
		if (log.isDebugEnabled())
			log.debug("Exit");
		return mav;
	}

    
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/removesettings", method = RequestMethod.POST)
	public ModelAndView removeAnchors(
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			@ModelAttribute("AdnsForm") DNSEntryForm AdnsForm,
			@ModelAttribute("NSdnsForm") DNSEntryForm NSdnsForm,
			@ModelAttribute("AAdnsForm") DNSEntryForm AAdnsForm,
			@ModelAttribute("CdnsForm") DNSEntryForm CdnsForm,
			@ModelAttribute("CertdnsForm") DNSEntryForm CertdnsForm,
			@ModelAttribute("SrvdnsForm") DNSEntryForm SrvdnsForm,
			@ModelAttribute("SoadnsForm") DNSEntryForm SoadnsForm,
			@ModelAttribute("MXdnsForm") DNSEntryForm MXdnsForm,
			HttpSession session, Model model,
			@RequestParam(value = "submitType") String actionPath) {
    	
		if (log.isDebugEnabled())
			log.debug("Enter domain/removesettings");
		// A records
		try {
			String strid = "" + AdnsForm.getId();
			Collection<DNSRecord> arecords = null;
			if (configSvc != null && AdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteADnsEntries") || actionPath.equalsIgnoreCase("Remove Selected As"))
					&& AdnsForm.getRemove() != null) {

				int cnt = AdnsForm.getRemove().size();
				arecords = configSvc.getDNSByType(DNSType.A.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = AdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = arecords.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}
		// A4 records
		try {
			String strid = "" + AAdnsForm.getId();
			Collection<DNSRecord> a4records = null;
			if (configSvc != null && AAdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteA4DnsEntries") || actionPath.equalsIgnoreCase("Remove Selected A4s"))
					&& AAdnsForm.getRemove() != null) {

				int cnt = AAdnsForm.getRemove().size();
				a4records = configSvc.getDNSByType(DNSType.AAAA.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = AAdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}

		
		// CNAME records
		try {
			String strid = "" + CdnsForm.getId();
			Collection<DNSRecord> a4records = null;
			if (configSvc != null && CdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteCNAMEDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected CNAMEs"))
					&& CdnsForm.getRemove() != null) {

				int cnt = AAdnsForm.getRemove().size();
				a4records = configSvc.getDNSByType(DNSType.CNAME.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = CdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}

		// MX records
		try {
			String strid = "" + MXdnsForm.getId();
			Collection<DNSRecord> a4records = null;
			if (configSvc != null && MXdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteMXDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected MXs"))
					&& MXdnsForm.getRemove() != null) {

				int cnt = MXdnsForm.getRemove().size();
				a4records = configSvc.getDNSByType(DNSType.MX.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = MXdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}
		
		// CERT records
		try {
			String strid = "" + CertdnsForm.getId();
			Collection<DNSRecord> a4records = null;
			if (configSvc != null && CertdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteCERTDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected CERTs"))
					&& CertdnsForm.getRemove() != null) {

				int cnt = CertdnsForm.getRemove().size();
				a4records = configSvc.getDNSByType(DNSType.CERT.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = CertdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}
		
		// SRV records
		try {
			String strid = "" + SrvdnsForm.getId();
			Collection<DNSRecord> a4records = null;
			if (configSvc != null && SrvdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteSRVDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected SRVs"))
					&& SrvdnsForm.getRemove() != null) {

				int cnt = SrvdnsForm.getRemove().size();
				a4records = configSvc.getDNSByType(DNSType.SRV.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = SrvdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}
		
		// SOA records
		try {
			String strid = "" + SoadnsForm.getId();
			Collection<DNSRecord> soarecords = null;
			if (configSvc != null && SoadnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteSOADnsEntries") || actionPath.equalsIgnoreCase("Remove Selected SOAs"))
					&& SoadnsForm.getRemove() != null) {

				int cnt = SoadnsForm.getRemove().size();
				soarecords = configSvc.getDNSByType(DNSType.SOA.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = SoadnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = soarecords.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}

		// NS records
		try {
			String strid = "" + NSdnsForm.getId();
			Collection<DNSRecord> nsrecords = null;
			if (configSvc != null && NSdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteNSDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected NSs"))
					&& NSdnsForm.getRemove() != null) {

				int cnt = NSdnsForm.getRemove().size();
				nsrecords = configSvc.getDNSByType(DNSType.NS.getValue());
				for (int x = 0; x < cnt; x++) {
					String removeid = NSdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator iter = nsrecords.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							configSvc.removeDNSByRecordId(remid);
						}
					}
				}
			}

		} catch (ConfigurationServiceException e1) {
		}
		
		
		// additional post clean up to redisplay

		ModelAndView mav = new ModelAndView("dns");

		if (AdnsForm.getRemove() != null) {
			if (log.isDebugEnabled())
				log.debug("the list of checkboxes checked or not is: "
						+ AdnsForm.getRemove().toString());
		}

		/*
		 * if (configSvc != null && simpleForm != null && actionPath != null &&
		 * actionPath.equalsIgnoreCase("delete") && simpleForm.getRemove() !=
		 * null) { int cnt = simpleForm.getRemove().size(); try{
		 * Collection<String> settingstoberemovedlist = simpleForm.getRemove();
		 * if (log.isDebugEnabled())
		 * log.debug(" Trying to remove settings from database");
		 * configSvc.deleteSetting(settingstoberemovedlist); if
		 * (log.isDebugEnabled())
		 * log.debug(" SUCCESS Trying to remove settings"); } catch
		 * (ConfigurationServiceException e) { if (log.isDebugEnabled())
		 * log.error(e); } }
		 */

		refreshModelFromService(model);

		model.addAttribute("dnsEntryForm", new DNSEntryForm());

		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		
		return mav;
	}           
        
    
	private Collection<SrvRecord> getSrvRecords(int type){
		Collection<SrvRecord> arecords = null;
        try {
        	Collection<DNSRecord> srvrecords = configSvc.getDNSByType(type);
			
		} catch (ConfigurationServiceException e) {
			e.printStackTrace();
		}
		return arecords;
	}

    private Collection<DNSRecord> getDnsRecords(int type){
		Collection<DNSRecord> arecords = null;
        try {
			arecords = configSvc.getDNSByType(type);
			
		} catch (ConfigurationServiceException e) {
			e.printStackTrace();
		}
		return arecords;
	}

    
    public void refreshModelFromService(Model model)
    {
        // GET A RECORDS
        Collection<DNSRecord> arecords = null;
		arecords = getDnsRecords(DNSType.A.getValue());
		
		Collection<DNSEntryForm> aform = new ArrayList<DNSEntryForm>();
		if (arecords != null)
		{
			for (Iterator iter = arecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
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
			for (Iterator iter = a4records.iterator(); iter.hasNext();) {
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
			for (Iterator iter = crecords.iterator(); iter.hasNext();) {
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
			for (Iterator iter = mxrecords.iterator(); iter.hasNext();) {
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
			for (Iterator iter = certrecords.iterator(); iter.hasNext();) {
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
				byte[] bytes = t.getData();
				
				
	    		try {
					CERTRecord newrec = (CERTRecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					String thumb = "";
		            byte[] certData = newrec.getCert();
					if (certData != null) {
						// get the owner from the certificate information
						// first transform into a certificate
						cont = toCertContainer(certData);
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
			for (Iterator iter = srvrecords.iterator(); iter.hasNext();) {
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
			for (Iterator iter = soarecords.iterator(); iter.hasNext();) {
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
			for (Iterator iter = nsrecords.iterator(); iter.hasNext();) {
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
    public String handleIOException(IOException ex, HttpServletRequest request) {
        return ClassUtils.getShortName(ex.getClass() + ":" + ex.getMessage());
    }    
    public CertContainer toCertContainer(byte[] data) throws Exception
    {
        CertContainer certContainer = null;
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);

            // lets try this a as a PKCS12 data stream first
            try
            {
                KeyStore localKeyStore = KeyStore.getInstance("PKCS12", Certificate.getJCEProviderName());

                localKeyStore.load(bais, "".toCharArray());
                Enumeration<String> aliases = localKeyStore.aliases();


                        // we are really expecting only one alias
                        if (aliases.hasMoreElements())
                        {
                                String alias = aliases.nextElement();
                                X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);

                                // check if there is private key
                                Key key = localKeyStore.getKey(alias, "".toCharArray());
                                if (key != null && key instanceof PrivateKey)
                                {
                                        certContainer = new CertContainer(cert, key);

                                }
                        }
            }
            catch (Exception e)
            {
                // must not be a PKCS12 stream, go on to next step
            }

            if (certContainer == null)
            {
                //try X509 certificate factory next
                bais.reset();
                bais = new ByteArrayInputStream(data);

                X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bais);
                certContainer = new CertContainer(cert, null);
            }
            bais.close();
        }
        catch (Exception e)
        {
            throw new ConfigurationServiceException("Data cannot be converted to a valid X.509 Certificate", e);
        }

        return certContainer;
    }

    public static class CertContainer
    {
                private final X509Certificate cert;
        private final Key key;

        public CertContainer(X509Certificate cert, Key key)
        {
                this.cert = cert;
                this.key = key;
        }

        public X509Certificate getCert()
        {
                        return cert;
                }

                public Key getKey()
                {
                        return key;
                }

    }

    private String getEmailAddress(X500Principal prin)
    {
        // get the domain name
                Map<String, String> oidMap = new HashMap<String, String>();
                oidMap.put("1.2.840.113549.1.9.1", "EMAILADDRESS");  // OID for email address
                String prinName = prin.getName(X500Principal.RFC1779, oidMap);

                // see if there is an email address first in the DN
                String searchString = "EMAILADDRESS=";
                int index = prinName.indexOf(searchString);
                if (index == -1)
                {
                        searchString = "CN=";
                        // no Email.. check the CN
                        index = prinName.indexOf(searchString);
                        if (index == -1)
                                return ""; // no CN... nothing else that can be done from here
                }

                // look for a "," to find the end of this attribute
                int endIndex = prinName.indexOf(",", index);
                String address;
                if (endIndex > -1)
                        address = prinName.substring(index + searchString.length(), endIndex);
                else
                        address= prinName.substring(index + searchString.length());

                return address;
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