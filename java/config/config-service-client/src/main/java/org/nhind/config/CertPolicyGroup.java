/**
 * CertPolicyGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class CertPolicyGroup  implements java.io.Serializable {
    private org.nhind.config.CertPolicyGroupReltn[] certPolicyGroupReltn;

    private java.util.Calendar createTime;

    private long id;

    private java.lang.String policyGroupName;

    public CertPolicyGroup() {
    }

    public CertPolicyGroup(
           org.nhind.config.CertPolicyGroupReltn[] certPolicyGroupReltn,
           java.util.Calendar createTime,
           long id,
           java.lang.String policyGroupName) {
           this.certPolicyGroupReltn = certPolicyGroupReltn;
           this.createTime = createTime;
           this.id = id;
           this.policyGroupName = policyGroupName;
    }


    /**
     * Gets the certPolicyGroupReltn value for this CertPolicyGroup.
     * 
     * @return certPolicyGroupReltn
     */
    public org.nhind.config.CertPolicyGroupReltn[] getCertPolicyGroupReltn() {
        return certPolicyGroupReltn;
    }


    /**
     * Sets the certPolicyGroupReltn value for this CertPolicyGroup.
     * 
     * @param certPolicyGroupReltn
     */
    public void setCertPolicyGroupReltn(org.nhind.config.CertPolicyGroupReltn[] certPolicyGroupReltn) {
        this.certPolicyGroupReltn = certPolicyGroupReltn;
    }

    public org.nhind.config.CertPolicyGroupReltn getCertPolicyGroupReltn(int i) {
        return this.certPolicyGroupReltn[i];
    }

    public void setCertPolicyGroupReltn(int i, org.nhind.config.CertPolicyGroupReltn _value) {
        this.certPolicyGroupReltn[i] = _value;
    }


    /**
     * Gets the createTime value for this CertPolicyGroup.
     * 
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }


    /**
     * Sets the createTime value for this CertPolicyGroup.
     * 
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }


    /**
     * Gets the id value for this CertPolicyGroup.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this CertPolicyGroup.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the policyGroupName value for this CertPolicyGroup.
     * 
     * @return policyGroupName
     */
    public java.lang.String getPolicyGroupName() {
        return policyGroupName;
    }


    /**
     * Sets the policyGroupName value for this CertPolicyGroup.
     * 
     * @param policyGroupName
     */
    public void setPolicyGroupName(java.lang.String policyGroupName) {
        this.policyGroupName = policyGroupName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CertPolicyGroup)) return false;
        CertPolicyGroup other = (CertPolicyGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.certPolicyGroupReltn==null && other.getCertPolicyGroupReltn()==null) || 
             (this.certPolicyGroupReltn!=null &&
              java.util.Arrays.equals(this.certPolicyGroupReltn, other.getCertPolicyGroupReltn()))) &&
            ((this.createTime==null && other.getCreateTime()==null) || 
             (this.createTime!=null &&
              this.createTime.equals(other.getCreateTime()))) &&
            this.id == other.getId() &&
            ((this.policyGroupName==null && other.getPolicyGroupName()==null) || 
             (this.policyGroupName!=null &&
              this.policyGroupName.equals(other.getPolicyGroupName())));
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
        if (getCertPolicyGroupReltn() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCertPolicyGroupReltn());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCertPolicyGroupReltn(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCreateTime() != null) {
            _hashCode += getCreateTime().hashCode();
        }
        _hashCode += new Long(getId()).hashCode();
        if (getPolicyGroupName() != null) {
            _hashCode += getPolicyGroupName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CertPolicyGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicyGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certPolicyGroupReltn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certPolicyGroupReltn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicyGroupReltn"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("policyGroupName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "policyGroupName"));
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
