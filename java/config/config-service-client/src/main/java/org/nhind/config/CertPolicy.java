/**
 * CertPolicy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class CertPolicy  implements java.io.Serializable {
    private java.util.Calendar createTime;

    private long id;

    private byte[] policyData;

    private java.lang.String policyName;

    private org.nhind.config.PolicyLexicon lexicon;  // attribute

    public CertPolicy() {
    }

    public CertPolicy(
           java.util.Calendar createTime,
           long id,
           byte[] policyData,
           java.lang.String policyName,
           org.nhind.config.PolicyLexicon lexicon) {
           this.createTime = createTime;
           this.id = id;
           this.policyData = policyData;
           this.policyName = policyName;
           this.lexicon = lexicon;
    }


    /**
     * Gets the createTime value for this CertPolicy.
     * 
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }


    /**
     * Sets the createTime value for this CertPolicy.
     * 
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }


    /**
     * Gets the id value for this CertPolicy.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this CertPolicy.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the policyData value for this CertPolicy.
     * 
     * @return policyData
     */
    public byte[] getPolicyData() {
        return policyData;
    }


    /**
     * Sets the policyData value for this CertPolicy.
     * 
     * @param policyData
     */
    public void setPolicyData(byte[] policyData) {
        this.policyData = policyData;
    }


    /**
     * Gets the policyName value for this CertPolicy.
     * 
     * @return policyName
     */
    public java.lang.String getPolicyName() {
        return policyName;
    }


    /**
     * Sets the policyName value for this CertPolicy.
     * 
     * @param policyName
     */
    public void setPolicyName(java.lang.String policyName) {
        this.policyName = policyName;
    }


    /**
     * Gets the lexicon value for this CertPolicy.
     * 
     * @return lexicon
     */
    public org.nhind.config.PolicyLexicon getLexicon() {
        return lexicon;
    }


    /**
     * Sets the lexicon value for this CertPolicy.
     * 
     * @param lexicon
     */
    public void setLexicon(org.nhind.config.PolicyLexicon lexicon) {
        this.lexicon = lexicon;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CertPolicy)) return false;
        CertPolicy other = (CertPolicy) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.createTime==null && other.getCreateTime()==null) || 
             (this.createTime!=null &&
              this.createTime.equals(other.getCreateTime()))) &&
            this.id == other.getId() &&
            ((this.policyData==null && other.getPolicyData()==null) || 
             (this.policyData!=null &&
              java.util.Arrays.equals(this.policyData, other.getPolicyData()))) &&
            ((this.policyName==null && other.getPolicyName()==null) || 
             (this.policyName!=null &&
              this.policyName.equals(other.getPolicyName()))) &&
            ((this.lexicon==null && other.getLexicon()==null) || 
             (this.lexicon!=null &&
              this.lexicon.equals(other.getLexicon())));
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
        if (getCreateTime() != null) {
            _hashCode += getCreateTime().hashCode();
        }
        _hashCode += new Long(getId()).hashCode();
        if (getPolicyData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPolicyData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPolicyData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPolicyName() != null) {
            _hashCode += getPolicyName().hashCode();
        }
        if (getLexicon() != null) {
            _hashCode += getLexicon().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CertPolicy.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicy"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("lexicon");
        attrField.setXmlName(new javax.xml.namespace.QName("", "lexicon"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "policyLexicon"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "createTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
        elemField.setFieldName("policyData");
        elemField.setXmlName(new javax.xml.namespace.QName("", "policyData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("policyName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "policyName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
