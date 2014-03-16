/**
 * Certificate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class Certificate  implements java.io.Serializable {
    private java.util.Calendar createTime;

    private byte[] data;

    private long id;

    private java.lang.String owner;

    private boolean privateKey;

    private org.nhind.config.EntityStatus status;

    private java.util.Calendar validEndDate;

    private java.util.Calendar validStartDate;

    public Certificate() {
    }

    public Certificate(
           java.util.Calendar createTime,
           byte[] data,
           long id,
           java.lang.String owner,
           boolean privateKey,
           org.nhind.config.EntityStatus status,
           java.util.Calendar validEndDate,
           java.util.Calendar validStartDate) {
           this.createTime = createTime;
           this.data = data;
           this.id = id;
           this.owner = owner;
           this.privateKey = privateKey;
           this.status = status;
           this.validEndDate = validEndDate;
           this.validStartDate = validStartDate;
    }


    /**
     * Gets the createTime value for this Certificate.
     * 
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }


    /**
     * Sets the createTime value for this Certificate.
     * 
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }


    /**
     * Gets the data value for this Certificate.
     * 
     * @return data
     */
    public byte[] getData() {
        return data;
    }


    /**
     * Sets the data value for this Certificate.
     * 
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }


    /**
     * Gets the id value for this Certificate.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this Certificate.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the owner value for this Certificate.
     * 
     * @return owner
     */
    public java.lang.String getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this Certificate.
     * 
     * @param owner
     */
    public void setOwner(java.lang.String owner) {
        this.owner = owner;
    }


    /**
     * Gets the privateKey value for this Certificate.
     * 
     * @return privateKey
     */
    public boolean isPrivateKey() {
        return privateKey;
    }


    /**
     * Sets the privateKey value for this Certificate.
     * 
     * @param privateKey
     */
    public void setPrivateKey(boolean privateKey) {
        this.privateKey = privateKey;
    }


    /**
     * Gets the status value for this Certificate.
     * 
     * @return status
     */
    public org.nhind.config.EntityStatus getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Certificate.
     * 
     * @param status
     */
    public void setStatus(org.nhind.config.EntityStatus status) {
        this.status = status;
    }


    /**
     * Gets the validEndDate value for this Certificate.
     * 
     * @return validEndDate
     */
    public java.util.Calendar getValidEndDate() {
        return validEndDate;
    }


    /**
     * Sets the validEndDate value for this Certificate.
     * 
     * @param validEndDate
     */
    public void setValidEndDate(java.util.Calendar validEndDate) {
        this.validEndDate = validEndDate;
    }


    /**
     * Gets the validStartDate value for this Certificate.
     * 
     * @return validStartDate
     */
    public java.util.Calendar getValidStartDate() {
        return validStartDate;
    }


    /**
     * Sets the validStartDate value for this Certificate.
     * 
     * @param validStartDate
     */
    public void setValidStartDate(java.util.Calendar validStartDate) {
        this.validStartDate = validStartDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Certificate)) return false;
        Certificate other = (Certificate) obj;
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
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              java.util.Arrays.equals(this.data, other.getData()))) &&
            this.id == other.getId() &&
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            this.privateKey == other.isPrivateKey() &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
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
        if (getCreateTime() != null) {
            _hashCode += getCreateTime().hashCode();
        }
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
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        _hashCode += (isPrivateKey() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
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
        new org.apache.axis.description.TypeDesc(Certificate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "certificate"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "createTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("", "owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("privateKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "privateKey"));
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
