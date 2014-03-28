/**
 * Domain.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class Domain  implements java.io.Serializable {
    private org.nhind.config.Address[] address;

    private java.util.Calendar createTime;

    private java.lang.String domainName;

    private java.lang.String postMasterEmail;

    private java.lang.Long postmasterAddressId;

    private java.util.Calendar updateTime;

    private long id;  // attribute

    private org.nhind.config.EntityStatus status;  // attribute

    public Domain() {
    }

    public Domain(
           org.nhind.config.Address[] address,
           java.util.Calendar createTime,
           java.lang.String domainName,
           java.lang.String postMasterEmail,
           java.lang.Long postmasterAddressId,
           java.util.Calendar updateTime,
           long id,
           org.nhind.config.EntityStatus status) {
           this.address = address;
           this.createTime = createTime;
           this.domainName = domainName;
           this.postMasterEmail = postMasterEmail;
           this.postmasterAddressId = postmasterAddressId;
           this.updateTime = updateTime;
           this.id = id;
           this.status = status;
    }


    /**
     * Gets the address value for this Domain.
     * 
     * @return address
     */
    public org.nhind.config.Address[] getAddress() {
        return address;
    }


    /**
     * Sets the address value for this Domain.
     * 
     * @param address
     */
    public void setAddress(org.nhind.config.Address[] address) {
        this.address = address;
    }

    public org.nhind.config.Address getAddress(int i) {
        return this.address[i];
    }

    public void setAddress(int i, org.nhind.config.Address _value) {
        this.address[i] = _value;
    }


    /**
     * Gets the createTime value for this Domain.
     * 
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }


    /**
     * Sets the createTime value for this Domain.
     * 
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }


    /**
     * Gets the domainName value for this Domain.
     * 
     * @return domainName
     */
    public java.lang.String getDomainName() {
        return domainName;
    }


    /**
     * Sets the domainName value for this Domain.
     * 
     * @param domainName
     */
    public void setDomainName(java.lang.String domainName) {
        this.domainName = domainName;
    }


    /**
     * Gets the postMasterEmail value for this Domain.
     * 
     * @return postMasterEmail
     */
    public java.lang.String getPostMasterEmail() {
        return postMasterEmail;
    }


    /**
     * Sets the postMasterEmail value for this Domain.
     * 
     * @param postMasterEmail
     */
    public void setPostMasterEmail(java.lang.String postMasterEmail) {
        this.postMasterEmail = postMasterEmail;
    }


    /**
     * Gets the postmasterAddressId value for this Domain.
     * 
     * @return postmasterAddressId
     */
    public java.lang.Long getPostmasterAddressId() {
        return postmasterAddressId;
    }


    /**
     * Sets the postmasterAddressId value for this Domain.
     * 
     * @param postmasterAddressId
     */
    public void setPostmasterAddressId(java.lang.Long postmasterAddressId) {
        this.postmasterAddressId = postmasterAddressId;
    }


    /**
     * Gets the updateTime value for this Domain.
     * 
     * @return updateTime
     */
    public java.util.Calendar getUpdateTime() {
        return updateTime;
    }


    /**
     * Sets the updateTime value for this Domain.
     * 
     * @param updateTime
     */
    public void setUpdateTime(java.util.Calendar updateTime) {
        this.updateTime = updateTime;
    }


    /**
     * Gets the id value for this Domain.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this Domain.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the status value for this Domain.
     * 
     * @return status
     */
    public org.nhind.config.EntityStatus getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Domain.
     * 
     * @param status
     */
    public void setStatus(org.nhind.config.EntityStatus status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Domain)) return false;
        Domain other = (Domain) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              java.util.Arrays.equals(this.address, other.getAddress()))) &&
            ((this.createTime==null && other.getCreateTime()==null) || 
             (this.createTime!=null &&
              this.createTime.equals(other.getCreateTime()))) &&
            ((this.domainName==null && other.getDomainName()==null) || 
             (this.domainName!=null &&
              this.domainName.equals(other.getDomainName()))) &&
            ((this.postMasterEmail==null && other.getPostMasterEmail()==null) || 
             (this.postMasterEmail!=null &&
              this.postMasterEmail.equals(other.getPostMasterEmail()))) &&
            ((this.postmasterAddressId==null && other.getPostmasterAddressId()==null) || 
             (this.postmasterAddressId!=null &&
              this.postmasterAddressId.equals(other.getPostmasterAddressId()))) &&
            ((this.updateTime==null && other.getUpdateTime()==null) || 
             (this.updateTime!=null &&
              this.updateTime.equals(other.getUpdateTime()))) &&
            this.id == other.getId() &&
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
        if (getAddress() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAddress());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAddress(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCreateTime() != null) {
            _hashCode += getCreateTime().hashCode();
        }
        if (getDomainName() != null) {
            _hashCode += getDomainName().hashCode();
        }
        if (getPostMasterEmail() != null) {
            _hashCode += getPostMasterEmail().hashCode();
        }
        if (getPostmasterAddressId() != null) {
            _hashCode += getPostmasterAddressId().hashCode();
        }
        if (getUpdateTime() != null) {
            _hashCode += getUpdateTime().hashCode();
        }
        _hashCode += new Long(getId()).hashCode();
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Domain.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "domain"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("id");
        attrField.setXmlName(new javax.xml.namespace.QName("", "id"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("status");
        attrField.setXmlName(new javax.xml.namespace.QName("", "status"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "entityStatus"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("", "address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "address"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
        elemField.setFieldName("domainName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "domainName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postMasterEmail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "postMasterEmail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postmasterAddressId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "postmasterAddressId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "updateTime"));
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
