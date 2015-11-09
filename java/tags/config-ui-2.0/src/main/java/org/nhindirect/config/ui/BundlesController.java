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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.config.rest.TrustBundleService;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.TrustBundle;
import org.nhindirect.config.model.TrustBundleDomainReltn;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.form.BundleForm;
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
import java.net.MalformedURLException;
import java.net.URL;


@Controller
@RequestMapping("/bundles")
public class BundlesController {
    private final Log log = LogFactory.getLog(getClass());

	private TrustBundleService bundleService;
	
	@Inject
	public void setCertificateServiceService(TrustBundleService bundleService)
    {
        this.bundleService = bundleService;
    }
    
    public BundlesController() {
	if (log.isDebugEnabled()) {
            log.error("BundlesController initialized");
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

        final ModelAndView mav = new ModelAndView();        

        // Debug Statement
        if (log.isDebugEnabled()) log.debug("Enter Add Trust Bundle");

        if(actionPath.equalsIgnoreCase("cancel"))
        {
                if (log.isDebugEnabled()) 
                {
                        log.debug("trying to cancel from saveupdate");
                }

                // If cancel then clear form	
                final SearchDomainForm form2 = (SearchDomainForm) session.getAttribute("searchDomainForm");
                model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
                model.addAttribute("ajaxRequest", AjaxUtils
                                .isAjaxRequest(requestedWith));

                mav.setViewName("main");
                mav.addObject("statusList", EntityStatus.getEntityStatusList());
                return mav;
        }

        

        if(actionPath.equalsIgnoreCase("newbundle") || actionPath.equalsIgnoreCase("add bundle"))
        {
            
            Boolean formValidated = true;

            if (log.isDebugEnabled()) 
            {
                    log.debug("Beginning to process signing certificate file");		
            }



            model.addAttribute("signingCertError", false);
            model.addAttribute("URLError", false);

            final TrustBundle trustBundle = new TrustBundle();
            
            String bundleName = bundleForm.getBundleName();

            trustBundle.setBundleName(bundleName);
            trustBundle.setRefreshInterval(bundleForm.getRefreshInterval()*3600);	// Convert Hours to Seconds for backend

            
            // Check if signing certificate is uploaded
            if (!bundleForm.getFileData().isEmpty()) 
            {
                
                byte[] bytes = bundleForm.getFileData().getBytes();

                final String fileType = bundleForm.getFileData().getContentType();

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
            
            // Check for empty bundle name
            if(bundleName.isEmpty()) 
            {
                model.addAttribute("EmptyBundleError", true);
                formValidated = false;
            } else 
            {
                // Check if trust bundle name is already used
                TrustBundle dupeBundle = null;
                try 
                {
                    dupeBundle = bundleService.getTrustBundle(bundleName);
                } catch (ServiceException cse) 
                {
                    log.error("Could not get bundle information from config service");
                }                        

                if(dupeBundle != null) 
                {
                    model.addAttribute("DupeBundleError", true);
                    formValidated = false;
                }
            }

            // Check for valid URL
            final String trustURL = bundleForm.getTrustURL();
                        
            try {                
                new URL(trustURL);
            } catch (MalformedURLException mu) {
                model.addAttribute("URLError", true);
                formValidated = false;
            }                        
            
            
            
            
            if(formValidated) 
            {            
            
                trustBundle.setBundleURL(trustURL);

                try {

                    trustBundle.setCheckSum("");

                    bundleService.addTrustBundle(trustBundle);                

                    if (log.isDebugEnabled())
                    {
                        log.debug("Add Trust Bundle to Database");
                    }

                } catch (Exception e) {
                        if (log.isDebugEnabled()) log.error(e);
                        e.printStackTrace();
                }

                

                final BundleForm bform = new BundleForm();

                model.addAttribute("bundleForm",bform);
            }
            
            // Process data for Trust Bundle View
            try {
                    
                // Get Trust Bundles
                final Collection<TrustBundle> trustBundles = bundleService.getTrustBundles(false);

                if(trustBundles != null) {
                    
                                                                                                                       
                    model.addAttribute("trustBundles", trustBundles);
                }


            } catch (ServiceException e1) {
                
            }    
            
            model.addAttribute("bundlesSelected");
            model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));            
            mav.setViewName("bundles");                         
        }
        
        return mav;
    }			
	
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/removebundle", method = RequestMethod.POST)
    public ModelAndView removeCertificates (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    Model model)  { 		

        final ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter bundles/removebundle");
        }
        
        if(simpleForm.getBundlesSelected() != null)
        {
            if (log.isDebugEnabled()) 
            {
                log.debug("Bundles marked for removal: "+simpleForm.getBundlesSelected().toString());
            }
        }

