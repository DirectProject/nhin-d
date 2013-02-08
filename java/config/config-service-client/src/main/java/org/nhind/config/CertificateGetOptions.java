/**
 * CertificateGetOptions.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class CertificateGetOptions  implements java.io.Serializable {
    private boolean includeData;

    private boolean includePrivateKey;

    private org.nhind.config.EntityStatus status;

    public CertificateGetOptions() {
    }

    public CertificateGetOptions(
           boolean includeData,
           boolean includePrivateKey,
           org.nhind.config.EntityStatus status) {
           this.includeData = includeData;
           this.includePrivateKey = includePrivateKey;
           this.status = status;
    }


    /**
     * Gets the includeData value for this CertificateGetOptions.
     * 
     * @return includeData
     */
    public boolean isIncludeData() {
        return includeData;
    }


    /**
     * Sets the includeData value for this CertificateGetOptions.
     * 
     * @param includeData
     */
    public void setIncludeData(boolean includeData) {
        this.includeData = includeData;
    }


    /**
     * Gets the includePrivateKey value for this CertificateGetOptions.
     * 
     * @return includePrivateKey
     */
    public boolean isIncludePrivateKey() {
        return includePrivateKey;
    }


    /**
     * Sets the includePrivateKey value for this CertificateGetOptions.
     * 
     * @param includePrivateKey
     */
    public void setIncludePrivateKey(boolean includePrivateKey) {
        this.includePrivateKey = includePrivateKey;
    }


    /**
     * Gets the status value for this CertificateGetOptions.
     * 
     * @return status
     */
    public org.nhind.config.EntityStatus getStatus() {
        return status;
    }


    /**
     * Sets the status value for this CertificateGetOptions.
     * 
     * @param status
     */
    public void setStatus(org.nhind.config.EntityStatus status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CertificateGetOptions)) return false;
        CertificateGetOptions other = (CertificateGetOptions) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.includeData == other.isIncludeData() &&
            this.includePrivateKey == other.isIncludePrivateKey() &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += (isIncludeData() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isIncludePrivateKey() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CertificateGetOptions.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certificateGetOptions"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeData");
        elemField.setXmlName(new javax.xml.namespace.QName("", "includeData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includePrivateKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "includePrivateKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "entityStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
