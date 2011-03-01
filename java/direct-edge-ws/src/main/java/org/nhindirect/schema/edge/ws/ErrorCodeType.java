
package org.nhindirect.schema.edge.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorCode.Type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorCode.Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOT_AUTH"/>
 *     &lt;enumeration value="ADDRESSING"/>
 *     &lt;enumeration value="MESSAGING"/>
 *     &lt;enumeration value="SYSTEM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ErrorCode.Type")
@XmlEnum
public enum ErrorCodeType {

    NOT_AUTH,
    ADDRESSING,
    MESSAGING,
    SYSTEM;

    public String value() {
        return name();
    }

    public static ErrorCodeType fromValue(String v) {
        return valueOf(v);
    }

}