        if (bundleService != null 
                && simpleForm != null 
                && simpleForm.getBundlesSelected() != null) 
        {
            
            final int bundleCount = simpleForm.getBundlesSelected().size();

            if (log.isDebugEnabled()) 
            {
                log.debug("Removing Bundles");
            }
            
            for(int i=0; i<bundleCount; i++) 
            {
                final String bundleName = simpleForm.getBundlesSelected().get(i);
                log.error(bundleName);
                
                // Delete Trust Bundle(s)
                try 
                {
                    bundleService.deleteTrustBundle(bundleName);
                } catch (ServiceException cse) 
                {
                    log.error("Problem removing bundles");
                }
                
            }
            

            
        }
        
        model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
        
        final BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("bundles"); 
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            final Collection<TrustBundle> trustBundles = bundleService.getTrustBundles(false);

            if(trustBundles != null) {
                model.addAttribute("trustBundles", trustBundles);
            }


        } catch (ServiceException e1) {

        }                            

        return mav;
    }		

    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/refreshBundles", method = RequestMethod.POST)
    public ModelAndView refreshBundles (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    Model model)  { 		

        final ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter bundles/refreshbundles");
        }
        
        
        
        if(simpleForm.getBundlesSelected() != null)
        {
            if (log.isDebugEnabled()) 
            {
                log.debug("Bundles marked for refresh: "+simpleForm.getBundlesSelected().toString());
            }
        }

        if (bundleService != null 
                && simpleForm != null 
                && simpleForm.getBundlesSelected() != null) 
        {
            
            int bundleCount = simpleForm.getBundlesSelected().size();            

            if (log.isDebugEnabled()) 
            {
                log.debug("Refreshing Bundles");
            }
            
            for(int i=0; i<bundleCount; i++) 
            {
                final String bundleName = simpleForm.getBundlesSelected().get(i);
                log.debug("Refreshing Bundle #"+bundleName);
                                
                // Refresh Trust Bundle(s)
                try 
                {
                    bundleService.refreshTrustBundle(bundleName);
                } catch (ServiceException cse) {
                    log.error("Could not refresh bundle: #"+bundleName);
                }
                
            }
                                    
        }
        
        model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
        
        final BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("bundles"); 
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            final Collection<TrustBundle> trustBundles = bundleService.getTrustBundles(false);

            if(trustBundles != null) {
                model.addAttribute("trustBundles", trustBundles);
            }


        } catch (ServiceException e1) {

        }                            
        return new ModelAndView("redirect:/config/main/search?domainName=&submitType=ManageTrustBundles");        
    }		

		
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/assignBundlesForm", method = RequestMethod.GET)
    public ModelAndView assignBundlesForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,                                                     
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter bundles/assignBundles");
        }    
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            final Collection<TrustBundle> trustBundles = bundleService.getTrustBundles(false);
            
            if(trustBundles != null) {
                model.addAttribute("trustBundles", trustBundles);
            }


        } catch (ServiceException e1) {

        }                                        
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        bform.setDomainName((String)session.getAttribute("currentDomainName"));
        model.addAttribute("bundleForm", bform);
        mav.setViewName("assignBundlesForm");
        
        return mav;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/addMoreBundlesForm", method = RequestMethod.GET)
    public ModelAndView addMoreBundlesForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,                                                     
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    @RequestParam(value="domainName") String domainName,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter bundles/addMoreBundlesForm");
        }    
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            final Collection<TrustBundle> trustBundles = new ArrayList<TrustBundle>();  
            Collection<TrustBundle> newBundles = new ArrayList<TrustBundle>();  
            final Collection<TrustBundleDomainReltn> bundleRelationships = bundleService.getTrustBundlesByDomain(domainName, false);
            final Collection<TrustBundle> allBundles = bundleService.getTrustBundles(false);
            boolean bundleMatch = false;
            
            if( bundleRelationships != null && !bundleRelationships.isEmpty()) {
                
                
                
               
 
                for(TrustBundleDomainReltn relationship : bundleRelationships) {                                   
                    
                    trustBundles.add(relationship.getTrustBundle());                                                                         

                }
                
                for(TrustBundle bundle : allBundles) {
                    bundleMatch = false;
                
                    for(TrustBundle subBundle : trustBundles) {
                        if(subBundle.getId() == bundle.getId()) {
                            bundleMatch = true;
                        }
                    }
                    if(!bundleMatch) {
                        newBundles.add(bundle);
                    }
                }
                
                
            } else { 
                
                newBundles = bundleService.getTrustBundles(false);
                
            }
                       
            //if(trustBundles != null) {
                model.addAttribute("trustBundles", newBundles);
            //}


        } catch (ServiceException e1) {

        }                               
        
        model.addAttribute("domainName", domainName);
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        bform.setDomainName((String)session.getAttribute("currentDomainName"));
        model.addAttribute("bundleForm", bform);
        mav.setViewName("addMoreBundlesForm");
        
        return mav;
    }
    
        @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/newBundleForm", method = RequestMethod.GET)
    public ModelAndView newBundleForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,                                                     
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter bundles/newBundles");
        }    
        
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("newBundleForm");
        
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
