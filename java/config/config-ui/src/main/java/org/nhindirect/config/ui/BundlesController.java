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
import org.nhindirect.config.service.ConfigurationService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.form.BundleForm;
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
import org.nhindirect.config.store.TrustBundle;
import java.security.cert.CertificateException;
import org.nhindirect.config.service.TrustBundleService;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Controller
@RequestMapping("/bundles")
public class BundlesController {
    private final Log log = LogFactory.getLog(getClass());

    private ConfigurationService configSvc;

    @Inject
    public void setConfigurationService(ConfigurationService certService)
    {
        this.configSvc = certService;        
    }

    public BundlesController() {
	if (log.isDebugEnabled()) {
            log.debug("BundlesController initialized");
        }
    }
	
    /*********************************
     *
     * Add Bundle Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/addbundle", method = RequestMethod.POST)
    public ModelAndView addBundle (
            @RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
        HttpSession session,
        @ModelAttribute BundleForm bundleForm,
        Model model,
        @RequestParam(value="submitType") String actionPath
    ) 
    { 		

        ModelAndView mav = new ModelAndView(); 
        String strid = "";

        // Debug Statement
        if (log.isDebugEnabled()) log.debug("Enter Add Trust Bundle");

        if(actionPath.equalsIgnoreCase("cancel"))
        {
                if (log.isDebugEnabled()) 
                {
                        log.debug("trying to cancel from saveupdate");
                }

                // If cancel then clear form	
                SearchDomainForm form2 = (SearchDomainForm) session.getAttribute("searchDomainForm");
                model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
                model.addAttribute("ajaxRequest", AjaxUtils
                                .isAjaxRequest(requestedWith));

                mav.setViewName("main");
                mav.addObject("statusList", EntityStatus.getEntityStatusList());
                return mav;
        }

        

        if(actionPath.equalsIgnoreCase("newbundle") || actionPath.equalsIgnoreCase("add bundle"))
        {

            strid = ""+bundleForm.getId();
            boolean formValidated = true;

            if (log.isDebugEnabled()) 
            {
                    log.debug("Beginning to process signing certificate file");		
            }



            model.addAttribute("signingCertError", false);
            model.addAttribute("URLError", false);

            TrustBundle trustBundle = new TrustBundle();

            trustBundle.setBundleName(bundleForm.getBundleName());
            trustBundle.setRefreshInterval(bundleForm.getRefreshInterval()*3600);	// Convert Hours to Seconds for backend

            
            // Check if signing certificate is uploaded
            if (!bundleForm.getFileData().isEmpty()) 
            {
                
                byte[] bytes = bundleForm.getFileData().getBytes();

                String fileType = bundleForm.getFileData().getContentType();

                if(!fileType.matches("application/x-x509-ca-cert") && 
                    !fileType.matches("application/x-x509-user-cert") &&
                    !fileType.matches("application/pkix-cert"))
                {		
                    model.addAttribute("signingCertError", true);	
                    formValidated = false;
                } else {                        
                    try {
                        trustBundle.setSigningCertificateData(bytes);
                    } catch (Exception ce) {

                    }	
                } 	                    
            } else {
                if (log.isDebugEnabled()) log.debug("DO NOT store the bundle into database BECAUSE THERE IS NO FILE");
            }

            // Check for valid URL
            URL u = null;
            String trustURL = bundleForm.getTrustURL();
                        
            try {                
                u = new URL(trustURL);
            } catch (MalformedURLException mu) {
                model.addAttribute("URLError", true);
                formValidated = false;
            }                        
            
            if(formValidated) 
            {
            
            
                trustBundle.setBundleURL(trustURL);

                try {

                    trustBundle.setCheckSum("");

                    configSvc.addTrustBundle(trustBundle);                

                    if (log.isDebugEnabled())
                    {
                        log.debug("Add Trust Bundle to Database");
                    }

                } catch (Exception e) {
                        if (log.isDebugEnabled()) log.error(e);
                        e.printStackTrace();
                }

                

                BundleForm bform = new BundleForm();

                model.addAttribute("bundleForm",bform);
            }
            
            // Process data for Trust Bundle View
            try {
                    /*Collection<Certificate> certs = configSvc.listCertificates(1, 1000, CertificateGetOptions.DEFAULT);
                    model.addAttribute("certificatesResults", certs);

                    CertificateForm cform = new CertificateForm();
                    cform.setId(0L);
                    model.addAttribute("certificateForm",cform);*/
                // Get Trust Bundles
                Collection<TrustBundle> trustBundles = configSvc.getTrustBundles(false);

                if(trustBundles != null) {
                    model.addAttribute("trustBundles", trustBundles);
                }


            } catch (ConfigurationServiceException e1) {
                    e1.printStackTrace();
            }    
            
            model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));            

            mav.setViewName("bundles");                         

            mav.addObject("statusList", EntityStatus.getEntityStatusList());
        }
        
        return mav;
    }			
	
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/removebundle", method = RequestMethod.POST)
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
		
		if (configSvc != null && simpleForm != null && actionPath != null && (actionPath.equalsIgnoreCase("deletebundle") || actionPath.equalsIgnoreCase("Remove Selected")) && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing certificates");
			try{
				// get list of certificates for this domain
				Collection<Certificate> certs = configSvc.listCertificates(1, 1000, CertificateGetOptions.DEFAULT);
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
				configSvc.removeCertificates(certtoberemovedlist);
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
			certlist = configSvc.listCertificates(1, 1000, CertificateGetOptions.DEFAULT);
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
