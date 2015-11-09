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
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.security.cert.CertificateEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.DNSService;
import org.nhind.config.rest.SettingService;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.DNSRecord;
import org.nhindirect.config.model.Setting;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.util.DNSRecordUtils;

import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.DNSEntryForm;
import org.nhindirect.config.ui.form.DNSType;
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
	private static final String DEFAULT_JCE_PROVIDER_STRING = "BC";
	private static final String JCE_PROVIDER_STRING_SYS_PARAM = "org.nhindirect.config.JCEProviderName";	
	
    private final Log log = LogFactory.getLog(getClass());
    
	private CertificateService certService;
	private DNSService dnsService;
	private SettingService settingsService;
	
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
    
	/**
	 * Gets the configured JCE crypto provider string for crypto operations.  This is configured using the
	 * -Dorg.nhindirect.config.JCEProviderName JVM parameters.  If the parameter is not set or is empty,
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
	
    public DNSController()
    {
        if (log.isDebugEnabled()) log.debug("ConfigurationController initialized");
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
        
        final ModelAndView mav = new ModelAndView();
        
        if ("gotodomains".equalsIgnoreCase(actionPath) || "domains".equalsIgnoreCase(actionPath))
        {
            final SearchDomainForm form2 = (SearchDomainForm) session
                    .getAttribute("searchDomainForm");
            model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
            model.addAttribute("ajaxRequest", AjaxUtils
                    .isAjaxRequest(requestedWith));
    
            mav.setViewName("main");
            mav.addObject("statusList", EntityStatus.getEntityStatusList());
        }
        else if ("gotosettings".equalsIgnoreCase(actionPath) || "settings".equalsIgnoreCase(actionPath))
        {
            final String action = "add";
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
            if (settingsService != null) {
                try {
                	final Collection<Setting> settings = settingsService.getSettings();
                	if (settings != null)
                		results = new ArrayList<Setting>(settings);
                	else
                		results = new ArrayList<Setting>();                		
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            model.addAttribute("simpleForm",new SimpleForm());
            model.addAttribute("settingsResults", results);
        }
        else if (actionPath.equalsIgnoreCase("gotocertificates") || actionPath.equalsIgnoreCase("certificates"))
        {           
            final String action = "Update";
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
            if (certService != null) {
                try {
                	final Collection<Certificate> certs = certService.getAllCertificates();
                	if (certs != null)
                		results = new ArrayList<Certificate>(certs);
                	else
                		results = new ArrayList<Certificate>();
                } catch (ServiceException e) {
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
        
        final ModelAndView mav = new ModelAndView("dns");
        model.addAttribute("dnsEntryForm", new DNSEntryForm());
       
        if (dnsService != null)
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
			@RequestParam(value = "submitType") String actionPath) throws ServiceException {
    	
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
            
            try {
				dnsService.addDNSRecord(DNSEntryForm.entityToModelRecord(DNSRecordUtils.createARecord(AdnsForm.getName(), AdnsForm.getTtl(), AdnsForm.getDest())));
			} catch (ServiceException e) {
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
            
            try {
				dnsService.addDNSRecord(DNSEntryForm.createNSRecord(NSdnsForm.getName(), NSdnsForm.getTtl(), NSdnsForm.getDest()));
			} catch (ServiceException e) {
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
            
            try {
				dnsService.addDNSRecord(DNSEntryForm.createA4Record(AAdnsForm.getName(), AAdnsForm.getTtl(), AAdnsForm.getDest()));
			} catch (ServiceException e) {
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
            
            
            try {
				dnsService.addDNSRecord(
						DNSEntryForm.entityToModelRecord(DNSRecordUtils.createMXRecord(MXdnsForm.getName(), MXdnsForm.getDest(), MXdnsForm.getTtl(), MXdnsForm.getPriority())));
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("AdnsForm", new DNSEntryForm());
		model.addAttribute("AAdnsForm", new DNSEntryForm());
		model.addAttribute("CdnsForm", new DNSEntryForm());
		model.addAttribute("MXdnsForm", new DNSEntryForm());
		model.addAttribute("CertdnsForm", new DNSEntryForm());
		model.addAttribute("SrvdnsForm", new DNSEntryForm());

		final ModelAndView mav = new ModelAndView("dns");
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
            
            try {
				dnsService.addDNSRecord(DNSEntryForm.toDNSRecord(CdnsForm));
			} catch (ServiceException e) {
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

            final DNSRecord rec = DNSEntryForm.entityToModelRecord(DNSRecordUtils.createSRVRecord("_"+SrvdnsForm.getService()+"._"+SrvdnsForm.getProtocol()+"."+SrvdnsForm.getName(), 
            		SrvdnsForm.getDest(), 
            		SrvdnsForm.getTtl(), 
            		SrvdnsForm.getPort(),
            		SrvdnsForm.getPriority(),
            		SrvdnsForm.getWeight()));
            
            try {
				dnsService.addDNSRecord(rec);
			} catch (ServiceException e) {
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


			X509Certificate tcert = null;
			byte[] certbytes = null; 
			try {
				if (!CertdnsForm.getFileData().isEmpty()) {
					byte[] bytes = CertdnsForm.getFileData().getBytes();
					
					certbytes = bytes;

					if (bytes != null) {
						// get the owner from the certificate information
						// first transform into a certificate
						final CertContainer cont = toCertContainer(bytes);
						if (cont != null && cont.getCert() != null) {

							final Certificate cert2 = new Certificate();
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

            CertdnsForm.setType("CERT");
            CertdnsForm.setCertificate(tcert);
            CertdnsForm.setCertificateData(certbytes);

            try {
				dnsService.addDNSRecord(DNSEntryForm.createCertRecord(CertdnsForm));
			} catch (ServiceException e) {
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
            
            final DNSRecord rec= DNSEntryForm.entityToModelRecord(DNSRecordUtils.createSOARecord(SoadnsForm.getName(), SoadnsForm.getTtl(), SoadnsForm.getDomain(), SoadnsForm.getAdmin(),
            		(int)SoadnsForm.getSerial(), SoadnsForm.getRefresh(), SoadnsForm.getRetry(), SoadnsForm.getExpire(), SoadnsForm.getMinimum()));
            
            try {
				dnsService.addDNSRecord(rec);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}

		final ModelAndView mav = new ModelAndView("dns");
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
			Collection<DNSRecord> arecords = null;
			if (dnsService != null && AdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteADnsEntries") || actionPath.equalsIgnoreCase("Remove Selected As"))
					&& AdnsForm.getRemove() != null) {

				int cnt = AdnsForm.getRemove().size();
				arecords = dnsService.getDNSRecord(DNSType.A.getValue(),"");
				for (int x = 0; x < cnt; x++) {
					String removeid = AdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
		}
		// A4 records
		try {
			Collection<DNSRecord> a4records = null;
			if (dnsService != null && AAdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteA4DnsEntries") || actionPath.equalsIgnoreCase("Remove Selected A4s"))
					&& AAdnsForm.getRemove() != null) {

				int cnt = AAdnsForm.getRemove().size();
				a4records = dnsService.getDNSRecord(DNSType.AAAA.getValue(), "");
				for (int x = 0; x < cnt; x++) {
					String removeid = AAdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
		}

		
		// CNAME records
		try {

			Collection<DNSRecord> a4records = null;
			if (dnsService != null && CdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteCNAMEDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected CNAMEs"))
					&& CdnsForm.getRemove() != null) {

				int cnt = AAdnsForm.getRemove().size();
				a4records = dnsService.getDNSRecord(DNSType.CNAME.getValue(), "");
				for (int x = 0; x < cnt; x++) {
					String removeid = CdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
		}

		// MX records
		try {
			Collection<DNSRecord> a4records = null;
			if (dnsService != null && MXdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteMXDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected MXs"))
					&& MXdnsForm.getRemove() != null) {

				int cnt = MXdnsForm.getRemove().size();
				a4records = dnsService.getDNSRecord(DNSType.MX.getValue(), "");
				for (int x = 0; x < cnt; x++) {
					String removeid = MXdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
		}
		
		// CERT records
		try {
			Collection<DNSRecord> a4records = null;
			if (dnsService != null && CertdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteCERTDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected CERTs"))
					&& CertdnsForm.getRemove() != null) {

				int cnt = CertdnsForm.getRemove().size();
				a4records = dnsService.getDNSRecord(DNSType.CERT.getValue(), "");
				for (int x = 0; x < cnt; x++) {
					String removeid = CertdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
		}
		
		// SRV records
		try {
			Collection<DNSRecord> a4records = null;
			if (dnsService != null && SrvdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteSRVDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected SRVs"))
					&& SrvdnsForm.getRemove() != null) {

				int cnt = SrvdnsForm.getRemove().size();
				a4records = dnsService.getDNSRecord(DNSType.SRV.getValue(), "");
				for (int x = 0; x < cnt; x++) {
					String removeid = SrvdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = a4records.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
		}
		
		// SOA records
		try {
			Collection<DNSRecord> soarecords = null;
			if (dnsService != null && SoadnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteSOADnsEntries") || actionPath.equalsIgnoreCase("Remove Selected SOAs"))
					&& SoadnsForm.getRemove() != null) {

				int cnt = SoadnsForm.getRemove().size();
				soarecords = dnsService.getDNSRecord(DNSType.SOA.getValue(), "");
				for (int x = 0; x < cnt; x++) {
					String removeid = SoadnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = soarecords.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
		}

		// NS records
		try {
			Collection<DNSRecord> nsrecords = null;
			if (dnsService != null && NSdnsForm != null && actionPath != null
					&& (actionPath.equalsIgnoreCase("deleteNSDnsEntries") || actionPath.equalsIgnoreCase("Remove Selected NSs"))
					&& NSdnsForm.getRemove() != null) {

				int cnt = NSdnsForm.getRemove().size();
				nsrecords = dnsService.getDNSRecord(DNSType.NS.getValue(), "");
				for (int x = 0; x < cnt; x++) {
					String removeid = NSdnsForm.getRemove().get(x);
					Long remid = Long.parseLong(removeid);
					for (Iterator<DNSRecord> iter = nsrecords.iterator(); iter.hasNext();) {
						DNSRecord t = (DNSRecord) iter.next();
						if (t.getId() == remid) {
							dnsService.deleteDNSRecordsByIds(Arrays.asList(remid));
						}
					}
				}
			}

		} catch (ServiceException e1) {
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

    private Collection<DNSRecord> getDnsRecords(int type){
		Collection<DNSRecord> arecords = null;
        try {
			arecords = dnsService.getDNSRecord(type, "");
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return arecords;
	}

    
    public void refreshModelFromService(Model model)
    {
        // GET A RECORDS
        Collection<DNSRecord> arecords = null;
		arecords = getDnsRecords(DNSType.A.getValue());
		
		final Collection<DNSEntryForm> aform = new ArrayList<DNSEntryForm>();
		if (arecords != null)
		{
			for (Iterator<DNSRecord> iter = arecords.iterator(); iter.hasNext();) {
				final DNSRecord t = (DNSRecord) iter.next();
				try {
					final ARecord newrec = (ARecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					final DNSEntryForm tmp = new DNSEntryForm();
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
		final Collection<DNSEntryForm> a4form = new ArrayList<DNSEntryForm>();
		if (a4records != null)
		{
			for (Iterator<DNSRecord> iter = a4records.iterator(); iter.hasNext();) {
				final DNSRecord t = (DNSRecord) iter.next();
				try {
					final AAAARecord newrec = (AAAARecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					final DNSEntryForm tmp = new DNSEntryForm();
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
		final Collection<DNSEntryForm> cform = new ArrayList<DNSEntryForm>();
		if (crecords != null)
		{
			for (Iterator<DNSRecord> iter = crecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					final CNAMERecord newrec = (CNAMERecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					final DNSEntryForm tmp = new DNSEntryForm();
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
		final Collection<DNSEntryForm> mxform = new ArrayList<DNSEntryForm>();
		if (mxrecords != null)
		{
			for (Iterator<DNSRecord> iter = mxrecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					final MXRecord newrec = (MXRecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					final DNSEntryForm tmp = new DNSEntryForm();
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
		final Collection<SrvRecord> form = new ArrayList<SrvRecord>();
		CertContainer cont;
		if (certrecords != null)
		{
			for (Iterator<DNSRecord> iter = certrecords.iterator(); iter.hasNext();) {
				final DNSRecord t = (DNSRecord) iter.next();
	
				final SrvRecord srv = new SrvRecord();
				srv.setCreateTime(t.getCreateTime());
				srv.setData(t.getData());
				srv.setDclass(t.getDclass());
				srv.setId(t.getId());
				srv.setName(t.getName());
				srv.setTtl(t.getTtl());
				srv.setType(t.getType());
				srv.setThumb("");
				
	    		try {
	    			final CERTRecord newrec = (CERTRecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
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
		final Collection<SrvRecord> form2 = new ArrayList<SrvRecord>();
		if (srvrecords != null)
		{
			for (Iterator<DNSRecord> iter = srvrecords.iterator(); iter.hasNext();) {
				final DNSRecord t = (DNSRecord) iter.next();
				final SrvRecord srv = new SrvRecord();
				try {
					SRVRecord srv4 = (SRVRecord) SRVRecord.newRecord(Name
							.fromString(t.getName()), t.getType(), t.getDclass(), t
							.getTtl(), t.getData());
	
					srv.setCreateTime(t.getCreateTime());
					srv.setData(t.getData());
					srv.setDclass(t.getDclass());
					srv.setId(t.getId());
					srv.setName(t.getName());
					final String name = t.getName();
					// parse the name to get service, protocol, priority , weight,
					// port
	
					int firstpos = name.indexOf("_");
					if (firstpos == 0) {
						// then this can be parsed as a srv record
						// ("_"+SrvdnsForm.getService()+"._"+SrvdnsForm.getProtocol()+"._"+SrvdnsForm.getPriority()+"._"+SrvdnsForm.getWeight()+"._"+SrvdnsForm.getPort()+"._"+SrvdnsForm.getDest()+"."+SrvdnsForm.getName()
						int secondpos = name.indexOf("._");
						int thirdpos = name.indexOf(".", secondpos + 2);
						// from first to second is service
						final String service_ = name.substring(firstpos + 1, secondpos);
						srv.setService(service_);
						// from second to third is protocol
						final String protocol_ = name.substring(secondpos + 2, thirdpos);
						;
						srv.setProtocol(protocol_);
						int last2pos = name.indexOf(".", thirdpos);
						final String name_ = name.substring(last2pos+1, name.length());
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
		final Collection<DNSEntryForm> soaform = new ArrayList<DNSEntryForm>();
		if (soarecords != null)
		{
			for (Iterator<DNSRecord> iter = soarecords.iterator(); iter.hasNext();) {
				DNSRecord t = (DNSRecord) iter.next();
				try {
					final SOARecord newrec = (SOARecord)Record.newRecord(Name.fromString(t.getName()), t.getType(), t.getDclass(), t.getTtl(), t.getData());
					final DNSEntryForm tmp = new DNSEntryForm();
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
        final Collection<DNSEntryForm> nsform = new ArrayList<DNSEntryForm>();
		if (nsrecords != null)
		{
			for (Iterator<DNSRecord> iter = nsrecords.iterator(); iter.hasNext();) {
				final DNSRecord t = (DNSRecord) iter.next();
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
            	final KeyStore localKeyStore = KeyStore.getInstance("PKCS12", getJCEProviderName());

                localKeyStore.load(bais, "".toCharArray());
                final Enumeration<String> aliases = localKeyStore.aliases();


                        // we are really expecting only one alias
                        if (aliases.hasMoreElements())
                        {
                                String alias = aliases.nextElement();
                                X509Certificate cert = (X509Certificate)localKeyStore.getCertificate(alias);

                                // check if there is private key
                                final Key key = localKeyStore.getKey(alias, "".toCharArray());
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