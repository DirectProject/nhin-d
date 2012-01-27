
package org.xml.xml.schema._7f0d86bd.healthcare_security_audit;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AuditSourceIdentificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuditSourceIdentificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AuditSourceTypeCode" type="{http://www.xml.org/xml/schema/7f0d86bd/healthcare-security-audit}CodedValueType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="AuditEnterpriseSiteID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="AuditSourceID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditSourceIdentificationType", propOrder = {
    "auditSourceTypeCode"
})
public class AuditSourceIdentificationType {

    @XmlElement(name = "AuditSourceTypeCode")
    protected List<CodedValueType> auditSourceTypeCode;
    @XmlAttribute(name = "AuditEnterpriseSiteID")
    protected String auditEnterpriseSiteID;
    @XmlAttribute(name = "AuditSourceID", required = true)
    protected String auditSourceID;

    /**
     * Gets the value of the auditSourceTypeCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the auditSourceTypeCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuditSourceTypeCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodedValueType }
     * 
     * 
     */
    public List<CodedValueType> getAuditSourceTypeCode() {
        if (auditSourceTypeCode == null) {
            auditSourceTypeCode = new ArrayList<CodedValueType>();
        }
        return this.auditSourceTypeCode;
    }

    /**
     * Gets the value of the auditEnterpriseSiteID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuditEnterpriseSiteID() {
        return auditEnterpriseSiteID;
    }

    /**
     * Sets the value of the auditEnterpriseSiteID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuditEnterpriseSiteID(String value) {
        this.auditEnterpriseSiteID = value;
    }

    /**
     * Gets the value of the auditSourceID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuditSourceID() {
        return auditSourceID;
    }

    /**
     * Sets the value of the auditSourceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuditSourceID(String value) {
        this.auditSourceID = value;
    }

}
