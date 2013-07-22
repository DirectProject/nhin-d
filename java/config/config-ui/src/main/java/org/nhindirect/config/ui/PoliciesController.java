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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import java.util.List;
import java.util.Map;
import org.nhindirect.config.store.TrustBundleAnchor;
import org.nhindirect.policy.PolicyLexicon;
import java.util.Random;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.nhindirect.policy.PolicyParseException;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping("/policies")
public class PoliciesController {
    private final Log log = LogFactory.getLog(getClass());

    private ConfigurationService configSvc;
    private String imagesWebPath;

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
    @RequestMapping(value="/removePolicies", method = RequestMethod.POST)
    public ModelAndView removePolicies (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                                    HttpSession session,
                                                    @ModelAttribute PolicyForm policyForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter remove policies method");
        }
        
        if(policyForm.getPoliciesSelected() != null)
        {
            if (log.isDebugEnabled()) 
            {
                log.debug("Policies marked for removal: "+policyForm.getPoliciesSelected().toString());
            }
        }

        if (configSvc != null 
                && policyForm != null 
                && policyForm.getPoliciesSelected() != null) 
        {
            
            int policyCount = policyForm.getPoliciesSelected().size();
            long[] policiesSelected = new long[policyCount];

            if (log.isDebugEnabled()) 
            {
                log.debug("Removing Policies");
            }
            
            for(int i=0; i<policyCount; i++) 
            {
                String bundleId = policyForm.getPoliciesSelected().get(i);
                log.error(bundleId);
                
                policiesSelected[i] = Long.parseLong(bundleId);
                
            }
            
            // Delete Trust Bundle(s)
            try 
            {
                configSvc.deletePolicies(policiesSelected);
            } catch (ConfigurationServiceException cse) 
            {
                log.error("Problem removing policies");
            }
            
        }        
        
        // Just redirect to Manage Policies page for now
        return new ModelAndView("redirect:/config/main/search?domainName=&submitType=ManagePolicies");
    }		

    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/updatePolicyForm", method = RequestMethod.GET)        
    public ModelAndView updatePolicyForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                            @RequestParam("id") String id,
                                                    HttpSession session,
                                                    @ModelAttribute PolicyForm policyForm,
                                                    Model model)  { 		
        CertPolicy policy = null;
        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter policies update form for policy #"+id);
        }
        
        try {
            policy = configSvc.getPolicyById(Long.parseLong(id));
        } catch (ConfigurationServiceException cse){
            cse.printStackTrace();
            return new ModelAndView("redirect:/");                    
        }
        
        PolicyForm pform = new PolicyForm();
                
        pform.setId(policy.getId());
        pform.setPolicyName(policy.getPolicyName());
        pform.setPolicyLexicon(policy.getLexicon());
        try {
            pform.setPolicyContent(new String(policy.getPolicyData(), "UTF-8"));
        } catch (UnsupportedEncodingException ie) {
            ie.printStackTrace();
        }
        
        
        
        model.addAttribute("policyForm", pform);
        mav.setViewName("updatePolicyForm"); 
        
        
        /*
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
        */
        return mav;     
    }		

    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/updatePolicy", method = RequestMethod.POST)    
    @ResponseBody
    public String updatePolicy (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                @RequestParam("updateType") String updateType,
                                      @ModelAttribute PolicyForm policyForm,
                                        HttpSession session,                                                    
                                        Model model)  { 		
        String jsonResponse = null;

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter update policy #"+policyForm.getId());
        }    
        
        log.error(policyForm.getPolicyName());
        
        // Convert policy data to byte array
        
        byte[] policyData = policyForm.getFileData().getBytes();
        
        try {
            configSvc.updatePolicyAttributes(policyForm.getId(), policyForm.getPolicyName(), policyForm.getPolicyLexicon(), policyData);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }                
        
        return jsonResponse;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/checkLexiconFile", method = {RequestMethod.GET, RequestMethod.POST } )    
    @ResponseBody
    public String checkLexiconFile (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,
        HttpServletResponse response, 
        Object command, 
        MultipartHttpServletRequest request) throws FileUploadException, IOException, Exception 
    { 		        

        String jsonResponse = null;
        UploadedFile ufile = new UploadedFile();
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());        

        try {                   
            ufile.length = mpf.getBytes().length;
            ufile.bytes= mpf.getBytes();
            ufile.type = mpf.getContentType();
            ufile.name = mpf.getOriginalFilename();
        } catch (IOException e) {            
            e.printStackTrace();
        }
        
        String lexicon = request.getParameter("lexicon");
        org.nhind.config.PolicyLexicon lex = null;
        
        // Check the file for three types of policies
        if (lexicon.isEmpty()) {
            lex = org.nhind.config.PolicyLexicon.SIMPLE_TEXT_V1;
        }
        else
        {
                try
                {
                        lex = org.nhind.config.PolicyLexicon.fromString(lexicon);
                }
                catch (Exception e)
                {
                        System.out.println("Invalid lexicon name.");				
                }
        }
		
		// validate the policy syntax
		final org.nhindirect.policy.PolicyLexicon parseLexicon;
                
		if (lex.equals(org.nhind.config.PolicyLexicon.JAVA_SER))
			parseLexicon = org.nhindirect.policy.PolicyLexicon.JAVA_SER;
		else if (lex.equals(org.nhind.config.PolicyLexicon.SIMPLE_TEXT_V1))
			parseLexicon = org.nhindirect.policy.PolicyLexicon.SIMPLE_TEXT_V1;
		else
			parseLexicon = org.nhindirect.policy.PolicyLexicon.XML;		
		
		InputStream inStr = null;
		try
		{			
			inStr = new ByteArrayInputStream(ufile.bytes);
			
			final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(parseLexicon);
			parser.parse(inStr);
		}
		catch (PolicyParseException e)
		{
			log.error("Syntax error in policy file " + " : " + e.getMessage());			
                        jsonResponse = "failed";
		}
		finally
		{
			IOUtils.closeQuietly(inStr);
		}
		
        

        
        if (log.isDebugEnabled()) 
        {
            log.debug("Checking uploaded lexicon file for format and validation");
        }                    
        
        jsonResponse = "success";             
        
        return jsonResponse;
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
        
        //log.error("test" + pform.getLexiconNames().toString());
        
        model.addAttribute("lexiconNames", pform.getLexiconNames());
        
        //log.error("test"+model.toString());
                
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
