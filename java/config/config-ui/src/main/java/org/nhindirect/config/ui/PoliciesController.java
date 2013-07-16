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
import org.nhindirect.config.ui.form.PolicyForm;
import org.nhindirect.config.store.CertPolicy;
import org.nhindirect.config.ui.form.SimpleForm;
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
import org.nhindirect.config.store.TrustBundle;
import java.security.cert.CertificateException;
import org.nhindirect.config.service.TrustBundleService;
import org.nhindirect.config.store.TrustBundleDomainReltn;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.policy.PolicyLexicon;
import java.util.Random;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
@RequestMapping("/policies")
public class PoliciesController {
    private final Log log = LogFactory.getLog(getClass());

    private ConfigurationService configSvc;

    @Inject
    public void setConfigurationService(ConfigurationService certService)
    {
        this.configSvc = certService;        
    }

    public PoliciesController() {
	if (log.isDebugEnabled()) {
            log.error("PoliciesController initialized");
        }
    }
	
    /*********************************
     *
     * Add Policy Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/addpolicy", method = RequestMethod.POST)
    public ModelAndView addPolicy (
            @RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
            HttpSession session,
            @ModelAttribute PolicyForm policyForm,
            Model model,
            @RequestParam(value="submitType") String actionPath
    ) 
    { 		

        ModelAndView mav = new ModelAndView();        

        // Debug Statement
        if (log.isDebugEnabled()) log.debug("Enter Add Policy");

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

        

        if(actionPath.equalsIgnoreCase("newpolicy") || actionPath.equalsIgnoreCase("add policy"))
        {
            
            Boolean formValidated = true;

            if (log.isDebugEnabled()) 
            {
                    log.debug("Beginning to add new policy");		
            }

            try {
                URL configURL = new URL("http://localhost:8081/config-service/ConfigurationService");
            } catch (MalformedURLException ue) {

            }

            

            model.addAttribute("badPolicyNameError", false);
            model.addAttribute("badPolicyFileError", false);

            CertPolicy newPolicy = new CertPolicy();
            
            newPolicy.setPolicyName(policyForm.getPolicyName());
            
            //log.error("Lexicon:"+policyForm.getPolicyLexicon().toString());
                        
            
            // Set policy lexicon type
            
            String lexiconName = "XML";//policyForm.getPolicyLexicon().toString();
            
            if(lexiconName.equalsIgnoreCase("XML")) {
                newPolicy.setLexicon(PolicyLexicon.XML);
            } else if (lexiconName.equalsIgnoreCase("JAVA_SER")) {
                newPolicy.setLexicon(PolicyLexicon.JAVA_SER);
            } else if (lexiconName.equalsIgnoreCase("SIMPLE_TEXT_V1")) {
                newPolicy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
            }                   
            
            // Get lexicon file from form
            CommonsMultipartFile lexiconFile = policyForm.getFileData();            
            newPolicy.setPolicyData(lexiconFile.getBytes());

            log.error(newPolicy); // Debug
           
            try {
                configSvc.addPolicy(newPolicy);
            } catch (ConfigurationServiceException cse) {
                cse.printStackTrace();
            }
            
            
            
            Collection policies = null;
                
            try {
                policies = configSvc.getPolicies();
            } catch (Exception e) {
                System.out.println("Failed to lookup policies: " + e.getMessage());
            }
            
            if(policies != null) {
                model.addAttribute("policies", policies);
                log.error(policies);
            } else {                    
                model.addAttribute("policies", "");
            }
            
            //TrustBundle trustBundle = new TrustBundle();
            
            /*
            
            String bundleName = policyForm.getBundleName();

            trustBundle.setBundleName(bundleName);
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
                    dupeBundle = configSvc.getTrustBundleByName(bundleName);
                } catch (ConfigurationServiceException cse) 
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
                    
                // Get Trust Bundles
                Collection<TrustBundle> trustBundles = configSvc.getTrustBundles(false);

                if(trustBundles != null) {
                    
                                                                                                                       
                    model.addAttribute("trustBundles", trustBundles);
                }


            } catch (ConfigurationServiceException e1) {
                
            } 
             
            */
            
