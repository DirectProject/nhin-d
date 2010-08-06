/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nhind.xdr;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import java.util.UUID;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;


/**
 *
 * @author vlewis
 */
@WebService(serviceName = "DocumentRepository_Service", portName = "DocumentRepository_Port_Soap12", endpointInterface = "ihe.iti.xds_b._2007.DocumentRepositoryPortType", targetNamespace = "urn:ihe:iti:xds-b:2007", wsdlLocation = "WEB-INF/wsdl/XDS.b_DocumentRepositoryWSDLSynchMTOM.wsdl")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
@HandlerChain(file = "DocumentRepository_Service_handler.xml")
public class XDR extends DocumentRepositoryAbstract{

    public RegistryResponseType documentRepositoryProvideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType body) {
      RegistryResponseType resp = null;
        try {
            resp = provideAndRegisterDocumentSet(body);
        } catch (Exception x) {
            relatesTo = messageId;
            action = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse";
            messageId = UUID.randomUUID().toString();
            to = endpoint;
            setHeaderData();
            resp = new RegistryResponseType();
            resp.setStatus("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure");
        }
        return resp;
    }

    public RetrieveDocumentSetResponseType documentRepositoryRetrieveDocumentSet(RetrieveDocumentSetRequestType body) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented for XDR");
    }

}
