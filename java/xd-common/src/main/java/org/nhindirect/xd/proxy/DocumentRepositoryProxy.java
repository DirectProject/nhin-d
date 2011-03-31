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

package org.nhindirect.xd.proxy;

import ihe.iti.xds_b._2007.DocumentRepositoryPortType;
import ihe.iti.xds_b._2007.DocumentRepositoryService;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;

import java.net.URL;

import javax.naming.OperationNotSupportedException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Wrapper class for handling calls to the Document Repository WS (XD*). The
 * initialization uses a WSDL located within the JAR for convenience. The
 * constructor requires and endpoint and an optional HandlerResolver.
 * 
 * @author beau
 */
public class DocumentRepositoryProxy
{
    private String endpoint = null;
    private HandlerResolver handlerResolver = null;

    private DocumentRepositoryPortType proxy;

    private static final Log LOGGER = LogFactory.getFactory().getInstance(DocumentRepositoryProxy.class);

    /**
     * Construct a new proxy using the provided endpoint.
     * 
     * @param endpoint
     *            The endpoint of the service.
     */
    public DocumentRepositoryProxy(String endpoint)
    {
        this.endpoint = endpoint;
        initProxy();
    }

    /**
     * Construct a new proxy using the provided endpoint and a HandlerResolver.
     * 
     * @param endpoint
     *            The endpoint of the service.
     * @param handlerResolver
     *            The HandlerResolver to attach.
     */
    public DocumentRepositoryProxy(String endpoint, HandlerResolver handlerResolver)
    {
        this(endpoint);
        this.handlerResolver = handlerResolver;
        initProxy();
    }

    private void initProxy()
    {
        try
        {
            URL url = DocumentRepositoryProxy.class.getClassLoader().getResource(
                    "XDS.b_DocumentRepositoryWSDLSynchMTOM.wsdl");

            QName qname = new QName("urn:ihe:iti:xds-b:2007", "DocumentRepository_Service");
            DocumentRepositoryService service = new DocumentRepositoryService(url, qname);

            if (handlerResolver != null)
                service.setHandlerResolver(handlerResolver);

            proxy = service.getDocumentRepositoryPortSoap12(new MTOMFeature(true, 1));

            BindingProvider bp = (BindingProvider) proxy;
            SOAPBinding binding = (SOAPBinding) bp.getBinding();
            binding.setMTOMEnabled(true);

            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
        }
        catch (Exception e)
        {
            LOGGER.error("Error initializing proxy.", e);
        }
    }

    /**
     * Wrapper method to call the
     * documentRepositoryProvideAndRegisterDocumentSetB
     * (ProvideAndRegisterDocumentSetRequestType) service method.
     * 
     * @param body
     *            The ProvideAndRegisterDocumentSetRequestType object.
     * @return a RegistryResponseType.
     */
    public RegistryResponseType provideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType body)
    {
        if (proxy == null)
            initProxy();

        LOGGER.debug("Sending to endpoint: " + endpoint);       
        return proxy.documentRepositoryProvideAndRegisterDocumentSetB(body);
    }

    /**
     * Wrapper method to call the
     * retrieveDocumentSet(RetrieveDocumentSetRequestType) service method.
     * 
     * @param body
     *            The RetrieveDocumentSetRequestType object.
     * @return a RetrieveDocumentSetResponseType.
     * @throws Exception
     */
    public RetrieveDocumentSetResponseType retrieveDocumentSet(RetrieveDocumentSetRequestType body) throws Exception
    {
        if (proxy == null)
            initProxy();

        throw new OperationNotSupportedException("Not implemented.");
    }
}