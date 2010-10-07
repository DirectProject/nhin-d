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

package org.nhindirect.xdclient;

import ihe.iti.xds_b._2007.DocumentRepositoryPortType;
import ihe.iti.xds_b._2007.DocumentRepositoryService;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * General utility class for methods dealing with DocumentRepository related
 * objects.
 * 
 * @author beau
 */
public class DocumentRepositoryUtils
{
    private static final Log LOGGER = LogFactory.getFactory().getInstance(DocumentRepositoryUtils.class);

    /**
     * Construct a DocumentRepositoryPortType object using the provided
     * endpoint.
     * 
     * @param endpoint
     *            The XDR endpoint.
     * @return a DocumentRepositoryPortType object.
     * @throws Exception
     */
    public static DocumentRepositoryPortType getDocumentRepositoryPortType(String endpoint) throws Exception
    {
        URL url = null;

        try
        {
            url = ihe.iti.xds_b._2007.DocumentRepositoryService.class.getClassLoader().getResource(
                    "XDS.b_DocumentRepositoryWSDLSynchMTOM.wsdl");
        }
        catch (Exception e)
        {
            LOGGER.error("Unable to access WSDL", e);
            throw e;
        }

        return getDocumentRepositoryPortType(endpoint, url);
    }

    /**
     * Construct a DocumentRepositoryPortType object using the provided
     * endpoint.
     * 
     * @param endpoint
     *            The XDR endpoint.
     * @param wsdlPath
     *            The path to the WSDL.
     * @return a DocumentRepositoryPortType object.
     * @throws Exception
     */
    public static DocumentRepositoryPortType getDocumentRepositoryPortType(String endpoint, URL wsdlPath)
            throws Exception
    {
        QName qname = new QName("urn:ihe:iti:xds-b:2007", "DocumentRepository_Service");
        DocumentRepositoryService service = new DocumentRepositoryService(wsdlPath, qname);
        service.setHandlerResolver(new RepositoryHandlerResolver());
        DocumentRepositoryPortType port = service.getDocumentRepositoryPortSoap12(new MTOMFeature(true, 1));

        BindingProvider bp = (BindingProvider) port;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);

        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

        return port;
    }

}
