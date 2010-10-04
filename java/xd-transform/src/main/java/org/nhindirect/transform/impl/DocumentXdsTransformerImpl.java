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

import java.util.List;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.transform.DocumentXdsTransformer;
import org.nhindirect.transform.exception.TransformationException;
import org.nhindirect.transform.util.XmlUtils;

/**
 * Class for handling the transformation of a Document to a
 * ProvideAndRegisterDocumentSetRequestType object.
 * 
 * @author Vince
 */
public class DocumentXdsTransformerImpl implements DocumentXdsTransformer
{
    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DocumentXdsTransformerImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.transform.DocumentXdsTransformer#transform(java.lang.String, java.lang.String)
     */
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(String document, String metadata)
            throws TransformationException
    {
        ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

        try
        {
            SubmitObjectsRequest sor = (SubmitObjectsRequest) XmlUtils.unmarshal(metadata,
                    oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
            request.setSubmitObjectsRequest(sor);

            List<Document> reqdocs = request.getDocument();

            byte[] buff = document.getBytes();
            Document newdoc = new Document();
            String docId = UUID.randomUUID().toString();
            newdoc.setId(docId);
            DataSource source = new ByteArrayDataSource(buff, "application/xml; charset=UTF-8"); // TODO: support more file types
            DataHandler dhnew = new DataHandler(source);
            newdoc.setValue(dhnew);
            reqdocs.add(newdoc);
        }
        catch (Exception e)
        {
            throw new TransformationException("Unable to complete transformation.", e);
        }

        return request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.transform.DocumentXdsTransformer#transform(java.lang.String, java.util.List)
     */
    @Deprecated
    @Override
    public ProvideAndRegisterDocumentSetRequestType transform(String meta, List<String> docs)
            throws TransformationException
    {
        ProvideAndRegisterDocumentSetRequestType request = new ProvideAndRegisterDocumentSetRequestType();

        try
        {
            SubmitObjectsRequest sor = (SubmitObjectsRequest) XmlUtils.unmarshal(meta,
                    oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class);
            request.setSubmitObjectsRequest(sor);
            List<Document> reqdocs = request.getDocument();
            for (String docstr : docs)
            {
                byte[] buff = docstr.getBytes();
                Document newdoc = new Document();
                String docId = UUID.randomUUID().toString();
                newdoc.setId(docId);
                DataSource source = new ByteArrayDataSource(buff, "application/xml; charset=UTF-8");
                DataHandler dhnew = new DataHandler(source);
                newdoc.setValue(dhnew);
                reqdocs.add(newdoc);
            }

        }
        catch (Exception e)
        {
            if (LOGGER.isErrorEnabled())
            {
                LOGGER.error("Unexpected MessagingException occured while handling MimeMessage", e);
            }
            throw new TransformationException("Unable to complete transformation.", e);
        }

        return request;
    }

}
