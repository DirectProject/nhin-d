/**
 * CertPolicyGroupReltn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class CertPolicyGroupReltn  implements java.io.Serializable {
    private org.nhind.config.CertPolicy certPolicy;

    private long id;

    private boolean incoming;

    private boolean outgoing;

    private org.nhind.config.CertPolicyUse policyUse;  // attribute

    public CertPolicyGroupReltn() {
    }

    public CertPolicyGroupReltn(
           org.nhind.config.CertPolicy certPolicy,
           long id,
           boolean incoming,
           boolean outgoing,
           org.nhind.config.CertPolicyUse policyUse) {
           this.certPolicy = certPolicy;
           this.id = id;
           this.incoming = incoming;
           this.outgoing = outgoing;
           this.policyUse = policyUse;
    }


    /**
     * Gets the certPolicy value for this CertPolicyGroupReltn.
     * 
     * @return certPolicy
     */
    public org.nhind.config.CertPolicy getCertPolicy() {
        return certPolicy;
    }


    /**
     * Sets the certPolicy value for this CertPolicyGroupReltn.
     * 
     * @param certPolicy
     */
    public void setCertPolicy(org.nhind.config.CertPolicy certPolicy) {
        this.certPolicy = certPolicy;
    }


    /**
     * Gets the id value for this CertPolicyGroupReltn.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this CertPolicyGroupReltn.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the incoming value for this CertPolicyGroupReltn.
     * 
     * @return incoming
     */
    public boolean isIncoming() {
        return incoming;
    }


    /**
     * Sets the incoming value for this CertPolicyGroupReltn.
     * 
     * @param incoming
     */
    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }


    /**
     * Gets the outgoing value for this CertPolicyGroupReltn.
     * 
     * @return outgoing
     */
    public boolean isOutgoing() {
        return outgoing;
    }


    /**
     * Sets the outgoing value for this CertPolicyGroupReltn.
     * 
     * @param outgoing
     */
    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }


    /**
     * Gets the policyUse value for this CertPolicyGroupReltn.
     * 
     * @return policyUse
     */
    public org.nhind.config.CertPolicyUse getPolicyUse() {
        return policyUse;
    }


    /**
     * Sets the policyUse value for this CertPolicyGroupReltn.
     * 
     * @param policyUse
     */
    public void setPolicyUse(org.nhind.config.CertPolicyUse policyUse) {
        this.policyUse = policyUse;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CertPolicyGroupReltn)) return false;
        CertPolicyGroupReltn other = (CertPolicyGroupReltn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.certPolicy==null && other.getCertPolicy()==null) || 
             (this.certPolicy!=null &&
              this.certPolicy.equals(other.getCertPolicy()))) &&
            this.id == other.getId() &&
            this.incoming == other.isIncoming() &&
            this.outgoing == other.isOutgoing() &&
            ((this.policyUse==null && other.getPolicyUse()==null) || 
             (this.policyUse!=null &&
              this.policyUse.equals(other.getPolicyUse())));
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
        if (getCertPolicy() != null) {
            _hashCode += getCertPolicy().hashCode();
        }
        _hashCode += new Long(getId()).hashCode();
        _hashCode += (isIncoming() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isOutgoing() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getPolicyUse() != null) {
            _hashCode += getPolicyUse().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CertPolicyGroupReltn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicyGroupReltn"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("policyUse");
        attrField.setXmlName(new javax.xml.namespace.QName("", "policyUse"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicyUse"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certPolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certPolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certPolicy"));
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
        elemField.setFieldName("incoming");
        elemField.setXmlName(new javax.xml.namespace.QName("", "incoming"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outgoing");
        elemField.setXmlName(new javax.xml.namespace.QName("", "outgoing"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
