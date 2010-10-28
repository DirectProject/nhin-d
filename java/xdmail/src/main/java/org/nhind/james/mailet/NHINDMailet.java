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

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;
import org.nhind.mail.service.DocumentRepository;
import org.nhindirect.xd.transform.MimeXdsTransformer;
import org.nhindirect.xd.transform.impl.DefaultMimeXdsTransformer;

/**
 * An Apache James Mailet that converts clinical messages into IHE
 * Cross-Enterprise Document Reliability (XDR) messages and transmits them to an
 * XDR Document Recipient via IHE XDS.b Provide and Register transaction
 * (ITI-41).
 */
public class NHINDMailet extends GenericMailet {
    
    private String endpointUrl;
    
    private MimeXdsTransformer mimeXDSTransformer;
    private DocumentRepository documentRepository;

    private static final Log LOGGER = LogFactory.getFactory().getInstance(NHINDMailet.class);
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.mailet.base.GenericMailet#service(org.apache.mailet.Mail)
     */
    @Override
    public void service(Mail mail) throws MessagingException {
        LOGGER.info("Servicing NHINDMailet");

        if (StringUtils.isBlank(endpointUrl)) {
            LOGGER.error("NHINDMailet endpoint URL cannot be empty or null.");
            throw new MessagingException("NHINDMailet endpoint URL cannot be empty or null.");
        }

        try
        {
            List<ProvideAndRegisterDocumentSetRequestType> requests = getMimeXDSTransformer().transform(mail.getMessage());

            for (ProvideAndRegisterDocumentSetRequestType request : requests)
            {
                String response = getDocumentRepository().forwardRequest(endpointUrl, request);

                if (!isSuccessful(response))
                    throw new MessagingException("Unsuccessful response received");
            }

            mail.setState(Mail.GHOST);
        }
        catch (Throwable e)
        {
            LOGGER.error("NHINDMailet delivery failure", e);
            throw new RuntimeException(e);
        }
    }

    private boolean isSuccessful(String response)
    {
        if (StringUtils.contains(response, "Failure"))
            return false;

        return true;
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
            LOGGER.error("NHINDMailet endpoint URL cannot be empty or null.");
            throw new MessagingException("NHINDMailet endpoint URL cannot be empty or null.");
        }   
    }
    
    /**
     * Return the value of endpointUrl.
     * 
     * @return the value of endpointUrl.
     */
    protected String getEndpointUrl() {
        return this.endpointUrl;
    }
    
    /**
     * Set the value of endpointUrl.
     * 
     * @param endpointUrl
     *            The value of endpointUrl.
     */
    protected void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }
    
    /**
     * Set the value of mimeXDSTransformer.
     * 
     * @param mimeXDSTransformer
     *            The value of mimeXDSTransformer.
     */
    protected void setMimeXDSTransformer(MimeXdsTransformer mimeXDSTransformer) {
        this.mimeXDSTransformer = mimeXDSTransformer;
    }

    /**
     * Get the value of mimeXDSTransfomer.
     * 
     * @return the value of mimeXDSTransformer, or a new object if null.
     */
    protected MimeXdsTransformer getMimeXDSTransformer() {
        if (this.mimeXDSTransformer == null) {
            this.mimeXDSTransformer = new DefaultMimeXdsTransformer();
        }
        
        return this.mimeXDSTransformer;
    }

    /**
     * Set the value of documentRepository.
     * 
     * @param documentRepository
     *            The value of documentRepository.
     */
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Get the value of documentRepository.
     * 
     * @return the value of documentRepository, or a new object if null.
     */
    public DocumentRepository getDocumentRepository() {
        if (this.documentRepository == null) {
            this.documentRepository = new DocumentRepository();
        }
        
        return documentRepository;
    }
}
