package org.nhindirect.config.ui;
import java.io.ByteArrayOutputStream;
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
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.StringUtils;
import org.nhind.config.rest.CertificateService;
import org.nhindirect.common.crypto.KeyStoreProtectionManager;
import org.nhindirect.common.crypto.MutableKeyStoreProtectionManager;
import org.nhindirect.common.crypto.WrappableKeyProtectionManager;
import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.Certificate;
import org.nhindirect.config.model.EntityStatus;
import org.nhindirect.config.model.utils.CertUtils;
import org.nhindirect.config.model.utils.CertUtils.CertContainer;
import org.nhindirect.config.ui.form.CertificateForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.form.SimpleForm;
import org.nhindirect.config.ui.util.AjaxUtils;
import org.nhindirect.config.ui.util.PrivateKeyType;
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
		//if (log.isDebugEnabled()) 
			log.error("Enter domain/addcertificate");
		
		if(actionPath.equalsIgnoreCase("cancel")){
			if (log.isDebugEnabled()) log.debug("trying to cancel from saveupdate");
			final SearchDomainForm form2 = (SearchDomainForm) session
					.getAttribute("searchDomainForm");
			model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
			model.addAttribute("ajaxRequest", AjaxUtils
					.isAjaxRequest(requestedWith));

			mav.setViewName("main");
			mav.addObject("privKeyTypeList", PrivateKeyType.getPrivKeyTypeList());
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
                            model.addAttribute("passphraseError", false);

                            if (!certificateForm.getFileData().isEmpty()) {


					final String passphrase = (certificateForm.getKeyPassphrase() == null) ? "" : certificateForm.getKeyPassphrase();
					
					PrivateKeyType privKeyType =  PrivateKeyType.fromString(certificateForm.getPrivKeyType());
					if ((privKeyType == PrivateKeyType.PKCS8_PASSPHRASE || privKeyType == PrivateKeyType.PKCS_12_PASSPHRASE) && 
							StringUtils.isEmpty(passphrase))
					{
						// can't move on if a passphrase is required and one is not supplied
						model.addAttribute("passphraseError", true);
					}
					
					
					
					else
					{
						byte[] certOrP12Bytes = certificateForm.getFileData().getBytes();
						byte[] privateKeyBytes = null;
						// need to determine if there is a private key or not

						//final CertUtils.CertContainer cont = CertUtils.toCertContainer(bytes, passphrase.toCharArray(), passphrase.toCharArray());
						
						if (privKeyType == PrivateKeyType.PKCS_12_PASSPHRASE || privKeyType == PrivateKeyType.PKCS_12_UNPROTECTED)
						{
							log.debug("Converting byte stream to cert container");

							// there is a private key present.. normalized it to an unproted format
							//if (cont.getKey() != null)
							//{
								log.debug("Private key exists; normalizing to non-protected p12 format.");
								certOrP12Bytes = CertUtils.changePkcs12Protection(certOrP12Bytes, passphrase.toCharArray(), passphrase.toCharArray(), 
									"".toCharArray(), "".toCharArray());
							//}
						}
						else if (privKeyType != PrivateKeyType.NONE)
						{
							// there is a private key file associated with this request
							privateKeyBytes = certificateForm.getPrivKeyData().getBytes();
							
							// get the private key... it may be different formats, so be on the watch
							if (privKeyType == PrivateKeyType.PKCS8_PASSPHRASE)
							{
								// this is a pass phrase protected private key... normalized it to an unprotected
								// key
								try
								{
									final EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(privateKeyBytes);
									final Cipher cipher = Cipher.getInstance(encryptPKInfo.getAlgName());
									final PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
									final SecretKeyFactory secFac = SecretKeyFactory.getInstance(encryptPKInfo.getAlgName());
									final Key pbeKey = secFac.generateSecret(pbeKeySpec);
									final AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
									cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
									final KeySpec pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
									final KeyFactory kf = KeyFactory.getInstance("RSA");
									privateKeyBytes = kf.generatePrivate(pkcs8KeySpec).getEncoded();
								}
								catch (Exception e)
								{
									return mav;
								}
							}
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
	                                            
	                                            // convert the cert and key to the proper storage format
	                                            cert.setData(toCertDataFormat(certOrP12Bytes, privateKeyBytes, privKeyType));
	                                            
	                                            cert.setOwner(owner);
	                                            cert.setStatus(org.nhindirect.config.model.EntityStatus.valueOf(estatus.toString()));
	
	                                            final ArrayList<Certificate> certlist = new ArrayList<Certificate>();
	                                            certlist.add(cert);
	                                            log.debug("Adding certificate to config store.");
	                                            certService.addCertificate(cert);
												
	                                            log.debug("Certificate add SUCCESSFUL");
	                                            
	
	                                        }
	
						}
					} 
					else 
					{
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
				if (this.keyManager != null && this.keyManager instanceof MutableKeyStoreProtectionManager)
				{
					final KeyStore keyStore = ((MutableKeyStoreProtectionManager)keyManager).getKS();
					// find the certs that don't have private keys and check them against the 
					// the key store manager to see if they have private keys
					for (Certificate cert : certs)
					{
						if (!cert.isPrivateKey())
						{
							
							try
							{
								final X509Certificate checkCert = CertUtils.toX509Certificate(cert.getData());
								final String alias = keyStore.getCertificateAlias(checkCert);
								
								if (!StringUtils.isEmpty(alias))
								{
									// check if this entry has a private key associated with
									// it
									final PrivateKey privKey = (PrivateKey)keyStore.getKey(alias, "".toCharArray());
									if (privKey != null)
										cert.setPrivateKey(true);
								}
							}
							catch (Exception e)
							{
								
							}
						}
					}
				}
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
	
			mav.addObject("privKeyTypeList", PrivateKeyType.getPrivKeyTypeList());
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
		}
		return mav;
	}			
	
	/*
	 * Converts an incoming P12 format to an appropriate format to be store in the config store.  If a keystore protection manager
	 * has been configured, then the private key is wrapped before sending to the config store.
	 */
	private byte[] toCertDataFormat(byte[] certOrP12Bytes, byte[] privateKeyBytes, PrivateKeyType privKeyType) throws CryptoException
	{
		try
		{
			// if there is no private key, then just return the encoded certificate
			if (privKeyType == PrivateKeyType.NONE)
				return certOrP12Bytes;
			
			final CertContainer cont =  CertUtils.toCertContainer(certOrP12Bytes);
			
			// if this is a PKCS12 format, then either return the bytes as is, or if there is keystore manager, wrap the private keys
			if (privKeyType == PrivateKeyType.PKCS_12_PASSPHRASE | privKeyType == PrivateKeyType.PKCS_12_UNPROTECTED)
			{
				// at this point, any PKCS12 byte stream should be normalized meaning that the private key is unencrypted
				
				// if there is no keystore manager, we can't wrap the keys, so we'll just send them over the wire
				// as PKCS12 file
				if (this.keyManager == null)
				{
					this.log.info("Storing PKCS12 file in PKCS12 unprotected format");
					return certOrP12Bytes;
				}
				else
				{
					this.log.info("Storing PKCS12 file in wrapped format");
					// now wrap the private key
					final byte[] wrappedKey = this.keyManager.wrapWithSecretKey((SecretKey)((KeyStoreProtectionManager)keyManager).getPrivateKeyProtectionKey(), 
							cont.getKey());
					
					// return the wrapped key format
					return CertUtils.certAndWrappedKeyToRawByteFormat(wrappedKey, cont.getCert());
				}
			}
			
			// when there is private key file, then either turn into a PKCS12 file (if there is no key manager), or wrap the key.
			else
			{
				
				// first thing, is the key is already wrapped, then do nothing to the key and return a bytes stream using the 
				// cert and wrapped key format
				if (privKeyType == PrivateKeyType.PKCS8_WRAPPED)
				{
					this.log.info("Storing already wrapped PKCS8 file");
					return CertUtils.certAndWrappedKeyToRawByteFormat(privateKeyBytes, cont.getCert());
				}
				
				// get a private key object, the private key is normalized at this point into an unencrypted format
				final KeyFactory kf = KeyFactory.getInstance("RSA", CertUtils.getJCEProviderName());
				final PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec (privateKeyBytes);
				final Key privKey = kf.generatePrivate (keysp);
				
	
				if (this.keyManager == null)
				{
					this.log.info("Storing PKCS8 private key in PKCS12 unprotected format");
					
					// if there is no keystore manager, we can't wrap the keys, so we'll just send them over the wire
					// as PKCS12 file.  need to turn this into a PKCS12 format
					final KeyStore localKeyStore = KeyStore.getInstance("PKCS12", CertUtils.getJCEProviderName());
					localKeyStore.load(null, null);
					
					localKeyStore.setKeyEntry("privCert", privKey, "".toCharArray(),  new java.security.cert.Certificate[] {cont.getCert()});
					final ByteArrayOutputStream outStr = new ByteArrayOutputStream();
					localKeyStore.store(outStr, "".toCharArray());		
					
					try
					{
						return outStr.toByteArray();
					}
					finally
					{
						IOUtils.closeQuietly(outStr);
					}
				}		
				else
				{
					this.log.info("Storing PKCS8 private key in wrapped format");
					// wrap the key and turn the stream in the wrapped key format
					final byte[] wrappedKey = this.keyManager.wrapWithSecretKey((SecretKey)((KeyStoreProtectionManager)keyManager).getPrivateKeyProtectionKey(), 
							privKey);
					return CertUtils.certAndWrappedKeyToRawByteFormat(wrappedKey, cont.getCert());
				}
			}
		}
		catch (Exception e)
		{
			throw new CryptoException("Failed to conver certificate and key to cert data format: " + e.getMessage(), e);
		}
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
			if (this.keyManager != null && this.keyManager instanceof MutableKeyStoreProtectionManager)
			{
				final KeyStore keyStore = ((MutableKeyStoreProtectionManager)keyManager).getKS();
				// find the certs that don't have private keys and check them against the 
				// the key store manager to see if they have private keys
				for (Certificate cert : certlist)
				{
					if (!cert.isPrivateKey())
					{
						
						try
						{
							final X509Certificate checkCert = CertUtils.toX509Certificate(cert.getData());
							final String alias = keyStore.getCertificateAlias(checkCert);
							
							if (!StringUtils.isEmpty(alias))
							{
								// check if this entry has a private key associated with
								// it
								final PrivateKey privKey = (PrivateKey)keyStore.getKey(alias, "".toCharArray());
								if (privKey != null)
									cert.setPrivateKey(true);
							}
						}
						catch (Exception e)
						{
							
						}
					}
				}
			}			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("certificatesResults", certlist);
		// END: temporary code for mocking purposes		
		mav.addObject("privKeyTypeList", PrivateKeyType.getPrivKeyTypeList());
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
