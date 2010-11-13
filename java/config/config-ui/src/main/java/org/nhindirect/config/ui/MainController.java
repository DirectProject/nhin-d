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
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.DomainService;
import org.nhindirect.config.service.SettingService;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.Setting;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.ui.form.AddressForm;
import org.nhindirect.config.ui.form.AnchorForm;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.SettingsForm;
import org.nhindirect.config.service.CertificateService;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.ui.form.SimpleForm;
import org.nhindirect.config.ui.form.LoginForm;
import org.nhindirect.config.ui.form.DomainForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.util.AjaxUtils;
import org.nhindirect.config.ui.flash.FlashMap.Message;
import org.nhindirect.config.ui.flash.FlashMap.MessageType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import org.springframework.security.access.prepost.PreAuthorize;
import org.nhindirect.config.store.Certificate;

@Controller
@RequestMapping("/main")
public class MainController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Inject
	private DomainService dService;
	
	@Inject
	private SettingService settingsService;
	
	@Inject
	private CertificateService certificatesService;
	
	public MainController() {
		if (log.isDebugEnabled()) log.debug("MainController initialized");
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
						        @RequestParam(value="submitType") String actionPath)  { 		
		if (log.isDebugEnabled()) log.debug("Enter search");

		if (log.isDebugEnabled()) log.debug("Enter search");
		String message = "Search complete";
		ModelAndView mav = new ModelAndView();
		// check to see if new domain requested
		if (actionPath.equalsIgnoreCase("gotosettings"))
		{
			if (log.isDebugEnabled()) log.debug("trying to go to the settings page");
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
			if (settingsService != null) {
				try {
					results = new ArrayList<Setting>(settingsService.getAllSettings());
				} catch (ConfigurationServiceException e) {
					e.printStackTrace();
				}
			}
			model.addAttribute("simpleForm",new SimpleForm());
			model.addAttribute("settingsResults", results);
			// display settings.jsp
			return mav;
		}
		
		if (actionPath.equalsIgnoreCase("gotocertificates"))
		{
			if (log.isDebugEnabled()) log.debug("trying to go to the certificates page");
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
			if (certificatesService != null) {
				try {
					results = new ArrayList<Certificate>(certificatesService.listCertificates(1, 1000, CertificateGetOptions.DEFAULT));
				} catch (ConfigurationServiceException e) {
					e.printStackTrace();
				}
			}
			model.addAttribute("simpleForm",new SimpleForm());
			model.addAttribute("certificatesResults", results);
			// display settings.jsp
			return mav;
		}
		
		if (actionPath.equalsIgnoreCase("newdomain"))
		{
			if (log.isDebugEnabled()) log.debug("trying to go to the new domain page");
			HashMap<String, String> msgs = new HashMap<String, String>();
			mav.addObject("msgs", msgs);
			
			model.addAttribute("simpleForm",new SimpleForm());

			AddressForm addrform = new AddressForm();
			addrform.setId(0L);
			model.addAttribute("addressForm",addrform);
			// TODO: once certificates and anchors are available change code accordingly
			CertificateForm cform = new CertificateForm();
			cform.setId(0L);
			AnchorForm aform = new AnchorForm();
			aform.setId(0L);
			
			model.addAttribute("certificateForm",cform);
			model.addAttribute("anchorForm",aform);
			String action = "Add";
			DomainForm form = (DomainForm) session.getAttribute("domainForm");
			if (form == null) {
				form = new DomainForm();
			}
			model.addAttribute("domainForm", form);
			model.addAttribute("action", action);
			
			mav.setViewName("domain");
			mav.addObject("actionPath", actionPath);
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
			return mav;
		}
		

		SearchDomainForm form = (SearchDomainForm) session.getAttribute("searchDomainForm");
		if (form == null) { 
			form = new SearchDomainForm();
		}
		model.addAttribute(form);
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		
		String domain = form.getDomainName();
		EntityStatus status = form.getStatus();
		List<Domain> results = null;
		if (dService != null) {
			results = new ArrayList<Domain>(dService.searchDomain(domain, status));
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

		if (log.isDebugEnabled()) log.debug("Exit");
		return mav;
	}

	
	/**
	 * This method takes a List of domain objects and turns them into a JSON response object
	 * that the jQuery datatable can consume.
	 * @param results
	 * @return
	 */
	private String buildResponse(List<Domain> domains) {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		StringBuffer result = new StringBuffer("{  \"Echo\":");
		result.append(domains.size())
		      .append(",  \"iTotalRecords\": ")
		      .append(domains.size())
		      .append(",  \"iTotalDisplayRecords\": ")
		      .append(domains.size())
		      .append("  \"aaData\": [");
		boolean first = true;
		for (Domain domain : domains) {
			if (!first) {
				result.append(", "); 
				first = false;
			}
			
			result.append("[")
				  .append(xformToJSON(String.valueOf(domain.getId()), false))
				  .append(xformToJSON(domain.getDomainName(), false))
				  .append(xformToJSON(String.valueOf(domain.getPostMasterEmail()), false))
				  .append(xformToJSON(domain.getStatus().toString(), true))
				  .append("] ");
		}
		result.append("] }");
		
		if (log.isDebugEnabled()) log.debug("Exit: " + result.toString());
		return result.toString();
	}

	private String xformToJSON(String item, boolean lastOne) {
		String result = "\"" + item + "\"";
		if (!lastOne) {
			result += ",";
		}
		return result;
	}
	/**
	 * Return the login page when requested
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView display(@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                HttpSession session, 
                                Model model) { 		
		if (log.isDebugEnabled()) log.debug("Enter");
		
		ModelAndView mav = new ModelAndView(); 
		
		SearchDomainForm form = (SearchDomainForm) session.getAttribute("searchDomainForm");
		model.addAttribute(form != null ? form : new SearchDomainForm());
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));

		mav.setViewName("main"); 
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
		
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
	
	/**
	 * Handle exceptions as gracefully as possible
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(IOException.class) 
	public String handleIOException(IOException ex, HttpServletRequest request) {
		//TODO Actually do something useful
		return ClassUtils.getShortName(ex.getClass() + ":" + ex.getMessage());
	}

	public void setdService(DomainService service) {
		this.dService = service;
	}
	
}
