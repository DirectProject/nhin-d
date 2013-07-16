/**
 * CertPolicyUse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class CertPolicyUse implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected CertPolicyUse(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _TRUST = "TRUST";
    public static final java.lang.String _PRIVATE_RESOLVER = "PRIVATE_RESOLVER";
    public static final java.lang.String _PUBLIC_RESOLVER = "PUBLIC_RESOLVER";
    public static final java.lang.String _VALIDATION = "VALIDATION";
    public static final CertPolicyUse TRUST = new CertPolicyUse(_TRUST);
    public static final CertPolicyUse PRIVATE_RESOLVER = new CertPolicyUse(_PRIVATE_RESOLVER);
    public static final CertPolicyUse PUBLIC_RESOLVER = new CertPolicyUse(_PUBLIC_RESOLVER);
    public static final CertPolicyUse VALIDATION = new CertPolicyUse(_VALIDATION);
    public java.lang.String getValue() { return _value_;}
    public static CertPolicyUse fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        CertPolicyUse enumeration = (CertPolicyUse)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static CertPolicyUse fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CertPolicyUse.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicyUse"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
