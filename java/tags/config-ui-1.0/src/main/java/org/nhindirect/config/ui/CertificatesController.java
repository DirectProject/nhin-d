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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.CertificateService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.LoginForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
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
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/certificates")
public class CertificatesController {
	private final Log log = LogFactory.getLog(getClass());
	
	@Inject
	private CertificateService certService;
	
	public CertificatesController(){
		if (log.isDebugEnabled()) log.debug("ConfigurationController initialized");
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/addcertificate", method = RequestMethod.POST)
	public ModelAndView addCertificate (
								@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute CertificateForm certificateForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath
						        ) { 		

		ModelAndView mav = new ModelAndView(); 
		String strid = "";
		if (log.isDebugEnabled()) log.debug("Enter domain/addcertificate");
		
		if(actionPath.equalsIgnoreCase("cancel")){
			if (log.isDebugEnabled()) log.debug("trying to cancel from saveupdate");
			SearchDomainForm form2 = (SearchDomainForm) session
					.getAttribute("searchDomainForm");
			model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
			model.addAttribute("ajaxRequest", AjaxUtils
					.isAjaxRequest(requestedWith));

			mav.setViewName("main");
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
			return mav;
		}
		if(actionPath.equalsIgnoreCase("newcertificate")){
			strid = ""+certificateForm.getId();
			// insert the new address into the Domain list of Addresses
			EntityStatus estatus = certificateForm.getStatus();
			if (log.isDebugEnabled()) log.debug("beginning to evaluate filedata");		
			try{
				if (!certificateForm.getFileData().isEmpty()) {
					byte[] bytes = certificateForm.getFileData().getBytes();
					String owner = "";
					Certificate cert = new Certificate();
					cert.setData(bytes);
					cert.setOwner(owner);
					cert.setStatus(certificateForm.getStatus());

					ArrayList<Certificate> certlist = new ArrayList<Certificate>();
					certlist.add(cert);
					certService.addCertificates(certlist);
					// store the bytes somewhere
					if (log.isDebugEnabled()) log.debug("store the certificate into database");
				} else {
					if (log.isDebugEnabled()) log.debug("DO NOT store the certificate into database BECAUSE THERE IS NO FILE");
				}

			} catch (ConfigurationServiceException ed) {
				if (log.isDebugEnabled())
					log.error(ed);
			} catch (Exception e) {
				if (log.isDebugEnabled()) log.error(e);
				e.printStackTrace();
			}
			// certificate form and result
			try {
				Collection<Certificate> certs = certService.listCertificates(1, 1000, CertificateGetOptions.DEFAULT);
				model.addAttribute("certificatesResults", certs);
				 
				CertificateForm cform = new CertificateForm();
				cform.setId(0);
				model.addAttribute("certificateForm",cform);
				
			} catch (ConfigurationServiceException e1) {
				e1.printStackTrace();
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			SimpleForm simple = new SimpleForm();
			simple.setId(Long.parseLong(strid));
			model.addAttribute("simpleForm",simple);

			mav.setViewName("certificates"); 
			// the Form's default button action
			String action = "Update";

			model.addAttribute("action", action);
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
	
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
		}
		return mav;
	}			
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/removecertifcates", method = RequestMethod.POST)
	public ModelAndView removeCertificates (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute CertificateForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/removecertificates");
		if(simpleForm.getRemove() != null){
			if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		}
		
		if (certService != null && simpleForm != null && actionPath != null && actionPath.equalsIgnoreCase("deletecertificate") && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing certificates");
			try{
				// get list of certificates for this domain
				Collection<Certificate> certs = certService.listCertificates(1, 1000, CertificateGetOptions.DEFAULT);
				ArrayList<Long> certtoberemovedlist = new ArrayList<Long>();
				// now iterate over each one and remove the appropriate ones
				for (int x = 0; x < cnt; x++) {
					String removeid = simpleForm.getRemove().get(x);
					for (Iterator iter = certs.iterator(); iter.hasNext();) {
						   Certificate t = (Certificate) iter.next();
						   //rest of the code block removed
				    	if(t.getId() == Long.parseLong(removeid)){
					    	if (log.isDebugEnabled()){
					    		log.debug(" ");
					    		log.debug("domain address id: " + t.getId());
					    		log.debug(" ");
					    	}
					    	// create a collection of matching anchor ids
					    	certtoberemovedlist.add(t.getId());
					    	break;
				    	}
					}			
				}
				// with the collection of anchor ids now remove them from the anchorService
				if (log.isDebugEnabled()) log.debug(" Trying to remove certificates from database");
				certService.removeCertificates(certtoberemovedlist);
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to update certificates");
			} catch (ConfigurationServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		// BEGIN: temporary code for mocking purposes
		CertificateForm cform = new CertificateForm();
		cform.setId(0);
		model.addAttribute("certificateForm",cform);
		
		mav.setViewName("certificates"); 
		// the Form's default button action
		String action = "Update";
		model.addAttribute("action", action);
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		mav.addObject("action", action);

		Collection<Certificate> certlist = null;
		try {
			certlist = certService.listCertificates(1, 1000, CertificateGetOptions.DEFAULT);
		} catch (ConfigurationServiceException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("certificatesResults", certlist);
		// END: temporary code for mocking purposes			
		mav.addObject("statusList", EntityStatus.getEntityStatusList());

		model.addAttribute("simpleForm",simpleForm);
		String strid = ""+simpleForm.getId();
		if (log.isDebugEnabled()) log.debug(" the value of id of simpleform is: "+strid);
		
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
		return ClassUtils.getShortName(ex.getClass() + ":" + ex.getMessage());
	}
}
