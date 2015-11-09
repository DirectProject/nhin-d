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

import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhind.config.rest.CertificateService;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.WrappableKeyProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.form.SimpleForm;
import org.nhindirect.config.ui.util.AjaxUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/certificates")
public class CertificatesController {
	private final Log log = LogFactory.getLog(getClass());

	private CertificateService certService;
	
	
	@Autowired(required=false)
	private WrappableKeyProtectionManager keyManager;
	
	@Inject
	public void setCertificateService(CertificateService certService)
    {
        this.certService = certService;
    }

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


		
		final ModelAndView mav = new ModelAndView(); 
		String strid = "";
		if (log.isDebugEnabled()) log.debug("Enter domain/addcertificate");
		
		if(actionPath.equalsIgnoreCase("cancel")){
			if (log.isDebugEnabled()) log.debug("trying to cancel from saveupdate");
			final SearchDomainForm form2 = (SearchDomainForm) session
					.getAttribute("searchDomainForm");
			model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
			model.addAttribute("ajaxRequest", AjaxUtils
					.isAjaxRequest(requestedWith));

			mav.setViewName("main");
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
			return mav;
		}
		if(actionPath.equalsIgnoreCase("newcertificate") || actionPath.equalsIgnoreCase("add certificate")){
			
			log.debug("Attempting to add certificate");
			if (this.keyManager == null)
				log.debug("Key manager is null");	
			else
				log.debug("Key manager is non-null");
			
			strid = ""+certificateForm.getId();
			// insert the new address into the Domain list of Addresses
			final EntityStatus estatus = certificateForm.getStatus();
			if (log.isDebugEnabled()) log.debug("beginning to evaluate filedata");		
			try{
                            model.addAttribute("certerror", false);

                            if (!certificateForm.getFileData().isEmpty()) {
					byte[] bytes = certificateForm.getFileData().getBytes();

					final String passphrase = (certificateForm.getKeyPassphrase() == null) ? "" : certificateForm.getKeyPassphrase();
					// need to determine if there is a private key or not
					log.debug("Converting byte stream to cert container");
					final CertUtils.CertContainer cont = CertUtils.toCertContainer(bytes, passphrase.toCharArray(), passphrase.toCharArray());
					
					// if there is a private key, then we need to convert
					// the p12 file to an unencrypted p12 file
					if (cont.getKey() != null)
					{
						log.debug("Private key exists; converting to non-protected p12 format.");
						bytes = CertUtils.changePkcs12Protection(bytes, passphrase.toCharArray(), passphrase.toCharArray(), 
							"".toCharArray(), "".toCharArray());
					}
					
					String owner = "";
                                        final String fileType = certificateForm.getFileData().getContentType();


                                        if(!fileType.matches("application/x-x509-ca-cert") && 
                                                !fileType.matches("application/octet-stream") &&
                                                !fileType.matches("application/x-pkcs12"))
                                        {

                                            model.addAttribute("certerror", true);

                                        } else {

                                        	
                                            final Certificate cert = new Certificate();
                                            
                                            // if there is no private key, then no need to convert to an appropriate storage type
                                            // otherwise we need to do proper conversion depending on the configured
                                            // key protection
                                            if (cont.getKey() == null)
                                            {
                                            	log.debug("Private key is null; setting data to plain data.");
                                            	cert.setData(bytes); // just a plain old public cert
                                            }
                                            else
                                            {
                                            	log.debug("Private key is non-null; converting to appropriate format");

                                            	cert.setData(toCertDataFormat(bytes)); // need to convert if necessary
 
                                            }
                                            
                                            cert.setOwner(owner);
                                            cert.setStatus(org.nhindirect.config.model.EntityStatus.valueOf(estatus.toString()));

                                            final ArrayList<Certificate> certlist = new ArrayList<Certificate>();
                                            certlist.add(cert);
                                            log.debug("Adding certificate to config store.");
                                            certService.addCertificate(cert);
											
                                            log.debug("Certificate add SUCCESSFUL");
                                            

                                        }


				} else {
					if (log.isDebugEnabled()) log.debug("DO NOT store the certificate into database BECAUSE THERE IS NO FILE");
				}

                                


			} catch (ServiceException ed) {
					log.error(ed);
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
			// certificate form and result
			try {
				final Collection<Certificate> certs = certService.getAllCertificates();
				model.addAttribute("certificatesResults", certs);
				 
				final CertificateForm cform = new CertificateForm();
				cform.setId(0);
				model.addAttribute("certificateForm",cform);
				
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			final SimpleForm simple = new SimpleForm();
			simple.setId(Long.parseLong(strid));
			model.addAttribute("simpleForm",simple);

			mav.setViewName("certificates"); 
			// the Form's default button action
			final String action = "Update";

			model.addAttribute("action", action);
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
	
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
		}
		return mav;
	}			
	
	/*
	 * Converts an incoming P12 format to an appropriate format to be store in the config store.  If a keystore protection manager
	 * has been configured, then the private key is wrapped before sending to the config store.
	 * The format of the byte stream is assumed to be a non-encrypted p12 format
	 */
	private byte[] toCertDataFormat(byte[] bytes) throws CryptoException
	{
		// if there is no keystore manager, then just return as is
		if (this.keyManager == null)
			return bytes;
		
		// get the private key from the byte stream
		final CertUtils.CertContainer cont = CertUtils.toCertContainer(bytes);
		
		// now wrap the private key
		
		final byte[] wrappedKey = this.keyManager.wrapWithSecretKey((SecretKey)((KeyStoreProtectionManager)keyManager).getPrivateKeyProtectionKey(), 
				cont.getKey());
		
		// return the wrapped key format
		return CertUtils.certAndWrappedKeyToRawByteFormat(wrappedKey, cont.getCert());
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/removecertifcates", method = RequestMethod.POST)
	public ModelAndView removeCertificates (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute CertificateForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		final ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/removecertificates");
		if(simpleForm.getRemove() != null){
			if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		}
		
		if (certService != null && simpleForm != null && actionPath != null && (actionPath.equalsIgnoreCase("deletecertificate") || actionPath.equalsIgnoreCase("Remove Selected")) && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			if (log.isDebugEnabled()) log.debug("removing certificates");
			try{
				// get list of certificates for this domain
				final Collection<Certificate> certs = certService.getAllCertificates();
				final ArrayList<Long> certtoberemovedlist = new ArrayList<Long>();
				// now iterate over each one and remove the appropriate ones
				for (int x = 0; x < cnt; x++) {
					final String removeid = simpleForm.getRemove().get(x);
					for (Certificate t : certs) 
					{
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
				certService.deleteCertificatesByIds(certtoberemovedlist);
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to update certificates");
			} catch (ServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		// BEGIN: temporary code for mocking purposes
		final CertificateForm cform = new CertificateForm();
		cform.setId(0);
		model.addAttribute("certificateForm",cform);
		
		mav.setViewName("certificates"); 
		// the Form's default button action
		final String action = "Update";
		model.addAttribute("action", action);
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		mav.addObject("action", action);

		Collection<Certificate> certlist = null;
		try {
			certlist = certService.getAllCertificates();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("certificatesResults", certlist);
		// END: temporary code for mocking purposes			
		mav.addObject("statusList", EntityStatus.getEntityStatusList());

		model.addAttribute("simpleForm",simpleForm);
		final String strid = ""+simpleForm.getId();
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
