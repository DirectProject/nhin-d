
package oasis.names.tc.ebxml_regrep.xsd.rim._3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * A Subscription for specified Events in an ebXML V3+ registry.
 * 
 * <p>Java class for SubscriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubscriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Action" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="selector" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="startTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="endTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="notificationInterval" type="{http://www.w3.org/2001/XMLSchema}duration" default="P1D" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriptionType", propOrder = {
    "action"
})
public class SubscriptionType
    extends RegistryObjectType
{

    @XmlElementRef(name = "Action", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", type = JAXBElement.class)
    protected List<JAXBElement<? extends ActionType>> action;
    @XmlAttribute(required = true)
    protected String selector;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startTime;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endTime;
    @XmlAttribute
    protected Duration notificationInterval;

    /**
     * Gets the value of the action property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the action property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ActionType }{@code >}
     * {@link JAXBElement }{@code <}{@link NotifyActionType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends ActionType>> getAction() {
        if (action == null) {
            action = new ArrayList<JAXBElement<? extends ActionType>>();
        }
        return this.action;
    }

    /**
     * Gets the value of the selector property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Sets the value of the selector property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSelector(String value) {
        this.selector = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndTime(XMLGregorianCalendar value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the notificationInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getNotificationInterval() {
        return notificationInterval;
    }

    /**
     * Sets the value of the notificationInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setNotificationInterval(Duration value) {
        this.notificationInterval = value;
    }

}
