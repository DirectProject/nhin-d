
package org.xml.xml.schema._7f0d86bd.healthcare_security_audit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for EventIdentificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventIdentificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EventID" type="{http://www.xml.org/xml/schema/7f0d86bd/healthcare-security-audit}CodedValueType"/>
 *         &lt;element name="EventTypeCode" type="{http://www.xml.org/xml/schema/7f0d86bd/healthcare-security-audit}CodedValueType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="EventActionCode">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="C"/>
 *             &lt;enumeration value="R"/>
 *             &lt;enumeration value="U"/>
 *             &lt;enumeration value="D"/>
 *             &lt;enumeration value="E"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="EventDateTime" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="EventOutcomeIndicator" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *             &lt;enumeration value="0"/>
 *             &lt;enumeration value="4"/>
 *             &lt;enumeration value="8"/>
 *             &lt;enumeration value="12"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventIdentificationType", propOrder = {
    "eventID",
    "eventTypeCode"
})
public class EventIdentificationType {

    @XmlElement(name = "EventID", required = true)
    protected CodedValueType eventID;
    @XmlElement(name = "EventTypeCode")
    protected List<CodedValueType> eventTypeCode;
    @XmlAttribute(name = "EventActionCode")
    protected String eventActionCode;
    @XmlAttribute(name = "EventDateTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventDateTime;
    @XmlAttribute(name = "EventOutcomeIndicator", required = true)
    protected BigInteger eventOutcomeIndicator;

    /**
     * Gets the value of the eventID property.
     * 
     * @return
     *     possible object is
     *     {@link CodedValueType }
     *     
     */
    public CodedValueType getEventID() {
        return eventID;
    }

    /**
     * Sets the value of the eventID property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodedValueType }
     *     
     */
    public void setEventID(CodedValueType value) {
        this.eventID = value;
    }

    /**
     * Gets the value of the eventTypeCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventTypeCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventTypeCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodedValueType }
     * 
     * 
     */
    public List<CodedValueType> getEventTypeCode() {
        if (eventTypeCode == null) {
            eventTypeCode = new ArrayList<CodedValueType>();
        }
        return this.eventTypeCode;
    }

    /**
     * Gets the value of the eventActionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventActionCode() {
        return eventActionCode;
    }

    /**
     * Sets the value of the eventActionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventActionCode(String value) {
        this.eventActionCode = value;
    }

    /**
     * Gets the value of the eventDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Sets the value of the eventDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventDateTime(XMLGregorianCalendar value) {
        this.eventDateTime = value;
    }

    /**
     * Gets the value of the eventOutcomeIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getEventOutcomeIndicator() {
        return eventOutcomeIndicator;
    }

    /**
     * Sets the value of the eventOutcomeIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setEventOutcomeIndicator(BigInteger value) {
        this.eventOutcomeIndicator = value;
    }

}
