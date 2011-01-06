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
import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.ConfigurationService;

import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.form.SettingsForm;
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

@Controller
@RequestMapping("/settings")
public class SettingsController {
	private final Log log = LogFactory.getLog(getClass());
	
	private ConfigurationService configSvc;
	
	@Inject
	public void setConfigurationService(ConfigurationService service)
	{
	    this.configSvc = service;
	}
	
	public SettingsController(){
		if (log.isDebugEnabled()) log.debug("SettingsController initialized");
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/addsetting", method = RequestMethod.POST)
	public ModelAndView addSetting (
								@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute SettingsForm settingsForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath
						        ) { 		

		ModelAndView mav = new ModelAndView(); 
		String strid = "";
		String key = "";
		String value = "";
		if (log.isDebugEnabled()) log.debug("Enter domain/addsetting");
		
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
		if(actionPath.equalsIgnoreCase("newsetting")){
			if (log.isDebugEnabled()) log.debug("trying to get/set settings");
			strid = ""+settingsForm.getId();
			key = ""+settingsForm.getKey();
			value = ""+settingsForm.getValue();
			try {
				if (log.isDebugEnabled()) log.debug("trying set settings services");
				configSvc.addSetting(key, value);
				if (log.isDebugEnabled()) log.debug("PAST trying set settings services");
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
			
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			SimpleForm simple = new SimpleForm();
			simple.setId(Long.parseLong(strid));
			model.addAttribute("simpleForm",simple);

			try {
				model.addAttribute("settingsResults", configSvc.getAllSettings());
			} catch (ConfigurationServiceException e) {
				e.printStackTrace();
			}
			mav.setViewName("settings"); 
			// the Form's default button action
			String action = "Update";
			model.addAttribute("settingsForm", settingsForm);
			model.addAttribute("action", action);
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
	
		}
		
		return mav;
	}			
	
	@PreAuthorize("hasRole('ROLE_ADMIN')") 
	@RequestMapping(value="/removesettings", method = RequestMethod.POST)
	public ModelAndView removeAnchors (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute SimpleForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		

		ModelAndView mav = new ModelAndView(); 
	
		if (log.isDebugEnabled()) log.debug("Enter domain/removesettings");
		if(simpleForm.getRemove() != null){
			if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		}
		
		String strid = ""+simpleForm.getId();
		if (configSvc != null && simpleForm != null && actionPath != null && actionPath.equalsIgnoreCase("delete") && simpleForm.getRemove() != null) {
			int cnt = simpleForm.getRemove().size();
			try{
				Collection<String> settingstoberemovedlist = simpleForm.getRemove();
				if (log.isDebugEnabled()) log.debug(" Trying to remove settings from database");
				configSvc.deleteSetting(settingstoberemovedlist);
	    		if (log.isDebugEnabled()) log.debug(" SUCCESS Trying to remove settings");
			} catch (ConfigurationServiceException e) {
				if (log.isDebugEnabled())
					log.error(e);
			}
		}
		try {
			model.addAttribute("settingsResults", configSvc.getAllSettings());
		} catch (ConfigurationServiceException e) {
			e.printStackTrace();
		}
		mav.setViewName("settings"); 
		String action = "Update";
		model.addAttribute("settingsForm", new SettingsForm());
		model.addAttribute("action", action);
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		model.addAttribute("simpleForm",simpleForm);
		strid = ""+simpleForm.getId();
		
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
