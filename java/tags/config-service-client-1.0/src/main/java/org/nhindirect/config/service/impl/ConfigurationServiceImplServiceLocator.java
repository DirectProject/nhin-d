/**
 * ConfigurationServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.nhindirect.config.service.impl;

public class ConfigurationServiceImplServiceLocator extends org.apache.axis.client.Service implements org.nhindirect.config.service.impl.ConfigurationServiceImplService {

    public ConfigurationServiceImplServiceLocator() {
    }


    public ConfigurationServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ConfigurationServiceImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ConfigurationServiceImplPort
    private java.lang.String ConfigurationServiceImplPort_address = "http://localhost:8080/config/ConfigurationService";

    public java.lang.String getConfigurationServiceImplPortAddress() {
        return ConfigurationServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ConfigurationServiceImplPortWSDDServiceName = "ConfigurationServiceImplPort";

    public java.lang.String getConfigurationServiceImplPortWSDDServiceName() {
        return ConfigurationServiceImplPortWSDDServiceName;
    }

    public void setConfigurationServiceImplPortWSDDServiceName(java.lang.String name) {
        ConfigurationServiceImplPortWSDDServiceName = name;
    }

    public org.nhind.config.ConfigurationService getConfigurationServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ConfigurationServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getConfigurationServiceImplPort(endpoint);
    }

    public org.nhind.config.ConfigurationService getConfigurationServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.nhindirect.config.service.impl.ConfigurationServiceImplServiceSoapBindingStub _stub = new org.nhindirect.config.service.impl.ConfigurationServiceImplServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getConfigurationServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setConfigurationServiceImplPortEndpointAddress(java.lang.String address) {
        ConfigurationServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.nhind.config.ConfigurationService.class.isAssignableFrom(serviceEndpointInterface)) {
                org.nhindirect.config.service.impl.ConfigurationServiceImplServiceSoapBindingStub _stub = new org.nhindirect.config.service.impl.ConfigurationServiceImplServiceSoapBindingStub(new java.net.URL(ConfigurationServiceImplPort_address), this);
                _stub.setPortName(getConfigurationServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ConfigurationServiceImplPort".equals(inputPortName)) {
            return getConfigurationServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://impl.service.config.nhindirect.org/", "ConfigurationServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://impl.service.config.nhindirect.org/", "ConfigurationServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ConfigurationServiceImplPort".equals(portName)) {
            setConfigurationServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
