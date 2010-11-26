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
 * 
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
     * @param endpoint
     * @throws Exception
     */
    public DocumentRepositoryProxy(String endpoint) throws Exception
    {
        this.endpoint = endpoint;
        initProxy();
    }

    /**
     * @param endpoint
     * @param handlerResolver
     * @throws Exception
     */
    public DocumentRepositoryProxy(String endpoint, HandlerResolver handlerResolver) throws Exception
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
     * @param body
     * @return
     * @throws Exception
     */
    public RegistryResponseType provideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType body)
            throws Exception
    {
        if (proxy == null)
            initProxy();

        LOGGER.debug("Sending to endpoint: " + endpoint);
        return proxy.documentRepositoryProvideAndRegisterDocumentSetB(body);
    }

    /**
     * @param body
     * @return
     * @throws Exception
     */
    public RetrieveDocumentSetResponseType retrieveDocumentSet(RetrieveDocumentSetRequestType body) throws Exception
    {
        if (proxy == null)
            initProxy();

        throw new OperationNotSupportedException("Not implemented.");
    }

}