/**
 * Anchor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class Anchor  implements java.io.Serializable {
    private long certificateId;

    private java.util.Calendar createTime;

    private byte[] data;

    private long id;

    private boolean incoming;

    private boolean outgoing;

    private java.lang.String owner;

    private org.nhind.config.EntityStatus status;

    private java.lang.String thumbprint;

    private java.util.Calendar validEndDate;

    private java.util.Calendar validStartDate;

    public Anchor() {
    }

    public Anchor(
           long certificateId,
           java.util.Calendar createTime,
           byte[] data,
           long id,
           boolean incoming,
           boolean outgoing,
           java.lang.String owner,
           org.nhind.config.EntityStatus status,
           java.lang.String thumbprint,
           java.util.Calendar validEndDate,
           java.util.Calendar validStartDate) {
           this.certificateId = certificateId;
           this.createTime = createTime;
           this.data = data;
           this.id = id;
           this.incoming = incoming;
           this.outgoing = outgoing;
           this.owner = owner;
           this.status = status;
           this.thumbprint = thumbprint;
           this.validEndDate = validEndDate;
           this.validStartDate = validStartDate;
    }


    /**
     * Gets the certificateId value for this Anchor.
     * 
     * @return certificateId
     */
    public long getCertificateId() {
        return certificateId;
    }


    /**
     * Sets the certificateId value for this Anchor.
     * 
     * @param certificateId
     */
    public void setCertificateId(long certificateId) {
        this.certificateId = certificateId;
    }


    /**
     * Gets the createTime value for this Anchor.
     * 
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }


    /**
     * Sets the createTime value for this Anchor.
     * 
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }


    /**
     * Gets the data value for this Anchor.
     * 
     * @return data
     */
    public byte[] getData() {
        return data;
    }


    /**
     * Sets the data value for this Anchor.
     * 
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }


    /**
     * Gets the id value for this Anchor.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this Anchor.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the incoming value for this Anchor.
     * 
     * @return incoming
     */
    public boolean isIncoming() {
        return incoming;
    }


    /**
     * Sets the incoming value for this Anchor.
     * 
     * @param incoming
     */
    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }


    /**
     * Gets the outgoing value for this Anchor.
     * 
     * @return outgoing
     */
    public boolean isOutgoing() {
        return outgoing;
    }


    /**
     * Sets the outgoing value for this Anchor.
     * 
     * @param outgoing
     */
    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }


    /**
     * Gets the owner value for this Anchor.
     * 
     * @return owner
     */
    public java.lang.String getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this Anchor.
     * 
     * @param owner
     */
    public void setOwner(java.lang.String owner) {
        this.owner = owner;
    }


    /**
     * Gets the status value for this Anchor.
     * 
     * @return status
     */
    public org.nhind.config.EntityStatus getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Anchor.
     * 
     * @param status
     */
    public void setStatus(org.nhind.config.EntityStatus status) {
        this.status = status;
    }


    /**
     * Gets the thumbprint value for this Anchor.
     * 
     * @return thumbprint
     */
    public java.lang.String getThumbprint() {
        return thumbprint;
    }


    /**
     * Sets the thumbprint value for this Anchor.
     * 
     * @param thumbprint
     */
    public void setThumbprint(java.lang.String thumbprint) {
        this.thumbprint = thumbprint;
    }


    /**
     * Gets the validEndDate value for this Anchor.
     * 
     * @return validEndDate
     */
    public java.util.Calendar getValidEndDate() {
        return validEndDate;
    }


    /**
     * Sets the validEndDate value for this Anchor.
     * 
     * @param validEndDate
     */
    public void setValidEndDate(java.util.Calendar validEndDate) {
        this.validEndDate = validEndDate;
    }


    /**
     * Gets the validStartDate value for this Anchor.
     * 
     * @return validStartDate
     */
    public java.util.Calendar getValidStartDate() {
        return validStartDate;
    }


    /**
     * Sets the validStartDate value for this Anchor.
     * 
     * @param validStartDate
     */
    public void setValidStartDate(java.util.Calendar validStartDate) {
        this.validStartDate = validStartDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Anchor)) return false;
        Anchor other = (Anchor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.certificateId == other.getCertificateId() &&
            ((this.createTime==null && other.getCreateTime()==null) || 
             (this.createTime!=null &&
              this.createTime.equals(other.getCreateTime()))) &&
            ((this.data==null && other.getData()==null) || 
             (this.data!=null &&
              java.util.Arrays.equals(this.data, other.getData()))) &&
            this.id == other.getId() &&
            this.incoming == other.isIncoming() &&
            this.outgoing == other.isOutgoing() &&
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.thumbprint==null && other.getThumbprint()==null) || 
             (this.thumbprint!=null &&
              this.thumbprint.equals(other.getThumbprint()))) &&
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
        _hashCode += new Long(getCertificateId()).hashCode();
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
        _hashCode += (isIncoming() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isOutgoing() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getThumbprint() != null) {
            _hashCode += getThumbprint().hashCode();
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
        new org.apache.axis.description.TypeDesc(Anchor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "anchor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certificateId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certificateId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("", "owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("thumbprint");
        elemField.setXmlName(new javax.xml.namespace.QName("", "thumbprint"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
