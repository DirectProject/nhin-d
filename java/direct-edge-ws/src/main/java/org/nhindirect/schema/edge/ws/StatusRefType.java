
package org.nhindirect.schema.edge.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusRef.Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusRef.Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="100" minOccurs="0">
 *         &lt;element name="Message-ID" type="{http://nhindirect.org/schema/edge/ws}MID.Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusRef.Type", propOrder = {
    "messageID"
})
public class StatusRefType {

    @XmlElement(name = "Message-ID")
    protected List<String> messageID;

    /**
     * Gets the value of the messageID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMessageID() {
        if (messageID == null) {
            messageID = new ArrayList<String>();
        }
        return this.messageID;
    }

}
