package org.nhind.mail.util;

import ihe.iti.xds_b._2007.DocumentRepositoryPortType;
import ihe.iti.xds_b._2007.DocumentRepositoryService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

import org.nhind.mail.service.RepositoryHandlerResolver;

/**
 * General utility class for methods dealing with DocumentRepository related
 * objects.
 * 
 * @author beau
 */
public class DocumentRepositoryUtils {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DocumentRepositoryUtils.class.getName());

    /**
     * Construct a DocumentRepositoryPortType object using the provided
     * endpoint.
     * 
     * @param endpoint
     *            The XDR endpoint.
     * @return a DocumentRepositoryPortType object.
     * @throws Exception
     */
    public static DocumentRepositoryPortType getDocumentRepositoryPortType(String endpoint) throws Exception {
        URL url = null;

        try {
            url = new URL(ihe.iti.xds_b._2007.DocumentRepositoryService.class.getResource(""),
                    "/XDS.b_DocumentRepositoryWSDLSynchMTOM.wsdl");
        } catch (MalformedURLException e) {
            LOGGER.severe("Unable to access WSDL");
            e.printStackTrace();
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
            throws Exception {
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
