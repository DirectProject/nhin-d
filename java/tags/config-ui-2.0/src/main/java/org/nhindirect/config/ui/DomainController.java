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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.config.rest.AddressService;
import org.nhind.config.rest.AnchorService;
import org.nhind.config.rest.CertificateService;
import org.nhind.config.rest.DomainService;
import org.nhind.config.rest.TrustBundleService;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Address;
import org.nhindirect.config.model.Anchor;
import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.Domain;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.TrustBundleAnchor;
import org.nhindirect.config.model.TrustBundleDomainReltn;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.ui.form.AddressForm;
import org.nhindirect.config.ui.form.AnchorForm;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.DomainForm;
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
@RequestMapping("/domain")
public class DomainController {
	
    private final Log log = LogFactory.getLog(getClass());
    
	private CertificateService certService;
	private DomainService domainService;
	private TrustBundleService bundleService;
	private AnchorService anchorService;
	private AddressService addressService;

	
	@Inject
	public void setCertificateService(CertificateService certService)
    {
        this.certService = certService;
    }
	
	@Inject
	public void setDomainService(DomainService domainService)
    {
        this.domainService = domainService;
    }
	
	@Inject
	public void setTrustBundleService(TrustBundleService bundleService)
    {
        this.bundleService = bundleService;
    }
	
	@Inject
	public void setAnchorService(AnchorService anchorService)
    {
        this.anchorService = anchorService;
    }
	
	@Inject
	public void setAdressService(AddressService addressService)
    {
        this.addressService = addressService;
    }	
	
    /*
	@Inject
	private DomainService configSvc;

	@Inject
	private AddressService configSvc;
	
	@Inject
	private configSvc configSvc;
	
	@Inject
	private CertificateService configSvc;
    */

	public DomainController() {
		if (log.isDebugEnabled()) log.debug("DomainController initialized");
	}
	
	private Collection<AnchorForm> convertAnchors(Collection<Anchor> anchors){
		Collection<AnchorForm> form = new ArrayList<AnchorForm>();
		if (anchors != null)
		{
    		for (Iterator<Anchor> iter = anchors.iterator(); iter.hasNext();) {
    			Anchor t = (Anchor) iter.next();
    			AnchorForm e = new AnchorForm();
    			e.setCertificateData(t.getCertificateData());
    			e.setCertificateId(t.getCertificateId());
    			e.setCreateTime(t.getCreateTime());
    			e.setId(t.getId());
    			e.setIncoming(t.isIncoming());
    			e.setOutgoing(t.isOutgoing());
    			e.setOwner(t.getOwner());
    			e.setStatus(t.getStatus());
    			e.setThumbprint(t.getThumbprint());
    			e.setValidEndDate(t.getValidEndDate());
    			e.setValidStartDate(t.getValidStartDate());
    
                String theUser = "";
                if (t.getCertificateData() != null)
                {
                        // get the owner from the certificate information
                        // first transform into a certificate
                        CertContainer cont;
    					try {
    						cont = toCertContainer(t.getCertificateData());
    	                    if (cont != null && cont.getCert() != null)
    	                    {
    	                            // now get the owner info from the cert
    	                            theUser = getTrustedEntityName(cont.getCert().getSubjectX500Principal());
    	                    }
    					} catch (Exception e1) {
    						e1.printStackTrace();
    					}
                }
    			e.setTrusteddomainoruser(theUser);
    			form.add(e);
    		}			
		}	
		return form;
	}
        
        @PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/addBundle", method = RequestMethod.POST)
	public ModelAndView addBundle (
            @RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
            HttpSession session,
            @ModelAttribute AnchorForm anchorForm,
            Model model,
            @RequestParam(value="bundles") String bundles
            ) { 		
           
        	    final String domainName = (String)session.getAttribute("currentDomainName");
                // DEBUG
                if ( log.isDebugEnabled() ) {
                    log.debug("Enter domain/addBundle");
                }
                
                bundles = bundles.replace("wHiTeSpAcE", " ");
                String[] bundleIds = bundles.split(":");

                for(String bundle : bundleIds) {
                    String[] bundleArray = bundle.split("\\|\\|\\|\\|");
                    
                    try {
                    
                        if(bundleArray[1].equals("both")) {
                        	bundleService.associateTrustBundleToDomain(bundleArray[0], domainName, true, true);
                        } else if (bundleArray[1].equals("in")) {
                        	bundleService.associateTrustBundleToDomain(bundleArray[0], domainName, true, false);
                        } else if (bundleArray[1].equals("out")) {
                        	bundleService.associateTrustBundleToDomain(bundleArray[0], domainName, false, true);
                        } else {
                        	bundleService.associateTrustBundleToDomain(bundleArray[0], domainName, false, false);
                        }
                    } catch (ServiceException cse) {
                        
                    }
                    
                }
                
                return new ModelAndView("redirect:/config/domain?domainName="+domainName+"&action=update#tab3");
                
        }
        
