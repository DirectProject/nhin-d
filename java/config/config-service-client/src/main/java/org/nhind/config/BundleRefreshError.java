/**
 * BundleRefreshError.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class BundleRefreshError implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected BundleRefreshError(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _SUCCESS = "SUCCESS";
    public static final java.lang.String _NOT_FOUND = "NOT_FOUND";
    public static final java.lang.String _DOWNLOAD_TIMEOUT = "DOWNLOAD_TIMEOUT";
    public static final java.lang.String _INVALID_BUNDLE_FORMAT = "INVALID_BUNDLE_FORMAT";
    public static final java.lang.String _INVALID_SIGNING_CERT = "INVALID_SIGNING_CERT";
    public static final java.lang.String _UNMATCHED_SIGNATURE = "UNMATCHED_SIGNATURE";
    public static final BundleRefreshError SUCCESS = new BundleRefreshError(_SUCCESS);
    public static final BundleRefreshError NOT_FOUND = new BundleRefreshError(_NOT_FOUND);
    public static final BundleRefreshError DOWNLOAD_TIMEOUT = new BundleRefreshError(_DOWNLOAD_TIMEOUT);
    public static final BundleRefreshError INVALID_BUNDLE_FORMAT = new BundleRefreshError(_INVALID_BUNDLE_FORMAT);
    public static final BundleRefreshError INVALID_SIGNING_CERT = new BundleRefreshError(_INVALID_SIGNING_CERT);
    public static final BundleRefreshError UNMATCHED_SIGNATURE = new BundleRefreshError(_UNMATCHED_SIGNATURE);
    public java.lang.String getValue() { return _value_;}
    public static BundleRefreshError fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        BundleRefreshError enumeration = (BundleRefreshError)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static BundleRefreshError fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(BundleRefreshError.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "bundleRefreshError"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