            //model.addAttribute("bundlesSelected");
            model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));            
            mav.setViewName("policies");                         
        }
        
        return mav;
    }			
	
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/removebundle", method = RequestMethod.POST)
    public ModelAndView removeCertificates (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

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

        if (configSvc != null 
                && simpleForm != null 
                && simpleForm.getBundlesSelected() != null) 
        {
            
            int bundleCount = simpleForm.getBundlesSelected().size();
            long[] bundlesSelected = new long[bundleCount];

            if (log.isDebugEnabled()) 
            {
                log.debug("Removing Bundles");
            }
            
            for(int i=0; i<bundleCount; i++) 
            {
                String bundleId = simpleForm.getBundlesSelected().get(i);
                log.error(bundleId);
                
                bundlesSelected[i] = Long.parseLong(bundleId);
                
            }
            
            // Delete Trust Bundle(s)
            try 
            {
                configSvc.deleteTrustBundles(bundlesSelected);
            } catch (ConfigurationServiceException cse) 
            {
                log.error("Problem removing bundles");
            }
            
        }
        
        model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("bundles"); 
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            Collection<TrustBundle> trustBundles = configSvc.getTrustBundles(false);

            if(trustBundles != null) {
                model.addAttribute("trustBundles", trustBundles);
            }


        } catch (ConfigurationServiceException e1) {

        }                            

        return mav;
    }		

    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/refreshBundles", method = RequestMethod.POST)
    public ModelAndView refreshBundles (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

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

        if (configSvc != null 
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
                String bundleId = simpleForm.getBundlesSelected().get(i);
                log.debug("Refreshing Bundle #"+bundleId);
                                
                // Refresh Trust Bundle(s)
                try 
                {
                    configSvc.refreshTrustBundle(Long.parseLong(bundleId));
                } catch (ConfigurationServiceException cse) {
                    log.error("Could not refresh bundle: #"+bundleId);
                }
                
            }
                                    
        }
        
        model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("bundles"); 
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            Collection<TrustBundle> trustBundles = configSvc.getTrustBundles(false);

            if(trustBundles != null) {
                model.addAttribute("trustBundles", trustBundles);
            }


        } catch (ConfigurationServiceException e1) {

        }                            
        return new ModelAndView("redirect:/config/main/search?domainName=&submitType=ManageTrustBundles");        
    }		

    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/updatePolicy", method = RequestMethod.GET)
    public ModelAndView updatePolicy (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,                                                     
                                                    HttpSession session,
                                                    @ModelAttribute PolicyForm policyForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter bundles/assignBundles");
        }    
                                              
        
        //PolicyForm policyForm = new PolicyForm();        
        model.addAttribute("policyForm", policyForm);
        mav.setViewName("updatePolicyForm");
        
        return mav;
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
            Collection<TrustBundle> trustBundles = configSvc.getTrustBundles(false);
            
            if(trustBundles != null) {
                model.addAttribute("trustBundles", trustBundles);
            }


        } catch (ConfigurationServiceException e1) {

        }                                        
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("assignBundlesForm");
        
        return mav;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/addMoreBundlesForm", method = RequestMethod.GET)
    public ModelAndView addMoreBundlesForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,                                                     
                                                    HttpSession session,
                                                    @ModelAttribute BundleForm simpleForm,
                                                    @RequestParam(value="domainId") String domainId,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter bundles/addMoreBundlesForm");
        }    
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            Collection<TrustBundle> trustBundles = new ArrayList();  
            Collection<TrustBundle> newBundles = new ArrayList();  
            Collection<TrustBundleDomainReltn> bundleRelationships = configSvc.getTrustBundlesByDomain(Long.parseLong(domainId), false);
            Collection<TrustBundle> allBundles = configSvc.getTrustBundles(false);
            boolean bundleMatch = false;
            TrustBundle tempBundle;
            
            if( bundleRelationships != null ) {
                
                
                
               
 
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
                
                newBundles = configSvc.getTrustBundles(false);
                
            }
                       
            //if(trustBundles != null) {
                model.addAttribute("trustBundles", newBundles);
            //}


        } catch (ConfigurationServiceException e1) {

        }                               
        
        model.addAttribute("domainId", domainId);
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("addMoreBundlesForm");
        
        return mav;
    }
    
        @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/newPolicyForm", method = RequestMethod.GET)
    public ModelAndView newPolicyForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,                                                     
                                                    HttpSession session,
                                                    @ModelAttribute PolicyForm policyForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter policies");
        }    
        
        
        PolicyForm pform = new PolicyForm();
        pform.setId(0);
        model.addAttribute("policyForm", pform);
        
        log.error("test" + pform.getLexiconNames().toString());
        
        model.addAttribute("lexiconNames", pform.getLexiconNames());
        
        log.error("test"+model.toString());
                
        mav.setViewName("newPolicyForm");
        
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
