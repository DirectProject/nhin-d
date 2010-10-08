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
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.config.service.ConfigurationServiceException;
import org.nhindirect.config.service.DomainService;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.ui.form.DomainForm;
import org.nhindirect.config.ui.form.LoginForm;
import org.nhindirect.config.ui.form.SearchDomainForm;
import org.nhindirect.config.ui.util.AjaxUtils;
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
import org.springframework.validation.BindException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/domain")
public class DomainController {
	private final Log log = LogFactory.getLog(getClass());
	
	@Inject
	private DomainService dService;
	
	public DomainController() {
		if (log.isDebugEnabled()) log.debug("DomainController initialized");
	}
	
	@RequestMapping(value="/remove", method = RequestMethod.POST)
	public ModelAndView removeDomain (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
						        HttpSession session,
						        @ModelAttribute SimpleForm simpleForm,
						        Model model,
						        @RequestParam(value="submitType") String actionPath)  { 		
	
		if (log.isDebugEnabled()) log.debug("Enter domain/remove");
		if (log.isDebugEnabled()) log.debug("the list of checkboxes checked or not is: "+simpleForm.getRemove().toString());
		Domain results = null;
		if (dService != null) {
			int cnt = simpleForm.getRemove().size();
			for(int x = 0;x< cnt;x++){
				try {
					String strid = simpleForm.getRemove().remove(x);
					if (log.isDebugEnabled()) log.debug("trying to remove id: "+strid);
					Domain dom = dService.getDomain(Long.parseLong(strid));
					if (log.isDebugEnabled()) log.debug("trying to remove name: "+dom.getDomainName());
					dService.removeDomain(strid);
					
				} catch (ConfigurationServiceException e) {
					if (log.isDebugEnabled()) log.error(e);
				}
			}
		}		
		
		ModelAndView mav = new ModelAndView(); 
		SearchDomainForm form2 = (SearchDomainForm) session.getAttribute("searchDomainForm");
		model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));

		mav.setViewName("main");
		mav.addObject("statusList", EntityStatus.getEntityStatusList());
		
		return mav;
	}	
	
	
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
		if (isLoggedIn(session)) {
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
				if (dService != null) {
					results = dService.getDomain(dId);
					if (results != null) {
						if (log.isDebugEnabled()) log.debug("Found a valid domain" + results.toString());		
						form.populate(results);
						action = "Update";
						model.addAttribute("action", action);
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
			
		}
		else {
			model.addAttribute(new LoginForm());
			mav.setViewName("login");
		}
		
		if (log.isDebugEnabled()) log.debug("Exit");
		return mav;
	}
	
	/**
	 * Execute the save and return the results
	 */
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
		if (isLoggedIn(session) && actionPath.equalsIgnoreCase("cancel")) {
			if (log.isDebugEnabled()) log.debug("trying to cancel from saveupdate");
			SearchDomainForm form2 = (SearchDomainForm) session
					.getAttribute("searchDomainForm");
			model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
			model.addAttribute("ajaxRequest", AjaxUtils
					.isAjaxRequest(requestedWith));

			mav.setViewName("main");
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
			return mav;
		} else {
			HashMap<String, String> msgs = new HashMap<String, String>();
			mav.addObject("msgs", msgs);
			if (log.isDebugEnabled()) log.debug("submitType: " + actionPath);
			if (isLoggedIn(session)) {
				mav.setViewName("domain");
				if (form.isValid()) {
					if (log.isDebugEnabled())
						log.debug("Form passed validation");
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

						if (log.isDebugEnabled())
							log.debug("Stored domain: "
									+ form.getDomainFromForm().toString());

					} catch (ConfigurationServiceException e) {
						log.error(e);
						msgs.put("domainService", "domainService.add.error");
					}
				}
			} else {
				model.addAttribute(new LoginForm());
				mav.setViewName("login");
			}
		}
		if (log.isDebugEnabled()) log.debug("Exit");
		return mav;
	}
	private boolean isLoggedIn(HttpSession session) {
		Boolean result = (Boolean)session.getAttribute("authComplete");
		if (result == null) {
			result = new Boolean(false);
		}
		return result.booleanValue();
	}
	
	/**
	 * Handle exceptions as gracefully as possible
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(IOException.class) 
	public String handleIOException(IOException ex, HttpServletRequest request) {
		//TODO Actually do something useful
		return ClassUtils.getShortName(ex.getClass() + ":" + ex.getMessage());
	}

	public void setdService(DomainService service) {
		this.dService = service;
	}
	
}
