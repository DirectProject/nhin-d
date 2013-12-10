/**
 * TrustBundle.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhind.config;

public class TrustBundle  implements java.io.Serializable {
    private java.lang.String bundleName;

    private java.lang.String bundleURL;

    private java.lang.String checkSum;

    private java.util.Calendar createTime;

    private long id;

    private java.util.Calendar lastRefreshAttempt;

    private org.nhind.config.BundleRefreshError lastRefreshError;

    private java.util.Calendar lastSuccessfulRefresh;

    private int refreshInterval;

    private byte[] signingCertificateData;

    private org.nhind.config.TrustBundleAnchor[] trustBundleAnchors;

    public TrustBundle() {
    }

    public TrustBundle(
           java.lang.String bundleName,
           java.lang.String bundleURL,
           java.lang.String checkSum,
           java.util.Calendar createTime,
           long id,
           java.util.Calendar lastRefreshAttempt,
           org.nhind.config.BundleRefreshError lastRefreshError,
           java.util.Calendar lastSuccessfulRefresh,
           int refreshInterval,
           byte[] signingCertificateData,
           org.nhind.config.TrustBundleAnchor[] trustBundleAnchors) {
           this.bundleName = bundleName;
           this.bundleURL = bundleURL;
           this.checkSum = checkSum;
           this.createTime = createTime;
           this.id = id;
           this.lastRefreshAttempt = lastRefreshAttempt;
           this.lastRefreshError = lastRefreshError;
           this.lastSuccessfulRefresh = lastSuccessfulRefresh;
           this.refreshInterval = refreshInterval;
           this.signingCertificateData = signingCertificateData;
           this.trustBundleAnchors = trustBundleAnchors;
    }


    /**
     * Gets the bundleName value for this TrustBundle.
     * 
     * @return bundleName
     */
    public java.lang.String getBundleName() {
        return bundleName;
    }


    /**
     * Sets the bundleName value for this TrustBundle.
     * 
     * @param bundleName
     */
    public void setBundleName(java.lang.String bundleName) {
        this.bundleName = bundleName;
    }


    /**
     * Gets the bundleURL value for this TrustBundle.
     * 
     * @return bundleURL
     */
    public java.lang.String getBundleURL() {
        return bundleURL;
    }


    /**
     * Sets the bundleURL value for this TrustBundle.
     * 
     * @param bundleURL
     */
    public void setBundleURL(java.lang.String bundleURL) {
        this.bundleURL = bundleURL;
    }


    /**
     * Gets the checkSum value for this TrustBundle.
     * 
     * @return checkSum
     */
    public java.lang.String getCheckSum() {
        return checkSum;
    }


    /**
     * Sets the checkSum value for this TrustBundle.
     * 
     * @param checkSum
     */
    public void setCheckSum(java.lang.String checkSum) {
        this.checkSum = checkSum;
    }


    /**
     * Gets the createTime value for this TrustBundle.
     * 
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }


    /**
     * Sets the createTime value for this TrustBundle.
     * 
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }


    /**
     * Gets the id value for this TrustBundle.
     * 
     * @return id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the id value for this TrustBundle.
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     * Gets the lastRefreshAttempt value for this TrustBundle.
     * 
     * @return lastRefreshAttempt
     */
    public java.util.Calendar getLastRefreshAttempt() {
        return lastRefreshAttempt;
    }


    /**
     * Sets the lastRefreshAttempt value for this TrustBundle.
     * 
     * @param lastRefreshAttempt
     */
    public void setLastRefreshAttempt(java.util.Calendar lastRefreshAttempt) {
        this.lastRefreshAttempt = lastRefreshAttempt;
    }


    /**
     * Gets the lastRefreshError value for this TrustBundle.
     * 
     * @return lastRefreshError
     */
    public org.nhind.config.BundleRefreshError getLastRefreshError() {
        return lastRefreshError;
    }


    /**
     * Sets the lastRefreshError value for this TrustBundle.
     * 
     * @param lastRefreshError
     */
    public void setLastRefreshError(org.nhind.config.BundleRefreshError lastRefreshError) {
        this.lastRefreshError = lastRefreshError;
    }


    /**
     * Gets the lastSuccessfulRefresh value for this TrustBundle.
     * 
     * @return lastSuccessfulRefresh
     */
    public java.util.Calendar getLastSuccessfulRefresh() {
        return lastSuccessfulRefresh;
    }


    /**
     * Sets the lastSuccessfulRefresh value for this TrustBundle.
     * 
     * @param lastSuccessfulRefresh
     */
    public void setLastSuccessfulRefresh(java.util.Calendar lastSuccessfulRefresh) {
        this.lastSuccessfulRefresh = lastSuccessfulRefresh;
    }


    /**
     * Gets the refreshInterval value for this TrustBundle.
     * 
     * @return refreshInterval
     */
    public int getRefreshInterval() {
        return refreshInterval;
    }


    /**
     * Sets the refreshInterval value for this TrustBundle.
     * 
     * @param refreshInterval
     */
    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }


    /**
     * Gets the signingCertificateData value for this TrustBundle.
     * 
     * @return signingCertificateData
     */
    public byte[] getSigningCertificateData() {
        return signingCertificateData;
    }


    /**
     * Sets the signingCertificateData value for this TrustBundle.
     * 
     * @param signingCertificateData
     */
    public void setSigningCertificateData(byte[] signingCertificateData) {
        this.signingCertificateData = signingCertificateData;
    }


    /**
     * Gets the trustBundleAnchors value for this TrustBundle.
     * 
     * @return trustBundleAnchors
     */
    public org.nhind.config.TrustBundleAnchor[] getTrustBundleAnchors() {
        return trustBundleAnchors;
    }


    /**
     * Sets the trustBundleAnchors value for this TrustBundle.
     * 
     * @param trustBundleAnchors
     */
    public void setTrustBundleAnchors(org.nhind.config.TrustBundleAnchor[] trustBundleAnchors) {
        this.trustBundleAnchors = trustBundleAnchors;
    }

    public org.nhind.config.TrustBundleAnchor getTrustBundleAnchors(int i) {
        return this.trustBundleAnchors[i];
    }

    public void setTrustBundleAnchors(int i, org.nhind.config.TrustBundleAnchor _value) {
        this.trustBundleAnchors[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TrustBundle)) return false;
        TrustBundle other = (TrustBundle) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bundleName==null && other.getBundleName()==null) || 
             (this.bundleName!=null &&
              this.bundleName.equals(other.getBundleName()))) &&
            ((this.bundleURL==null && other.getBundleURL()==null) || 
             (this.bundleURL!=null &&
              this.bundleURL.equals(other.getBundleURL()))) &&
            ((this.checkSum==null && other.getCheckSum()==null) || 
             (this.checkSum!=null &&
              this.checkSum.equals(other.getCheckSum()))) &&
            ((this.createTime==null && other.getCreateTime()==null) || 
             (this.createTime!=null &&
              this.createTime.equals(other.getCreateTime()))) &&
            this.id == other.getId() &&
            ((this.lastRefreshAttempt==null && other.getLastRefreshAttempt()==null) || 
             (this.lastRefreshAttempt!=null &&
              this.lastRefreshAttempt.equals(other.getLastRefreshAttempt()))) &&
            ((this.lastRefreshError==null && other.getLastRefreshError()==null) || 
             (this.lastRefreshError!=null &&
              this.lastRefreshError.equals(other.getLastRefreshError()))) &&
            ((this.lastSuccessfulRefresh==null && other.getLastSuccessfulRefresh()==null) || 
             (this.lastSuccessfulRefresh!=null &&
              this.lastSuccessfulRefresh.equals(other.getLastSuccessfulRefresh()))) &&
            this.refreshInterval == other.getRefreshInterval() &&
            ((this.signingCertificateData==null && other.getSigningCertificateData()==null) || 
             (this.signingCertificateData!=null &&
              java.util.Arrays.equals(this.signingCertificateData, other.getSigningCertificateData()))) &&
            ((this.trustBundleAnchors==null && other.getTrustBundleAnchors()==null) || 
             (this.trustBundleAnchors!=null &&
              java.util.Arrays.equals(this.trustBundleAnchors, other.getTrustBundleAnchors())));
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
        if (getBundleName() != null) {
            _hashCode += getBundleName().hashCode();
        }
        if (getBundleURL() != null) {
            _hashCode += getBundleURL().hashCode();
        }
        if (getCheckSum() != null) {
            _hashCode += getCheckSum().hashCode();
        }
        if (getCreateTime() != null) {
            _hashCode += getCreateTime().hashCode();
        }
        _hashCode += new Long(getId()).hashCode();
        if (getLastRefreshAttempt() != null) {
            _hashCode += getLastRefreshAttempt().hashCode();
        }
        if (getLastRefreshError() != null) {
            _hashCode += getLastRefreshError().hashCode();
        }
        if (getLastSuccessfulRefresh() != null) {
            _hashCode += getLastSuccessfulRefresh().hashCode();
        }
        _hashCode += getRefreshInterval();
        if (getSigningCertificateData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSigningCertificateData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSigningCertificateData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTrustBundleAnchors() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTrustBundleAnchors());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTrustBundleAnchors(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TrustBundle.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "trustBundle"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bundleName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bundleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bundleURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bundleURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkSum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "checkSum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastRefreshAttempt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastRefreshAttempt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastRefreshError");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastRefreshError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "bundleRefreshError"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastSuccessfulRefresh");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastSuccessfulRefresh"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refreshInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("", "refreshInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signingCertificateData");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signingCertificateData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trustBundleAnchors");
        elemField.setXmlName(new javax.xml.namespace.QName("", "trustBundleAnchors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nhind.org/config", "trustBundleAnchor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