        @PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/removeBundles", method = RequestMethod.POST)
	public ModelAndView removeBundles (
            @RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
            HttpSession session,
            @ModelAttribute AnchorForm anchorForm,
            Model model,
            @RequestParam(value="bundles") String bundles
            ) { 		

        	    final String domainName = (String)session.getAttribute("currentDomainName");
                // DEBUG
                if ( log.isDebugEnabled() ) {
                    log.debug("Enter domain/removeBundles");
                }
                
                String[] bundleIds = bundles.split("\\|\\|\\|\\|");

                for(String bundle : bundleIds) {
                    
                    
                    try {
                    
                        bundleService.disassociateTrustBundleFromDomain(bundle, domainName);
                        
                    } catch (ServiceException cse) {
                        
                    }
                    
                }
                
                return new ModelAndView("redirect:/config/domain?domainName="+domainName+"&action=update#tab3");
                
        }
	
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/addanchor", method = RequestMethod.POST)
	public ModelAndView addAnchor (
								@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute AnchorForm anchorForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath,
						        @RequestParam(value="id") String id
						        ) { 		

		final String domAttr = (String)session.getAttribute("currentDomainName");
		ModelAndView mav = new ModelAndView(); 
		String strid = "";
		strid = ""+ domAttr;//anchorForm.getId();

		Domain dom = null;
		try
		{
			dom = domainService.getDomain(strid);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
		if (log.isDebugEnabled()) log.debug("Enter domain/addanchor");
		
		if(actionPath.equalsIgnoreCase("newanchor") || actionPath.equalsIgnoreCase("add anchor")){
			strid = ""+anchorForm.getId();

			String owner = "";
			if(dom != null){
				owner = dom.getDomainName();
			}
			
			// insert the new address into the Domain list of Addresses
			if (log.isDebugEnabled()) log.debug("beginning to evaluate filedata");		
			try{
				if (!anchorForm.getFileData().isEmpty()) {
					final byte[] bytes = anchorForm.getFileData().getBytes();
                   String theUser = "";
                    if (bytes != null)
                    {
                            // get the owner from the certificate information
                            // first transform into a certificate
                            CertContainer cont = toCertContainer(bytes);
                            if (cont != null && cont.getCert() != null)
                            {
                                    // now get the owner info from the cert
                                    theUser = getTrustedEntityName(cont.getCert().getSubjectX500Principal());
                                    anchorForm.setTrusteddomainoruser(theUser);
                            }
                    }

					// store the bytes somewhere
					final Anchor ank = new Anchor();
					ank.setCertificateData(bytes);
					if (log.isDebugEnabled()) log.debug("incoming is: "+anchorForm.isIncoming()+" and outgoing is: "+anchorForm.isOutgoing());
					ank.setIncoming(anchorForm.isIncoming());
					ank.setOutgoing(anchorForm.isOutgoing());
					ank.setOwner(owner);
					ank.setStatus(anchorForm.getStatus());
					
					anchorService.addAnchor(ank);
					if (log.isDebugEnabled()) log.debug("store the anchor certificate into database");
				} else {
					if (log.isDebugEnabled()) log.debug("DO NOT store the anchor certificate into database BECAUSE THERE IS NO FILE");
				}

			} catch (ConfigurationServiceException ed) {
				if (log.isDebugEnabled())
					log.error(ed);
			} catch (Exception e) {
				if (log.isDebugEnabled()) log.error(e.getMessage());
				e.printStackTrace();
			}
			// certificate and anchor forms and results
			try {
				final Collection<Certificate> certs = certService.getCertificatesByOwner(owner);
				model.addAttribute("certificatesResults", certs);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				 
				final Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, false, false, "");
				final Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				CertificateForm cform = new CertificateForm();
				cform.setId(dom.getId());
				model.addAttribute("certificateForm",cform);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				
				AnchorForm aform = new AnchorForm();
				aform.setId(dom.getId());
				model.addAttribute("anchorForm",aform);
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			SimpleForm simple = new SimpleForm();
			simple.setId(dom.getId());
			simple.setDomainName(dom.getDomainName());
			model.addAttribute("simpleForm",simple);

			model.addAttribute("addressesResults", dom.getAddresses());
			mav.setViewName("domain"); 
			// the Form's default button action
			String action = "Update";
			DomainForm form = (DomainForm) session.getAttribute("domainForm");
			if (form == null) {
				form = new DomainForm();
				form.populate(dom);
			}
			model.addAttribute("domainForm", form);
			model.addAttribute("action", action);
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
	
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
		}
		
		AddressForm addressForm2 = new AddressForm();
		
		addressForm2.setDisplayName("");
		addressForm2.setEndpoint("");
		addressForm2.setEmailAddress("");
		addressForm2.setType("");
		addressForm2.setId(Long.parseLong(strid));
		
		model.addAttribute("addressForm",addressForm2);
		
        return new ModelAndView("redirect:/config/domain?id="+dom.getDomainName()+"#tab2");
		//return mav;
	}			
	
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/removeanchors", method = RequestMethod.POST)
	public ModelAndView removeAnchors (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute AnchorForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		final ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/removeanchor");
		if(simpleForm.getRemove() != null){
			if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		}
	
		final String domAttr = (String)session.getAttribute("currentDomainName");
		String strid = ""+  domAttr;//simpleForm.getId();
		Domain dom = null;
		try
		{
			dom = domainService.getDomain(strid);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
		String owner = "";
		String domname = "";
		if( dom != null){
			domname = dom.getDomainName();
			owner = domname;
		}
		if (anchorService != null && simpleForm != null && actionPath != null && (actionPath.equalsIgnoreCase("deleteanchors") || actionPath.equalsIgnoreCase("Remove Selected Anchors")) && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing anchors for domain with name: " + domname);
			try{
				// get list of certificates for this domain
				final Collection<Anchor> certs = anchorService.getAnchorsForOwner(owner, false, false, "");
				final ArrayList<Long> certtoberemovedlist = new ArrayList<Long>();
				// now iterate over each one and remove the appropriate ones
				for (int x = 0; x < cnt; x++) {
					String removeid = simpleForm.getRemove().get(x);
					for (Iterator<Anchor> iter = certs.iterator(); iter.hasNext();) {
						Anchor t = (Anchor) iter.next();
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
				// with the collection of anchor ids now remove them from the configSvc
				if (log.isDebugEnabled()) log.debug(" Trying to remove anchors from database");
				anchorService.deleteAnchorsByIds(certtoberemovedlist);
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to update the domain with removed anchors");
				final AddressForm addrform = new AddressForm();
				addrform.setId(dom.getId());
				model.addAttribute("addressForm",addrform);
			} catch (ServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		// BEGIN: temporary code for mocking purposes
		final CertificateForm cform = new CertificateForm();
		cform.setId(dom.getId());
		model.addAttribute("certificateForm",cform);
		
		final AnchorForm aform = new AnchorForm();
		aform.setId(dom.getId());
		model.addAttribute("anchorForm",aform);
		
		
		model.addAttribute("addressesResults", dom.getAddresses());
		mav.setViewName("domain"); 
		// the Form's default button action
		String action = "Update";
		DomainForm form = (DomainForm) session.getAttribute("domainForm");
		if (form == null) {
			form = new DomainForm();
			form.populate(dom);
		}
		model.addAttribute("domainForm", form);
		model.addAttribute("action", action);
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		mav.addObject("action", action);

		// SETTING THE ADDRESSES OBJECT
		model.addAttribute("addressesResults", form.getAddresses());
		Collection<Certificate> certlist = null;
		try {
			certlist = certService.getCertificatesByOwner(owner);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		Collection<Anchor> anchorlist = null;
		try {
			anchorlist = anchorService.getAnchorsForOwner(owner, false, false, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("certificatesResults", certlist);
		// convert Anchor to AnchorForm
		final Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);					
		// now set anchorsResults
		model.addAttribute("anchorsResults", convertedanchors);
		
		// END: temporary code for mocking purposes			
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
		
		model.addAttribute("simpleForm",simpleForm);
		strid = ""+simpleForm.getId();
		if (log.isDebugEnabled()) log.debug(" the value of id of simpleform is: "+strid);
		
		return new ModelAndView("redirect:/config/domain?id="+dom.getDomainName()+"#tab2");
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

		final ModelAndView mav = new ModelAndView(); 
		String strid = "";
		if (log.isDebugEnabled()) log.debug("Enter domain/addcertificate");
	
		if(actionPath.equalsIgnoreCase("newcertificate") || actionPath.equalsIgnoreCase("add certificate")){
			strid = ""+certificateForm.getId();
			Domain dom = null;
			try
			{
				dom = domainService.getDomain(strid);
			}
			catch (ServiceException e)
			{
				e.printStackTrace();
			}
			String owner = "";

			// insert the new address into the Domain list of Addresses
			if (log.isDebugEnabled()) log.debug("beginning to evaluate filedata");		
			try{
				if (!certificateForm.getFileData().isEmpty()) {
					final byte[] bytes = certificateForm.getFileData().getBytes();
					owner = certificateForm.getOwner();
					final Certificate cert = new Certificate();
					cert.setData(bytes);
					cert.setOwner(owner);
					cert.setStatus(certificateForm.getStatus());

					certService.addCertificate(cert);
					// store the bytes somewhere
					if (log.isDebugEnabled()) log.debug("store the certificate into database");
				} else {
					if (log.isDebugEnabled()) log.debug("DO NOT store the certificate into database BECAUSE THERE IS NO FILE");
				}

			} catch (ServiceException ed) {
				if (log.isDebugEnabled())
					log.error(ed);
			} catch (Exception e) {
				if (log.isDebugEnabled()) log.error(e);
				e.printStackTrace();
			}
			// certificate and anchor forms and results
			try {
				final Collection<Certificate> certs = certService.getCertificatesByOwner(owner);
				model.addAttribute("certificatesResults", certs);
				 
				final Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, false, false, "");
				final Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
				
				final CertificateForm cform = new CertificateForm();
				cform.setId(dom.getId());
				model.addAttribute("certificateForm",cform);
				
				final AnchorForm aform = new AnchorForm();
				aform.setId(dom.getId());
				model.addAttribute("anchorForm",aform);
				
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			final SimpleForm simple = new SimpleForm();
			simple.setId(Long.parseLong(strid));
			model.addAttribute("simpleForm",simple);

			model.addAttribute("addressesResults", dom.getAddresses());
			mav.setViewName("domain"); 
			// the Form's default button action
			final String action = "Update";
			DomainForm form = (DomainForm) session.getAttribute("domainForm");
			if (form == null) {
				form = new DomainForm();
				form.populate(dom);
			}
			model.addAttribute("domainForm", form);
			model.addAttribute("action", action);
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
	
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
		}
		
		final AddressForm addressForm2 = new AddressForm();
		
		addressForm2.setDisplayName("");
		addressForm2.setEndpoint("");
		addressForm2.setEmailAddress("");
		addressForm2.setType("");
		addressForm2.setId(Long.parseLong(strid));
		
		model.addAttribute("addressForm",addressForm2);
		
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
		
		String strid = ""+simpleForm.getId();
		Domain dom = null;
		try
		{
			dom = domainService.getDomain(strid);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
		String owner = "";
		String domname = "";

		if (certService != null && simpleForm != null && actionPath != null && (actionPath.equalsIgnoreCase("deletecertificate") || actionPath.equalsIgnoreCase("remove selected")) && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing certificates for domain with name: " + domname);
			try{
				// get list of certificates for this domain
				final Collection<Certificate> certs = certService.getCertificatesByOwner(owner);
				final ArrayList<Long> certtoberemovedlist = new ArrayList<Long>();
				// now iterate over each one and remove the appropriate ones
				for (int x = 0; x < cnt; x++) {
					String removeid = simpleForm.getRemove().get(x);
					for (Iterator<Certificate> iter = certs.iterator(); iter.hasNext();) {
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
				// with the collection of anchor ids now remove them from the configSvc
				if (log.isDebugEnabled()) log.debug(" Trying to remove certificates from database");
				certService.deleteCertificatesByIds(certtoberemovedlist);
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to update the domain with removed certificates");
				final AddressForm addrform = new AddressForm();
				addrform.setId(dom.getId());
				model.addAttribute("addressForm",addrform);
			} catch (ServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		// BEGIN: temporary code for mocking purposes
		final CertificateForm cform = new CertificateForm();
		cform.setId(dom.getId());
		model.addAttribute("certificateForm",cform);
		
		final AnchorForm aform = new AnchorForm();
		aform.setId(dom.getId());
		model.addAttribute("anchorForm",aform);
		
		
		model.addAttribute("addressesResults", dom.getAddresses());
		mav.setViewName("domain"); 
		// the Form's default button action
		final String action = "Update";
		DomainForm form = (DomainForm) session.getAttribute("domainForm");
		if (form == null) {
			form = new DomainForm();
			form.populate(dom);
		}
		model.addAttribute("domainForm", form);
		model.addAttribute("action", action);
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		mav.addObject("action", action);

		// SETTING THE ADDRESSES OBJECT
		model.addAttribute("addressesResults", form.getAddresses());
		Collection<Certificate> certlist = null;
		try {
			certlist = certService.getCertificatesByOwner(owner);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		Collection<Anchor> anchorlist = null;
		try {
			anchorlist = anchorService.getAnchorsForOwner(owner, false, false, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("certificatesResults", certlist);
		// convert Anchor to AnchorForm
		Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);					
		// now set anchorsResults
		model.addAttribute("anchorsResults", convertedanchors);
		
		// END: temporary code for mocking purposes			
		mav.addObject("statusList", EntityStatus.getEntityStatusList());

		model.addAttribute("simpleForm",simpleForm);
	    strid = ""+simpleForm.getId();
		if (log.isDebugEnabled()) log.debug(" the value of id of simpleform is: "+strid);
		
		return mav;
	}			
	
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/addaddress", method = RequestMethod.POST)
	public ModelAndView addAddress (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute AddressForm addressForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath) { 		

		final String domAttr = (String)session.getAttribute("currentDomainName");
		ModelAndView mav = new ModelAndView(); 
		String strid = "";
		if (log.isDebugEnabled()) log.debug("Enter domain/addaddress");
		Domain dom = null;
		if(actionPath.equalsIgnoreCase("newaddress") || actionPath.equalsIgnoreCase("add address")){
			strid = ""+ domAttr;//addressForm.getId();
			
			try
			{
				dom = domainService.getDomain(strid);
			}
			catch (ServiceException e)
			{
				e.printStackTrace();
			}
			String owner = dom.getDomainName();
			
			// insert the new address into the Domain list of Addresses
			final String anEmail = addressForm.getEmailAddress();
			final String displayname = addressForm.getDisplayName();
			final String endpoint = addressForm.getEndpoint();
			final EntityStatus estatus = addressForm.getaStatus();
			final String etype = addressForm.getType();
			
			if (log.isDebugEnabled()) log.debug(" Trying to add address: "+anEmail);
			final Address e = new Address();
			e.setEmailAddress(anEmail);
			e.setDisplayName(displayname);
			e.setEndpoint(endpoint);
			e.setStatus(estatus);
			e.setType(etype);
			
			final List<Address> modAddrs = new ArrayList<Address>(dom.getAddresses());
			modAddrs.add(e);
			
			dom.setAddresses(modAddrs);
			
			try{
			    domainService.updateDomain(dom);
				if (log.isDebugEnabled()) log.debug(" After attempt to insert new email address ");
			} catch (ServiceException ed) {
				if (log.isDebugEnabled())
					log.error(ed);
			}
			// certificate and anchor forms and results
			try {
				final Collection<Certificate> certs = certService.getCertificatesByOwner(owner);
				model.addAttribute("certificatesResults", certs);
			} catch (ServiceException e1) {
			}
				 
			try {
				final Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, false, false, "");
				// convert Anchor to AnchorForm
				final Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
			} catch (Exception e1) {
			}
				
			try {
				CertificateForm cform = new CertificateForm();
				cform.setId(dom.getId());
				model.addAttribute("certificateForm",cform);
				
				AnchorForm aform = new AnchorForm();
				aform.setId(dom.getId());
				model.addAttribute("anchorForm",aform);
				
			} catch (Exception e1x) {
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			SimpleForm simple = new SimpleForm();
			//simple.setId(Long.parseLong(strid));
			simple.setDomainName(dom.getDomainName());
			model.addAttribute("simpleForm",simple);

			model.addAttribute("addressesResults", dom.getAddresses());
			mav.setViewName("domain"); 
			// the Form's default button action
			String action = "Update";
			DomainForm form = (DomainForm) session.getAttribute("domainForm");
			if (form == null) {
				form = new DomainForm();
				form.populate(dom);
			}
			model.addAttribute("domainForm", form);
			model.addAttribute("action", action);
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
	
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
		}
		
		AddressForm addressForm2 = new AddressForm();
		
		addressForm2.setDisplayName("");
		addressForm2.setEndpoint("");
		addressForm2.setEmailAddress("");
		addressForm2.setType("");
		//addressForm2.setId(Long.parseLong(strid));
		addressForm2.setDomainName(strid);
		
		model.addAttribute("addressForm",addressForm2);
		//return new ModelAndView("redirect:/config/domain?id="+dom.getDomainName()+"#tab1");
		return mav;
	}		
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/removeaddresses", method = RequestMethod.POST)
	public ModelAndView removeAddresses (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute SimpleForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		final String domAttr = (String)session.getAttribute("currentDomainName");
		ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/removeaddresses");
		if(simpleForm.getRemove() != null){
			if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		}
		

		Domain dom = null;
		try
		{
			dom = domainService.getDomain(domAttr);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
		String strid = "" + dom.getDomainName();//+simpleForm.getId();
		String domname = "";
		if (dom != null) {
			domname = dom.getDomainName();
			if (addressService != null
					&& simpleForm != null
					&& actionPath != null
					&& (actionPath.equalsIgnoreCase("delete") || actionPath
							.equalsIgnoreCase("remove selected Addresses"))
					&& simpleForm.getRemove() != null) {
				int cnt = simpleForm.getRemove().size();
				if (log.isDebugEnabled())
					log.debug("removing addresses for domain with name: "
							+ domname);
				try {
					
					for (int x = 0; x < cnt; x++) {
						String removeid = simpleForm.getRemove().get(x);
						Collection<Address> t = dom.getAddresses();
						for (Iterator<Address> iter = t.iterator(); iter.hasNext();) {
							Address ts = (Address) iter.next();
							if (ts.getId() == Long.parseLong(removeid)) {
								dom.getAddresses().remove(ts);
								if (addressService != null) {
									addressService.deleteAddress(ts.getEmailAddress());
									try
									{
										dom = domainService.getDomain(strid);
									}
									catch (ServiceException e)
									{
										e.printStackTrace();
									}
									break;
								}
							}
						}
					}
					if (log.isDebugEnabled())
						log
								.debug(" Trying to update the domain with removed addresses");
					domainService.updateDomain(dom);
					try
					{
						dom = domainService.getDomain(strid);
					}
					catch (ServiceException e)
					{
						e.printStackTrace();
					}
					if (log.isDebugEnabled())
						log
								.debug(" SUCCESS Trying to update the domain with removed addresses");
					final AddressForm addrform = new AddressForm();
					addrform.setId(dom.getId());
					addrform.setDomainName(dom.getDomainName());
					model.addAttribute("addressForm", addrform);
					// BEGIN: temporary code for mocking purposes
					String owner = "";
	
					model.addAttribute("addressesResults", dom.getAddresses());

					Collection<Certificate> certlist = null;
					try {
						certlist = certService.getCertificatesByOwner(owner);
					} catch (ServiceException e) {
						e.printStackTrace();
					}

					Collection<Anchor> anchorlist = null;
					try {
						anchorlist = anchorService.getAnchorsForOwner(owner, false, false, "");
					} catch (Exception e) {
						
					}

					model.addAttribute("certificatesResults", certlist);
					// convert Anchor to AnchorForm
					Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);
					// now set anchorsResults
					model.addAttribute("anchorsResults", convertedanchors);

					// END: temporary code for mocking purposes

				} catch (ServiceException e) {
					if (log.isDebugEnabled())
						log.error(e);
				}
			} else if (domainService != null
					&& (actionPath.equalsIgnoreCase("newaddress") || actionPath
							.equalsIgnoreCase("add address"))) {
				// insert the new address into the Domain list of Addresses
				final String anEmail = simpleForm.getPostmasterEmail();
				if (log.isDebugEnabled())
					log.debug(" Trying to add address: " + anEmail);
				final Address e = new Address();
				e.setEmailAddress(anEmail);
				dom.getAddresses().add(e);
				simpleForm.setPostmasterEmail("");
				try {
					domainService.updateDomain(dom);
					if (log.isDebugEnabled())
						log
								.debug(" After attempt to insert new email address ");
				} catch (ServiceException ed) {
					if (log.isDebugEnabled())
						log.error(ed);
				}
			}
		}

		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
		final String action = "Update";
		model.addAttribute("action", action);			
		DomainForm form = (DomainForm) session.getAttribute("domainForm");
		if (form == null) {
			form = new DomainForm();
			form.populate(dom);
		}
		model.addAttribute("domainForm", form);
		mav.setViewName("domain");
		String owner = "";
		// certificate and anchor forms and results
		try {
			if(owner != null && !owner.equalsIgnoreCase("")){
				final Collection<Certificate> certs = certService.getCertificatesByOwner(owner);
				model.addAttribute("certificatesResults", certs);
				 
				final Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, false, false, "");
				// convert Anchor to AnchorForm
				final Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
			}
			
			final CertificateForm cform = new CertificateForm();

			model.addAttribute("certificateForm",cform);
			
			final AnchorForm aform = new AnchorForm();
			//aform.setId(dom.getId());
			aform.setDomainName(dom.getDomainName());
			model.addAttribute("anchorForm",aform);
			
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
			
		model.addAttribute("simpleForm",simpleForm);
		strid = ""+ dom.getDomainName();//simpleForm.getId();
		if (log.isDebugEnabled()) log.debug(" the value of id of simpleform is: "+strid);
		 
		return new ModelAndView("redirect:/config/domain?id="+dom.getDomainName()+"#tab1");
		//return mav;
	}		
	
        
        /**  removeDomain
        * 
        * 
        * 
        * 
        */        
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/remove", method = RequestMethod.POST)
	public ModelAndView removeDomain (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute SimpleForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		final ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/remove");
		if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		
		if (domainService != null) 
                {
                    int cnt = simpleForm.getRemove().size();
                    for (int x = 0; x < cnt; x++) {
                        try 
                        {
                            String strid = simpleForm.getRemove().get(x);
                            
                            Domain dom = null;
                    		try
                    		{
                    			dom = domainService.getDomain(strid);
                    		}
                    		catch (ServiceException e)
                    		{
                    			e.printStackTrace();
                    		}
                            
                            if(dom != null) {
                                String owner = dom.getDomainName();
                            
                                //String domname = dom.getDomainName();

                                if (log.isDebugEnabled()) log.debug("removing domain with name: " + strid);

                                domainService.deleteDomain(dom.getDomainName());

                                // now delete anchors
                                try{
                                        // get list of certificates for this domain
                                        final Collection<Anchor> certs = anchorService.getAnchorsForOwner(owner, false, false, "");
                                        if (certs != null && !certs.isEmpty()) {
                                                final ArrayList<Long> certtoberemovedlist = new ArrayList<Long>();
                                                // now iterate over each one and remove the
                                                // appropriate ones
                                                for (Iterator<Anchor> iter = certs.iterator(); iter
                                                                .hasNext();) {
                                                        Anchor t = (Anchor) iter.next();
                                                        certtoberemovedlist.add(t.getId());
                                                }
                                                // with the collection of anchor ids now remove them
                                                // from the configSvc
                                                if (log.isDebugEnabled())
                                                        log
                                                                        .debug(" Trying to remove anchors from database");
                                                anchorService.deleteAnchorsByIds(certtoberemovedlist);
                                                if (log.isDebugEnabled())
                                                        log.debug(" SUCCESS Trying to remove anchors");

                                        }
                                } catch (ServiceException e) {
                                        if (log.isDebugEnabled())
                                                log.error(e);
                                }
                            
                            }
                            
                        } catch (ServiceException e) {
                                if (log.isDebugEnabled())
                                        log.error(e);
                        }
                        
                    }
		}
		SearchDomainForm form2 = (SearchDomainForm) session.getAttribute("searchDomainForm");
		model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));

		// Get all domains managed by this HISP
                String domain = "%";
                

                List<Domain> results = null;

                if (domainService != null) 
                {
                    try
                    {
	                    List<Domain> domains = new ArrayList<Domain>();
	                    
	                    Collection<Domain> enabledDomains = domainService.searchDomains(domain,EntityStatus.ENABLED);
	                    Collection<Domain> disabledDomains = domainService.searchDomains(domain,EntityStatus.DISABLED);                    
	                    Collection<Domain> newDomains = domainService.searchDomains(domain,EntityStatus.NEW);
	                    
	                    if(!enabledDomains.isEmpty()) 
	                    {
	                        domains.addAll(enabledDomains);
	                    }
	                    
	                    if(!disabledDomains.isEmpty())
	                    {
	                        domains.addAll(disabledDomains);
	                    }                    
	                    
	                    if (newDomains != null)
	                    {
	                        domains.addAll(newDomains);
	                        
	                    }
	                    
	                    results = domains;
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
                
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
		
		return mav;
	}	
	
        
        
        
        

	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView onSubmitAndView(Object command){
		if (log.isDebugEnabled()) log.debug("Enter onSubmit");
		return new ModelAndView(new RedirectView("main"));
	}
	
    @RequestMapping(value = "/simpleForm", method = RequestMethod.GET)
    public void simpleForm(Model model) {
            model.addAttribute(new SimpleForm());
    }	
    
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/updateBundleDirection", method = RequestMethod.POST)
    public ModelAndView updateBundleDirection (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,         
        @RequestParam(required=true) String domainName,
        @RequestParam(required=true) String bundle,
        @RequestParam(required=true) String direction,
        @RequestParam(required=true) String directionValue,
                                                    HttpSession session, Model model)  { 
        
        Collection<TrustBundleDomainReltn> bundles = null;
        
        try {
            bundles = bundleService.getTrustBundlesByDomain(domainName, false);
        } catch (ServiceException ex) {
            Logger.getLogger(DomainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(TrustBundleDomainReltn bundleReltn : bundles) {
            if(bundleReltn.getId() == Long.parseLong(bundle)) {
                if(direction.toLowerCase().equals("incoming"))
                {
                    if(Integer.parseInt(directionValue) == 1) {
                        bundleReltn.setIncoming(true); 
                    } else {
                        bundleReltn.setIncoming(false);                        
                    }
                } else {
                    if(Integer.parseInt(directionValue) == 1) {
                        bundleReltn.setOutgoing(true); 
                    } else {
                        bundleReltn.setOutgoing(false);
                    }
                }
                
            }
        }
        
        
        
        final ModelAndView mav = new ModelAndView(); 
        
        mav.setViewName("updateBundleDirection");
        
        return mav;
    }
    
    
    
    /**
     * Display a Domain
     */
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView viewDomain (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                                            @RequestParam(required=false) String domainName,
                                                            HttpSession session, 
                                                            Model model) throws java.security.cert.CertificateException { 		
            if (log.isDebugEnabled()) {
                log.debug("Enter View Domain");
            }		
            
            if (StringUtils.isEmpty(domainName))
            	domainName = (String)session.getAttribute("currentDomainName");
            
            ModelAndView mav = new ModelAndView(); 

            mav.setViewName("domain"); 

            String action = "Add";

            DomainForm form = (DomainForm) session.getAttribute("domainForm");
            
            if (form == null) {
                    form = new DomainForm();
            }
            
            model.addAttribute("domainForm", form);
            model.addAttribute("action", action);
            model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
            

            mav.addObject("action", action);
            mav.addObject("statusList", EntityStatus.getEntityStatusList());

            session.setAttribute("currentDomainName", domainName);
            if ((domainName != null) && (domainName.length() > 0)) 
            {
                if (log.isDebugEnabled()) {
                    log.debug("Need to search for Domain ID: " + domainName);
                }		

                Domain results = null;
                model.addAttribute("domainName", domainName);

                AddressForm addrform = new AddressForm();
                addrform.setDomainName(domainName);
                model.addAttribute("addressForm",addrform);

                final CertificateForm cform = new CertificateForm();
                cform.setDomainName(domainName);
                final AnchorForm aform = new AnchorForm();
                aform.setDomainName(domainName);

                model.addAttribute("certificateForm",cform);
                model.addAttribute("anchorForm",aform);
                
                if (domainService != null) {
                	try
                	{
                        results = domainService.getDomain(domainName);
                	}
                	catch (ServiceException e)
                	{
                		e.printStackTrace();
                	}
                        if (results != null) {
                            
                                if (log.isDebugEnabled()) {
                                    log.debug("Found a valid domain" + results.toString());
                                }		
                                
                                Collection<TrustBundleDomainReltn> bundles = null;
                                
                                // Get Trust Bundles
                                try {
                                    bundles = bundleService.getTrustBundlesByDomain(domainName, true);
                                } catch (ServiceException cse) {
                                    
                                }
                                
                                if(bundles != null) {
                                
                                    model.addAttribute("trustBundles", bundles);                                                                        
                                    
                                    final Map<String, Object> bundleMap = new HashMap<String, Object>(bundles.size());                                                                                                            
                                    
                                    Collection<TrustBundleAnchor> tbAnchors;    // Store anchors for each bundle   
                                                                        

                                    for(TrustBundleDomainReltn bundle : bundles) 
                                    {                                        
                                        tbAnchors = bundle.getTrustBundle().getTrustBundleAnchors();    
                                        final Map<TrustBundleAnchor, String> anchorMap = new HashMap<TrustBundleAnchor, String>(tbAnchors.size());                                                                                
                                        
                                        //String[] anchorDNs = new String[tbAnchors.size()];  // String array for storing anchor DNs
                                        
                                        // Loop through anchors to collect some information about the certificates
                                        for(TrustBundleAnchor anchor : tbAnchors) {
                                             
 
                                                final X509Certificate cert = anchor.getAsX509Certificate();                                         
                                           
                                                final String subjectDN = cert.getSubjectDN().toString();
                                                anchorMap.put(anchor, subjectDN);
    
                                        }
                                                                                                                                                               
                                        bundleMap.put(bundle.getTrustBundle().getBundleName(), anchorMap);
                                                                                
                                    }
                                    
                                    model.addAttribute("bundleMap", bundleMap);
                                }
                                
                                form.populate(results);
                                action = "Update";
                                model.addAttribute("action", action);
                                
                                // SETTING THE ADDRESSES OBJECT
                                model.addAttribute("addressesResults", results.getAddresses());

                                // BEGIN: temporary code for mocking purposes
                                String owner = "";
                                owner = results.getDomainName();
                                model.addAttribute("addressesResults", results.getAddresses());
                                Collection<Certificate> certlist = null;
                                try {
                                        certlist = certService.getCertificatesByOwner(owner);
                                } catch (ServiceException e) {
                                        e.printStackTrace();
                                }

                                Collection<Anchor> anchorlist = null;
                                try {
                                        anchorlist = anchorService.getAnchorsForOwner(owner, false, false, "");
                                } catch (ServiceException e) {
                                        e.printStackTrace();
                                }

                                model.addAttribute("certificatesResults", certlist);

                                // convert Anchor to AnchorForm
                                final Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);					
                                // now set anchorsResults
                                model.addAttribute("anchorsResults", convertedanchors);

                                // END: temporary code for mocking purposes			

                                final SimpleForm simple = new SimpleForm();
                                simple.setDomainName(domainName);
                                model.addAttribute("simpleForm",simple);
                                mav.addObject("action", action);
                        }
                        else {
                                log.warn("Service returned a null Domain for a known key: " + domainName);		
                        }
                }
                else { 
                        log.error("Web Service bean is null.  Configuration error detected.");		
                }
                
                if (AjaxUtils.isAjaxRequest(requestedWith)) {
                        // prepare model for rendering success message in this request
                        model.addAttribute("message", "");
                        model.addAttribute("ajaxRequest", true);
                        model.addAttribute("action", action);
                        return null;
                }
            }

            if (log.isDebugEnabled()) log.debug("Exit");
            return mav;
    }
	
	/**
	 * Execute the save and return the results
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/saveupdate", method=RequestMethod.POST )
	public ModelAndView saveDomain (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 							   
								HttpSession session, 
								@RequestParam(value="submitType") String actionPath,
							        @ModelAttribute("domainForm") DomainForm form,
							        Model model)  
        { 		
		if (log.isDebugEnabled()) log.debug("Enter");
		if (log.isDebugEnabled()) log.debug("Entered saveDomain");
		if (log.isDebugEnabled()) log.debug("The value of actionPath: "+actionPath);
		
                ModelAndView mav = new ModelAndView(); 
		
                if (actionPath.equalsIgnoreCase("cancel")) {
			
                    if (log.isDebugEnabled()) 
                    {
                        log.debug("trying to cancel from saveupdate");
                    }			                    
                    
                    return new ModelAndView("redirect:/config/main");
                    
		} else if ((actionPath.equalsIgnoreCase("update") || actionPath.equalsIgnoreCase("add"))) {
                    
                    HashMap<String, String> msgs = new HashMap<String, String>();
                    
                    mav.addObject("msgs", msgs);

                    mav.setViewName("domain");

                    try {
                        
                        if (actionPath.equalsIgnoreCase("add")) {
                            
                            // Add domain to configuration service
                            domainService.addDomain(form.getDomainFromForm());
                            session.setAttribute("currentDomainName", form.getDomainName());
                            final List<Domain> result = new ArrayList<Domain>(domainService.searchDomains(form.getDomainName(), form.getStatus()));
                            
                            if(form.getSelectedBundles() != "") {                                                                
                            
                                // Associate trust bundles if selected
                                final String selBundle = form.getSelectedBundles().replace("wHiTeSpAcE", " ");
                                
                                final String[] bundles = selBundle.split(",");

                                int bundleCount = bundles.length;

                                log.debug("# of bundles associated: "+bundleCount);

                                // Associate trust bundles to Domain
                                for(int i=0; i<bundleCount; i++) {                                
                                    /*
                                     * TODO: Add  incoming and outgoing indicators
                                     */
                                    final String[] bundleString = bundles[i].split("\\|\\|\\|\\|");
                                    
                                    if(bundleString[1].equals("both")) {
                                        bundleService.associateTrustBundleToDomain(bundleString[0], result.get(0).getDomainName(), true, true);
                                    } else if (bundleString[1].equals("in")) {
                                    	bundleService.associateTrustBundleToDomain(bundleString[0], result.get(0).getDomainName(), true, false);
                                    } else if (bundleString[1].equals("out")) {
                                    	bundleService.associateTrustBundleToDomain(bundleString[0], result.get(0).getDomainName(), false, true);
                                    } else {
                                    	bundleService.associateTrustBundleToDomain(bundleString[0], result.get(0).getDomainName(), false, false);
                                    }
                                    
                                    log.error("Added Bundle ID #"+bundles[i]);
                                }   
                            }
                            
                            if (result.size() > 0) {
                                    form = new DomainForm();
                                    form.populate(result.get(0));
                                    form.setDomainName(result.get(0).getDomainName());
                                    msgs.put("msg", "domain.add.success");
                            }
                            
                        } else if (actionPath.equalsIgnoreCase("update")) {
                            
                            domainService.updateDomain(form.getDomainFromForm());
                            
                            final List<Domain> result = new ArrayList<Domain>(domainService.searchDomains(form.getDomainName(), form.getStatus()));
                            
                            if (result.size() > 0) {
                                    form = new DomainForm();
                                    form.populate(result.get(0));
                            }
                            
                            msgs.put("msg", "domain.update.success");
                        }

                        final AddressForm addrform = new AddressForm();
                        addrform.setId(form.getDomainFromForm().getId());
                        model.addAttribute("domainForm",form);
                        model.addAttribute("addressForm",addrform);

                        final CertificateForm cform = new CertificateForm();
                        cform.setId(form.getDomainFromForm().getId());
                        AnchorForm aform = new AnchorForm();
                        aform.setId(form.getDomainFromForm().getId());

                        model.addAttribute("certificateForm",cform);
                        model.addAttribute("anchorForm",aform);
                        
                        SimpleForm simple = new SimpleForm();
                        simple.setId(form.getDomainFromForm().getId());
                        model.addAttribute("simpleForm",simple);

                        // once certificates and anchors are available change code accordingly
                        // begin: add these dummy records too
                        final String owner = form.getDomainFromForm().getPostmasterAddress().getEmailAddress();

                        try {
                            if(owner != null && !owner.equalsIgnoreCase("")){
                                // BEGIN: temporary code for mocking purposes
                                Collection<Certificate> certlist = null;
                                try {
                                        certlist = certService.getCertificatesByOwner(owner);
                                        model.addAttribute("certificatesResults", certlist);

                                } catch (ServiceException e) {
                                        e.printStackTrace();
                                }

                                Collection<Anchor> anchorlist = null;
                                anchorlist = anchorService.getAnchorsForOwner(owner, false, false, "");
                                // convert Anchor to AnchorForm
                                Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);
                                // now set anchorsResults
                                model.addAttribute("anchorsResults", convertedanchors);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        
                        
                        
                        
                        // END: temporary code for mocking purposes			


                        //  end: add these dummy records too
                        model.addAttribute("addressesResults", form.getDomainFromForm().getAddresses());

                        model.addAttribute("action", "update");
                        if (log.isDebugEnabled()) {
                            log.debug("Stored domain: " + form.getDomainFromForm().toString());
                        }
                                

                    } catch (ServiceException e) {
                            log.error(e);
                            msgs.put("domainService", "domainService.add.error");
                    }catch(Exception ed){
                            log.error(ed);
                    }
		}
		if (log.isDebugEnabled()) log.debug("Exit");
		
                return new ModelAndView("redirect:/config/domain?id="+form.getDomainName());
                //return mav;
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
                final KeyStore localKeyStore = KeyStore.getInstance("PKCS12", DNSController.getJCEProviderName());

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

    private String getTrustedEntityName(X500Principal prin)
    {
    	// check the CN attribute first
    	
    	
        // get the domain name
                Map<String, String> oidMap = new HashMap<String, String>();
                oidMap.put("1.2.840.113549.1.9.1", "EMAILADDRESS");  // OID for email address
                String prinName = prin.getName(X500Principal.RFC1779, oidMap);

                
                String searchString = "CN=";
                int index = prinName.indexOf(searchString);
                if (index == -1)
                {
                        searchString = "EMAILADDRESS=";
                        // fall back to email
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
}
