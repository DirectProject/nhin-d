
package org.nhindirect.schema.edge.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Status.Type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Status.Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="pending"/>
 *     &lt;enumeration value="delivered"/>
 *     &lt;enumeration value="retrieved"/>
 *     &lt;enumeration value="error"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Status.Type")
@XmlEnum
public enum StatusType {

    @XmlEnumValue("pending")
    PENDING("pending"),
    @XmlEnumValue("delivered")
    DELIVERED("delivered"),
    @XmlEnumValue("retrieved")
    RETRIEVED("retrieved"),
    @XmlEnumValue("error")
    ERROR("error");
    private final String value;

    StatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusType fromValue(String v) {
        for (StatusType c: StatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
