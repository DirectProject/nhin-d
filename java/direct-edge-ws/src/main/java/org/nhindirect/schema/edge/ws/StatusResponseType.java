
package org.nhindirect.schema.edge.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusResponse.Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusResponse.Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="100" minOccurs="0">
 *         &lt;element name="Message-ID" type="{http://nhindirect.org/schema/edge/ws}MID.Type"/>
 *         &lt;element name="Status" type="{http://nhindirect.org/schema/edge/ws}Status.Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusResponse.Type", propOrder = {
    "messageIDAndStatus"
})
public class StatusResponseType {

    @XmlElements({
        @XmlElement(name = "Status", type = StatusType.class),
        @XmlElement(name = "Message-ID", type = String.class)
    })
    protected List<Object> messageIDAndStatus;

    /**
     * Gets the value of the messageIDAndStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageIDAndStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageIDAndStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StatusType }
     * {@link String }
     * 
     * 
     */
    public List<Object> getMessageIDAndStatus() {
        if (messageIDAndStatus == null) {
            messageIDAndStatus = new ArrayList<Object>();
        }
        return this.messageIDAndStatus;
    }

}
