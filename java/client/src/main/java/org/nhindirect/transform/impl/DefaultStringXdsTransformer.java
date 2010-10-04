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
package org.nhindirect.transform.impl;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;




import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.util.XMLUtils;
import org.nhindirect.transform.StringXdsTransformer;

import org.nhindirect.transform.exception.TransformationException;

/*
 * FIXME
 * 
 * The system currently handles multiple documents and recipients. 
 * 
 * Each document is placed into its own ProvideAndRegisterDocumentSetRequestType 
 * object, and correspondingly its own SOAP message. 
 * 
 * ProvideAndRegisterDocumentSetRequestType allows for multiple documents in a 
 * single request, and this class should eventually be updated to support this.
 */
/**
 * Transform a MimeMessage into a XDS request.
 * 
 * @author vlewis
 */
public class DefaultStringXdsTransformer implements StringXdsTransformer {

    private static final String CODE_FORMAT_TEXT = "TEXT";
    private static final String CODE_FORMAT_CDAR2 = "CDAR2/IHE 1.0";
    private byte[] xdsDocument = null;
    private String xdsMimeType = null;
    private String xdsFormatCode = null;
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DefaultStringXdsTransformer.class);

    /**
     * Construct a new DefaultMimeXdsTransformer object.
     */
    public DefaultStringXdsTransformer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nhindirect.transform.MimeXdsTransformer#transform(javax.mail.internet
     * .MimeMessage)
     */
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(String meta, ArrayList<String> docs)
            throws TransformationException {
        ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

        try {
            QName sname = new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "SubmitObjectsRequest");
            SubmitObjectsRequest sor = (SubmitObjectsRequest)XMLUtils.unmarshal(meta, oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
            request.setSubmitObjectsRequest(sor);
            Iterator<String> it = docs.iterator();
            List reqdocs = request.getDocument();
            while(it.hasNext()){
                String docstr = it.next();
                    byte[] buff = docstr.getBytes();
                    Document newdoc = new Document();
                    String docId = UUID.randomUUID().toString();
                    newdoc.setId(docId);
                    DataSource source = new ByteArrayDataSource(buff, "application/xml; charset=UTF-8");
                    DataHandler dhnew = new DataHandler(source);
                    newdoc.setValue(dhnew);
                    reqdocs.add(newdoc);
            }
            
        }catch  (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Unexpected MessagingException occured while handling MimeMessage", e);
            }
            throw new TransformationException("Unable to complete transformation.", e);
        }


        return request;
    }
    /**
     * Get an XDM Request from a BodyPart object.
     * 
     * @param bodyPart
     *            The BodyPart object containing the XDM request.
     * @return a ProvideAndRegisterDocumentSetRequestType object.
     * @throws Exception
     */
}
