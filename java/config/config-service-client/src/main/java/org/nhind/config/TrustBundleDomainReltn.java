/**
 * TrustBundleDomainReltn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class TrustBundleDomainReltn  implements java.io.Serializable {
    private org.nhind.config.Domain domain;

    private long id;

    private boolean incoming;

    private boolean outgoing;

    private org.nhind.config.TrustBundle trustBundle;

    public TrustBundleDomainReltn() {
    }

    public TrustBundleDomainReltn(
           org.nhind.config.Domain domain,
           long id,
           boolean incoming,
           boolean outgoing,
           org.nhind.config.TrustBundle trustBundle) {
           this.domain = domain;
           this.id = id;
           this.incoming = incoming;
           this.outgoing = outgoing;
           this.trustBundle = trustBundle;
    }


    /**
     * Gets the domain value for this TrustBundleDomainReltn.
     * 
     * @return domain
     */
    public org.nhind.config.Domain getDomain() {
        return domain;
    }


    /**
     * Sets the domain value for this TrustBundleDomainReltn.
     * 
     * @param domain
     */
    public void setDomain(org.nhind.config.Domain domain) {
        this.domain = domain;
    }


    /**
     * Gets the id value for this TrustBundleDomainReltn.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this TrustBundleDomainReltn.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the incoming value for this TrustBundleDomainReltn.
     * 
     * @return incoming
     */
    public boolean isIncoming() {
        return incoming;
    }


    /**
     * Sets the incoming value for this TrustBundleDomainReltn.
     * 
     * @param incoming
     */
    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }


    /**
     * Gets the outgoing value for this TrustBundleDomainReltn.
     * 
     * @return outgoing
     */
    public boolean isOutgoing() {
        return outgoing;
    }


    /**
     * Sets the outgoing value for this TrustBundleDomainReltn.
     * 
     * @param outgoing
     */
    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }


    /**
     * Gets the trustBundle value for this TrustBundleDomainReltn.
     * 
     * @return trustBundle
     */
    public org.nhind.config.TrustBundle getTrustBundle() {
        return trustBundle;
    }


    /**
     * Sets the trustBundle value for this TrustBundleDomainReltn.
     * 
     * @param trustBundle
     */
    public void setTrustBundle(org.nhind.config.TrustBundle trustBundle) {
        this.trustBundle = trustBundle;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TrustBundleDomainReltn)) return false;
        TrustBundleDomainReltn other = (TrustBundleDomainReltn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.domain==null && other.getDomain()==null) || 
             (this.domain!=null &&
              this.domain.equals(other.getDomain()))) &&
            this.id == other.getId() &&
            this.incoming == other.isIncoming() &&
            this.outgoing == other.isOutgoing() &&
            ((this.trustBundle==null && other.getTrustBundle()==null) || 
             (this.trustBundle!=null &&
              this.trustBundle.equals(other.getTrustBundle())));
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
        if (getDomain() != null) {
            _hashCode += getDomain().hashCode();
        }
        _hashCode += new Long(getId()).hashCode();
        _hashCode += (isIncoming() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isOutgoing() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getTrustBundle() != null) {
            _hashCode += getTrustBundle().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TrustBundleDomainReltn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "trustBundleDomainReltn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trustBundle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "trustBundle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "trustBundle"));
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
