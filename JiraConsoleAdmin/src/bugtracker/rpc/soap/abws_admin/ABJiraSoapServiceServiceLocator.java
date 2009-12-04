/**
 * ABJiraSoapServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package bugtracker.rpc.soap.abws_admin;

public class ABJiraSoapServiceServiceLocator extends org.apache.axis.client.Service implements bugtracker.rpc.soap.abws_admin.ABJiraSoapServiceService {

    public ABJiraSoapServiceServiceLocator() {
    }


    public ABJiraSoapServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ABJiraSoapServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AbwsAdmin
    private java.lang.String AbwsAdmin_address = "http://bugtracker/rpc/soap/abws-admin";

    public java.lang.String getAbwsAdminAddress() {
        return AbwsAdmin_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AbwsAdminWSDDServiceName = "abws-admin";

    public java.lang.String getAbwsAdminWSDDServiceName() {
        return AbwsAdminWSDDServiceName;
    }

    public void setAbwsAdminWSDDServiceName(java.lang.String name) {
        AbwsAdminWSDDServiceName = name;
    }

    public bugtracker.rpc.soap.abws_admin.ABJiraSoapService getAbwsAdmin() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AbwsAdmin_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAbwsAdmin(endpoint);
    }

    public bugtracker.rpc.soap.abws_admin.ABJiraSoapService getAbwsAdmin(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            bugtracker.rpc.soap.abws_admin.AbwsAdminSoapBindingStub _stub = new bugtracker.rpc.soap.abws_admin.AbwsAdminSoapBindingStub(portAddress, this);
            _stub.setPortName(getAbwsAdminWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAbwsAdminEndpointAddress(java.lang.String address) {
        AbwsAdmin_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (bugtracker.rpc.soap.abws_admin.ABJiraSoapService.class.isAssignableFrom(serviceEndpointInterface)) {
                bugtracker.rpc.soap.abws_admin.AbwsAdminSoapBindingStub _stub = new bugtracker.rpc.soap.abws_admin.AbwsAdminSoapBindingStub(new java.net.URL(AbwsAdmin_address), this);
                _stub.setPortName(getAbwsAdminWSDDServiceName());
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
        if ("abws-admin".equals(inputPortName)) {
            return getAbwsAdmin();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://bugtracker/rpc/soap/abws-admin", "ABJiraSoapServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://bugtracker/rpc/soap/abws-admin", "abws-admin"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AbwsAdmin".equals(portName)) {
            setAbwsAdminEndpointAddress(address);
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
