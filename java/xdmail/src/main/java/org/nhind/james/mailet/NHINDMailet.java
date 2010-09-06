/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhind.james.mailet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.base.GenericMailet;
import org.nhind.mail.service.MimeXDSTransformer;

/**
 * An Apache James Mailet that converts clinical messages into IHE
 * Cross-Enterprise Document Reliability (XDR) messages and transmits them to an
 * XDR Document Recipient via IHE XDS.b Provide and Register transaction
 * (ITI-41).
 */
public class NHINDMailet extends GenericMailet {

    MailetConfig mc = null;
    
    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(NHINDMailet.class.getPackage().getName());
    
    private String endpointUrl;
    
    
//    /*
//     * Relax the OHT IHE Profiles CDA validation to allow meta-data extraction
//     * to work correctly on CDA instances that carry CCD or HITSP C83 extensions
//     */
//    static {
//        System.setProperty("relax.validation", "true");
//    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mailet.base.GenericMailet#service(org.apache.mailet.Mail)
     */
    @Override
    public void service(Mail mail) throws MessagingException {
        LOGGER.info("Servicing NHINDMailet");
        
        if (StringUtils.isBlank(endpointUrl))
        {
            LOGGER.severe("NHINDMailet endpoint URL cannot be empty or null.");
            throw new MessagingException("NHINDMailet endpoint URL cannot be empty or null.");
        }   
        
        try {
            MimeXDSTransformer mxt = new MimeXDSTransformer();
            
            boolean forwardToXdr = true; // should be based on some routing lookup
            if (forwardToXdr) {
                mxt.forward(endpointUrl, mail.getMessage());
            } else {
                // forward it to another email server based on routing
                // iformation
            }
            
            mail.setState(Mail.GHOST);
        } catch (Throwable e) {
            e.printStackTrace();
            LOGGER.severe("NHINDMailet delivery failure" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mailet.base.GenericMailet#init()
     */
    @Override
    public void init() throws MessagingException {
        LOGGER.info("Initializing NHINDMailet");
        
        // Get the configuration URL
        endpointUrl = getInitParameter("EndpointURL");
        
        if (StringUtils.isBlank(endpointUrl))
        {
            LOGGER.severe("NHINDMailet endpoint URL cannot be empty or null.");
            throw new MessagingException("NHINDMailet endpoint URL cannot be empty or null.");
        }   
    }
}
