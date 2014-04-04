
package org.nhindirect.schema.edge.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Head.Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Head.Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="From" type="{http://nhindirect.org/schema/edge/ws}Address.Type"/>
 *         &lt;element name="To" type="{http://nhindirect.org/schema/edge/ws}Address.Type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Cc" type="{http://nhindirect.org/schema/edge/ws}Address.Type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Bcc" type="{http://nhindirect.org/schema/edge/ws}Address.Type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="In-Reply-To" type="{http://nhindirect.org/schema/edge/ws}MID.Type" minOccurs="0"/>
 *         &lt;element name="References" type="{http://nhindirect.org/schema/edge/ws}MID.Type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Head.Type", propOrder = {
    "from",
    "to",
    "cc",
    "bcc",
    "subject",
    "inReplyTo",
    "references"
})
public class HeadType {

    @XmlElement(name = "From", required = true)
    protected AddressType from;
    @XmlElement(name = "To")
    protected List<AddressType> to;
    @XmlElement(name = "Cc")
    protected List<AddressType> cc;
    @XmlElement(name = "Bcc")
    protected List<AddressType> bcc;
    @XmlElement(name = "Subject")
    protected String subject;
    @XmlElement(name = "In-Reply-To")
    protected String inReplyTo;
    @XmlElement(name = "References")
    protected String references;

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setFrom(AddressType value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the to property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressType }
     * 
     * 
     */
    public List<AddressType> getTo() {
        if (to == null) {
            to = new ArrayList<AddressType>();
        }
        return this.to;
    }

    /**
     * Gets the value of the cc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressType }
     * 
     * 
     */
    public List<AddressType> getCc() {
        if (cc == null) {
            cc = new ArrayList<AddressType>();
        }
        return this.cc;
    }

    /**
     * Gets the value of the bcc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bcc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBcc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressType }
     * 
     * 
     */
    public List<AddressType> getBcc() {
        if (bcc == null) {
            bcc = new ArrayList<AddressType>();
        }
        return this.bcc;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the inReplyTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInReplyTo() {
        return inReplyTo;
    }

    /**
     * Sets the value of the inReplyTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInReplyTo(String value) {
        this.inReplyTo = value;
    }

    /**
     * Gets the value of the references property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferences() {
        return references;
    }

    /**
     * Sets the value of the references property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferences(String value) {
        this.references = value;
    }

}
