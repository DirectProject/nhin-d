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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/login")
@SessionAttributes("loginForm")
public class LoginController {
	
	private static final Log log = LogFactory.getLog(LoginController.class);

	public LoginController() {
		if (log.isDebugEnabled()) log.debug("LoginController instantiated");
	}
	
	@RequestMapping(method=RequestMethod.GET)
    public ModelAndView login() {
	    
	    return new ModelAndView("login");
    }
	
	/**
	 * Return the login page when requested
	 * @return
	 */
	/*
	@RequestMapping(method = RequestMethod.GET) 
	public ModelAndView login(@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
			                  HttpSession session, 
			                  Model model) {		
		if (log.isDebugEnabled()) log.debug("Enter");
		
		LoginForm form = (LoginForm) session.getAttribute("loginForm");
		
		model.addAttribute(form != null ? form : new LoginForm());
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
		
		ModelAndView mav = new ModelAndView(); 
		mav.setViewName("login"); 
		if (log.isDebugEnabled()) log.debug("Exit");
		return mav;
	}
	*/
	/**
	 * On a POST, validate the supplied credentials.  If the user is authenticated, 
	 * then send them on to the main page.
	 * @param username
	 * @param password
	 * @return
	 */
	/*
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView authenticate(@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
			                         @ModelAttribute("loginForm") LoginForm form, 
			                         BindingResult result,
			                         HttpSession session, 
			                         Model model) {
		if (log.isDebugEnabled()) log.debug("Enter");
		
		ModelAndView mav = new ModelAndView(); 
		HashMap<String, String> msgs = new HashMap<String, String>();
		mav.addObject(msgs);
		boolean ok = true;
		
		//TODO Implement a real authentication service invocation	
		if (log.isDebugEnabled()) log.debug("Userid: " + form.getUserid() + ", Password: " + form.getPassword());
		if (validator  != null) {
		//	if (log.isDebugEnabled()) log.debug("Got a validator!");
		//	validator.validate(form, result);
		}
		else {
			if (log.isDebugEnabled()) log.debug("No validator was injected.");
		}
		
		if (result.hasErrors()) {
			if (log.isDebugEnabled()) log.debug("Either userid or password is null");
			mav.setViewName("login");
		}
		else {
			if (log.isDebugEnabled()) log.debug("Login successful!");
			form.setPassword("");
			session.setAttribute("loginForm", form);	
			session.setAttribute("authComplete", new Boolean(true));
			
			mav.setViewName("main"); 
			
			SearchDomainForm sdform = (SearchDomainForm) session.getAttribute("searchDomainForm");
			model.addAttribute(sdform != null ? sdform : new SearchDomainForm());
			model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
			mav.addObject("statusList", EntityStatus.getEntityStatusList());
		}
		
		if (log.isDebugEnabled()) log.debug("Exit");
		return mav;
	}
    */

	/**
	 * Handle exceptions as gracefully as possible
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(IOException.class) 
	public String handleIOException(IOException ex, HttpServletRequest request) {
		//TODO Actually do something useful
		return ClassUtils.getShortName(ex.getClass());
	}


}
