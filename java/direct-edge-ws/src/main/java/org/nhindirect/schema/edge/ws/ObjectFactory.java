
package org.nhindirect.schema.edge.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.nhindirect.schema.edge.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SendResponse_QNAME = new QName("http://nhindirect.org/schema/edge/ws", "SendResponse");
    private final static QName _StatusResponse_QNAME = new QName("http://nhindirect.org/schema/edge/ws", "StatusResponse");
    private final static QName _Message_QNAME = new QName("http://nhindirect.org/schema/edge/ws", "Message");
    private final static QName _Status_QNAME = new QName("http://nhindirect.org/schema/edge/ws", "Status");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.nhindirect.schema.edge.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AttachmentType }
     * 
     */
    public AttachmentType createAttachmentType() {
        return new AttachmentType();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link StatusResponseType }
     * 
     */
    public StatusResponseType createStatusResponseType() {
        return new StatusResponseType();
    }

    /**
     * Create an instance of {@link ServiceInvocationFault }
     * 
     */
    public ServiceInvocationFault createServiceInvocationFault() {
        return new ServiceInvocationFault();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link StatusRefType }
     * 
     */
    public StatusRefType createStatusRefType() {
        return new StatusRefType();
    }

    /**
     * Create an instance of {@link SendResponseType }
     * 
     */
    public SendResponseType createSendResponseType() {
        return new SendResponseType();
    }

    /**
     * Create an instance of {@link HeadType }
     * 
     */
    public HeadType createHeadType() {
        return new HeadType();
    }

    /**
     * Create an instance of {@link BodyType }
     * 
     */
    public BodyType createBodyType() {
        return new BodyType();
    }

    /**
     * Create an instance of {@link EmailType }
     * 
     */
    public EmailType createEmailType() {
        return new EmailType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nhindirect.org/schema/edge/ws", name = "SendResponse")
    public JAXBElement<SendResponseType> createSendResponse(SendResponseType value) {
        return new JAXBElement<SendResponseType>(_SendResponse_QNAME, SendResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nhindirect.org/schema/edge/ws", name = "StatusResponse")
    public JAXBElement<StatusResponseType> createStatusResponse(StatusResponseType value) {
        return new JAXBElement<StatusResponseType>(_StatusResponse_QNAME, StatusResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmailType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nhindirect.org/schema/edge/ws", name = "Message")
    public JAXBElement<EmailType> createMessage(EmailType value) {
        return new JAXBElement<EmailType>(_Message_QNAME, EmailType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nhindirect.org/schema/edge/ws", name = "Status")
    public JAXBElement<StatusRefType> createStatus(StatusRefType value) {
        return new JAXBElement<StatusRefType>(_Status_QNAME, StatusRefType.class, null, value);
    }

}
