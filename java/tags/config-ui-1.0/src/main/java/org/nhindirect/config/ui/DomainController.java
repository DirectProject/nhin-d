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
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.AddressService;
import org.nhindirect.config.service.AnchorService;
import org.nhindirect.config.service.CertificateService;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.DomainService;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.Address;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.ui.form.DomainForm;
import org.nhindirect.config.ui.form.AddressForm;
import org.nhindirect.config.ui.form.LoginForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.form.SimpleForm;
import org.nhindirect.config.ui.form.AnchorForm;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.util.AjaxUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.Anchor;
import java.io.FileOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/domain")
public class DomainController {
	private final Log log = LogFactory.getLog(getClass());
	@Inject
	private DomainService dService;

	@Inject
	private AddressService aService;
	
	@Inject
	private AnchorService anchorService;
	
	@Inject
	private CertificateService certService;

	public DomainController() {
		if (log.isDebugEnabled()) log.debug("DomainController initialized");
	}
	
	private Collection<AnchorForm> convertAnchors(Collection<Anchor> anchors){
		Collection<AnchorForm> form = new ArrayList<AnchorForm>();
		for (Iterator iter = anchors.iterator(); iter.hasNext();) {
			Anchor t = (Anchor) iter.next();
			AnchorForm e = new AnchorForm();
			e.setCertificateData(t.getData());
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
            if (t.getData() != null)
            {
                    // get the owner from the certificate information
                    // first transform into a certificate
                    CertContainer cont;
					try {
						cont = toCertContainer(t.getData());
	                    if (cont != null && cont.getCert() != null)
	                    {
	                            // now get the owner info from the cert
	                            theUser = getEmailAddress(cont.getCert().getSubjectX500Principal());
	                    }
					} catch (Exception e1) {
						e1.printStackTrace();
					}
            }
			e.setTrusteddomainoruser(theUser);
			form.add(e);
		}			
		
		return form;
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/addanchor", method = RequestMethod.POST)
	public ModelAndView addAnchor (
								@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute AnchorForm anchorForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath
						        ) { 		

		ModelAndView mav = new ModelAndView(); 
		String strid = "";
		if (log.isDebugEnabled()) log.debug("Enter domain/addanchor");
		
		if(actionPath.equalsIgnoreCase("newanchor")){
			strid = ""+anchorForm.getId();
			Domain dom = dService.getDomain(Long.parseLong(strid));
			String owner = "";
			if(dom != null){
				owner = dom.getDomainName();
			}
			// insert the new address into the Domain list of Addresses
			EntityStatus estatus = anchorForm.getStatus();
			if (log.isDebugEnabled()) log.debug("beginning to evaluate filedata");		
			try{
				if (!anchorForm.getFileData().isEmpty()) {
					byte[] bytes = anchorForm.getFileData().getBytes();
                   String theUser = "";
                    if (bytes != null)
                    {
                            // get the owner from the certificate information
                            // first transform into a certificate
                            CertContainer cont = toCertContainer(bytes);
                            if (cont != null && cont.getCert() != null)
                            {
                                    // now get the owner info from the cert
                                    theUser = getEmailAddress(cont.getCert().getSubjectX500Principal());
                                    anchorForm.setTrusteddomainoruser(theUser);
                            }
                    }

					// store the bytes somewhere
					Anchor ank = new Anchor();
					ank.setData(bytes);
					if (log.isDebugEnabled()) log.debug("incoming is: "+anchorForm.isIncoming()+" and outgoing is: "+anchorForm.isOutgoing());
					ank.setIncoming(anchorForm.isIncoming());
					ank.setOutgoing(anchorForm.isOutgoing());
					ank.setOwner(owner);
					ank.setStatus(anchorForm.getStatus());

					ArrayList<Anchor> anchorlist = new ArrayList<Anchor>();
					anchorlist.add(ank);
					
					anchorService.addAnchors(anchorlist);
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
				Collection<Certificate> certs = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
				model.addAttribute("certificatesResults", certs);
				 
				Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
				// convert Anchor to AnchorForm
				Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
				
				CertificateForm cform = new CertificateForm();
				cform.setId(dom.getId());
				model.addAttribute("certificateForm",cform);
				
				AnchorForm aform = new AnchorForm();
				aform.setId(dom.getId());
				model.addAttribute("anchorForm",aform);
				
			} catch (ConfigurationServiceException e1) {
				e1.printStackTrace();
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			SimpleForm simple = new SimpleForm();
			simple.setId(Long.parseLong(strid));
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
		
		return mav;
	}			
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/removeanchors", method = RequestMethod.POST)
	public ModelAndView removeAnchors (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute AnchorForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/removeanchor");
		if(simpleForm.getRemove() != null){
			if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		}
		
		String strid = ""+simpleForm.getId();
		Domain dom = dService.getDomain(Long.parseLong(strid));
		String owner = "";
		String domname = "";
		if( dom != null){
			domname = dom.getDomainName();
			owner = domname;
		}
		if (dService != null && simpleForm != null && actionPath != null && actionPath.equalsIgnoreCase("deleteanchors") && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing anchors for domain with name: " + domname);
			try{
				// get list of certificates for this domain
				Collection<Anchor> certs = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
				ArrayList<Long> certtoberemovedlist = new ArrayList<Long>();
				// now iterate over each one and remove the appropriate ones
				for (int x = 0; x < cnt; x++) {
					String removeid = simpleForm.getRemove().get(x);
					for (Iterator iter = certs.iterator(); iter.hasNext();) {
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
				// with the collection of anchor ids now remove them from the anchorService
				if (log.isDebugEnabled()) log.debug(" Trying to remove anchors from database");
				anchorService.removeAnchors(certtoberemovedlist);
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to update the domain with removed anchors");
				AddressForm addrform = new AddressForm();
				addrform.setId(dom.getId());
				model.addAttribute("addressForm",addrform);
			} catch (ConfigurationServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		// BEGIN: temporary code for mocking purposes
		CertificateForm cform = new CertificateForm();
		cform.setId(dom.getId());
		model.addAttribute("certificateForm",cform);
		
		AnchorForm aform = new AnchorForm();
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
			certlist = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
		} catch (ConfigurationServiceException e) {
			e.printStackTrace();
		}
		
		Collection<Anchor> anchorlist = null;
		try {
			anchorlist = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
		} catch (ConfigurationServiceException e) {
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
	
		if(actionPath.equalsIgnoreCase("newcertificate")){
			strid = ""+certificateForm.getId();
			Domain dom = dService.getDomain(Long.parseLong(strid));
			String owner = "";
			if(dom != null){
				owner = dom.getPostMasterEmail();
			}
			// insert the new address into the Domain list of Addresses
			EntityStatus estatus = certificateForm.getStatus();
			if (log.isDebugEnabled()) log.debug("beginning to evaluate filedata");		
			try{
				if (!certificateForm.getFileData().isEmpty()) {
					byte[] bytes = certificateForm.getFileData().getBytes();
					owner = certificateForm.getOwner();
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
			// certificate and anchor forms and results
			try {
				Collection<Certificate> certs = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
				model.addAttribute("certificatesResults", certs);
				 
				Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
				// convert Anchor to AnchorForm
				Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
				
				CertificateForm cform = new CertificateForm();
				cform.setId(dom.getId());
				model.addAttribute("certificateForm",cform);
				
				AnchorForm aform = new AnchorForm();
				aform.setId(dom.getId());
				model.addAttribute("anchorForm",aform);
				
			} catch (ConfigurationServiceException e1) {
				e1.printStackTrace();
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			SimpleForm simple = new SimpleForm();
			simple.setId(Long.parseLong(strid));
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
		Domain dom = dService.getDomain(Long.parseLong(strid));
		String owner = "";
		String domname = "";
		if( dom != null){
			domname = dom.getPostMasterEmail();
			owner = domname;
		}
		if (dService != null && simpleForm != null && actionPath != null && actionPath.equalsIgnoreCase("deletecertificate") && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing certificates for domain with name: " + domname);
			try{
				// get list of certificates for this domain
				Collection<Certificate> certs = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
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
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to update the domain with removed certificates");
				AddressForm addrform = new AddressForm();
				addrform.setId(dom.getId());
				model.addAttribute("addressForm",addrform);
			} catch (ConfigurationServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		// BEGIN: temporary code for mocking purposes
		CertificateForm cform = new CertificateForm();
		cform.setId(dom.getId());
		model.addAttribute("certificateForm",cform);
		
		AnchorForm aform = new AnchorForm();
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
			certlist = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
		} catch (ConfigurationServiceException e) {
			e.printStackTrace();
		}
		
		Collection<Anchor> anchorlist = null;
		try {
			anchorlist = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
		} catch (ConfigurationServiceException e) {
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

		ModelAndView mav = new ModelAndView(); 
		String strid = "";
		if (log.isDebugEnabled()) log.debug("Enter domain/addaddress");
		
		if(actionPath.equalsIgnoreCase("newaddress")){
			strid = ""+addressForm.getId();
			Domain dom = dService.getDomain(Long.parseLong(strid));
			String owner = dom.getPostMasterEmail();
			// insert the new address into the Domain list of Addresses
			String anEmail = addressForm.getEmailAddress();
			String displayname = addressForm.getDisplayName();
			String endpoint = addressForm.getEndpoint();
			EntityStatus estatus = addressForm.getaStatus();
			String etype = addressForm.getType();
			
			if (log.isDebugEnabled()) log.debug(" Trying to add address: "+anEmail);
			Address e = new Address();
			e.setEmailAddress(anEmail);
			e.setDisplayName(displayname);
			e.setEndpoint(endpoint);
			e.setStatus(estatus);
			e.setType(etype);
			
			dom.getAddresses().add(e);
			
			try{
				dService.updateDomain(dom);
				if (log.isDebugEnabled()) log.debug(" After attempt to insert new email address ");
			} catch (ConfigurationServiceException ed) {
				if (log.isDebugEnabled())
					log.error(ed);
			}
			// certificate and anchor forms and results
			try {
				Collection<Certificate> certs = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
				model.addAttribute("certificatesResults", certs);
				 
				Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
				// convert Anchor to AnchorForm
				Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
				
				CertificateForm cform = new CertificateForm();
				cform.setId(dom.getId());
				model.addAttribute("certificateForm",cform);
				
				AnchorForm aform = new AnchorForm();
				aform.setId(dom.getId());
				model.addAttribute("anchorForm",aform);
				
			} catch (ConfigurationServiceException e1) {
				e1.printStackTrace();
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			SimpleForm simple = new SimpleForm();
			simple.setId(Long.parseLong(strid));
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
		
		return mav;
	}		
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/removeaddresses", method = RequestMethod.POST)
	public ModelAndView removeAddresses (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute SimpleForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/removeaddresses");
		if(simpleForm.getRemove() != null){
			if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		}
		
		String strid = ""+simpleForm.getId();
		Domain dom = dService.getDomain(Long.parseLong(strid));
		String domname = "";
		if( dom != null){
			domname = dom.getDomainName();
		}
		if (dService != null && simpleForm != null && actionPath != null && actionPath.equalsIgnoreCase("delete") && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing addresses for domain with name: " + domname);
			try{
				for (int x = 0; x < cnt; x++) {
					String removeid = simpleForm.getRemove().get(x);
				    for (Address t : dom.getAddresses()){
				    	if(t.getId() == Long.parseLong(removeid)){
					    	if (log.isDebugEnabled()){
					    		log.debug(" ");
					    		log.debug("domain address id: " + t.getId());
					    		log.debug(" ");
					    	}
				    		dom.getAddresses().remove(t);
				    		if(aService != null){
				    			if (log.isDebugEnabled()) log.debug("Address Service is not null. Now trying to remove address: "+t.getEmailAddress());
				    			aService.removeAddress(t.getEmailAddress());
				    		}
					    	if (log.isDebugEnabled()){
					    		log.debug(" REMOVED ");
					    		log.debug(" ");
					    		break;
					    	}
				    	}
					}			
				}
				if (log.isDebugEnabled()) log.debug(" Trying to update the domain with removed addresses");
				dService.updateDomain(dom);
				dom = dService.getDomain(Long.parseLong(strid));
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to update the domain with removed addresses");
				AddressForm addrform = new AddressForm();
				addrform.setId(dom.getId());
				model.addAttribute("addressForm",addrform);
				// BEGIN: temporary code for mocking purposes
				String owner = "";
				owner = dom.getPostMasterEmail();
				model.addAttribute("addressesResults", dom.getAddresses());

				Collection<Certificate> certlist = null;
				try {
					certlist = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
				} catch (ConfigurationServiceException e) {
					e.printStackTrace();
				}
				
				Collection<Anchor> anchorlist = null;
				try {
					anchorlist = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
				} catch (ConfigurationServiceException e) {
					e.printStackTrace();
				}
				
				model.addAttribute("certificatesResults", certlist);
				// convert Anchor to AnchorForm
				Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
			
				// END: temporary code for mocking purposes			
				
			} catch (ConfigurationServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}else if (dService != null && actionPath.equalsIgnoreCase("newaddress")) {
			// insert the new address into the Domain list of Addresses
			String anEmail = simpleForm.getPostmasterEmail();
			if (log.isDebugEnabled()) log.debug(" Trying to add address: "+anEmail);
			Address e = new Address();
			e.setEmailAddress(anEmail);
			dom.getAddresses().add(e);
			simpleForm.setPostmasterEmail("");
			try{
				dService.updateDomain(dom);
				if (log.isDebugEnabled()) log.debug(" After attempt to insert new email address ");
			} catch (ConfigurationServiceException ed) {
				if (log.isDebugEnabled())
					log.error(ed);
			}
		}
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
		String action = "Update";
		model.addAttribute("action", action);			
		DomainForm form = (DomainForm) session.getAttribute("domainForm");
		if (form == null) {
			form = new DomainForm();
			form.populate(dom);
		}
		model.addAttribute("domainForm", form);
		mav.setViewName("domain");
		String owner = dom.getPostMasterEmail();
		// certificate and anchor forms and results
		try {
			Collection<Certificate> certs = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
			model.addAttribute("certificatesResults", certs);
			 
			Collection<Anchor> anchors = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
			// convert Anchor to AnchorForm
			Collection<AnchorForm> convertedanchors = convertAnchors(anchors);					
			// now set anchorsResults
			model.addAttribute("anchorsResults", convertedanchors);
			
			CertificateForm cform = new CertificateForm();
			cform.setId(dom.getId());
			model.addAttribute("certificateForm",cform);
			
			AnchorForm aform = new AnchorForm();
			aform.setId(dom.getId());
			model.addAttribute("anchorForm",aform);
			
		} catch (ConfigurationServiceException e1) {
			e1.printStackTrace();
		}
			
		model.addAttribute("simpleForm",simpleForm);
		strid = ""+simpleForm.getId();
		if (log.isDebugEnabled()) log.debug(" the value of id of simpleform is: "+strid);
		 
		return mav;
	}		
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value="/remove", method = RequestMethod.POST)
	public ModelAndView removeDomain (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute SimpleForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/remove");
		if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		
		if (dService != null) {
			int cnt = simpleForm.getRemove().size();
			for (int x = 0; x < cnt; x++) {
				try {
					String strid = simpleForm.getRemove().remove(x);
					Domain dom = dService.getDomain(Long.parseLong(strid));
					String owner = dom.getDomainName();
					String domname = dom.getDomainName();
					if (log.isDebugEnabled()) log.debug("removing domain with name: " + domname);
					dService.removeDomain(domname);
					// now delete anchors
					try{
						// get list of certificates for this domain
						Collection<Anchor> certs = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
						ArrayList<Long> certtoberemovedlist = new ArrayList<Long>();
						// now iterate over each one and remove the appropriate ones
						for (Iterator iter = certs.iterator(); iter.hasNext();) {
							Anchor t = (Anchor) iter.next();
					    	certtoberemovedlist.add(t.getId());
						}			
						// with the collection of anchor ids now remove them from the anchorService
						if (log.isDebugEnabled()) log.debug(" Trying to remove anchors from database");
						anchorService.removeAnchors(certtoberemovedlist);
			    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to remove anchors");
					} catch (ConfigurationServiceException e) {
						if (log.isDebugEnabled())
							log.error(e);
					}
				} catch (ConfigurationServiceException e) {
					if (log.isDebugEnabled())
						log.error(e);
				}
			}
		}
		SearchDomainForm form2 = (SearchDomainForm) session.getAttribute("searchDomainForm");
		model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));

		mav.setViewName("main");
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
	/**
	 * Display a Domain
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView viewDomain (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
							        @RequestParam(required=false) String id,
									HttpSession session, 
							        Model model) { 		
		if (log.isDebugEnabled()) log.debug("Enter");		
		ModelAndView mav = new ModelAndView(); 
		
		mav.setViewName("domain"); 
		
		// the Form's default button action
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
		
		if ((id != null) &&
			(id.length() > 0)) {
			if (log.isDebugEnabled()) log.debug("Need to search for Domain ID: " + id);		
			
			Domain results = null;
			Long dId = Long.decode(id);
			
			AddressForm addrform = new AddressForm();
			addrform.setId(dId);
			model.addAttribute("addressForm",addrform);

			CertificateForm cform = new CertificateForm();
			cform.setId(dId);
			AnchorForm aform = new AnchorForm();
			aform.setId(dId);
			
			model.addAttribute("certificateForm",cform);
			model.addAttribute("anchorForm",aform);
			if (dService != null) {
				results = dService.getDomain(dId);
				if (results != null) {
					if (log.isDebugEnabled()) log.debug("Found a valid domain" + results.toString());		
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
						certlist = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
					} catch (ConfigurationServiceException e) {
						e.printStackTrace();
					}
					
					Collection<Anchor> anchorlist = null;
					try {
						anchorlist = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
					} catch (ConfigurationServiceException e) {
						e.printStackTrace();
					}
					
					model.addAttribute("certificatesResults", certlist);
					
					// convert Anchor to AnchorForm
					Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);					
					// now set anchorsResults
					model.addAttribute("anchorsResults", convertedanchors);
				
					// END: temporary code for mocking purposes			

					SimpleForm simple = new SimpleForm();
					simple.setId(dId);
					model.addAttribute("simpleForm",simple);
					mav.addObject("action", action);
				}
				else {
					log.warn("Service returned a null Domain for a known key: " + dId);		
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
							        Model model)  { 		
		if (log.isDebugEnabled()) log.debug("Enter");
		if (log.isDebugEnabled()) log.debug("Entered saveDomain");
		if (log.isDebugEnabled()) log.debug("The value of actionPath: "+actionPath);
		ModelAndView mav = new ModelAndView(); 
		if (actionPath.equalsIgnoreCase("cancel")) {
			if (log.isDebugEnabled()) log.debug("trying to cancel from saveupdate");
			SearchDomainForm form2 = (SearchDomainForm) session
					.getAttribute("searchDomainForm");
			model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
			model.addAttribute("ajaxRequest", AjaxUtils
					.isAjaxRequest(requestedWith));

			mav.setViewName("main");
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
			return mav;
		} else if ((actionPath.equalsIgnoreCase("update") || actionPath.equalsIgnoreCase("add"))){
			HashMap<String, String> msgs = new HashMap<String, String>();
			mav.addObject("msgs", msgs);
			if (log.isDebugEnabled()) log.debug("Inside update else if: submitType: " + actionPath);
		
			mav.setViewName("domain");

			if (log.isDebugEnabled()) log.debug("Form passed validation");
			try {
				if (actionPath.equals("add")) {
					dService.addDomain(form.getDomainFromForm());
					List<Domain> result = new ArrayList<Domain>(
							dService.searchDomain(form.getDomainName(),
									form.getStatus()));
					if (result.size() > 0) {
						form = new DomainForm();
						form.populate(result.get(0));
						msgs.put("msg", "domain.add.success");
					}
				} else if (actionPath.equals("update")) {
					dService.updateDomain(form.getDomainFromForm());
					List<Domain> result = new ArrayList<Domain>(
							dService.searchDomain(form.getDomainName(),
									form.getStatus()));
					if (result.size() > 0) {
						form = new DomainForm();
						form.populate(result.get(0));
					}
					msgs.put("msg", "domain.update.success");
				}

				AddressForm addrform = new AddressForm();
				addrform.setId(form.getDomainFromForm().getId());
				model.addAttribute("domainForm",form);
				model.addAttribute("addressForm",addrform);

				CertificateForm cform = new CertificateForm();
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
				String owner = form.getDomainFromForm().getPostMasterEmail();

				// BEGIN: temporary code for mocking purposes
				Collection<Certificate> certlist = null;
				try {
					certlist = certService.getCertificatesForOwner(owner, CertificateGetOptions.DEFAULT);
				} catch (ConfigurationServiceException e) {
					e.printStackTrace();
				}
				
				Collection<Anchor> anchorlist = null;
				try {
					anchorlist = anchorService.getAnchorsForOwner(owner, CertificateGetOptions.DEFAULT);
				} catch (ConfigurationServiceException e) {
					e.printStackTrace();
				}
				
				model.addAttribute("certificatesResults", certlist);
				
				// convert Anchor to AnchorForm
				Collection<AnchorForm> convertedanchors = convertAnchors(anchorlist);					
				// now set anchorsResults
				model.addAttribute("anchorsResults", convertedanchors);
			
				// END: temporary code for mocking purposes			

					
				//  end: add these dummy records too
				model.addAttribute("addressesResults", form.getDomainFromForm().getAddresses());

				model.addAttribute("action", "update");
				if (log.isDebugEnabled())
					log.debug("Stored domain: "
							+ form.getDomainFromForm().toString());

			} catch (ConfigurationServiceException e) {
				log.error(e);
				msgs.put("domainService", "domainService.add.error");
			}catch(Exception ed){
				log.error(ed);
			}
		}
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
		return ClassUtils.getShortName(ex.getClass() + ":" + ex.getMessage());
	}

	public void setdService(DomainService service) {
		this.dService = service;
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
                KeyStore localKeyStore = KeyStore.getInstance("PKCS12", "BC");

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
}
