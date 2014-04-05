
package org.xml.xml.schema._7f0d86bd.healthcare_security_audit;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.xml.org/xml/schema/7f0d86bd/healthcare-security-audit}AuditMessage" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "auditMessage"
})
@XmlRootElement(name = "findAuditEventsResponse")
public class FindAuditEventsResponse {

    @XmlElement(name = "AuditMessage", namespace = "http://www.xml.org/xml/schema/7f0d86bd/healthcare-security-audit", required = true)
    protected List<AuditMessage> auditMessage;

    /**
     * Gets the value of the auditMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the auditMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuditMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuditMessage }
     * 
     * 
     */
    public List<AuditMessage> getAuditMessage() {
        if (auditMessage == null) {
            auditMessage = new ArrayList<AuditMessage>();
        }
        return this.auditMessage;
    }

}
