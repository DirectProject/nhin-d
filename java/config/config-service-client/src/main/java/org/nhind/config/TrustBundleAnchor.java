/**
 * TrustBundleAnchor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class TrustBundleAnchor  implements java.io.Serializable {
    private byte[] data;

    private long id;

    private java.lang.String thumbprint;

    private org.nhind.config.TrustBundle trustBundle;

    private java.util.Calendar validEndDate;

    private java.util.Calendar validStartDate;

    public TrustBundleAnchor() {
    }

    public TrustBundleAnchor(
           byte[] data,
           long id,
           java.lang.String thumbprint,
           org.nhind.config.TrustBundle trustBundle,
           java.util.Calendar validEndDate,
           java.util.Calendar validStartDate) {
           this.data = data;
           this.id = id;
           this.thumbprint = thumbprint;
           this.trustBundle = trustBundle;
           this.validEndDate = validEndDate;
           this.validStartDate = validStartDate;
    }


    /**
     * Gets the data value for this TrustBundleAnchor.
     * 
     * @return data
     */
    public byte[] getData() {
        return data;
    }


    /**
     * Sets the data value for this TrustBundleAnchor.
     * 
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }


    /**
     * Gets the id value for this TrustBundleAnchor.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this TrustBundleAnchor.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the thumbprint value for this TrustBundleAnchor.
     * 
     * @return thumbprint
     */
    public java.lang.String getThumbprint() {
        return thumbprint;
    }


    /**
     * Sets the thumbprint value for this TrustBundleAnchor.
     * 
     * @param thumbprint
     */
    public void setThumbprint(java.lang.String thumbprint) {
        this.thumbprint = thumbprint;
    }


    /**
     * Gets the trustBundle value for this TrustBundleAnchor.
     * 
     * @return trustBundle
     */
    public org.nhind.config.TrustBundle getTrustBundle() {
        return trustBundle;
    }


    /**
     * Sets the trustBundle value for this TrustBundleAnchor.
     * 
     * @param trustBundle
     */
    public void setTrustBundle(org.nhind.config.TrustBundle trustBundle) {
        this.trustBundle = trustBundle;
    }


    /**
     * Gets the validEndDate value for this TrustBundleAnchor.
     * 
     * @return validEndDate
     */
    public java.util.Calendar getValidEndDate() {
        return validEndDate;
    }


    /**
     * Sets the validEndDate value for this TrustBundleAnchor.
     * 
     * @param validEndDate
     */
    public void setValidEndDate(java.util.Calendar validEndDate) {
        this.validEndDate = validEndDate;
    }


    /**
     * Gets the validStartDate value for this TrustBundleAnchor.
     * 
     * @return validStartDate
     */
    public java.util.Calendar getValidStartDate() {
        return validStartDate;
    }


    /**
     * Sets the validStartDate value for this TrustBundleAnchor.
     * 
     * @param validStartDate
     */
    public void setValidStartDate(java.util.Calendar validStartDate) {
        this.validStartDate = validStartDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TrustBundleAnchor)) return false;
        TrustBundleAnchor other = (TrustBundleAnchor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              java.util.Arrays.equals(this.data, other.getData()))) &&
            this.id == other.getId() &&
            ((this.thumbprint==null && other.getThumbprint()==null) || 
             (this.thumbprint!=null &&
              this.thumbprint.equals(other.getThumbprint()))) &&
            ((this.trustBundle==null && other.getTrustBundle()==null) || 
             (this.trustBundle!=null &&
              this.trustBundle.equals(other.getTrustBundle()))) &&
            ((this.validEndDate==null && other.getValidEndDate()==null) || 
             (this.validEndDate!=null &&
              this.validEndDate.equals(other.getValidEndDate()))) &&
            ((this.validStartDate==null && other.getValidStartDate()==null) || 
             (this.validStartDate!=null &&
              this.validStartDate.equals(other.getValidStartDate())));
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
        if (getData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Long(getId()).hashCode();
        if (getThumbprint() != null) {
            _hashCode += getThumbprint().hashCode();
        }
        if (getTrustBundle() != null) {
            _hashCode += getTrustBundle().hashCode();
        }
        if (getValidEndDate() != null) {
            _hashCode += getValidEndDate().hashCode();
        }
        if (getValidStartDate() != null) {
            _hashCode += getValidStartDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TrustBundleAnchor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "trustBundleAnchor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("data");
        elemField.setXmlName(new javax.xml.namespace.QName("", "data"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thumbprint");
        elemField.setXmlName(new javax.xml.namespace.QName("", "thumbprint"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trustBundle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "trustBundle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "trustBundle"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validEndDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "validEndDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validStartDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "validStartDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
