/**
 * CertPolicyGroupDomainReltn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class CertPolicyGroupDomainReltn  implements java.io.Serializable {
    private org.nhind.config.CertPolicyGroup certPolicyGroup;

    private org.nhind.config.Domain domain;

    private long id;

    public CertPolicyGroupDomainReltn() {
    }

    public CertPolicyGroupDomainReltn(
           org.nhind.config.CertPolicyGroup certPolicyGroup,
           org.nhind.config.Domain domain,
           long id) {
           this.certPolicyGroup = certPolicyGroup;
           this.domain = domain;
           this.id = id;
    }


    /**
     * Gets the certPolicyGroup value for this CertPolicyGroupDomainReltn.
     * 
     * @return certPolicyGroup
     */
    public org.nhind.config.CertPolicyGroup getCertPolicyGroup() {
        return certPolicyGroup;
    }


    /**
     * Sets the certPolicyGroup value for this CertPolicyGroupDomainReltn.
     * 
     * @param certPolicyGroup
     */
    public void setCertPolicyGroup(org.nhind.config.CertPolicyGroup certPolicyGroup) {
        this.certPolicyGroup = certPolicyGroup;
    }


    /**
     * Gets the domain value for this CertPolicyGroupDomainReltn.
     * 
     * @return domain
     */
    public org.nhind.config.Domain getDomain() {
        return domain;
    }


    /**
     * Sets the domain value for this CertPolicyGroupDomainReltn.
     * 
     * @param domain
     */
    public void setDomain(org.nhind.config.Domain domain) {
        this.domain = domain;
    }


    /**
     * Gets the id value for this CertPolicyGroupDomainReltn.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this CertPolicyGroupDomainReltn.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CertPolicyGroupDomainReltn)) return false;
        CertPolicyGroupDomainReltn other = (CertPolicyGroupDomainReltn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.certPolicyGroup==null && other.getCertPolicyGroup()==null) || 
             (this.certPolicyGroup!=null &&
              this.certPolicyGroup.equals(other.getCertPolicyGroup()))) &&
            ((this.domain==null && other.getDomain()==null) || 
             (this.domain!=null &&
              this.domain.equals(other.getDomain()))) &&
            this.id == other.getId();
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
        if (getCertPolicyGroup() != null) {
            _hashCode += getCertPolicyGroup().hashCode();
        }
        if (getDomain() != null) {
            _hashCode += getDomain().hashCode();
        }
        _hashCode += new Long(getId()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CertPolicyGroupDomainReltn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicyGroupDomainReltn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certPolicyGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certPolicyGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicyGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domain");
        elemField.setXmlName(new javax.xml.namespace.QName("", "domain"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "domain"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
